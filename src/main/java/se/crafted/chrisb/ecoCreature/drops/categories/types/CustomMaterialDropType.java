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
package se.crafted.chrisb.ecoCreature.drops.categories.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

public enum CustomMaterialDropType
{
    LEGACY_SPAWNER("Spawner", true),
    INVALID("__Invalid__", false);

    private static final Map<String, CustomMaterialDropType> NAME_MAP = new HashMap<>();

    static {
        for (CustomMaterialDropType type : EnumSet.allOf(CustomMaterialDropType.class)) {
            NAME_MAP.put(type.name, type);
        }
    }

    private final String name;
    private final boolean valid;

    CustomMaterialDropType(String name, boolean isValid)
    {
        this.name = name.toLowerCase();
        this.valid = isValid;
    }

    public boolean isValid()
    {
        return valid;
    }

    public static CustomMaterialDropType fromName(String name)
    {
        CustomMaterialDropType material = INVALID;
        if (name != null && NAME_MAP.containsKey(name.toLowerCase())) {
            material = NAME_MAP.get(name.toLowerCase());
        }
        return material;
    }

    public static CustomMaterialDropType fromMaterial(Material material)
    {
        CustomMaterialDropType type = INVALID;

        if (material == Material.MOB_SPAWNER) {
            type = LEGACY_SPAWNER;
        }

        return type;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
