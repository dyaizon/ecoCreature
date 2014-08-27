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
package se.crafted.chrisb.ecoCreature.drops.categories;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang.math.NumberRange;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.drops.DropSourceFactory;
import se.crafted.chrisb.ecoCreature.messages.CoinMessageDecorator;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.MessageHandler;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
import se.crafted.chrisb.ecoCreature.messages.NoCoinMessageDecorator;
import se.crafted.chrisb.ecoCreature.drops.rules.AbstractRule;
import se.crafted.chrisb.ecoCreature.drops.rules.BattleArenaRule;
import se.crafted.chrisb.ecoCreature.drops.rules.CreativeModeRule;
import se.crafted.chrisb.ecoCreature.drops.rules.HeroesRule;
import se.crafted.chrisb.ecoCreature.drops.rules.MobArenaRule;
import se.crafted.chrisb.ecoCreature.drops.rules.MurderedPetRule;
import se.crafted.chrisb.ecoCreature.drops.rules.ProjectileRule;
import se.crafted.chrisb.ecoCreature.drops.rules.Rule;
import se.crafted.chrisb.ecoCreature.drops.rules.SimpleClansRule;
import se.crafted.chrisb.ecoCreature.drops.rules.SpawnerDistanceRule;
import se.crafted.chrisb.ecoCreature.drops.rules.SpawnerMobRule;
import se.crafted.chrisb.ecoCreature.drops.rules.TamedCreatureRule;
import se.crafted.chrisb.ecoCreature.drops.rules.TownyRule;
import se.crafted.chrisb.ecoCreature.drops.rules.UnderSeaLevelRule;
import se.crafted.chrisb.ecoCreature.drops.sources.AbstractDropSource;

public abstract class AbstractDropCategory<T>
{
    private Map<T, Collection<AbstractDropSource>> sources = Collections.emptyMap();

    public AbstractDropCategory(Map<T, Collection<AbstractDropSource>> sources)
    {
        if (sources != null) {
            this.sources = sources;
        }
    }

    public Collection<AbstractDropSource> getDropSources(final Event event)
    {
        Collection<AbstractDropSource> sources = Collections.emptySet();

        if (isValidEvent(event)) {
            sources = Collections2.filter(getDropSources(extractType(event)), new Predicate<AbstractDropSource>() {

                @Override
                public boolean apply(AbstractDropSource source)
                {
                    return source.hasPermission(extractPlayer(event)) && isNotRuleBroken(event, source);
                }

            });
        }

        return sources;
    }

    protected abstract boolean isValidEvent(Event event);

    protected abstract T extractType(Event event);

    protected abstract Player extractPlayer(Event event);

    private boolean hasDropSource(T type)
    {
        return type != null && sources.containsKey(type) && !sources.get(type).isEmpty();
    }

    private Collection<AbstractDropSource> getDropSources(T type)
    {
        Collection<AbstractDropSource> source = Collections.emptySet();

        if (hasDropSource(type)) {
            source = sources.get(type);
        }

        LoggerUtil.getInstance().debugTrue("No reward defined for type: " + type, source.isEmpty());
        return source;
    }

    private boolean isNotRuleBroken(Event event, AbstractDropSource source)
    {
        for (Rule rule : source.getHuntingRules().values()) {
            if (rule.isBroken(event)) {
                rule.handleDrops(event);

                Map<MessageToken, String> parameters = Collections.emptyMap();
                MessageHandler message = new MessageHandler(rule.getMessage(), parameters);
                message.send(rule.getKiller(event));

                LoggerUtil.getInstance().debug("Rule " + rule.getClass().getSimpleName() + " broken");
                return false;
            }
        }

        return true;
    }

