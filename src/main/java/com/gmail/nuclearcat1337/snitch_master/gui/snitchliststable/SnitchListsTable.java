package com.gmail.nuclearcat1337.snitch_master.gui.snitchliststable;

import com.gmail.nuclearcat1337.snitch_master.Settings;
import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.api.SnitchListQualifier;
import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.gui.screens.EditColorGui;
import com.gmail.nuclearcat1337.snitch_master.gui.screens.EditStringGui;
import com.gmail.nuclearcat1337.snitch_master.gui.screens.NewSnitchListGui;
import com.gmail.nuclearcat1337.snitch_master.gui.snitchtable.SnitchesTable;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableButtonColumn;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableColumn;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableTopGui;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchManager;
import com.gmail.nuclearcat1337.snitch_master.util.Acceptor;
import com.gmail.nuclearcat1337.snitch_master.util.Color;
import com.gmail.nuclearcat1337.snitch_master.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SnitchListsTable extends TableTopGui<SnitchList> {
	private static final String SNITCH_LIST_COLUMNS_KEY = "snitch-list-table-columns";

	private static final int EDIT_COLOR_BUTTON_WIDTH = 60;
	private static final int EDIT_QUALIFIER_BUTTON_WIDTH = 60;
	private static final int VIEW_SNITCHES_BUTTON_WIDTH = 60;

	private static final int NEW_BUTTON_WIDTH = GuiConstants.SMALL_BUTTON_WIDTH;
	private static final int RENDER_ON_BUTTON_WIDTH = GuiConstants.SMALL_BUTTON_WIDTH;
	private static final int RENDER_OFF_BUTTON_WIDTH = GuiConstants.SMALL_BUTTON_WIDTH;

	private final SnitchMaster snitchMaster;
	private final SnitchManager manager;
	private final boolean fullList;

	public SnitchListsTable(GuiScreen parentScreen, Collection<SnitchList> items, String title, boolean fullList, SnitchMaster snitchMaster) {
		super(parentScreen, items, title);
		this.fullList = fullList;
		this.snitchMaster = snitchMaster;
		this.manager = snitchMaster.getManager();
	}

	int firstId;

	@Override
	protected void initializeButtons(int firstId) {
		this.firstId = firstId;
		int xPos = (this.width / 2) + (GuiConstants.STANDARD_SEPARATION_DISTANCE / 2);
		int yPos = this.height - GuiConstants.STANDARD_BUTTON_HEIGHT - GuiConstants.STANDARD_SEPARATION_DISTANCE;

		if (fullList) {
			this.buttonList.add(new GuiButton(firstId, xPos, yPos, NEW_BUTTON_WIDTH, 18, "New"));
		}

		xPos += GuiConstants.STANDARD_SEPARATION_DISTANCE + NEW_BUTTON_WIDTH;
		this.buttonList.add(new GuiButton(firstId + 1, xPos, yPos, RENDER_ON_BUTTON_WIDTH, 18, "All On"));

		xPos += GuiConstants.STANDARD_SEPARATION_DISTANCE + RENDER_ON_BUTTON_WIDTH;
		this.buttonList.add(new GuiButton(firstId + 2, xPos, yPos, RENDER_OFF_BUTTON_WIDTH, 18, "All Off"));
	}

	@Override
	public void saveColumns(List<TableColumn<SnitchList>> allColumns, List<TableColumn<SnitchList>> renderColumns) {
		Settings settings = snitchMaster.getSettings();

		String saveString = "";
		for (TableColumn<SnitchList> col : allColumns) {
			String entry = col.getColumnName() + "," + renderColumns.contains(col);
			saveString += entry + ";";
		}
		settings.setValue(SNITCH_LIST_COLUMNS_KEY, saveString.substring(0, saveString.length() - 1));
		settings.saveSettings();
	}

	public void actionPerformed(GuiButton button) {
		if (!button.enabled) {
			return;
		}

		if (button.id == firstId) { //New snitch list
			Minecraft.getMinecraft().displayGuiScreen(new NewSnitchListGui(this, snitchMaster));
		} else if (button.id == firstId + 1) { //Set all render on
			setAllRender(true);
		} else if (button.id == firstId + 2) { //Set all render off
			setAllRender(false);
		}
		super.actionPerformed(button);
	}

	private void setAllRender(boolean on) {
		for (SnitchList list : getItems()) {
			list.setShouldRenderSnitches(on);
		}
		manager.saveSnitchLists();
	}

	@Override
	protected Collection<Pair<TableColumn<SnitchList>, Boolean>> initializeColumns() {
		Settings settings = snitchMaster.getSettings();

		ArrayList<Pair<TableColumn<SnitchList>, Boolean>> columns = new ArrayList<>();
		columns.add(packageValues(new SnitchListRemoveColumn(snitchMaster), false));

		columns.add(packageValues(new SnitchListControlsColumn(this, manager), true));
		columns.add(packageValues(new SnitchListNameColumn(manager), true));

		columns.add(packageValues(new TableButtonColumn<>("Color", "Edit", EDIT_COLOR_BUTTON_WIDTH, colorClick), true));
		columns.add(packageValues(new TableButtonColumn<>("Qualifier", "Edit", EDIT_QUALIFIER_BUTTON_WIDTH, qualifierClick), true));
		columns.add(packageValues(new TableButtonColumn<>("Snitches", "View", VIEW_SNITCHES_BUTTON_WIDTH, viewSnitchesClick), true));

		if (!settings.hasValue(SNITCH_LIST_COLUMNS_KEY)) {
			String saveString = "";
			for (Pair<TableColumn<SnitchList>, Boolean> pair : columns) {
				String entry = pair.getOne().getColumnName() + "," + pair.getTwo().toString();
				saveString += entry + ";";
			}
			settings.setValue(SNITCH_LIST_COLUMNS_KEY, saveString.substring(0, saveString.length() - 1));
			settings.saveSettings();
		} else {
			String[] entries = settings.getValue(SNITCH_LIST_COLUMNS_KEY).toString().split(";");
			for (int entryIndex = 0; entryIndex < entries.length; entryIndex++) {
				String[] vals = entries[entryIndex].split(",");
				for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
					Pair<TableColumn<SnitchList>, Boolean> pair = columns.get(colIndex);
					//Find the column with this name
					if (pair.getOne().getColumnName().equalsIgnoreCase(vals[0])) {
						pair.setValues(pair.getOne(), Boolean.parseBoolean(vals[1]));

						//Swap the columns in the list so they are in their proper place
						Pair<TableColumn<SnitchList>, Boolean> temp = columns.get(entryIndex);
						columns.set(entryIndex, pair);
						columns.set(colIndex, temp);

						break;
					}
				}
			}
		}
		return columns;
	}

	private final TableButtonColumn.OnButtonClick<SnitchList> qualifierClick = new TableButtonColumn.OnButtonClick<SnitchList>() {
		@Override
		public void onClick(SnitchList list, GuiButton button, GuiScreen parentScreen) {
			mc.displayGuiScreen(new EditStringGui(parentScreen, list.getQualifier().toString(), "Edit Qualifier", new EditQualifierAcceptor(list), 100));
		}
	};

	private final TableButtonColumn.OnButtonClick<SnitchList> colorClick = new TableButtonColumn.OnButtonClick<SnitchList>() {
		@Override
		public void onClick(SnitchList list, GuiButton button, GuiScreen parentScreen) {
			mc.displayGuiScreen(new EditColorGui(parentScreen, list.getListColor(), "Edit Color", new EditColorAcceptor(list)));
		}
	};

	private final TableButtonColumn.OnButtonClick<SnitchList> viewSnitchesClick = new TableButtonColumn.OnButtonClick<SnitchList>() {
		@Override
		public void onClick(SnitchList list, GuiButton button, GuiScreen parentScreen) {
			ArrayList<Snitch> snitches = manager.getSnitchesInList(list);
			if (snitches.isEmpty()) {
				button.displayString = "None";
			} else {
				mc.displayGuiScreen(new SnitchesTable(parentScreen, snitches, "Snitches for Snitch List: " + list.getListName(), snitchMaster));
			}
		}
	};

	private class EditColorAcceptor implements Acceptor<Color> {
		private final SnitchList list;

		private EditColorAcceptor(SnitchList list) {
			this.list = list;
		}

		@Override
		public boolean accept(Color item) {
			if (!Color.AreEqual(item, list.getListColor())) {
				list.setListColor(item);
				manager.saveSnitchLists();
				return true;
			}
			return false;
		}
	}

	private class EditQualifierAcceptor implements Acceptor<String> {
		private final SnitchList list;

		private EditQualifierAcceptor(SnitchList list) {
			this.list = list;
		}

		@Override
		public boolean accept(String item) {
			//Make sure what they provide is valid syntax and it isn't what the qualifier already is
			boolean valid = SnitchListQualifier.isSyntaxValid(item) && !list.getQualifier().equalsString(item);
			if (valid) {
				//Change the qualifier to the new one
				list.updateQualifier(item);
				manager.saveSnitchLists();
				return true;
			}
			return false;
		}
	}

	private static Pair<TableColumn<SnitchList>, Boolean> packageValues(TableColumn<SnitchList> col, Boolean render) {
		return new Pair<>(col, render);
	}
}
