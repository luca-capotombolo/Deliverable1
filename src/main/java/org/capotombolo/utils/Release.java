package org.capotombolo.utils;

import java.sql.Date;

public class Release {
    public String release;
    public Date date;
    public int index;

    public Release(String release, Date date){
        this.release = release;
        this.date = date;
    }

    public String getRelease() {
        return this.release;
    }

    public Date getDate() {
        return this.date;
    }
}
