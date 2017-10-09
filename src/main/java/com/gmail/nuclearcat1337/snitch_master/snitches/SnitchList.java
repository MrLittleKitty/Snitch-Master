package com.gmail.nuclearcat1337.snitch_master.snitches;

import com.gmail.nuclearcat1337.snitch_master.api.SnitchListQualifier;
import com.gmail.nuclearcat1337.snitch_master.util.Color;

/**
 * A structure that handles the grouping of Snitches by common traits or specified functions.
 */
public class SnitchList {
	public static final String MAX_NAME_CHARACTERS = "WWWWWWWWWWWWWWWWWWWW";

	private static Color defaultColor = new Color(240, 255, 240); //"HoneyDew"
	private static String defaultName = "Undefined";

	private final SnitchManager manager;
	private int renderPriority;
	private String listName;
	private Color listColor;
	private SnitchListQualifier listQualifier;
	private boolean renderSnitches;

	SnitchList(SnitchManager manager, SnitchListQualifier listQualifier, boolean render) {
		this.manager = manager;
		listName = defaultName;
		listColor = defaultColor;
		this.listQualifier = listQualifier;
		this.renderSnitches = render;
	}

	SnitchList(SnitchManager manager, SnitchListQualifier listQualifier, boolean render, String name) {
		this.manager = manager;
		this.listName = name;
		listColor = defaultColor;
		this.listQualifier = listQualifier;
		this.renderSnitches = render;
	}

	SnitchList(SnitchManager manager, SnitchListQualifier listQualifier, boolean render, Color color) {
		this.manager = manager;
		listName = defaultName;
		listColor = color;
		this.listQualifier = listQualifier;
		this.renderSnitches = render;
	}

	SnitchList(SnitchManager manager, SnitchListQualifier listQualifier, boolean render, String name, Color color) {
		this.manager = manager;
		this.listName = name;
		listColor = color;
		this.listQualifier = listQualifier;
		this.renderSnitches = render;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String name) {
		this.listName = name;
		if (listName.length() > 20) {
			listName = listName.substring(0, 19);
		}
		manager.journeyMapRedisplay(this);
	}

	public int getRenderPriority() {
		return renderPriority;
	}

	void setRenderPriorityUnchecked(int newPriority) {
		this.renderPriority = newPriority;
	}

	public void increaseRenderPriority() {
		manager.changeListRenderPriority(this, true);
	}

	public void decreaseRenderPriority() {
		manager.changeListRenderPriority(this, false);
	}

	public Color getListColor() {
		return listColor;
	}

	public void setListColor(Color newColor) {
		this.listColor = newColor;
		manager.journeyMapRedisplay(this);
	}

	public boolean shouldRenderSnitches() {
		return renderSnitches;
	}

	public void setShouldRenderSnitches(boolean render) {
		this.renderSnitches = render;
		manager.journeyMapRedisplay(this);
	}

	public SnitchListQualifier getQualifier() {
		return listQualifier;
	}

	public boolean updateQualifier(String newQualifier) {
		if (SnitchListQualifier.isSyntaxValid(newQualifier)) {
			this.listQualifier = new SnitchListQualifier(newQualifier);
			manager.requalifyList(this);
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SnitchList that = (SnitchList) o;
		return listName.equals(that.listName);
	}

	@Override
	public int hashCode() {
		return listName.hashCode();
	}

	static final int NUMBER_OF_CSV_PARAMS = 5;
	static final String CSV_SEPARATOR = ",";

	public static String ConvertSnitchListToCSV(SnitchList list) {
		StringBuilder builder = new StringBuilder();
		builder.append(Scrub(list.getListName())).append(CSV_SEPARATOR);
		builder.append(list.getListColor().serialize()).append(CSV_SEPARATOR);
		builder.append(list.getRenderPriority()).append(CSV_SEPARATOR);
		builder.append(list.shouldRenderSnitches()).append(CSV_SEPARATOR);
		builder.append(Scrub(list.getQualifier().toString()));
		return builder.toString();
	}

	static SnitchList GetSnitchListFromCSV(String csv, SnitchManager manager) {
		String[] args = csv.split(CSV_SEPARATOR);
		if (args.length != NUMBER_OF_CSV_PARAMS) {
			new NumberFormatException("The CSV string provided does not have the correct number of arguments for a Snitch List.").printStackTrace();
			return null;
		}

		int index = 0;

		String name = Scrub(args[index++]);
		Color color = new Color(args[index++]);
		int priority = Integer.parseInt(args[index++]);
		boolean shouldRender = Boolean.parseBoolean(args[index++]);
		SnitchListQualifier qualifier = new SnitchListQualifier(Scrub(args[index++]));

		SnitchList list = new SnitchList(manager, qualifier, shouldRender, name, color); //TODO---You need to not have null as the first parameter
		list.renderPriority = priority;

		return list;
	}

	private static String Scrub(String string) {
		return string.replace(CSV_SEPARATOR, "");
	}
}