    protected static Collection<AbstractDropSource> getSets(String rewardSection, ConfigurationSection config)
    {
        Collection<AbstractDropSource> sources = new HashSet<AbstractDropSource>();
        ConfigurationSection rewardConfig = config.getConfigurationSection(rewardSection);
        ConfigurationSection rewardSets = config.getConfigurationSection("RewardSets");
        Collection<String> sets = rewardConfig.getStringList("Sets");

        if (!sets.isEmpty() && rewardSets != null) {
            for (String setName : sets) {
                String name = setName.split(":")[0];
                if (rewardSets.getConfigurationSection(name) != null) {
                    AbstractDropSource setSource = DropSourceFactory.createSetSource(name, rewardSets);
                    setSource.setHuntingRules(loadHuntingRules(rewardSets.getConfigurationSection(name)));
                    setSource.setRange(parseRange(setName, new NumberRange(1, 1)));
                    setSource.setPercentage(parsePercentage(setName, 100));
                    sources.add(setSource);
                }
            }
        }

        return sources;
    }

    protected static NumberRange parseRange(String dropString, NumberRange defaultRange)
    {
        NumberRange range = defaultRange;

        String[] dropParts = dropString.split(":");

        if (dropParts.length > 1) {
            String[] rangeParts = dropParts[1].split("-");

            if (rangeParts.length == 2) {
                range = new NumberRange(Integer.parseInt(rangeParts[0]), Integer.parseInt(rangeParts[1]));
            }
            else {
                range = new NumberRange(0, Integer.parseInt(dropParts[1]));
            }
        }

        return range;
    }

    protected static double parsePercentage(String dropString, double defaultPercentage)
    {
        String[] dropParts = dropString.split(":");

        if (dropParts.length > 2) {
            return Double.parseDouble(dropParts[2]);
        }

        return defaultPercentage;
    }

    protected static AbstractDropSource configureDropSource(AbstractDropSource source, ConfigurationSection config)
    {
        if (source != null && config != null) {
            source.setIntegerCurrency(config.getBoolean("System.Economy.IntegerCurrency", false));
            source.setFixedDrops(config.getBoolean("System.Hunting.FixedDrops", false));

            source.setCoinRewardMessage(configureMessage(source.getCoinRewardMessage(), config));
            source.setCoinPenaltyMessage(configureMessage(source.getCoinPenaltyMessage(), config));
            source.setNoCoinRewardMessage(configureMessage(source.getNoCoinRewardMessage(), config));
        }

        return source;
    }

    private static Message configureMessage(Message message, ConfigurationSection config)
    {
        if (message != null && config != null) {
            message.setMessageOutputEnabled(config.getBoolean("System.Messages.Output", true));
            if (message instanceof CoinMessageDecorator) {
                ((CoinMessageDecorator) message).setCoinLoggingEnabled(config.getBoolean("System.Messages.LogCoinRewards", true));
            }
            if (message instanceof NoCoinMessageDecorator) {
                ((NoCoinMessageDecorator) message).setNoRewardMessageEnabled(config.getBoolean("System.Messages.NoReward", false));
            }
        }

        return message;
    }

    protected static Map<Class<? extends AbstractRule>, Rule> loadHuntingRules(ConfigurationSection config)
    {
        Map<Class<? extends AbstractRule>, Rule> rules = new HashMap<Class<? extends AbstractRule>, Rule>();

        rules.putAll(CreativeModeRule.parseConfig(config));
        rules.putAll(MobArenaRule.parseConfig(config));
        rules.putAll(BattleArenaRule.parseConfig(config));
        rules.putAll(MurderedPetRule.parseConfig(config));
        rules.putAll(ProjectileRule.parseConfig(config));
        rules.putAll(SpawnerDistanceRule.parseConfig(config));
        rules.putAll(SpawnerMobRule.parseConfig(config));
        rules.putAll(TamedCreatureRule.parseConfig(config));
        rules.putAll(UnderSeaLevelRule.parseConfig(config));
        rules.putAll(HeroesRule.parseConfig(config));
        rules.putAll(SimpleClansRule.parseConfig(config));

        return rules;
    }

    protected static Map<Class<? extends AbstractRule>, Rule> loadGainRules(ConfigurationSection config)
    {
        Map<Class<? extends AbstractRule>, Rule> rules = new HashMap<Class<? extends AbstractRule>, Rule>();

        rules.putAll(TownyRule.parseConfig(config));

        return rules;
    }
}
