package com.gmail.nuclearcat1337.snitch_master.util;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by Mr_Little_Kitty on 6/26/2016.
 */
public class IOHandler
{
    private static final String modDataFolder = "mods/Snitch-Master";

    private static final String modSettingsFile = modDataFolder+"/Settings.txt";

    private static final String modSnitchesFile = modDataFolder+"/Snitches.csv";
    private static final String modSnitchListsFile = modDataFolder+"/SnitchLists.csv";

//    private static boolean savingSnitches = false;
//    private static boolean savingSnitchLists = false;

    public static void prepareFiles()
    {
        File file = new File(modDataFolder);
        if(!file.exists())
            file.mkdir();
    }

    public static File getSettingsFile()
    {
        File file = new File(modSettingsFile);
        if(file.exists())
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static void saveSnitches(Iterable<Snitch> snitches)
    {
        ArrayList<String> csvs = new ArrayList<>();
        for(Snitch snitch : snitches)
            csvs.add(Snitch.ConvertSnitchToCSV(snitch));

        //new Thread(new AsyncSaveToCSV(csvs,modSnitchesFile)).start();
       new AsyncSaveToCSV(csvs,modSnitchesFile).run();
    }

    public static void saveSnitchLists(Iterable<SnitchList> snitchLists)
    {
        ArrayList<String> csvs = new ArrayList<>();
        for(SnitchList list : snitchLists)
            csvs.add(SnitchList.ConvertSnitchListToCSV(list));

        //new Thread(new AsyncSaveToCSV(csvs,modSnitchListsFile)).start();
        new AsyncSaveToCSV(csvs,modSnitchListsFile).run();
    }

    public static void loadSnitches(SnitchMaster snitchMaster) throws IOException
    {
        File file = new File(modSnitchesFile);
        if(file.exists())
        {
            try (BufferedReader br = Files.newBufferedReader(Paths.get(modSnitchesFile), StandardCharsets.UTF_8))
            {
                for (String line = null; (line = br.readLine()) != null;)
                {
                    Snitch snitch = Snitch.GetSnitchFromCSV(line);
                    if(snitch != null)
                        snitchMaster.submitSnitch(snitch);
                }
            }
        }
    }

    public static void loadSnitchLists(SnitchMaster snitchMaster) throws IOException
    {
        File file = new File(modSnitchListsFile);
        if(file.exists())
        {
            try(FileReader reader = new FileReader(file);)
            {
                BufferedReader buf = new BufferedReader(reader);
                String line;
                while((line = buf.readLine()) != null)
                {
                    SnitchList list = SnitchList.GetSnitchListFromCSV(line);
                    if(list != null)
                        snitchMaster.getSnitchLists().addSnitchList(list);
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static class AsyncSaveToCSV implements Runnable
    {
        private final ArrayList<String> linesToWrite;
        private final String fileToWriteTo;

        public AsyncSaveToCSV(ArrayList<String> linesToWrite, String fileToWriteTo)
        {
            this.linesToWrite = linesToWrite;
            this.fileToWriteTo = fileToWriteTo;
        }

        @Override
        public void run()
        {

            File file = new File(fileToWriteTo);
            if(!file.exists())
            {
                try
                {
                    file.createNewFile();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    return;
                }
            }

            try(FileWriter writer = new FileWriter(file))
            {
                for(String line : linesToWrite)
                {
                    writer.write(line);
                    writer.write(System.lineSeparator());
                }
                writer.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
