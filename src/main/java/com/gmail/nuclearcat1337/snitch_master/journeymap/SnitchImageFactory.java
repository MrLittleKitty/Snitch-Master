package com.gmail.nuclearcat1337.snitch_master.journeymap;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.ILocation;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import journeymap.client.api.display.ImageOverlay;
import journeymap.client.api.model.MapImage;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Mr_Little_Kitty on 9/20/2016.
 * Handles the creation of the Snitch images to be displayed on JourneyMap (both on minimap and on the fullscreen map)
 */
public class SnitchImageFactory
{
    private static final String SNITCH_FORMAT_STRING = "Group: {0}, Name: {1}, List: {2}";

    /**
     * Returns an ImageOverlay object used to display the provided Snitch on JourneyMap (both on minimap and on the fullscreen map)
     */
    public static ImageOverlay createSnitchOverlay(Snitch snitch)
    {
        String listName = null;
        com.gmail.nuclearcat1337.snitch_master.util.Color renderColor = null;
        for(SnitchList list : snitch.getAttachedSnitchLists())
        {
            if(!list.shouldRenderSnitches())
                continue;

            renderColor = list.getListColor();
            listName = list.getListName();
            break;
        }

        if(renderColor != null)
        {
            MapImage image = new MapImage(createSnitchField((float)renderColor.getRed(),(float)renderColor.getGreen(),(float)renderColor.getBlue()));
            ILocation loc = snitch.getLocation();
            String displayID = loc.getX()+","+loc.getY()+","+loc.getZ()+","+loc.getWorld();

            BlockPos nw = new BlockPos(snitch.getFieldMinX(),loc.getY(),snitch.getFieldMinZ());
            BlockPos se = new BlockPos(snitch.getFieldMaxX()+1,loc.getY(),snitch.getFieldMaxZ()+1);

            ImageOverlay overlay = new ImageOverlay(SnitchMaster.MODID,displayID,nw,se,image);

            overlay.getImage().setOpacity(0.5F);
            overlay.setTitle(SNITCH_FORMAT_STRING.replace("{0}",snitch.getGroupName()).replace("{1}",snitch.getSnitchName()).replace("{2}",listName));

            return overlay;
        }

        return null;
    }

    /**
     * Returns a BufferedImage object that is a square filled with the given color.
     */
    static BufferedImage createSnitchField(float red, float green, float blue)
    {
        int length = (Snitch.SNITCH_RADIUS*2)+1;
        BufferedImage bufferedImage = new BufferedImage(length, length, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();

        // Garish background
        g.setColor(new java.awt.Color(red,green,blue));
        g.fillRect(0, 0, length, length);

        // Done
        g.dispose();

        return bufferedImage;
    }
}
