package com.vhbob.tinkerscombat.util;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import com.vhbob.tinkerscombat.TinkersCombat;

public class Validate {

	/*
	 * This method will validate an augment. If an augment does not have a recipe,
	 * or item, it is not valid
	 * 
	 * @param augConfig the config file for the augment
	 * 
	 * @return a boolean representing the augment's validity
	 */
	public static boolean isValidAugment(FileConfiguration augConfig) {
		if (!augConfig.contains("recipe.format") || !augConfig.contains("recipe.shaped")
				|| !augConfig.contains("recipe.items")) {
			Bukkit.getConsoleSender()
					.sendMessage(TinkersCombat.prefix + "A recipe error exists in for" + augConfig.getName());
			return false;
		}
		List<String> format = augConfig.getStringList("recipe.format");
		if (format.size() != 3) {
			Bukkit.getConsoleSender().sendMessage(
					TinkersCombat.prefix + "The recipe for " + augConfig.getName() + " does not have 3 rows");
			return false;
		}
		for (String s : format) {
			if (s.length() != 3) {
				Bukkit.getConsoleSender().sendMessage(TinkersCombat.prefix + "A row in the recipe of "
						+ augConfig.getName() + " does not have 3 spaces");
				return false;
			}
			for (char c : s.toCharArray()) {
				if (Character.isWhitespace(c))
					continue;
				if (!augConfig.contains("recipe.items." + c)
						|| Material.valueOf(augConfig.getString("recipe.items." + c)) == null) {
					Bukkit.getConsoleSender().sendMessage(
							TinkersCombat.prefix + "An ingredient in " + augConfig.getName() + " is undefined");
					return false;
				}
			}
		}
		if (!augConfig.contains("item.type") || !augConfig.contains("item.name") || !augConfig.contains("item.lore")) {
			Bukkit.getConsoleSender()
					.sendMessage(TinkersCombat.prefix + "The item for " + augConfig.getName() + " is undefined");
			return false;
		}
		return true;
	}

	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			@SuppressWarnings("unused")
			int i = Integer.parseInt(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

}
