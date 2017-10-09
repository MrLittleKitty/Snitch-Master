package com.gmail.nuclearcat1337.snitch_master.gui.tables;

import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.List;

public class TableColumnSelectorTop<T> extends GuiScreen {
	private static final int DONE_BUTTON_WIDTH = GuiConstants.SMALL_BUTTON_WIDTH * 3;
	private static final int SAVE_BUTTON_WIDTH = GuiConstants.SMALL_BUTTON_WIDTH * 3;

	private TableTopGui<T> tableTop;

	private TableColumnSelector<T> selectorGui;

	private List<TableColumn<T>> allColumns;
	private List<TableColumn<T>> renderColumns;

	private final String title;
	private final int titleWidth;

	public TableColumnSelectorTop(TableTopGui<T> tableTop, List<TableColumn<T>> allColumns, List<TableColumn<T>> renderColumns, String title) {
		this.tableTop = tableTop;

		this.allColumns = allColumns;
		this.renderColumns = renderColumns;

		this.title = title;
		this.titleWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(title);
	}

	@Override
	public void initGui() {
		selectorGui = new TableColumnSelector<>(this, allColumns, renderColumns);

		buttonList.clear();

		int xPos = (this.width / 2) - DONE_BUTTON_WIDTH - (GuiConstants.STANDARD_SEPARATION_DISTANCE / 2);
		int yPos = this.height - GuiConstants.STANDARD_BUTTON_HEIGHT - GuiConstants.STANDARD_SEPARATION_DISTANCE;

		buttonList.add(new GuiButton(0, xPos, yPos, DONE_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, "Back"));

		xPos = (this.width / 2) + (GuiConstants.STANDARD_SEPARATION_DISTANCE / 2);

		buttonList.add(new GuiButton(1, xPos, yPos, SAVE_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, "Save"));

		super.initGui();
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (!button.enabled) {
			return;
		}
		switch (button.id) {
			case 0: //Done
				this.mc.displayGuiScreen(tableTop);
				break;
			case 1: //Save
				allColumns = selectorGui.getAllColumns();
				renderColumns = selectorGui.getRenderColumns();
				this.mc.displayGuiScreen(tableTop);
				tableTop.setRenderColumns(allColumns, renderColumns);
				tableTop.saveColumns(allColumns, renderColumns);
				break;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		//Draw the background, the actual table, and anything from out parent
		this.drawDefaultBackground();
		this.selectorGui.drawScreen(mouseX, mouseY, partialTicks);
		super.drawScreen(mouseX, mouseY, partialTicks);

		//Create positioning info for drawing the title
		int yPos = 16 - (mc.fontRenderer.FONT_HEIGHT / 2);
		int xPos = (this.width / 2) - (titleWidth / 2);

		//Draw the title
		mc.fontRenderer.drawString(title, xPos, yPos, 16777215);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseEvent) {
		selectorGui.mouseClicked(mouseX, mouseY, mouseEvent);
		try {
			super.mouseClicked(mouseX, mouseY, mouseEvent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseReleased(int arg1, int arg2, int arg3) {
		//This method is ESSENTIAL to the functioning of the scroll bar
		selectorGui.mouseReleased(arg1, arg2, arg3);
		super.mouseReleased(arg1, arg2, arg3);
	}

	@Override
	public void handleMouseInput() {
		//This method is ESSENTIAL to the functioning of the scroll bar
		selectorGui.handleMouseInput();
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
