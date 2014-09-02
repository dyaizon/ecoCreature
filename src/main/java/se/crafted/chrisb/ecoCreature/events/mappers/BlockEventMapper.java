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
package se.crafted.chrisb.ecoCreature.events.mappers;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.EntityUtils;
import se.crafted.chrisb.ecoCreature.events.DropEvent;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
import se.crafted.chrisb.ecoCreature.drops.Drop;
import se.crafted.chrisb.ecoCreature.drops.categories.DropConfig;

public class BlockEventMapper extends AbstractEventMapper
{
    public BlockEventMapper(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public boolean canMap(Event event)
    {
        return event instanceof BlockBreakEvent;
    }

    @Override
    public Collection<DropEvent> mapEvent(Event event)
    {
        return event instanceof BlockBreakEvent ? createDropEvents((BlockBreakEvent) event) : EMPTY_COLLECTION;
    }

    private Collection<DropEvent> createDropEvents(BlockBreakEvent event)
    {
        final Player player = event.getPlayer();
        final DropConfig dropConfig = getDropConfig(player.getWorld());

        Collection<Drop> drops = Collections2.transform(dropConfig.createDrops(event), new Function<Drop, Drop>() {

            @Override
            public Drop apply(Drop drop)
            {
                drop.setGain(dropConfig.getGainMultiplier(player));
                drop.addParameter(MessageToken.ITEM, EntityUtils.getItemNameInHand(player))
                    .addParameter(MessageToken.CREATURE, drop.getName());
                return drop;
            }
        });

        return Lists.newArrayList(new DropEvent(player, drops));
    }
}