package org.capotombolo.utils;

public class MyFile {

    public String path;
    public StateFile state;
    public int lines = -1;

    public MyFile(String path, StateFile state){
        this.path = path;
        this.state = state;
    }

    public void setLines(int lines){
        this.lines = lines;
    }

    public enum StateFile{
        BUG,
        NO_BUG
    }
}
