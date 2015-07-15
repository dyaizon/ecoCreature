/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2015, R. Ramos <http://github.com/mung3r/>
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

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;

public class EntityDropSource extends AbstractDropSource
{
    public EntityDropSource(String section, ConfigurationSection config)
    {
        super(section, config);
    }

    @Override
    protected Location getLocation(Event event)
    {
        if (event instanceof PlayerKilledEvent) {
            return ((PlayerKilledEvent) event).getEntity().getLocation();
        }
        else if (event instanceof EntityKilledEvent) {
            return ((EntityKilledEvent) event).getEntity().getLocation();
        }
        else {
            throw new IllegalArgumentException("Unrecognized event");
        }
    }

    @Override
    protected int getLootLevel(Event event)
    {
        int lootLevel = 0;

        ItemStack weapon = null;
        if (event instanceof PlayerKilledEvent) {
            weapon = ((PlayerKilledEvent) event).getKiller().getItemInHand();
        }
        else if (event instanceof EntityKilledEvent) {
            weapon = ((EntityKilledEvent) event).getKiller().getItemInHand();
        }

        if (weapon != null) {
            lootLevel = weapon.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
        }

        return lootLevel;
    }
}
