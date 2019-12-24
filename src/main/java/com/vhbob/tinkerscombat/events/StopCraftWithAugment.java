package com.vhbob.tinkerscombat.events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import com.vhbob.tinkerscombat.TinkersCombat;

public class StopCraftWithAugment implements Listener {

	@EventHandler
	public void onCraft(CraftItemEvent e) {
		for (int i = 1; i < e.getInventory().getSize(); i++)
			if (e.getInventory().getItem(i) != null
					&& TinkersCombat.enabledAugments.containsKey(e.getInventory().getItem(i))) {
				e.getWhoClicked().sendMessage(
						TinkersCombat.prefix + ChatColor.DARK_RED + "You may not use augments in recipes!");
				e.setCancelled(true);
			}
	}

}
