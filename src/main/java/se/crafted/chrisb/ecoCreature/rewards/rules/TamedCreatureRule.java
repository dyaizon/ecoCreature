/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2012, R. Ramos <http://github.com/mung3r/>
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
package se.crafted.chrisb.ecoCreature.rewards.rules;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;

public class TamedCreatureRule extends AbstractEntityRule
{
    private boolean wolverineMode;

    public TamedCreatureRule()
    {
        wolverineMode = true;
    }

    public void setWolverineMode(boolean wolverineMode)
    {
        this.wolverineMode = wolverineMode;
    }

    @Override
    protected boolean isBroken(EntityKilledEvent event)
    {
        boolean ruleBroken = !wolverineMode && event.isTamedCreatureKill();
        LoggerUtil.getInstance().debugTrue("No reward for " + event.getKiller().getName() + " using tamed creatures.", ruleBroken);

        return ruleBroken;
    }

    public static Map<Class<? extends AbstractRule>, Rule> parseConfig(ConfigurationSection config)
    {
        Map<Class<? extends AbstractRule>, Rule> rules = Collections.emptyMap();

        if (config != null) {
            TamedCreatureRule rule = new TamedCreatureRule();
            rule.setWolverineMode(config.getBoolean("Hunting.WolverineMode", true));
            rules = new HashMap<Class<? extends AbstractRule>, Rule>();
            rules.put(TamedCreatureRule.class, rule);
        }

        return rules;
    }
}
