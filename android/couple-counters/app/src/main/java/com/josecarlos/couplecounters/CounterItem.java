package com.josecarlos.couplecounters;

import java.util.Date;

/**
 * Created by Jose Carlos on 11/11/2015.
 */
public class CounterItem {

    public static final String ITEM_SEP = System.getProperty("line.separator");

    private String counterName;
    private boolean common;

    CounterItem(String counterName, boolean common) {
        this.counterName = counterName;
        this.common = common;
    }

    CounterItem(String counterName)
    {
        this(counterName, false);
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String value)
    {
        counterName = value;
    }

    public boolean isCommon() {
        return common;
    }

    public void setCommon (boolean value)
    {
        common = value;
    }


    public String toString()
    {
        return counterName + ITEM_SEP + common;
    }
}
