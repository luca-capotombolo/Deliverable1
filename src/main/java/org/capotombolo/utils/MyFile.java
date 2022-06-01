package org.capotombolo.utils;

import java.util.HashSet;
import java.util.Set;

public class MyFile {

    public final String path;

    public StateFile getState() {
        return state;
    }

    public void setState(StateFile state) {
        this.state = state;
    }

    private StateFile state;

    public int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    private int lines = -1;

    public int getMaxNumberTouchedFile() {
        return maxNumberTouchedFile;
    }

    public void setMaxNumberTouchedFile(int maxNumberTouchedFile) {
        this.maxNumberTouchedFile = maxNumberTouchedFile;
    }

    private int maxNumberTouchedFile = 0;

    public float getAvgNumberTouchedFile() {
        return avgNumberTouchedFile;
    }

    public void setAvgNumberTouchedFile(float avgNumberTouchedFile) {
        this.avgNumberTouchedFile = avgNumberTouchedFile;
    }

    private float avgNumberTouchedFile = 0;

    public Set<String> getSetTouchedFileWithCRelease() {
        return setTouchedFileWithCRelease;
    }

    public void setSetTouchedFileWithCRelease(Set<String> setTouchedFileWithCRelease) {
        this.setTouchedFileWithCRelease = setTouchedFileWithCRelease;
    }

    private Set<String> setTouchedFileWithCRelease = new HashSet<>();

    public int getNumberRevisionTotal() {
        return numberRevisionTotal;
    }

    public void setNumberRevisionTotal(int numberRevisionTotal) {
        this.numberRevisionTotal = numberRevisionTotal;
    }

    private int numberRevisionTotal = 0;

    public int getNumberRevisionRelease() {
        return numberRevisionRelease;
    }

    public void setNumberRevisionRelease(int numberRevisionRelease) {
        this.numberRevisionRelease = numberRevisionRelease;
    }

    private int numberRevisionRelease=0;

    public int getNumberLocAddedRelease() {
        return numberLocAddedRelease;
    }

    public void setNumberLocAddedRelease(int numberLocAddedRelease) {
        this.numberLocAddedRelease = numberLocAddedRelease;
    }

    private int numberLocAddedRelease = 0;

    public long getMaxNumberLocAdded() {
        return maxNumberLocAdded;
    }

    public void setMaxNumberLocAdded(long maxNumberLocAdded) {
        this.maxNumberLocAdded = maxNumberLocAdded;
    }

    private long maxNumberLocAdded = 0;

    public long getAverageNumberLocAdded() {
        return averageNumberLocAdded;
    }

    public void setAverageNumberLocAdded(long averageNumberLocAdded) {
        this.averageNumberLocAdded = averageNumberLocAdded;
    }

    private long averageNumberLocAdded = 0;

    public Set<String> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<String> authors) {
        this.authors = authors;
    }

    private Set<String> authors  =new HashSet<>();

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
