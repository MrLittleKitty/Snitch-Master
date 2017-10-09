package com.gmail.nuclearcat1337.snitch_master.snitches;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.ILocation;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.LocatableObject;
import com.gmail.nuclearcat1337.snitch_master.util.Location;

import java.util.*;

public class Snitch extends LocatableObject<Snitch> {
	public static final String MAX_NAME_CHARACTERS = "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW";

	public static final String MAX_CT_GROUP_NAME_CHARACTERS = "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW";

	public static final int SNITCH_RADIUS = 11;

	public static final String DEFAULT_NAME = "[Undefined]";

	/**
	 * The cull time amount that Snitches are reset to when a player walks through them.
	 */
	public static final Double MAX_CULL_TIME = 672D;

	private final ILocation location;

	HashSet<String> tags;

	/**
	 * The cull time of this Snitch. This field can be NaN (Not a Number).
	 */
	private double cullTime;

	/**
	 * The Citadel group name of the group this Snitch is reinforced under.
	 */
	private String ctGroup;

	private String name;
	private String previousName;

	/**
	 * 'entry' or 'logging'
	 */
	private String type;

	final List<SnitchList> attachedSnitchLists;

	private List<String> description;

	/**
	 * Creates a new Snitch and populates "Name" and "Citadel Group Name" with the default name.
	 *
	 * @param location The location of this Snitch block.
	 */
	public Snitch(ILocation location) {
		this.location = location;

		this.tags = new HashSet<>();

		this.cullTime = SnitchMaster.CULL_TIME_ENABLED ? MAX_CULL_TIME : Double.NaN;
		this.ctGroup = DEFAULT_NAME;
		this.name = DEFAULT_NAME;
		this.previousName = DEFAULT_NAME;
		this.type = "entry";
		attachedSnitchLists = new ArrayList<>();
		description = null;
	}

	public Snitch(ILocation location, String initialTag) {
		this(location);
		if (initialTag != null) {
			tags.add(initialTag);
		}
	}

	/**
	 * Creates a new Snitch with all the values specified.
	 *
	 * @param location   The location of this Snitch block.
	 * @param tag        The initial tag to attach to this Snitch object.
	 * @param culltime   The cull time remaining for this snitch. (Can be NaN)
	 * @param ctGroup    The Citadel group name of the group this Snitch is reinforced under.
	 * @param name The name of this Snitch.
	 */
	public Snitch(ILocation location, String tag, double culltime, String ctGroup, String name, String previousName, String snitchType) {
		this(location, tag);
		this.cullTime = culltime;
		this.ctGroup = ctGroup == null ? DEFAULT_NAME : ctGroup;
		this.name = name == null || name.isEmpty() ? DEFAULT_NAME : name;
		this.previousName = previousName == null || previousName.isEmpty() ? DEFAULT_NAME : previousName;
		if (snitchType != null) {
			this.type = snitchType.toLowerCase();
		} else {
			this.type = "entry";
		}
	}

	void setCullTime(double cullTime) {
		this.cullTime = cullTime;
	}

	public double getCullTime() {
		return cullTime;
	}

	void setDescription(List<String> description) {
		this.description = description;
	}

	public List<String> getDescription() {
		return description;
	}

	public boolean isPointInThisSnitch(int x, int y, int z) {
		return x >= getFieldMinX() && x <= getFieldMaxX() && z >= getFieldMinZ() && z <= getFieldMaxZ() && y >= getFieldMinY() && y <= getFieldMaxY();
	}

	@Override
	public ILocation getLocation() {
		return location;
	}

	public int getFieldMinX() {
		return location.getX() - SNITCH_RADIUS;
	}

	public int getFieldMinY() {
		return location.getY() - SNITCH_RADIUS;
	}

	public int getFieldMinZ() {
		return location.getZ() - SNITCH_RADIUS;
	}

	public int getFieldMaxX() {
		return location.getX() + SNITCH_RADIUS;
	}

	public int getFieldMaxY() {
		return location.getY() + SNITCH_RADIUS;
	}

	public int getFieldMaxZ() {
		return location.getZ() + SNITCH_RADIUS;
	}

	public String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

	public String getPreviousName() {
		return previousName;
	}

	void setPreviousName(String name) {
		this.previousName = name;
	}

	public String getType() {
		return type;
	}

	public String getGroupName() {
		return ctGroup;
	}

	public Set<String> getTags() {
		return tags;
	}

	public boolean isTagged(String tag) {
		return tags.contains(tag);
	}

	void setGroupName(String groupName) {
		this.ctGroup = groupName;
	}

	void setType(String type) {
		this.type = type;
	}

	@Override
	public String getWorld() {
		return location.getWorld();
	}

