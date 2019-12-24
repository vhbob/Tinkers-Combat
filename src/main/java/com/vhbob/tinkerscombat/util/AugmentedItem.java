package com.vhbob.tinkerscombat.util;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.vhbob.tinkerscombat.TinkersCombat;

public class AugmentedItem {

	private ArrayList<Augment> augments;
	private ItemStack item;

	public AugmentedItem(ItemStack item) {
		this.item = item;
		// Load prexisting augments
		augments = new ArrayList<Augment>();
		if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
			ArrayList<String> lore = (ArrayList<String>) item.getItemMeta().getLore();
			for (ItemStack augItem : TinkersCombat.enabledAugments.keySet()) {
				if (lore.contains(augItem.getItemMeta().getDisplayName())) {
					augments.add(TinkersCombat.enabledAugments.get(augItem));
				}
			}
		}
	}

	// returns the augments
	public ArrayList<Augment> getAugments() {
		return augments;
	}

	/*
	 * This method adds an Augment to the item
	 * 
	 * @param a the augment
	 * 
	 */
	public void addAugment(Augment a) {
		ArrayList<String> lore = new ArrayList<String>();
		if (item.hasItemMeta() && item.getItemMeta().hasLore())
			lore = (ArrayList<String>) item.getItemMeta().getLore();
		lore.add(a.getItem().getItemMeta().getDisplayName());
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);
		augments.add(a);
	}

	/*
	 * This method removes an Augment from the item
	 * 
	 * @param a the augment
	 * 
	 */
	public void removeAugment(Augment a) {
		if (augments.contains(a)) {
			ArrayList<String> lore = (ArrayList<String>) item.getItemMeta().getLore();
			lore.remove(a.getItem().getItemMeta().getDisplayName());
			augments.remove(a);
		}
	}

	public void clearAugments() {
		if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
			ArrayList<String> lore = (ArrayList<String>) item.getItemMeta().getLore();
			for (Augment a : augments) {
				lore.remove(a.getItem().getItemMeta().getDisplayName());
			}
			ItemMeta meta = item.getItemMeta();
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		augments.clear();
	}

	public ItemStack getItem() {
		return item;
	}

}
