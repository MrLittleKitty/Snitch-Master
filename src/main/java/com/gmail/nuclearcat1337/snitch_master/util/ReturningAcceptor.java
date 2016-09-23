package com.gmail.nuclearcat1337.snitch_master.util;

/**
 * Created by Mr_Little_Kitty on 9/23/2016.
 */
public interface ReturningAcceptor<T,S>
{
    T accept(S item);
}
