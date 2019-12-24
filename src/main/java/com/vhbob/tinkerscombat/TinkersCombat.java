package com.vhbob.tinkerscombat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import com.vhbob.tinkerscombat.events.AugmentEvents;
import com.vhbob.tinkerscombat.events.EquipAugmentedArmor;
import com.vhbob.tinkerscombat.events.StopCraftWithAugment;
import com.vhbob.tinkerscombat.events.ApplyAugmentOnAttack;
import com.vhbob.tinkerscombat.util.Augment;
import com.vhbob.tinkerscombat.util.AugmentType;
import com.vhbob.tinkerscombat.util.ChatUtils;
import com.vhbob.tinkerscombat.util.ItemBuilder;
import com.vhbob.tinkerscombat.util.Validate;

public class TinkersCombat extends JavaPlugin {

	public static TinkersCombat instance;
	public static String prefix;
	public static HashMap<ItemStack, Augment> enabledAugments;

	@Override
	public void onEnable() {
		instance = this;
		prefix = ChatUtils.parseColor(getConfig().getString("prefix") + "&r ");
		saveDefaultConfig();
		Bukkit.getConsoleSender().sendMessage(prefix + "Loading Augments");
		enabledAugments = new HashMap<ItemStack, Augment>();
		// Register weapon augments
		File weaponAugsFile = new File(getDataFolder() + File.separator + "Augments" + File.separator + "Weapons");
		registerAugments(weaponAugsFile, AugmentType.WEAPON);
		// Register armor augments
		File armorAugsFile = new File(getDataFolder() + File.separator + "Augments" + File.separator + "Armor");
		registerAugments(armorAugsFile, AugmentType.ARMOR);
		Bukkit.getPluginManager().registerEvents(new AugmentEvents(), this);
		Bukkit.getPluginManager().registerEvents(new ApplyAugmentOnAttack(), this);
		Bukkit.getPluginManager().registerEvents(new EquipAugmentedArmor(), this);
		Bukkit.getPluginManager().registerEvents(new StopCraftWithAugment(), this);
		Bukkit.getConsoleSender().sendMessage(prefix + "Tinker's Combat has been enabled!");
	}

	private void registerAugments(File folder, AugmentType type) {
		if (folder.exists()) {
			// Loop through augment files
			for (File augment : folder.listFiles()) {
				YamlConfiguration augConfig = new YamlConfiguration();
				try {
					augConfig.load(augment);
				} catch (FileNotFoundException e) {
					Bukkit.getConsoleSender().sendMessage(prefix + "Failed to load augment: " + augment.getName());
					continue;
				} catch (IOException e) {
					Bukkit.getConsoleSender().sendMessage(prefix + "Failed to load augment: " + augment.getName());
					continue;
				} catch (InvalidConfigurationException e) {
					Bukkit.getConsoleSender().sendMessage(prefix + "Failed to load augment: " + augment.getName());
					continue;
				}
				if (!Validate.isValidAugment(augConfig)) {
					Bukkit.getConsoleSender().sendMessage(prefix + "Failed to load augment: " + augment.getName());
					continue;
				}
				// Create recipe if it is valid
				List<String> recipeFormat = augConfig.getStringList("recipe.format");
				ItemStack augItem = ItemBuilder.buildItem(Material.valueOf(augConfig.getString("item.type")),
						augConfig.getStringList("item.lore"), augConfig.getString("item.name"));
				String l1 = recipeFormat.get(0), l2 = recipeFormat.get(1), l3 = recipeFormat.get(2);
				if (augConfig.getBoolean("recipe.shaped")) {
					ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, augment.getName()), augItem);
					recipe.shape(l1, l2, l3);
					// Load ingredients
					for (String s : augConfig.getConfigurationSection("recipe.items").getKeys(false)) {
						Material ingre = Material.valueOf(augConfig.getString("recipe.items." + s.charAt(0)));
						recipe.setIngredient(s.charAt(0), ingre);
					}
					Bukkit.addRecipe(recipe);
					enabledAugments.put(augItem, new Augment(augConfig, type, augItem));
				} else {
					ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(this, augment.getName()), augItem);
					String toAdd = l1 + l2 + l3;
					for (char c : toAdd.toCharArray()) {
						if (!Character.isWhitespace(c)) {
							recipe.addIngredient(Material.valueOf(augConfig.getString("recipe.items." + c)));
						}
					}
					Bukkit.addRecipe(recipe);
					enabledAugments.put(augItem, new Augment(augConfig, type, augItem));
				}
			}
		}
	}

}
