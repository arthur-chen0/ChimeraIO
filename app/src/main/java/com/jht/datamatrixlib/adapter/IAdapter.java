package com.jht.datamatrixlib.adapter;

/**
 * Created by chris.foyer on 12/15/2016.
 */

public interface IAdapter
{
    void deregister();
    String getKey();
    boolean hasCallback();
}