	/**
	 * Returns an arbitrary number meant to sort ILocation objects according to their location.
	 */
	@Override
	public int compareTo(ILocation other) {
		int compare = location.getWorld().compareTo(other.getWorld());
		if (compare != 0) {
			return compare;
		}
		if (location.getX() < other.getX()) {
			return -1;
		}
		if (location.getX() > other.getX()) {
			return 1;
		}

		if (location.getZ() < other.getZ()) {
			return -1;
		}
		if (location.getZ() > other.getZ()) {
			return 1;
		}

		if (location.getY() < other.getY()) {
			return -1;
		}
		if (location.getY() > other.getY()) {
			return 1;
		}
		return 0;
	}

	/**
	 * Returns an arbitrary number which is the comparison of the two Snitch's locations.
	 */
	@Override
	public int compareTo(Snitch other) {
		return compareTo(other.getLocation());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Snitch snitch = (Snitch) o;
		return location.equals(snitch.location);
	}

	@Override
	public int hashCode() {
		return location.hashCode();
	}

	/**
	 * The number of parameters in a comma separated value string representing a Snitch object
	 */
	private static final int NUMBER_OF_CSV_PARAMS = 9;
	private static final String CSV_SEPARATOR = ",";
	private static final String DESCRIPTION_SEPARATOR = ";";
	private static final String TAG_SEPARATOR = "#";

	/**
	 * Returns a string that represents the given Snitch object.
	 * The returned string is in comma separated value form.
	 */
	static String ConvertSnitchToCSV(Snitch snitch) {
		//x, y, z, world, oring, groupName, snitchName, cullTime, type
		StringBuilder builder = new StringBuilder();
		builder.append(snitch.location.getX()).append(CSV_SEPARATOR);
		builder.append(snitch.location.getY()).append(CSV_SEPARATOR);
		builder.append(snitch.location.getZ()).append(CSV_SEPARATOR);
		builder.append(Scrub(snitch.location.getWorld())).append(CSV_SEPARATOR);
		builder.append(concatenate(snitch.tags, TAG_SEPARATOR)).append(CSV_SEPARATOR);
		builder.append(Scrub(snitch.getGroupName())).append(CSV_SEPARATOR);
		builder.append(Scrub(snitch.getName())).append(CSV_SEPARATOR);
		builder.append(snitch.getCullTime()).append(CSV_SEPARATOR);
		builder.append(snitch.getType()).append(CSV_SEPARATOR);

		List<String> description = snitch.getDescription();
		if (description != null) {
			for (String line : description) {
				builder.append(Scrub(line)).append(DESCRIPTION_SEPARATOR);
			}
		}
		builder.append(CSV_SEPARATOR);
		return builder.toString();
	}

	/**
	 * Returns a Snitch object created from the given comma separated value string.
	 * The given Snitch has NO attached SnitchLists.
	 * Throws a NumberFormatException if the given CSV string has the wrong number of parameters.
	 */
	static Snitch GetSnitchFromCSV(String csv) {

		String[] args = csv.split(CSV_SEPARATOR);

		//We allow one less than the correct for when we didn't have a description
		if (args.length == NUMBER_OF_CSV_PARAMS || args.length == NUMBER_OF_CSV_PARAMS - 1) {
			try {
				int index = 0;

				int x = Integer.parseInt(args[index++]);
				int y = Integer.parseInt(args[index++]);
				int z = Integer.parseInt(args[index++]);
				String world = Scrub(args[index++]);

				String originString = args[index++];
				String[] origins = originString.split(TAG_SEPARATOR);

				String groupName = Scrub(args[index++]);
				String snitchName = Scrub(args[index++]);
				double cullTime = Double.parseDouble(args[index++]);
				String type = Scrub(args[index++]).toLowerCase();

				if (snitchName.isEmpty()) {
					snitchName = DEFAULT_NAME;
				}

				Snitch snitch = new Snitch(
					new Location(x, y, z, world),
					null,
					cullTime,
					groupName,
					snitchName,
					null,
					type);
				for (String str : origins) {
					snitch.tags.add(str);
				}

				//If there is an argument for the description
				if (args.length > index) {
					String descriptionCompressed = args[index++];
					if (!descriptionCompressed.isEmpty()) {
						String[] lines = descriptionCompressed.split(DESCRIPTION_SEPARATOR);
						ArrayList<String> description = new ArrayList<>(lines.length);
						for (int i = 0; i < lines.length; i++) {
							description.set(i, Scrub(lines[i]));
						}
						snitch.setDescription(description);
					}
				}
				return snitch;
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	static String concatenate(Collection<String> list, String seperator) {
		StringBuilder builder = new StringBuilder();
		for (String str : list) {
			builder.append(str).append(seperator);
		}
		if (list.size() > 0) {
			builder.setLength(builder.length() - 1);
		}
		return builder.toString();
	}

	static String Scrub(String string) {
		return string.replace(CSV_SEPARATOR, "").replace(DESCRIPTION_SEPARATOR, "").replace(TAG_SEPARATOR, "");
	}
}
