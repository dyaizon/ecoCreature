/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2014, R. Ramos <http://github.com/mung3r/>
 * ecoCreature is licensed under the GNU Lesser General Public License.
 *
 * ecoCreature is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ecoCreature is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.crafted.chrisb.ecoCreature.drops.sources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.math.NumberRange;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.drops.AssembledDrop;
import se.crafted.chrisb.ecoCreature.drops.models.AbstractDrop;
import se.crafted.chrisb.ecoCreature.drops.models.BookDrop;
import se.crafted.chrisb.ecoCreature.drops.models.CoinDrop;
import se.crafted.chrisb.ecoCreature.drops.models.EntityDrop;
import se.crafted.chrisb.ecoCreature.drops.models.ItemDrop;
import se.crafted.chrisb.ecoCreature.drops.models.JockeyDrop;
import se.crafted.chrisb.ecoCreature.drops.models.LoreDrop;
import se.crafted.chrisb.ecoCreature.drops.rules.AbstractRule;
import se.crafted.chrisb.ecoCreature.drops.rules.Rule;
import se.crafted.chrisb.ecoCreature.messages.CoinMessageDecorator;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.NoCoinMessageDecorator;

public abstract class AbstractDropSource extends AbstractDrop
{
    private static final String NO_COIN_REWARD_MESSAGE = "&7You slayed a &5<crt>&7 using a &3<itm>&7.";
    private static final String COIN_REWARD_MESSAGE = "&7You are awarded &6<amt>&7 for slaying a &5<crt>&7.";
    private static final String COIN_PENALTY_MESSAGE = "&7You are penalized &6<amt>&7 for slaying a &5<crt>&7.";

    private String name;

    private CoinDrop coin;
    private Collection<ItemDrop> itemDrops;
    private Collection<EntityDrop> entityDrops;
    private Collection<JockeyDrop> jockeyDrops;

    private Message noCoinRewardMessage;
    private Message coinRewardMessage;
    private Message coinPenaltyMessage;

    private boolean fixedDrops;
    private boolean integerCurrency;
    private boolean addToInventory;

    private Map<Class<? extends AbstractRule>, Rule> huntingRules;

    public AbstractDropSource()
    {
        setRange(new NumberRange(1, 1));
        setPercentage(100.0D);
        huntingRules = Collections.emptyMap();
    }

    public AbstractDropSource(String section, ConfigurationSection config)
    {
        this();

        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }
        ConfigurationSection dropConfig = config.getConfigurationSection(section);
        name = dropConfig.getName();

        itemDrops = new ArrayList<>();
        itemDrops.addAll(ItemDrop.parseConfig(dropConfig));
        itemDrops.addAll(BookDrop.parseConfig(dropConfig));
        itemDrops.addAll(LoreDrop.parseConfig(dropConfig));
        entityDrops = EntityDrop.parseConfig(dropConfig);
        // TODO: hack - need to fix
        jockeyDrops = new ArrayList<>();
        for (EntityDrop drop : JockeyDrop.parseConfig(dropConfig)) {
            if (drop instanceof JockeyDrop) {
                jockeyDrops.add((JockeyDrop) drop);
            }
        }
        coin = CoinDrop.parseConfig(dropConfig);

        coinRewardMessage = new CoinMessageDecorator(new DefaultMessage(dropConfig.getString("Reward_Message", config.getString("System.Messages.Reward_Message", COIN_REWARD_MESSAGE))));
        coinPenaltyMessage = new CoinMessageDecorator(new DefaultMessage(dropConfig.getString("Penalty_Message", config.getString("System.Messages.Penalty_Message", COIN_PENALTY_MESSAGE))));
        noCoinRewardMessage = new NoCoinMessageDecorator(new DefaultMessage(dropConfig.getString("NoReward_Message", config.getString("System.Messages.NoReward_Message", NO_COIN_REWARD_MESSAGE))));

        addToInventory = dropConfig.getBoolean("AddItemsToInventory", false);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean hasPermission(Player player)
    {
        return DependencyUtils.hasPermission(player, "reward." + name);
    }

