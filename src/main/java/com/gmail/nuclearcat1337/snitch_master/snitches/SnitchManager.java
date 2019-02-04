package com.gmail.nuclearcat1337.snitch_master.snitches;

import com.gmail.nuclearcat1337.snitch_master.Settings;
import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.api.SnitchListQualifier;
import com.gmail.nuclearcat1337.snitch_master.journeymap.JourneyMapInterface;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.LocatableObjectList;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.Location;
import com.gmail.nuclearcat1337.snitch_master.util.Color;
import com.gmail.nuclearcat1337.snitch_master.util.GeneralUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.*;
import java.util.*;

import org.apache.commons.io.FileUtils;

/**
 * Created by Mr_Little_Kitty on 3/10/2017.
 */
public class SnitchManager {
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final String serversFolder = SnitchMaster.modDataFolder + "/Servers";
    private static final String SNITCHES_FILE = "Snitches.csv";
    private static final String SNITCH_LISTS_FILE = "SnitchLists.csv";

    public static final String GLOBAL_RENDER_KEY = "global-render";

    private static final SnitchListQualifier FRIENDLY = new SnitchListQualifier(String.format("origin == '%s' || origin == '%s' && origin != '%s'", SnitchTags.FROM_JALIST, SnitchTags.FROM_TEXT, SnitchTags.IS_REMOVED));
    private static final SnitchListQualifier NEUTRAL = new SnitchListQualifier(String.format("origin == '%s'", SnitchTags.FROM_MANUAL));
    private static final SnitchListQualifier REMOVED = new SnitchListQualifier(String.format("origin == '%s'", SnitchTags.IS_REMOVED));

    private static SnitchList[] getDefaultSnitchLists(SnitchManager manager) {
        return new SnitchList[]{new SnitchList(manager, SnitchManager.FRIENDLY, true, "Friendly", new Color(0, (int) (0.56D * 255D), 255)),
                new SnitchList(manager, SnitchManager.NEUTRAL, true, "Neutral", new Color(238, 210, 2)), //"Safety Yellow"
                new SnitchList(manager, SnitchManager.REMOVED, true, "Removed", new Color(220, 20, 60)) //TODO---Finish implementing this feature
        };
    }

    private final JourneyMapInterface journeyMapInterface;
    private final Settings settings;

    private final LocatableObjectList<Snitch> snitches;
    private final List<SnitchList> snitchLists;

    private String currentServer;

