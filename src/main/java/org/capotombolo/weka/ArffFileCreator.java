package org.capotombolo.weka;

import org.capotombolo.utils.MyFile;
import org.capotombolo.utils.Release;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

public class ArffFileCreator {
    List<String> attributes;
    private final static String MACRO = "class";
    private final static String MACRO1 = "@ATTRIBUTE ";

    public ArffFileCreator(){
        this.attributes = new ArrayList<>();
        //Add all attribute
        this.attributes.add("numberRevision");
        this.attributes.add("locAdded");
        this.attributes.add("avgLocAdded");
        this.attributes.add("maxLocAdded");
        this.attributes.add("size");
        this.attributes.add("chgSetSize");
        this.attributes.add("avgChgSetSize");
        this.attributes.add("maxChgSetSize");
        this.attributes.add("authors");
        this.attributes.add(MACRO);
    }

    public boolean createArffFileTrainingSet(Map<Release, List<MyFile>> hashMap, List<Release> releaseList, String relation, Release youngerRelease){
        List<MyFile> myFiles;
        try{
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(relation+"_"+youngerRelease.name +".arff"));
            bufferedWriter.write("@RELATION " + relation+"_"+youngerRelease.name);
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.flush();
            for(String attribute: this.attributes){
                if(Objects.equals(attribute, MACRO))
                {
                    bufferedWriter.write(MACRO1 + attribute + " {BUG,NO_BUG}");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    continue;
                }
                bufferedWriter.write(MACRO1 + attribute + " NUMERIC");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            bufferedWriter.newLine();
            bufferedWriter.write("@DATA");
            bufferedWriter.newLine();
            bufferedWriter.flush();

            for(Release release: releaseList){
                if(release.date.compareTo(youngerRelease.date) <=0){
                    myFiles = hashMap.get(release);
                    for(MyFile myFile: myFiles){
                        bufferedWriter.write(myFile.getNumberRevisionRelease() +",");
                        bufferedWriter.write(myFile.getNumberLocAddedRelease() + ",");
                        bufferedWriter.write(myFile.getAverageNumberLocAdded() + ",");
                        bufferedWriter.write(myFile.getMaxNumberLocAdded() + ",");
                        bufferedWriter.write(myFile.getLines() + ",");
                        bufferedWriter.write(myFile.getSetTouchedFileWithCRelease().size()+",");
                        if(Float.isNaN(myFile.getAvgNumberTouchedFile()))
                            bufferedWriter.write("?,");
                        else
                            bufferedWriter.write(myFile.getAvgNumberTouchedFile() +",");
                        bufferedWriter.write(myFile.getMaxNumberTouchedFile() +",");
                        bufferedWriter.write(myFile.getAuthors().size()+",");
                        bufferedWriter.write(myFile.getState().toString());
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                }
            }
            bufferedWriter.flush();
            bufferedWriter.close();

        }catch (Exception e){
            return false;
        }

        return true;
    }

    public boolean createArffFileTestingSet(Map<Release, List<MyFile>> hashMap, List<Release> releaseList, String relation){

        List<MyFile> myFiles;
        int count = 0;

        for(Release release: releaseList){
            if(count==0){
                //first release
                count++;
                continue;
            }
            if(count<=releaseList.size()/2){
                myFiles = hashMap.get(release);
                try {
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(relation + "_" + release.name + ".arff"));
                    bufferedWriter.write("@RELATION " + relation + "_" + release.name);
                    bufferedWriter.newLine();
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    for (String attribute : this.attributes) {
                        if (Objects.equals(attribute, MACRO)) {
                            bufferedWriter.write(MACRO1 + attribute + " {BUG,NO_BUG}");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            continue;
                        }
                        bufferedWriter.write(MACRO1 + attribute + " NUMERIC");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                    bufferedWriter.newLine();
                    bufferedWriter.write("@DATA");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    for(MyFile myFile: myFiles){
                        bufferedWriter.write(myFile.getNumberRevisionRelease() +",");
                        bufferedWriter.write(myFile.getNumberLocAddedRelease() + ",");
                        bufferedWriter.write(myFile.getAverageNumberLocAdded() + ",");
                        bufferedWriter.write(myFile.getMaxNumberLocAdded() + ",");
                        bufferedWriter.write(myFile.getLines() + ",");
                        bufferedWriter.write(myFile.getSetTouchedFileWithCRelease().size()+",");
                        if(Float.isNaN(myFile.getAvgNumberTouchedFile()))
                            bufferedWriter.write("?,");
                        else
                            bufferedWriter.write(myFile.getAvgNumberTouchedFile() +",");
                        bufferedWriter.write(myFile.getMaxNumberTouchedFile() +",");
                        bufferedWriter.write(myFile.getAuthors().size()+",");
                        bufferedWriter.write(myFile.getState().toString());
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }catch (Exception e){
                    return false;
                }
                count++;
            }
        }
       return true;
    }

}
