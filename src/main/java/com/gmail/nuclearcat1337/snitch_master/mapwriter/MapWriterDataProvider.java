package com.gmail.nuclearcat1337.snitch_master.mapwriter;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.ILocation;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import mapwriter.api.*;
import mapwriter.util.Render;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class MapWriterDataProvider implements IMwDataProvider {
	private final SnitchMaster snitchMaster;

	public MapWriterDataProvider(SnitchMaster snitchMaster) {
		this.snitchMaster = snitchMaster;
	}

	@Override
	public ArrayList<IMwChunkOverlay> getChunksOverlay(int i, double v, double v1, double v2, double v3, double v4, double v5) {
		return null;
	}

	@Override
	public String getStatusString(int dim, int bX, int bY, int bZ) {
		return "";
	}

	@Override
	public void onDraw(IMapView mapView, IMapMode mapMode) {
		for (Snitch snitch : snitchMaster.getSnitches().getItemsForWorld(snitchMaster.getCurrentWorld())) {
			// snitched field northwest/southeast conversion, clamp to stay inside visible area
			Point2D.Double fnw = blockToMap(mapView, mapMode, snitch.getFieldMinX(), snitch.getFieldMinZ());
			Point2D.Double fse = blockToMap(mapView, mapMode, snitch.getFieldMaxX() + 1, snitch.getFieldMaxZ() + 1);

			int color = 0x0099ff; // blue // TODO per group

			// filling
			Render.setColour(0x88 << 24 | color);
			Render.drawRect(fnw.x, fnw.y, fse.x - fnw.x, fse.y - fnw.y);

			// outline
			Render.setColour(0xff << 24 | color);
			Render.drawRectBorder(fnw.x, fnw.y, fse.x - fnw.x, fse.y - fnw.y, 1);

			// snitch
			ILocation sLoc = snitch.getLocation();
			Point2D.Double snw = blockToMap(mapView, mapMode, sLoc.getX(), sLoc.getZ());
			Point2D.Double sse = blockToMap(mapView, mapMode, sLoc.getX() + 1, sLoc.getZ() + 1);
			Render.setColour(0xff << 24 | color);
			Render.drawRect(snw.x, snw.y, sse.x - snw.x, sse.y - snw.y);
		}
	}

	private static Point.Double blockToMap(IMapView mapView, IMapMode mapMode, double x, double z) {
		Point2D.Double p = mapMode.blockXZtoScreenXY(mapView, x, z);
		if (p.x < mapMode.getX())
			p.x = mapMode.getX();
		else if (p.x > mapMode.getX() + mapMode.getW())
			p.x = mapMode.getX() + mapMode.getW();
		if (p.y < mapMode.getY())
			p.y = mapMode.getY();
		else if (p.y > mapMode.getY() + mapMode.getH())
			p.y = mapMode.getY() + mapMode.getH();
		return p;
	}

	@Override
	public ILabelInfo getLabelInfo(int mouseX, int mouseY) {
		return null;
	}

	@Override
	public boolean onMouseInput(IMapView iMapView, IMapMode iMapMode) {
		return false;
	}

	@Override
	public void onMiddleClick(int i, int i1, int i2, IMapView iMapView) {
	}

	@Override
	public void onDimensionChanged(int i, IMapView iMapView) {
	}

	@Override
	public void onMapCenterChanged(double v, double v1, IMapView iMapView) {
	}

	@Override
	public void onZoomChanged(int i, IMapView iMapView) {
	}

	@Override
	public void onOverlayActivated(IMapView iMapView) {
	}

	@Override
	public void onOverlayDeactivated(IMapView iMapView) {
	}
}
