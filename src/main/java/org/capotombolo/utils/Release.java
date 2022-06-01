package org.capotombolo.utils;

import java.sql.Date;

public class Release {
    public final String name;
    public final Date date;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private int index;

    public Release(String release, Date date){
        this.name = release;
        this.date = date;
    }

    public String getName() {
        return this.name;
    }

    public Date getDate() {
        return this.date;
    }
}
