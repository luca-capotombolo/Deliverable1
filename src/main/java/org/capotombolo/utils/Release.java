package org.capotombolo.utils;

import java.sql.Date;

public class Release {
    public String name;
    public final Date date;
    public int index;

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
