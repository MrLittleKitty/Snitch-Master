package com.gmail.nuclearcat1337.snitch_master.handlers;

import com.gmail.nuclearcat1337.snitch_master.Settings;
import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.api.IAlertRecipient;
import com.gmail.nuclearcat1337.snitch_master.api.SnitchAlert;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;

/**
 * Created by Mr_Little_Kitty on 12/30/2016.
 */
public class QuietTimeHandler implements IAlertRecipient
{
    private final Settings settings;

    public QuietTimeHandler(Settings settings)
    {
        this.settings = settings;
    }

    @Override
    public void receiveSnitchAlert(SnitchAlert alert)
    {
        Boolean state = (Boolean)settings.getValue(Settings.QUIET_TIME_KEY);

        if(state.booleanValue())
        {
            // by default, move the coordinates into hovertext
            String snitchLocation = alert.getLocation().toString();
            Style aqua = new Style().setColor(TextFormatting.AQUA);

            HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(snitchLocation));

            ITextComponent hoverAreaComponent = new TextComponentString("[world X X X]").setStyle(new Style().setHoverEvent(hover));

            //ITextComponent snitchNameComponent = new TextComponentString(alert.getSnitchName()).setStyle(new Style().setHoverEvent(hover));

            String visibleText = String.format(" * %s %s %s ", alert.getPlayerName(), alert.getActivity(),alert.getSnitchName());

            ITextComponent newMessage = new TextComponentString(visibleText).setStyle(aqua).appendSibling(hoverAreaComponent);

            SnitchMaster.logger.info("<Snitch location converted to hovertext> " + alert.getSnitchName() + " " + snitchLocation);

            alert.setRawMessage(newMessage);
        }
    }
}
