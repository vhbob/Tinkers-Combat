package com.vhbob.tinkerscombat.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

	public static ItemStack buildItem(Material type, List<String> unparsedLore, String unparsedName) {
		ItemStack i = new ItemStack(type);
		ItemMeta im = i.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();
		for (String s : unparsedLore) {
			lore.add(ChatUtils.parseColor(s));
		}
		im.setLore(lore);
		im.setDisplayName(ChatUtils.parseColor(unparsedName));
		i.setItemMeta(im);
		return i;
	}

}
