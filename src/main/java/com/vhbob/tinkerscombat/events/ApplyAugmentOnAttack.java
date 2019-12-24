package com.vhbob.tinkerscombat.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.vhbob.tinkerscombat.util.Augment;
import com.vhbob.tinkerscombat.util.AugmentType;
import com.vhbob.tinkerscombat.util.AugmentedItem;

public class ApplyAugmentOnAttack implements Listener {

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof LivingEntity))
			return;
		LivingEntity le = (LivingEntity) e.getEntity();
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if (p.getInventory().getItemInMainHand() != null) {
				AugmentedItem augItem = new AugmentedItem(p.getInventory().getItemInMainHand());
				if (!augItem.getAugments().isEmpty()) {
					for (Augment a : augItem.getAugments()) {
						if (a.getType() == AugmentType.WEAPON) {
							// Apply user effects
							for (PotionEffect effect : a.getUserEffects()) {
								p.addPotionEffect(effect);
							}
							// Apply target effects
							for (PotionEffect effect : a.getNonUserEffects()) {
								le.addPotionEffect(effect);
							}
						}
					}

				}
			}
		}
	}

	@EventHandler
	public void onDefend(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			Player defend = (Player) e.getEntity();
			if (e.getDamager() instanceof LivingEntity) {
				LivingEntity le = (LivingEntity) e.getDamager();
				// Loop throgh defender's armor
				for (ItemStack i : defend.getInventory().getArmorContents()) {
					if (i != null) {
						AugmentedItem augItem = new AugmentedItem(i);
						if (!augItem.getAugments().isEmpty()) {
							// Loop through each piece's augments
							for (Augment a : augItem.getAugments()) {
								// Apply effects to agressor
								for (PotionEffect effect : a.getNonUserEffects()) {
									le.addPotionEffect(effect);
								}
							}
						}
					}
				}
			}
		}
	}

}
