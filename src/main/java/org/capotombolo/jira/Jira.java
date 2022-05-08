package org.capotombolo.jira;

import org.capotombolo.utils.Issue;
import org.capotombolo.utils.Release;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Jira {
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONArray(jsonText);
        }
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }

    public  static List<Issue> getBugs(String project) throws IOException {
        Integer j, i = 0, total, h;
        List<Issue> issueList = new ArrayList<>();
        String key;
        List<Release> fixVersions;
        JSONArray fixVersionsJSONArray, issues;
        JSONObject json;
        do {
            //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
            j = i + 1000;
            String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                    + project + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
                    + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,fixVersions,versions,created&startAt="
                    + i.toString() + "&maxResults=" + j.toString();
            json = readJsonFromUrl(url);
            issues = json.getJSONArray("issues");
            total = json.getInt("total");                               //Number of Issues
            for (; i < total && i < j; i++) {
                fixVersions = new ArrayList<>();
                key = issues.getJSONObject(i%1000).get("key").toString();
                fixVersionsJSONArray = issues.getJSONObject(i%1000).getJSONObject("fields").getJSONArray("fixVersions");
                for(h = 0;h<fixVersionsJSONArray.length();h++){
                    try{
                        fixVersions.add(new Release(fixVersionsJSONArray.getJSONObject(h).getString("name"),Date.valueOf(fixVersionsJSONArray.getJSONObject(h).getString("releaseDate"))));
                    }catch (Exception e){
                        //no release date
                        System.out.println(fixVersionsJSONArray.getJSONObject(h).getString("name"));
                    }
                }
                if(!fixVersions.isEmpty())
                    issueList.add(new Issue(key, null, fixVersions));
            }
        } while (i < total);

        return issueList;

    }



    public  static List<Release> getReleases(String project) throws IOException {
        String url = "https://issues.apache.org/jira/rest/api/2/project/"+project+"/versions";
        List<Release> releaseList = new ArrayList<>();
        Release release;
        boolean already;                                         //initially empty list
        boolean released;
        JSONArray json = readJsonArrayFromUrl(url);
        int len = json.length();
        for(int count = 0; count<len; count++){
            already=false;
            JSONObject jsonObject = json.getJSONObject(count);
            released = jsonObject.getBoolean("released");
            if(released){
                try {
                    //check if there is already one release in the list with same date
                    for(Release release1: releaseList){
                        if(release1.getDate().compareTo(Date.valueOf(jsonObject.getString("releaseDate")))==0){
                            already = true;         //there is a release with same date
                            break;
                        }
                    }
                    if(!already)
                    {
                        release = new Release(jsonObject.getString("name"), Date.valueOf(jsonObject.getString("releaseDate")));
                        releaseList.add(release);
                    }
                }catch (Exception e){
                    //no released date
                }

            }
        }

        //sorting
        releaseList.sort(Comparator.comparing(o -> o.getDate().toString()));

        return releaseList;
    }

}
