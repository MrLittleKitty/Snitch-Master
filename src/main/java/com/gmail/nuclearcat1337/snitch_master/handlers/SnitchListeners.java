package com.gmail.nuclearcat1337.snitch_master.handlers;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import com.gmail.nuclearcat1337.snitch_master.util.Color;
import com.gmail.nuclearcat1337.snitch_master.util.IOHandler;
import com.gmail.nuclearcat1337.snitch_master.util.Location;
import com.gmail.nuclearcat1337.snitch_master.util.PointLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Mr_Little_Kitty on 9/8/2016.
 * Handles misc events related to Snitches.
 */
public class SnitchListeners
{
    private final SnitchMaster snitchMaster;

    public SnitchListeners(SnitchMaster snitchMaster)
    {
        this.snitchMaster = snitchMaster;
    }

    /**
     * Event that handles when a Snitch is right clicked.
     */
    @SubscribeEvent
    public void onSnitchPlace(PlayerInteractEvent event)
    {
        if (event instanceof PlayerInteractEvent.RightClickBlock)
        {
            PlayerInteractEvent.RightClickBlock event1 = (PlayerInteractEvent.RightClickBlock) event;
            BlockPos pos = event1.getPos();
            if (pos != null)
            {
                IBlockState state = event1.getWorld().getBlockState(pos);
//                if (state.getBlock().equals(Blocks.SPONGE))
//                {
//                    block = new Block(pos.getX(), pos.getY(), pos.getZ(), 10, new Color(0.86, 0.08, 0.24, 0.25));
//                }
                if (state.getBlock().equals(Blocks.JUKEBOX) || state.getBlock().equals(Blocks.NOTEBLOCK))
                {
                    Location loc = new Location(pos.getX(),pos.getY(),pos.getZ(),snitchMaster.getCurrentWorld());
                    Snitch snitch = new Snitch(loc,"manual");

                    snitchMaster.submitSnitch(snitch);

                    IOHandler.saveSnitches(snitchMaster.getSnitches());
                }
            }
        }
    }

    @SubscribeEvent
    public void onSnitchBreak(BlockEvent.BreakEvent event)
    {
        BlockPos pos = event.getPos();
        Location loc = new Location(pos.getX(),pos.getY(),pos.getZ(),snitchMaster.getCurrentWorld());

        Snitch snitch = snitchMaster.getSnitches().remove(loc);
        if(snitch != null)
        {
            if(SnitchMaster.jmInterface != null)
                SnitchMaster.jmInterface.refresh(snitchMaster.getSnitches());

            IOHandler.saveSnitches(snitchMaster.getSnitches());

            SnitchMaster.SendMessageToPlayer("Removed snitch at "+loc.toString());
        }
    }
}
