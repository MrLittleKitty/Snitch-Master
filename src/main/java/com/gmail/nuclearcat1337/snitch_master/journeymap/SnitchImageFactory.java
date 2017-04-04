package com.gmail.nuclearcat1337.snitch_master.journeymap;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.ILocation;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import com.gmail.nuclearcat1337.snitch_master.util.*;
import journeymap.client.api.display.ImageOverlay;
import journeymap.client.api.model.MapImage;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Created by Mr_Little_Kitty on 9/20/2016.
 * Handles the creation of the Snitch images to be displayed on JourneyMap (both on minimap and on the fullscreen map)
 */
public class SnitchImageFactory
{
    private static final String SNITCH_FORMAT_STRING = "Group: {0}\nName: {1}\nList: {2}";

    /**
     * Returns an ImageOverlay object used to display the provided Snitch on JourneyMap (both on minimap and on the fullscreen map)
     */
    public static ImageOverlay createSnitchOverlay(Snitch snitch)
    {
        SnitchList renderList = SnitchMaster.instance.getManager().getRenderListForSnitch(snitch);

        if(renderList != null)
        {
			com.gmail.nuclearcat1337.snitch_master.util.Color color = renderList.getListColor();
            MapImage image = new MapImage(createSnitchField((float)color.getRed(),(float)color.getGreen(),(float)color.getBlue()));
            ILocation loc = snitch.getLocation();
            String displayID = loc.getX()+","+loc.getY()+","+loc.getZ()+","+loc.getWorld();

            BlockPos nw = new BlockPos(snitch.getFieldMinX(),loc.getY(),snitch.getFieldMinZ());
            BlockPos se = new BlockPos(snitch.getFieldMaxX()+1,loc.getY(),snitch.getFieldMaxZ()+1);

            ImageOverlay overlay = new ImageOverlay(SnitchMaster.MODID,displayID,nw,se,image);

            overlay.setTitle(SNITCH_FORMAT_STRING.replace("{0}",snitch.getGroupName()).replace("{1}",snitch.getSnitchName()).replace("{2}",renderList.getListName()));

            return overlay;
        }

        return null;
    }

    /**
     * Returns a BufferedImage object that is a square filled with the given color.
     */
    private static BufferedImage createSnitchField(float red, float green, float blue)
    {
        int snitchLength = (Snitch.SNITCH_RADIUS*2)+1;
        // oversampling to make the image less blurry
        int blockPixels = 8;
        int imgLength = snitchLength * blockPixels;
        BufferedImage bufferedImage = new BufferedImage(imgLength, imgLength, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();

        g.setColor(new java.awt.Color(red,green,blue, .5f));
        g.fillRect(0, 0, imgLength, imgLength);
        g.setStroke(new BasicStroke(blockPixels*2 / 2));
        g.drawRect(0, 0, imgLength, imgLength);
        int centerBlockOffset = snitchLength / 2 * blockPixels;
        g.fillRect(centerBlockOffset, centerBlockOffset, blockPixels, blockPixels);

        // Done
        g.dispose();

        return bufferedImage;
    }
}
