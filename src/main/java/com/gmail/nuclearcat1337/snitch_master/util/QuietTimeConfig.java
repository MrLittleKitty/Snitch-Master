package com.gmail.nuclearcat1337.snitch_master.util;

import java.util.Arrays;

/**
 * Created by Mr_Little_Kitty on 2/22/2017.
 */
public class QuietTimeConfig
{
    public byte[] instructions;
    public String[] literals;

    public QuietTimeConfig(byte[] instructions, String[] literals)
    {
        this.instructions = instructions;
        this.literals = literals;
    }

    @Override
    public String toString()
    {
        return Arrays.toString(instructions) + ":" + ToString(literals);
    }

    public static QuietTimeConfig GetDefaultQuietTimeConfig()
    {
        byte[] b = new byte[]{1,8,2,8,3,8,7,4,6,0};
        String[] literals = new String[]{"[world X X X]"};
        return new QuietTimeConfig(b,literals);
    }

    public static QuietTimeConfig FromString(String value)
    {
        String[] parts = value.split(":");
        byte[] bytes = ParseByteArray(parts[0]);
        String[] literals = ParseStringArray(parts[1]);
        return new QuietTimeConfig(bytes,literals);
    }

    private static String ToString(String[] literals)
    {
        StringBuilder builder = new StringBuilder();
        for(String str : literals)
            builder.append(str.replace(":","").replace(";","")).append(';');
        return builder.toString();
    }

    private static String[] ParseStringArray(String str)
    {
        return str.split(";");
    }

    private static byte[] ParseByteArray(String str)
    {
        str = str.replace("[","").replace("]","").replace(",","");
        String[] b = str.split(" ");
        byte[] bytes = new byte[b.length];
        for(int i = 0; i < b.length; i++)
            bytes[i] = Byte.parseByte(b[i]);
        return bytes;
    }
}
