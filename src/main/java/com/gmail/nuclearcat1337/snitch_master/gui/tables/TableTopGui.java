package com.gmail.nuclearcat1337.snitch_master.gui.tables;

import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class TableTopGui<T> extends GuiScreen {
	private static final int DONE_BUTTON_WIDTH = GuiConstants.SMALL_BUTTON_WIDTH * 3;

	protected final GuiScreen parentScreen;
	protected GuiButton doneButton;
	protected GuiButton columnsButton;

	private Collection<T> items;

	private List<TableColumn<T>> allColumns;
	private List<TableColumn<T>> renderColumns;
	private List<TableColumn<T>> columnsToBoundsCheck;

	private final String title;
	private final int titleWidth;

	private TableGui<T> tableGui;

	public TableTopGui(GuiScreen parentScreen, Collection<T> items, String title) {
		this.parentScreen = parentScreen;
		this.items = items;
		this.title = title;
		this.titleWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(title);
	}

	protected abstract void initializeButtons(int firstId);

	protected abstract Collection<Pair<TableColumn<T>, Boolean>> initializeColumns();

	public void saveColumns(List<TableColumn<T>> allColumns, List<TableColumn<T>> renderColumns) {

	}

	@Override
	public void initGui() {
		Collection<Pair<TableColumn<T>, Boolean>> initialColumns = initializeColumns();

		assert initialColumns != null && !initialColumns.isEmpty();

		allColumns = new ArrayList<>();
		renderColumns = new ArrayList<>();
		columnsToBoundsCheck = new ArrayList<>();

		for (Pair<TableColumn<T>, Boolean> pair : initialColumns) {
			allColumns.add(pair.getOne());

			if (pair.getTwo()) {
				renderColumns.add(pair.getOne());

				if (pair.getOne().doBoundsCheck()) {
					columnsToBoundsCheck.add(pair.getOne());
				}
			}
		}

		tableGui = new TableGui<T>(this, items, renderColumns);

		buttonList.clear();

		int xPos = (this.width / 2) - DONE_BUTTON_WIDTH - (GuiConstants.STANDARD_SEPARATION_DISTANCE / 2);
		int yPos = this.height - GuiConstants.STANDARD_BUTTON_HEIGHT - GuiConstants.STANDARD_SEPARATION_DISTANCE;

		doneButton = new GuiButton(0, xPos, yPos, DONE_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, "Back");

		int buttonWidth = mc.fontRenderer.getStringWidth("--Columns--");
		xPos -= ((GuiConstants.STANDARD_SEPARATION_DISTANCE * 4) + buttonWidth);

		columnsButton = new GuiButton(1, xPos, yPos, buttonWidth, GuiConstants.STANDARD_BUTTON_HEIGHT, "Columns");

		buttonList.add(doneButton);
		buttonList.add(columnsButton);

		initializeButtons(3);

		super.initGui();
	}

	protected Collection<T> getItems() {
		return items;
	}

	public int getTableSize() {
		return tableGui.getSize();
	}

	public T getTableItem(int tableIndex) {
		return tableGui.getItemForSlotIndex(tableIndex);
	}

	public void swapTableItems(int tableIndex, int nextTableIndex) {
		tableGui.swapItems(tableIndex, nextTableIndex);
	}

	public void setRenderColumns(List<TableColumn<T>> allColumns, List<TableColumn<T>> renderColumns) {
		//We need both columns lists so that the column order can be changed
		this.allColumns = allColumns;
		this.renderColumns = renderColumns;

		columnsToBoundsCheck.clear();
		for (TableColumn<T> col : renderColumns)
			if (col.doBoundsCheck()) {
				columnsToBoundsCheck.add(col);
			}
		tableGui = new TableGui<T>(this, items, this.renderColumns);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (!button.enabled) {
			return;
		}
		switch (button.id) {
			case 0: //Done
				this.mc.displayGuiScreen(parentScreen);
				break;
			case 1:
				this.mc.displayGuiScreen(new TableColumnSelectorTop<T>(this, allColumns, renderColumns, "Select Columns"));
				break;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		//Draw the background, the actual table, and anything from out parent
		this.drawDefaultBackground();
		this.tableGui.drawScreen(mouseX, mouseY, partialTicks);
		super.drawScreen(mouseX, mouseY, partialTicks);

		//Create positioning info for drawing the title
		int yPos = 16 - (mc.fontRenderer.FONT_HEIGHT / 2);
		int xPos = (this.width / 2) - (titleWidth / 2);

		//Draw the title
		mc.fontRenderer.drawString(title, xPos, yPos, 16777215);

		if (mouseY >= tableGui.top && mouseY <= tableGui.bottom) {
			int index = tableGui.getSlotIndexFromScreenCoords(mouseX, mouseY);
			if (index >= 0) {
				for (TableColumn<T> col : columnsToBoundsCheck) {
					Pair<Integer, Integer> bounds = tableGui.getBoundsForColumn(col);
					if (mouseX >= bounds.getOne() && mouseX <= bounds.getTwo()) {
						List<String> text = col.hover(tableGui.getItemForSlotIndex(index), xPos, yPos);
						if (text != null && !text.isEmpty()) {
							drawHoveringText(text, mouseX, mouseY);
						}
						break;
					}
				}
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseEvent) {
		tableGui.mouseClicked(mouseX, mouseY, mouseEvent);

		//Hopefully this determines if they clicked in the header area
		if (mouseY >= tableGui.top && mouseY <= tableGui.top + tableGui.headerPadding && tableGui.getSlotIndexFromScreenCoords(mouseX, mouseY) < 0) {
			for (TableColumn<T> col : renderColumns) {
				Pair<Integer, Integer> bounds = tableGui.getBoundsForColumn(col);
				if (mouseX >= bounds.getOne() && mouseX <= bounds.getTwo()) {
					tableGui.sortByColumn(col);
					break;
				}
			}
		}

		try {
			super.mouseClicked(mouseX, mouseY, mouseEvent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseReleased(int arg1, int arg2, int arg3) {
		//This method is ESSENTIAL to the functioning of the scroll bar
		tableGui.mouseReleased(arg1, arg2, arg3);
		super.mouseReleased(arg1, arg2, arg3);
	}

	@Override
	public void handleMouseInput() {
		//This method is ESSENTIAL to the functioning of the scroll bar
		tableGui.handleMouseInput();
		try {
			super.handleMouseInput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
