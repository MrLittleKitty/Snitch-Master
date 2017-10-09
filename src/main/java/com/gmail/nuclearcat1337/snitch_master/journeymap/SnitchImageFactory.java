package com.gmail.nuclearcat1337.snitch_master.journeymap;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.gui.screens.EditSnitchScreen;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.ILocation;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import journeymap.client.api.display.IOverlayListener;
import journeymap.client.api.display.ImageOverlay;
import journeymap.client.api.model.MapImage;
import journeymap.client.api.util.UIState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.BlockPos;
import scala.collection.parallel.ParIterableLike;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class SnitchImageFactory {
	private static final String SNITCH_FORMAT_STRING = "Group: {0}\nName: {1}\nList: {2}";

	/**
	 * Returns an ImageOverlay object used to display the provided Snitch on JourneyMap (both on minimap and on the fullscreen map)
	 */
	public static ImageOverlay createSnitchOverlay(Snitch snitch) {
		SnitchList renderList = SnitchMaster.instance.getManager().getRenderListForSnitch(snitch);

		if (renderList != null) {
			com.gmail.nuclearcat1337.snitch_master.util.Color color = renderList.getListColor();
			MapImage image = new MapImage(createSnitchField((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue()));
			ILocation loc = snitch.getLocation();
			String displayID = loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getWorld();

			BlockPos nw = new BlockPos(snitch.getFieldMinX(), loc.getY(), snitch.getFieldMinZ());
			BlockPos se = new BlockPos(snitch.getFieldMaxX() + 1, loc.getY(), snitch.getFieldMaxZ() + 1);

			ImageOverlay overlay = new ImageOverlay(SnitchMaster.MODID, displayID, nw, se, image);

			overlay.setTitle(SNITCH_FORMAT_STRING.replace("{0}", snitch.getGroupName()).replace("{1}", snitch.getName()).replace("{2}", renderList.getListName()));
			overlay.setOverlayListener(new IOverlayListener() {
				@Override
				public void onActivate(UIState uiState) {

				}

				@Override
				public void onDeactivate(UIState uiState) {

				}

				@Override
				public void onMouseMove(UIState uiState, Point2D.Double aDouble, BlockPos blockPos) {

				}

				@Override
				public void onMouseOut(UIState uiState, Point2D.Double aDouble, BlockPos blockPos) {

				}

				@Override
				public boolean onMouseClick(UIState uiState, Point2D.Double aDouble, BlockPos blockPos, int i, boolean b) {
					GuiScreen current = Minecraft.getMinecraft().currentScreen;
					Minecraft.getMinecraft().displayGuiScreen(new EditSnitchScreen(snitch,SnitchMaster.instance.getManager(),current));
					return true;
				}
			});
			return overlay;
		}
		return null;
	}

	/**
	 * Returns a BufferedImage object that is a square filled with the given color.
	 */
	private static BufferedImage createSnitchField(float red, float green, float blue) {
		int snitchLength = (Snitch.SNITCH_RADIUS * 2) + 1;
		// oversampling to make the image less blurry
		int blockPixels = 8;
		int imgLength = snitchLength * blockPixels;
		BufferedImage bufferedImage = new BufferedImage(imgLength, imgLength, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bufferedImage.createGraphics();

		g.setColor(new java.awt.Color(red, green, blue, .5f));
		g.fillRect(0, 0, imgLength, imgLength);
		g.setStroke(new BasicStroke(blockPixels * 2 / 2));
		g.drawRect(0, 0, imgLength, imgLength);
		int centerBlockOffset = snitchLength / 2 * blockPixels;
		g.fillRect(centerBlockOffset, centerBlockOffset, blockPixels, blockPixels);

		// Done
		g.dispose();

		return bufferedImage;
	}
}