    public SnitchManager(final JourneyMapInterface journeyMapInterface, final Settings settings) {
        this.journeyMapInterface = journeyMapInterface;
        this.settings = settings;

        snitches = new LocatableObjectList<>();
        snitchLists = new ArrayList<>();

        currentServer = null;
        File file = new File(serversFolder);
        if (!file.exists()) {
            file.mkdir();
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        //Make sure the player isn't dead and isn't null (idk why?)
        if (mc.player == null || (!mc.player.isDead && mc.player.getDisplayName().equals(event.getEntity().getDisplayName()))) {
            //The name of the server they just joined
            String newServer = null;
            if (mc.isSingleplayer()) {
                newServer = "single-player"; //Obviously
            } else {
                //Clean the name of their current server
                if (mc.getCurrentServerData() != null) {
                    newServer = mc.getCurrentServerData().serverIP.replace(":", "").replace("/", "");
                } else {
                    return; //Idk wtf happened here so just return
                }
            }

            //They joined a new server
            if (!newServer.equalsIgnoreCase(currentServer)) {
                saveSnitchLists();
                saveSnitches();

                snitchLists.clear();
                snitches.clear();

                currentServer = newServer;

                File serverDirectory = new File(serversFolder, "/" + currentServer);
                if (!serverDirectory.exists() || !serverDirectory.isDirectory()) {
                    serverDirectory.mkdir();

                    if (snitchLists.isEmpty()) { //If we load the lists from the file and there are none, create the default ones
                        Collections.addAll(snitchLists, getDefaultSnitchLists(this));
                        saveSnitchLists();
                    }
                } else { //If we just created the directory for this server then obviously there is no data yet
                    loadSnitchLists();

                    if (snitchLists.isEmpty()) { //If we load the lists from the file and there are none, create the default ones
                        Collections.addAll(snitchLists, getDefaultSnitchLists(this));
                        saveSnitchLists();
                    }

                    loadSnitches();

                    if (snitches.size() > 0) {
                        SnitchMaster.SendMessageToPlayer("Loaded " + snitches.size() + " snitches for server: " + currentServer);
                    }
                    if (snitchLists.size() > 0) {
                        SnitchMaster.SendMessageToPlayer("Loaded " + snitchLists.size() + " snitch lists for server: " + currentServer);
                    }
                }
            }
        }
    }

    public SnitchList getRenderListForSnitch(Snitch snitch) {
        for (SnitchList list : snitch.attachedSnitchLists)
            if (list.shouldRenderSnitches()) {
                return list;
            }
        return null;
    }

    public List<SnitchList> getSnitchListsForSnitch(Snitch snitch) {
        return snitch.attachedSnitchLists;
    }

    public boolean doesListWithNameExist(String name) {
        for (SnitchList list : snitchLists) {
            if (list.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public SnitchList createSnitchList(String name, SnitchListQualifier qualifier, boolean render, Color color) {
        if (doesListWithNameExist(name)) {
            return null;
        }

        SnitchList list = new SnitchList(this, qualifier, render, name, color);
        for (Snitch snitch : getSnitches()) {
            if (qualifier.isQualified(snitch)) {
                attachListToSnitch(list, snitch);
            }
        }
        snitchLists.add(list);
        saveSnitchLists();
        return list;
    }

    public boolean removeSnitchList(String name) {
        if (!doesListWithNameExist(name)) {
            return false;
        }

        for (int i = 0; i < snitchLists.size(); i++) {
            if (snitchLists.get(i).getName().equalsIgnoreCase(name)) {
                SnitchList list = snitchLists.remove(i);

                //Now we need to update the render priority of all the remaining snitch lists
                //And remove any references to this list from the attached snitch lists
                for (int j = 0; j < snitchLists.size(); j++) {
                    snitchLists.get(j).setRenderPriorityUnchecked(j + 1);
                }

                for (Snitch snitch : getSnitchesInList(list)) {
                    snitch.attachedSnitchLists.remove(list);
                }

                saveSnitchLists();

                return true;
            }
        }
        return false;
    }

    public void toggleGlobalRender() {
        settings.setGlobalRender(!settings.getGlobalRender());
        settings.saveSettings();
    }

    public boolean getGlobalRender() {
        return settings.getGlobalRender();
    }

    public ArrayList<Snitch> getSnitchesInList(SnitchList list) {
        ArrayList<Snitch> attachedSnitches = new ArrayList<>();
        for (Snitch snitch : snitches) {
            if (snitch.attachedSnitchLists.contains(list)) {
                attachedSnitches.add(snitch);
            }
        }
        return attachedSnitches;
    }

    public void setSnitchName(Snitch snitch, String name) {
        snitch.setName(name);
        requalifySnitch(snitch);
    }

    public void setSnitchGroup(Snitch snitch, String group) {
        snitch.setGroupName(group);
        requalifySnitch(snitch);
    }

    public void setSnitchCullTime(Snitch snitch, double cullTime) {
        snitch.setCullTime(cullTime);
        requalifySnitch(snitch);
    }

    public void addTag(Snitch snitch, String tag) {
        snitch.tags.add(tag);
        requalifySnitch(snitch);
    }

    public boolean removeTag(Snitch snitch, String tag) {
        if (!snitch.tags.remove(tag)) {
            return false;
        }
        requalifySnitch(snitch);
        return true;
    }

    private void requalifySnitch(Snitch snitch) {
        snitch.attachedSnitchLists.clear();
        for (SnitchList list : snitchLists) {
            if (list.getQualifier().isQualified(snitch)) {
                attachListToSnitch(list, snitch);
            }
        }
        journeyMapInterface.displaySnitch(snitch);
    }


    public List<Snitch> getIntersectingSnitches(final Location loc) {
        final List<Snitch> value = new ArrayList<>();
        for (final Snitch snitch : this.getSnitches().getItemsForWorld(loc.getWorld())) {
            if (snitch.isPointInThisSnitch(loc.getX(), loc.getY(), loc.getZ())) {
                value.add(snitch);
            }
        }
        value.sort(new Comparator<Snitch>() {
            @Override
            public int compare(Snitch o1, Snitch o2) {
                final Location loc1 = o1.getLocation();
                final Location loc2 = o2.getLocation();
                return Double.compare(GeneralUtils.distanceSquaredToPlayer(loc1.getX(), loc1.getY(), loc1.getZ()),
                        GeneralUtils.distanceSquaredToPlayer(loc2.getX(), loc2.getY(), loc2.getZ()));
            }
        });
        return value;
    }

    /**
     * Submits a Snitch for processing and adding to the Snitch collection.
     * The Snitch is added to all SnitchLists, JourneyMap, (if applicable) and then saved to a file.
     */
    public void submitSnitch(Snitch snitch) {
        //Check to see if there is already a snitch at this location
        Snitch contains = snitches.get(snitch.getLocation());

        //Check if the snitch that was submitted already exists
        if (contains != null) {
            //If it does then change the cull time and group
            contains.setCullTime(snitch.getCullTime());
            contains.setGroupName(snitch.getGroupName());
            contains.setName(snitch.getName());

            if (snitch.isTagged(SnitchTags.FROM_JALIST)) {
                contains.tags.remove(SnitchTags.IS_REMOVED); //Remove it the dirty way cause this is the manager
            }

            for (String str : snitch.getTags()) {
                contains.tags.add(str);
            }

            //Clear the attached snitch lists because we are going to requalify the snitch because some attributes changed
            contains.attachedSnitchLists.clear();
        } else {
            //Just some reference rearranging
            contains = snitch;
            //add the snitch to the collection
            snitches.add(contains);
        }

        //Go through all the snitch lists to see if this snitch should be in them
        for (SnitchList list : snitchLists) {
            //If it should then attach the snitch list to the snitch
            if (list.getQualifier().isQualified(contains)) {
                attachListToSnitch(list, contains);
            }
        }

        //send it to journey map if that is enabled
        journeyMapInterface.displaySnitch(contains);
    }

    public void saveSnitchLists() {
        if (currentServer != null) {
            ArrayList<String> csvs = new ArrayList<>();
            for (SnitchList list : snitchLists) {
                csvs.add(SnitchList.ConvertSnitchListToCSV(list));
            }
            writeToCSV(new File(serversFolder + "/" + currentServer + "/" + SNITCH_LISTS_FILE), csvs);
        }
    }

    public void saveSnitches() {
        if (currentServer != null) {
            ArrayList<String> csvs = new ArrayList<>();
            for (Snitch snitch : snitches) {
                csvs.add(Snitch.ConvertSnitchToCSV(snitch));
            }
            writeToCSV(new File(serversFolder + "/" + currentServer + "/" + SNITCHES_FILE), csvs);
        }
    }

    public LocatableObjectList<Snitch> getSnitches() {
        return snitches;
    }

    public Collection<SnitchList> getSnitchLists() {
        return snitchLists;
    }

    void journeyMapRedisplay(SnitchList list) {
        journeyMapInterface.displaySnitches(getSnitchesInList(list));
    }

    void changeListRenderPriority(SnitchList list, boolean increase) {
        int index = snitchLists.indexOf(list);
        int targetIndex = increase ? index - 1 : index + 1;

        if (swapIfPossible(snitchLists, index, targetIndex)) {
            //Update the list objects actual render priority
            snitchLists.get(index).setRenderPriorityUnchecked(index + 1);
            snitchLists.get(targetIndex).setRenderPriorityUnchecked(targetIndex + 1);

            for (Snitch snitch : getSnitchesInList(list)) {
                //TODO---What we need to do is just sort the attached snitch lists by their render priority
                Collections.sort(snitch.attachedSnitchLists, listComparator);
            }
        }
    }

    void requalifyList(SnitchList list) {
        for (Snitch snitch : getSnitchesInList(list)) {
            snitch.attachedSnitchLists.remove(list);
        }
        SnitchListQualifier qualifier = list.getQualifier();
        for (Snitch snitch : getSnitches()) {
            if (qualifier.isQualified(snitch)) {
                attachListToSnitch(list, snitch);
            }
        }
        journeyMapInterface.refresh(getSnitches());
    }

    private void attachListToSnitch(SnitchList list, Snitch snitch) {
        List<SnitchList> attached = snitch.attachedSnitchLists;
        int i = 0;
        for (; i < attached.size(); i++) {
            if (list.getRenderPriority() < attached.get(i).getRenderPriority()) {
                break;
            }
        }
        attached.add(i, list);
    }

    private static <T> boolean swapIfPossible(List<T> list, int index, int targetIndex) {
        if (list.size() < 2) {
            return false;
        }

        //Make sure both the indices are valid for the list
        if (index >= 0 && index < list.size() && targetIndex >= 0 && targetIndex < list.size()) {
            T temp = list.get(targetIndex);
            list.set(targetIndex, list.get(index));
            list.set(index, temp);
            return true;
        }
        return false;
    }

    private static void writeToCSV(File file, List<String> lines) {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line);
            sb.append("\n");
        }
        String snitchList = sb.toString();

        try {
            FileUtils.writeStringToFile(file, snitchList, "UTF-8");
        } catch (IOException e) {
        }
    }

    private void loadSnitches() {
        if (currentServer != null) {
            File file = new File(serversFolder, "/" + currentServer + "/" + SNITCHES_FILE);
            try {
                if (file.exists()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        for (String line = null; (line = br.readLine()) != null; ) {
                            Snitch snitch = Snitch.GetSnitchFromCSV(line);
                            if (snitch != null) {
                                submitSnitch(snitch);
                            }
                        }
                    }
                }
            } catch (IOException e) {
            }
        }
    }

    private void loadSnitchLists() {
        if (currentServer != null) {
            File file = new File(serversFolder, "/" + currentServer + "/" + SNITCH_LISTS_FILE);
            try {
                if (file.exists()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        for (String line = null; (line = br.readLine()) != null; ) {
                            SnitchList list = SnitchList.GetSnitchListFromCSV(line, this);
                            if (list != null) {
                                this.snitchLists.add(list);
                            }
                        }
                    }
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * A comparator that sorts SnitchLists according to their render priorities.
     */
    private static class SnitchListComparator implements Comparator<SnitchList> {
        @Override
        public int compare(SnitchList one, SnitchList two) {
            return Integer.compare(one.getRenderPriority(), two.getRenderPriority());
        }
    }

    /**
     * A static instance of the SnitchList comparator to use in all instances of the Snitch class.
     */
    private static final SnitchListComparator listComparator = new SnitchListComparator();
}
