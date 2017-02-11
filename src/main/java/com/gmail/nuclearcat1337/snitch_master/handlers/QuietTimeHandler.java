package com.gmail.nuclearcat1337.snitch_master.handlers;

import com.gmail.nuclearcat1337.snitch_master.Settings;
import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.api.IAlertRecipient;
import com.gmail.nuclearcat1337.snitch_master.api.SnitchAlert;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
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
    private static final String QUIET_TIME_CONFIG_KEY = "quiet-time-config";
    private final Settings settings;

    public QuietTimeHandler(Settings settings)
    {
        this.settings = settings;
    }

    @Override
    public void receiveSnitchAlert(SnitchAlert alert)
    {
        Settings.QuietTimeState state = (Settings.QuietTimeState)settings.getValue(Settings.QUIET_TIME_KEY);

        //If it isnt off then we are going to do some sort of hiding
        if(state != Settings.QuietTimeState.OFF)
        {
            // Move the coordinates into hovertext
            String snitchLocation = alert.getLocation().toString();
            Style aqua = new Style().setColor(TextFormatting.AQUA);

            HoverEvent hoverLocation = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(snitchLocation));

            ITextComponent hoverLocationComponent = new TextComponentString("[world X X X]").setStyle(new Style().setHoverEvent(hoverLocation));

            String visibleText;
            ITextComponent newMessage;
            if(state == Settings.QuietTimeState.HIDE_NAME_AND_COORDINATES)
            {
                HoverEvent hoverName = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(alert.getSnitchName().isEmpty() ? Snitch.DEFAULT_NAME : alert.getSnitchName()));
                ITextComponent hoverNameComponent = new TextComponentString("Hidden ").setStyle(new Style().setHoverEvent(hoverName));

                visibleText = String.format(" * %s %s ", alert.getPlayerName(), alert.getActivity());
                newMessage = new TextComponentString(visibleText).setStyle(aqua).appendSibling(hoverNameComponent).appendSibling(hoverLocationComponent);
            }
            else
            {
                visibleText = String.format(" * %s %s %s ", alert.getPlayerName(), alert.getActivity(), alert.getSnitchName());
                newMessage = new TextComponentString(visibleText).setStyle(aqua).appendSibling(hoverLocationComponent);
            }

            SnitchMaster.logger.info("<Snitch location converted to hovertext> " + alert.getRawMessage().getUnformattedText());

            alert.setRawMessage(newMessage);
        }
    }
}
