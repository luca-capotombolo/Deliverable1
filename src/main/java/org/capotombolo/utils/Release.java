package org.capotombolo.utils;

public class Release {
    public String release;
    public String date;

    public Release(String release, String date){
        this.release = release;
        this.date = date;
    }

    public String getRelease() {
        return this.release;
    }

    public String getDate() {
        return this.date;
    }
}
