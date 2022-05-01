package org.capotombolo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.capotombolo.utils.Release;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.capotombolo.jira.Jira.readJsonArrayFromUrl;


public class App 
{

    public static void main(String[] args) throws IOException {
        String url = "https://issues.apache.org/jira/rest/api/2/project/ZOOKEEPER/versions";
        //String projName ="ZOOKEEPER";
        List<Release> releaseList = new ArrayList<>();
        Release release;
        boolean released;
        JSONArray json = readJsonArrayFromUrl(url);
        int len = json.length();
        for(int count = 0; count<len; count++){
            JSONObject jsonObject = json.getJSONObject(count);
            released = jsonObject.getBoolean("released");
            if(released){
                try {
                    System.out.println(jsonObject.getString("name"));
                    System.out.println(jsonObject.getString("releaseDate"));
                    release = new Release(jsonObject.getString("name"), jsonObject.getString("releaseDate"));
                    releaseList.add(release);
                }catch (Exception e){
                    release = new Release(jsonObject.getString("name"), null);
                    releaseList.add(release);
                }
            }
        }

        System.out.println("----------------------------------------------------------------");

        for (Release value : releaseList) {
            System.out.println(value.getRelease());
            System.out.println(value.getDate());
        }

        System.out.println("----------------------------------------------------------------");

    }

}
