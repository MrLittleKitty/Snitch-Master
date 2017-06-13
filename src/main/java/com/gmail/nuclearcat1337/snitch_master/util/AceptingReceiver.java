package com.gmail.nuclearcat1337.snitch_master.util;

/**
 * Created by Mr_Little_Kitty on 6/11/2017.
 */
public interface AceptingReceiver<T,V>
{
    V accept(T t);
}
