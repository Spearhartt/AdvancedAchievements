package com.hm.achievement.listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import com.hm.achievement.AdvancedAchievements;

/**
 * Listener class to deal with Trades, AnvilsUsed and Brewing achievements.
 * 
 * @author Pyves
 *
 */
public class AchieveTradeAnvilBrewListener implements Listener {

	private AdvancedAchievements plugin;

	public AchieveTradeAnvilBrewListener(AdvancedAchievements plugin) {

		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {

		if (event.getRawSlot() != 0 && event.getRawSlot() != 1 && event.getRawSlot() != 2
				|| event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
			return;

		Player player = (Player) event.getWhoClicked();
		if (plugin.isRestrictCreative() && player.getGameMode() == GameMode.CREATIVE || plugin.isInExludedWorld(player)
				|| event.isShiftClick() && player.getInventory().firstEmpty() < 0)
			return;

		String configAchievement;

		if (player.hasPermission("achievement.count.trades") && !plugin.getDisabledCategorySet().contains("Trades")
				&& event.getRawSlot() == 2 && event.getInventory().getType() == InventoryType.MERCHANT) {
			int trades = plugin.getPoolsManager().getPlayerTradeAmount(player) + 1;

			plugin.getPoolsManager().getTradeHashMap().put(player.getUniqueId().toString(), trades);
			configAchievement = "Trades." + trades;

		} else if (player.hasPermission("achievement.count.anvilsused")
				&& !plugin.getDisabledCategorySet().contains("AnvilsUsed") && event.getRawSlot() == 2
				&& event.getInventory().getType() == InventoryType.ANVIL) {
			int anvils = plugin.getPoolsManager().getPlayerAnvilAmount(player) + 1;

			plugin.getPoolsManager().getAnvilHashMap().put(player.getUniqueId().toString(), anvils);
			configAchievement = "AnvilsUsed." + anvils;

		} else if (player.hasPermission("achievement.count.brewing")
				&& !plugin.getDisabledCategorySet().contains("Brewing")
				&& event.getInventory().getType() == InventoryType.BREWING) {
			int brewings = plugin.getPoolsManager().getPlayerBrewingAmount(player) + 1;

			plugin.getPoolsManager().getBrewingHashMap().put(player.getUniqueId().toString(), brewings);
			configAchievement = "Brewing." + brewings;

		} else
			return;

		if (plugin.getPluginConfig().getString(configAchievement + ".Message", null) != null) {

			plugin.getAchievementDisplay().displayAchievement(player, configAchievement);
			plugin.getDb().registerAchievement(player, plugin.getPluginConfig().getString(configAchievement + ".Name"),
					plugin.getPluginConfig().getString(configAchievement + ".Message"));

			plugin.getReward().checkConfig(player, configAchievement);
		}
	}
}
