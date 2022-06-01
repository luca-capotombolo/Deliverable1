package org.capotombolo.utils;

import java.util.HashSet;
import java.util.Set;

public class MyFile {

    public final String path;
    public StateFile state;
    public int lines = -1;
    public int maxNumberTouchedFile = 0;
    public float avgNumberTouchedFile = 0;
    public Set<String> setTouchedFileWithCRelease = new HashSet<>();
    public int numberRevisionTotal = 0;
    public int numberRevisionRelease=0;
    public int numberLocAddedRelease = 0;
    public long maxNumberLocAdded = 0;
    public long averageNumberLocAdded = 0;
    public Set<String> authors  =new HashSet<>();

    public MyFile(String path, StateFile state){
        this.path = path;
        this.state = state;
    }

    public MyFile(String path, StateFile state, int lines){
        this.path = path;
        this.state = state;
        this.lines = lines;
    }

    public MyFile(String path, StateFile state, int lines, int numberRevisionTotal){
        this.path = path;
        this.state = state;
        this.lines = lines;
        this.numberRevisionTotal = numberRevisionTotal;
    }

    public enum StateFile{
        BUG,
        NO_BUG
    }
}
