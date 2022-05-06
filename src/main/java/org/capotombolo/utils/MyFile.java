package org.capotombolo.utils;

public class MyFile {

    public String path;
    public StateFile state;

    public MyFile(String path, StateFile state){
        this.path = path;
        this.state = state;
    }

    public enum StateFile{
        BUG,
        NO_BUG
    }
}
