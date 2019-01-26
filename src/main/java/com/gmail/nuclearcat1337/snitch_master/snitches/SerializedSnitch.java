package com.gmail.nuclearcat1337.snitch_master.snitches;

import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.Location;

import java.util.Collection;

class SerializedSnitch {
    public String name;
    public String group;
    public Location location;
    public Collection<String> tags;
    public Double cullTime;
    public String type;
    public Collection<String> description;
}
