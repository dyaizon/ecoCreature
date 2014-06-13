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
package se.crafted.chrisb.ecoCreature.rewards.parties;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;

import com.gmail.nossr50.api.PartyAPI;

public class McMMOParty extends AbstractParty
{
    @Override
    public Set<String> getMembers(Player player)
    {
        Set<String> party = Collections.emptySet();

        if (DependencyUtils.hasMcMMO() && PartyAPI.inParty(player)) {
            List<Player> members = PartyAPI.getOnlineMembers(player);
            if (members != null) {
                party = new HashSet<String>();

                for (Player member : members) {
                    party.add(member.getName());
                }
            }
        }
        LoggerUtil.getInstance().debug("Party size: " + party.size());

        return party;
    }

    public static Set<Party> parseConfig(ConfigurationSection config)
    {
        Set<Party> parties = Collections.emptySet();

        if (config != null) {
            McMMOParty party = new McMMOParty();
            party.setShared(config.getBoolean("InParty.Share", false));
            parties = new HashSet<Party>();
            parties.add(party);
        }

        return parties;
    }
}
