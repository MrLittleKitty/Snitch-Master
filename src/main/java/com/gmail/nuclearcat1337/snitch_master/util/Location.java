package com.gmail.nuclearcat1337.snitch_master.util;

import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.ILocation;

/**
 * Created by Mr_Little_Kitty on 7/27/2016.
 */
public class Location implements ILocation {
	private final int x;
	private final int y;
	private final int z;
	private final String world;

	public Location(int x, int y, int z, String world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}

	@Override
	public String getWorld() {
		return world;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getZ() {
		return z;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Location location = (Location) o;

		if (x != location.x) {
			return false;
		}
		if (y != location.y) {
			return false;
		}
		if (z != location.z) {
			return false;
		}
		return world.equals(location.world);

	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		result = 31 * result + z;
		result = 31 * result + world.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return String.format("[%s %d %d %d]", world, x, y, z);
	}
}