    public boolean hasCoin()
    {
        return coin != null;
    }

    public Message getNoCoinRewardMessage()
    {
        return noCoinRewardMessage;
    }

    public void setNoCoinRewardMessage(Message noCoinRewardMessage)
    {
        this.noCoinRewardMessage = noCoinRewardMessage;
    }

    public Message getCoinRewardMessage()
    {
        return coinRewardMessage;
    }

    public void setCoinRewardMessage(Message coinRewardMessage)
    {
        this.coinRewardMessage = coinRewardMessage;
    }

    public Message getCoinPenaltyMessage()
    {
        return coinPenaltyMessage;
    }

    public void setCoinPenaltyMessage(Message coinPenaltyMessage)
    {
        this.coinPenaltyMessage = coinPenaltyMessage;
    }

    public Boolean isFixedDrops()
    {
        return fixedDrops;
    }

    public void setFixedDrops(Boolean fixedDrops)
    {
        this.fixedDrops = fixedDrops;
    }

    public Boolean isIntegerCurrency()
    {
        return integerCurrency;
    }

    public void setIntegerCurrency(Boolean integerCurrency)
    {
        this.integerCurrency = integerCurrency;
    }

    public Boolean isAddToInventory()
    {
        return addToInventory;
    }

    public void setAddToInventory(Boolean addToInventory)
    {
        this.addToInventory = addToInventory;
    }

    public Map<Class<? extends AbstractRule>, Rule> getHuntingRules()
    {
        return huntingRules;
    }

    public void setHuntingRules(Map<Class<? extends AbstractRule>, Rule> huntingRules)
    {
        this.huntingRules = huntingRules;
    }

    public Collection<AssembledDrop> assembleDrops(Event event)
    {
        Collection<AssembledDrop> drops = new ArrayList<>();
        int amount = nextIntAmount();

        for (int i = 0; i < amount; i++) {
            drops.add(assembleDrop(event));
        }

        return drops;
    }

    protected AssembledDrop assembleDrop(Event event)
    {
        AssembledDrop drop = new AssembledDrop(getLocation(event));

        drop.setName(name);
        drop.setItemDrops(getItemDropOutcomes());
        drop.setEntityDrops(getEntityDropOutcomes());
        drop.setJockeyDrops(getJockeyDropOutcomes());

        if (hasCoin()) {
            drop.setCoin(coin.nextDoubleAmount());

            if (drop.getCoin() > 0.0) {
                drop.setMessage(coinRewardMessage);
            }
            else if (drop.getCoin() < 0.0) {
                drop.setMessage(coinPenaltyMessage);
            }
            else {
                drop.setMessage(noCoinRewardMessage);
            }
        }

        drop.setIntegerCurrency(integerCurrency);
        drop.setAddToInventory(addToInventory);

        return drop;
    }

    protected abstract Location getLocation(Event event);

    private Collection<ItemStack> getItemDropOutcomes()
    {
        Collection<ItemStack> stacks = Collections.emptyList();

        if (itemDrops != null) {
            stacks = new ArrayList<>();

            for (ItemDrop drop : itemDrops) {
                ItemStack itemStack = drop.nextItemStack(fixedDrops);
                if (itemStack != null) {
                    stacks.add(itemStack);
                }
            }
        }

        return stacks;
    }

    private Collection<EntityType> getEntityDropOutcomes()
    {
        Collection<EntityType> types = Collections.emptyList();

        if (entityDrops != null) {
            types = new ArrayList<>();

            for (EntityDrop drop : entityDrops) {
                types.addAll(drop.nextEntityTypes());
            }
        }

        return types;
    }

    private Collection<EntityType> getJockeyDropOutcomes()
    {
        Collection<EntityType> types = Collections.emptyList();

        if (jockeyDrops != null) {
            types = new ArrayList<>();

            for (EntityDrop drop : jockeyDrops) {
                if (drop instanceof JockeyDrop) {
                    JockeyDrop jockeyDrop = (JockeyDrop) drop;
                    types.addAll(jockeyDrop.nextEntityTypes());
                }
            }
        }

        return types;
    }
}
