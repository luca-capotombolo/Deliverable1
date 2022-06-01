package org.capotombolo.weka;

import org.capotombolo.utils.MyFile;
import org.capotombolo.utils.Release;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ArffFileCreator {
    List<String> attributes;

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
        this.attributes.add("class");
    }

    public boolean createArffFileTrainingSet(HashMap<Release, List<MyFile>> hashMap, List<Release> releaseList, String relation, Release youngerRelease){
        List<MyFile> myFiles;
        try{
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(relation+"_"+youngerRelease.name +".arff"));
            bufferedWriter.write("@RELATION " + relation+"_"+youngerRelease.name);
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.flush();
            for(String attribute: this.attributes){
                if(Objects.equals(attribute, "class"))
                {
                    bufferedWriter.write("@ATTRIBUTE " + attribute + " {BUG,NO_BUG}");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    continue;
                }
                bufferedWriter.write("@ATTRIBUTE " + attribute + " NUMERIC");
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
                        bufferedWriter.write(myFile.numberRevisionRelease+",");
                        bufferedWriter.write(myFile.numberLocAddedRelease + ",");
                        bufferedWriter.write(myFile.averageNumberLocAdded + ",");
                        bufferedWriter.write(myFile.maxNumberLocAdded + ",");
                        bufferedWriter.write(myFile.lines+ ",");
                        bufferedWriter.write(myFile.setTouchedFileWithCRelease.size()+",");
                        if(Float.isNaN(myFile.avgNumberTouchedFile))
                            bufferedWriter.write("?,");
                        else
                            bufferedWriter.write(myFile.avgNumberTouchedFile+",");
                        bufferedWriter.write(myFile.maxNumberTouchedFile+",");
                        bufferedWriter.write(myFile.authors.size()+",");
                        bufferedWriter.write(myFile.state.toString());
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

    public boolean createArffFileTestingSet(HashMap<Release, List<MyFile>> hashMap, List<Release> releaseList, String relation){

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
                        if (Objects.equals(attribute, "class")) {
                            bufferedWriter.write("@ATTRIBUTE " + attribute + " {BUG,NO_BUG}");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            continue;
                        }
                        bufferedWriter.write("@ATTRIBUTE " + attribute + " NUMERIC");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                    bufferedWriter.newLine();
                    bufferedWriter.write("@DATA");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    for(MyFile myFile: myFiles){
                        bufferedWriter.write(myFile.numberRevisionRelease+",");
                        bufferedWriter.write(myFile.numberLocAddedRelease + ",");
                        bufferedWriter.write(myFile.averageNumberLocAdded + ",");
                        bufferedWriter.write(myFile.maxNumberLocAdded + ",");
                        bufferedWriter.write(myFile.lines+ ",");
                        bufferedWriter.write(myFile.setTouchedFileWithCRelease.size()+",");
                        if(Float.isNaN(myFile.avgNumberTouchedFile))
                            bufferedWriter.write("?,");
                        else
                            bufferedWriter.write(myFile.avgNumberTouchedFile+",");
                        bufferedWriter.write(myFile.maxNumberTouchedFile+",");
                        bufferedWriter.write(myFile.authors.size()+",");
                        bufferedWriter.write(myFile.state.toString());
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
