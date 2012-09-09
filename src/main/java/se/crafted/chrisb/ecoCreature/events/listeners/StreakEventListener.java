package se.crafted.chrisb.ecoCreature.events.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.events.handlers.RewardEventHandler;

public class StreakEventListener implements Listener
{
    private final RewardEventHandler handler;

    public StreakEventListener(RewardEventHandler handler)
    {
        this.handler = handler;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeathStreakEvent(DeathStreakEvent event)
    {
        for (RewardEvent rewardEvent : handler.getRewardEvents(event)) {
            Bukkit.getPluginManager().callEvent(rewardEvent);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKillStreakEvent(KillStreakEvent event)
    {
        for (RewardEvent rewardEvent : handler.getRewardEvents(event)) {
            Bukkit.getPluginManager().callEvent(rewardEvent);
        }
    }
}