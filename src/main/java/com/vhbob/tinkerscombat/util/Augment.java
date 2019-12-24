package com.vhbob.tinkerscombat.util;

import java.util.ArrayList;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.vhbob.tinkerscombat.TinkersCombat;

public class Augment {

	private AugmentType type;
	private ItemStack item;
	// Effects 1 will be for the one who owns the item
	ArrayList<PotionEffect> effects1, effects2;

	/*
	 * This method will be called when an Augment is enabled. This will allow for us
	 * to retrieve augments based on the ItemStack used by the player
	 * 
	 * @param config the config of the Augment
	 * 
	 * @type the type of the augment
	 */
	public Augment(YamlConfiguration config, AugmentType type, ItemStack baseItem) {
		this.type = type;
		// Load the respective effects
		effects1 = new ArrayList<PotionEffect>();
		effects2 = new ArrayList<PotionEffect>();
		if (config.contains("effects.user"))
			for (String effUser : config.getConfigurationSection("effects.user").getKeys(false)) {
				PotionEffect effect = new PotionEffect(PotionEffectType.getByName(effUser),
						config.getInt("effects.user." + effUser + ".duration") * 20,
						config.getInt("effects.user." + effUser + ".level") - 1);
				effects1.add(effect);
			}
		if (config.contains("effects.other"))
			for (String effTarget : config.getConfigurationSection("effects.other").getKeys(false)) {
				PotionEffect effect = new PotionEffect(PotionEffectType.getByName(effTarget),
						config.getInt("effects.other." + effTarget + ".duration") * 20,
						config.getInt("effects.other." + effTarget + ".level") - 1);
				effects2.add(effect);
			}
		// Load item
		item = baseItem.clone();
	}

	public AugmentType getType() {
		return type;
	}

	public ItemStack getItem() {
		return item;
	}

	public ArrayList<PotionEffect> getUserEffects() {
		return effects1;
	}

	public ArrayList<PotionEffect> getNonUserEffects() {
		return effects2;
	}

	/*
	 * This method will return a boolean representing whether or not an Augment can
	 * be applied to a certain item
	 * 
	 * @param item the item
	 * 
	 * @return a boolean representing if the Augment can be applied
	 */
	public boolean checkValid(ItemStack item) {
		String mat = item.getType().toString();
		if (type == AugmentType.ARMOR && TinkersCombat.instance.getConfig().contains("sockets.armor." + mat)) {
			return true;
		}
		if (type == AugmentType.WEAPON && TinkersCombat.instance.getConfig().contains("sockets.weapon." + mat)) {
			return true;
		}
		return false;
	}

}
