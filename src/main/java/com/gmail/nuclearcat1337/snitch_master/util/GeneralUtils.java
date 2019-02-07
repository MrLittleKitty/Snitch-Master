package com.gmail.nuclearcat1337.snitch_master.util;

import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

/**
 * Created by Mr_Little_Kitty on 7/16/2016.
 */
public class GeneralUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static double distanceSquared(final int x1, final int z1, final int x2, final int z2) {
        return Math.pow(x2 - x1, 2) + Math.pow(z2 - z1, 2);
    }

    public static double distanceSquared(final int x1, final int z1, final int y1,
                                         final int x2, final int z2, final int y2) {
        return Math.pow(x2 - x1, 2) + Math.pow(z2 - z1, 2) + Math.pow(y2 - y1, 2);
    }

    public static double distanceSquaredToPlayer(final int x, final int y, final int z) {
        return GeneralUtils.distanceSquared(x, y, z, (int) mc.player.posX, (int) mc.player.posZ, (int) mc.player.posY);
    }

    public static double distanceSquaredToPlayer(final int x, final int z) {
        return GeneralUtils.distanceSquared(x, z, (int) mc.player.posX, (int) mc.player.posZ);
    }

    public static Location getPlayerLocation(final String world) {
        final int x = (int) Math.floor(mc.player.posX);
        final int z = (int) Math.floor(mc.player.posZ);
        return new Location(x, (int) mc.player.posY, z, world);
    }

    //Provides a consistent way to order locations
    //Return -1 if loc comes first, 1 if other comes first, 0 if they are equal
    public static int compareLocations(final Location loc, final Location other) {
        if (loc == null && other != null) {
            return 1;
        } else if (loc != null && other == null) {
            return -1;
        } else if (loc == null) { // if loc is null then other must also be null
            return 0;
        }

        int compare = loc.getWorld().compareTo(other.getWorld());
        if (compare != 0) {
            return compare;
        }
        if (loc.getX() < other.getX()) {
            return -1;
        }
        if (loc.getX() > other.getX()) {
            return 1;
        }

        if (loc.getZ() < other.getZ()) {
            return -1;
        }
        if (loc.getZ() > other.getZ()) {
            return 1;
        }

        return Integer.compare(loc.getY(), other.getY());
    }
}
