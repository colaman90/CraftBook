// $Id$
/*
 * Copyright (C) 2010, 2011 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.craftbook.util;

import com.sk89q.craftbook.bukkit.CraftBookPlugin;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.registry.LegacyMapper;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public class BlockSyntax {
    private static ParserContext BLOCK_CONTEXT = new ParserContext();

    static {
        BLOCK_CONTEXT.setPreferringWildcard(true);
        BLOCK_CONTEXT.setRestricted(false);
    }

    public static BlockStateHolder getBlock(String line) {
        return getBlock(line, false);
    }

    public static BlockStateHolder getBlock(String line, boolean wild) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        BLOCK_CONTEXT.setPreferringWildcard(wild);

        BlockStateHolder blockState = null;
        try {
            blockState = WorldEdit.getInstance().getBlockFactory().parseFromInput(line, BLOCK_CONTEXT);
        } catch (InputParseException e) {
        }

        if (blockState == null) {
            String[] dataSplit = RegexUtil.COLON_PATTERN.split(line.replace("\\:", ":"), 2);
            Material material = Material.getMaterial(dataSplit[0], true);
            if (material != null) {
                int data = 0;
                if (dataSplit.length > 1) {
                    data = Integer.parseInt(dataSplit[1]);
                    if (data < 0 || data > 15) {
                        data = 0;
                    }
                }
                blockState = LegacyMapper.getInstance().getBlockFromLegacy(BukkitAdapter.asBlockType(material).getLegacyId(), data);
            }
            if (material == null) {
                CraftBookPlugin.logger().warning("Invalid block format: " + line);
            }
        }
        return blockState;
    }

    public static BlockData getBukkitBlock(String line) {
        return BukkitAdapter.adapt(getBlock(line));
    }
}
