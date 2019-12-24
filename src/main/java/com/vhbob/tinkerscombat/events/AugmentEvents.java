package com.vhbob.tinkerscombat.events;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.vhbob.tinkerscombat.TinkersCombat;
import com.vhbob.tinkerscombat.util.Augment;
import com.vhbob.tinkerscombat.util.AugmentedItem;

public class AugmentEvents implements Listener {

	private static HashMap<Player, AugmentedItem> modifying;

	public AugmentEvents() {
		if (modifying == null)
			modifying = new HashMap<Player, AugmentedItem>();
	}

	@EventHandler
	public void ModifyAugment(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		// Make sure they didnt click a block which opens an inventory
		if (e.getClickedBlock() != null && e.getClickedBlock().getState() instanceof Container) {
			return;
		}
		// Check if they have an augmentable item in their hand, open inv
		if (p.getInventory().getItemInMainHand() != null) {
			ItemStack item = p.getInventory().getItemInMainHand();
			int sockets = -1;
			if (TinkersCombat.instance.getConfig().contains("sockets.armor." + item.getType().toString())) {
				// Cancel if its not a left click
				if (!e.getAction().toString().contains("LEFT"))
					return;
				sockets = TinkersCombat.instance.getConfig().getInt("sockets.armor." + item.getType().toString());
			} else if (TinkersCombat.instance.getConfig().contains("sockets.weapon." + item.getType().toString())) {
				// Cancel if its not a right click
				if (!e.getAction().toString().contains("RIGHT"))
					return;
				sockets = TinkersCombat.instance.getConfig().getInt("sockets.weapon." + item.getType().toString());
			}
			if (sockets > 0) {
				Inventory inv = Bukkit.createInventory(p, (sockets / 9 + 1) * 9, "Apply your augmentations");
				AugmentedItem augItem = new AugmentedItem(item);
				// Add augments to the inventory
				for (Augment a : augItem.getAugments()) {
					inv.addItem(a.getItem());
				}
				// Fill unslotable slots
				ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
				for (int i = inv.getSize() - 1; i >= sockets; i--) {
					inv.setItem(i, filler);
				}
				p.openInventory(inv);
				modifying.put(p, augItem);
			}
		}
	}

	// Stop invalid clicks while modifying
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (modifying.keySet().contains(p)) {
			Set<ItemStack> validAugments = TinkersCombat.enabledAugments.keySet();
			ItemStack clicked = e.getCurrentItem(), cursor = e.getCursor();
			if (!validAugments.contains(clicked) && !validAugments.contains(cursor)) {
				e.setCancelled(true);
				p.sendMessage(TinkersCombat.prefix + ChatColor.DARK_RED
						+ "You can only touch augments (make sure they are not stacked!)");
			}
			if (e.getAction() == InventoryAction.SWAP_WITH_CURSOR
					&& (!validAugments.contains(clicked) || !validAugments.contains(cursor))) {
				e.setCancelled(true);
				p.sendMessage(TinkersCombat.prefix + ChatColor.DARK_RED
						+ "You can only touch augments (make sure they are not stacked!)");
			}
			if (e.getInventory().contains(clicked) && e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
					&& e.getClickedInventory().equals(p.getInventory())) {
				e.setCancelled(true);
				p.sendMessage(TinkersCombat.prefix + ChatColor.DARK_RED + "You already applied that augment");
			}
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if (modifying.keySet().contains(e.getPlayer())) {
			// clear old augments
			AugmentedItem augItem = modifying.get(e.getPlayer());
			augItem.clearAugments();
			// update new ones
			for (ItemStack i : e.getInventory()) {
				if (i == null || i.getType() == Material.BLACK_STAINED_GLASS_PANE)
					continue;
				if (TinkersCombat.enabledAugments.keySet().contains(i)) {
					Augment a = TinkersCombat.enabledAugments.get(i);
					// Check if applicable
					if (!a.checkValid(augItem.getItem())) {
						e.getPlayer().sendMessage(TinkersCombat.prefix + ChatColor.DARK_RED
								+ "One or more of your augments are not applicable to that item!");
						e.getPlayer().getInventory().addItem(i);
						continue;
					}
					// Check for duplicates
					if (augItem.getAugments().contains(a)) {
						e.getPlayer().sendMessage(TinkersCombat.prefix + ChatColor.DARK_RED
								+ "You used two or more of the same augment! Here are the extras.");
						e.getPlayer().getInventory().addItem(i);
					} else {
						augItem.addAugment(a);
						e.getPlayer().sendMessage(TinkersCombat.prefix + ChatColor.GREEN + "Eqipped your item with: "
								+ a.getItem().getItemMeta().getDisplayName());
					}
				} else {
					e.getPlayer().getInventory().addItem(i);
				}
			}
			// Play sound if needed
			if (TinkersCombat.instance.getConfig().getBoolean("sounds.modify.enabled"))
				e.getPlayer().getLocation().getWorld().playSound(e.getPlayer().getLocation(),
						Sound.valueOf(TinkersCombat.instance.getConfig().getString("sounds.modify.sound")), 1, 1);
			modifying.remove(e.getPlayer());
		}
	}

}
