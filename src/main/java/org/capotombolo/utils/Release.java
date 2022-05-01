package org.capotombolo.utils;

public class Release {
    private String release;
    private String date;

    public Release(String release, String date){
        this.release = release;
        this.date = date;
    }

    public void setRelease(String release){
        this.release = release;
    }

    public String getRelease(){
        return this.release;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getDate(){
        return this.date;
    }
}
