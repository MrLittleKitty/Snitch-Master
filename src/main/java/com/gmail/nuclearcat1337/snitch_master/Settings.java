package com.gmail.nuclearcat1337.snitch_master;

import com.gmail.nuclearcat1337.snitch_master.util.ValueParser;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mr_Little_Kitty on 7/9/2016.
 */
public class Settings
{
    private final String filePath;
    private final ValueParser parser;

    private final HashMap<String,Object> values;

    public Settings(String filePath, ValueParser parser)
    {
        this.filePath = filePath;
        this.parser = parser;
        values = new HashMap<>();
    }

    public void setValueIfNotSet(String key, Object value)
    {
        if(!values.containsKey(key))
            values.put(key,value);
    }

    public void setValue(String key, Object value)
    {
        values.put(key,value);
    }

    public Object getValue(String key)
    {
        return values.get(key);
    }

    public void loadSettings()
    {
        values.clear();
        File file = new File(filePath);
        if(file.exists())
        {
            try
            {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = null;
                while((line = reader.readLine()) != null)
                {
                    String[] tokens = line.split("=");
                    Object value = parser.parse(tokens[0],tokens[1]);
                    values.put(tokens[0],value);
                }
                reader.close();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void saveSettings()
    {
        //TODO---Need to create the folder path before the file can be created
        File file = new File(filePath);
        BufferedWriter writer = null;
        try
        {
            if(!file.exists())
                file.createNewFile();

            writer = new BufferedWriter(new FileWriter(file));
            for(Map.Entry<String,Object> entry : values.entrySet())
            {
                writer.write(entry.getKey());
                writer.write('=');
                writer.write(entry.getValue().toString());
                writer.write(System.lineSeparator());
            }
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
