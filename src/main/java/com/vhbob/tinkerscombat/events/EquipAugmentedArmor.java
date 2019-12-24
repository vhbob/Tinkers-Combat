package com.vhbob.tinkerscombat.events;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.vhbob.tinkerscombat.util.Augment;
import com.vhbob.tinkerscombat.util.AugmentedItem;

public class EquipAugmentedArmor implements Listener {

	@EventHandler
	public void onEquip(ArmorEquipEvent e) {
		// Remove previous effect if needed
		if (e.getOldArmorPiece() != null) {
			AugmentedItem augItem = new AugmentedItem(e.getOldArmorPiece());
			for (Augment aug : augItem.getAugments()) {
				for (PotionEffect effect : aug.getUserEffects()) {
					if (e.getPlayer().hasPotionEffect(effect.getType()))
						e.getPlayer().removePotionEffect(effect.getType());
				}
			}
		}
		// Load current armor
		ArrayList<ItemStack> equippedItems = new ArrayList<ItemStack>();
		for (ItemStack i : e.getPlayer().getInventory().getArmorContents())
			if (i != null)
				equippedItems.add(i);
		if (e.getOldArmorPiece() != null && equippedItems.contains(e.getOldArmorPiece()))
			equippedItems.remove(e.getOldArmorPiece());
		if (e.getNewArmorPiece() != null && !equippedItems.contains(e.getNewArmorPiece()))
			equippedItems.add(e.getNewArmorPiece());
		// Give user new effects
		for (ItemStack item : equippedItems) {
			if (item != null && item.getType() != Material.AIR) {
				AugmentedItem augItem = new AugmentedItem(item);
				for (Augment aug : augItem.getAugments()) {
					for (PotionEffect effect : aug.getUserEffects()) {
						PotionEffect effectToApply = new PotionEffect(effect.getType(), 370000000,
								effect.getAmplifier());
						e.getPlayer().addPotionEffect(effectToApply);
					}
				}
			}
		}
	}

}
