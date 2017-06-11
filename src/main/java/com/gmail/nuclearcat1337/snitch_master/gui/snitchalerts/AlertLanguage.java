package com.gmail.nuclearcat1337.snitch_master.gui.snitchalerts;

/**
 * Created by Mr_Little_Kitty on 6/11/2017.
 */
public class AlertLanguage
{
    public static final byte PLAYER_NAME = 1;

    public static final byte ACTIVITY_TEXT = 2;

    public static final byte SNITCH_NAME = 3;

    public static final byte LOCATION = 4;

    public static final byte SNITCH_NAME_AND_LOCATION = 5;

    public static final byte STRING_SUBSTITUTION = 6;

    public static final byte HOVER_EVENT = 7;

    public static final byte INSERT_SPACE = 8;

    public static final byte[] INSTRUCTIONS = {
            PLAYER_NAME,
            ACTIVITY_TEXT,
            SNITCH_NAME,
            LOCATION,
            SNITCH_NAME_AND_LOCATION,
            STRING_SUBSTITUTION,
            HOVER_EVENT,
            INSERT_SPACE
    };

    public static String getInstructionName(byte instruction)
    {
        if(instruction == PLAYER_NAME)
            return "Player Name";
        if(instruction == ACTIVITY_TEXT)
            return "Activity Text";
        if(instruction == SNITCH_NAME)
            return "Snitch Name";
        if(instruction == LOCATION)
            return "Snitch Location";
        if(instruction == SNITCH_NAME_AND_LOCATION)
            return "Snitch Name + Location";
        if(instruction == STRING_SUBSTITUTION)
            return "String Literal";
        if(instruction == HOVER_EVENT)
            return "Hover Event";
        if(instruction == INSERT_SPACE)
            return "INSERT SPACE";
        return "None";
    }
}
