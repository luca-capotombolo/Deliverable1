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

    private static final String  MACRO = "fields";
    private static final String MACRO1 = "releaseDate";

    private Jira(){
        throw new IllegalStateException("Utility class");
    }
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

    public  static List<Issue> getBugs(String project, List<Release> releaseList) throws IOException {
        int j;
        int i = 0;
        int total;
        List<Issue> issueList = new ArrayList<>();
        String key;
        List<Release> affectedVersions;
        JSONArray issues;
        JSONArray affectedVersionsJSONArray;
        JSONObject json;
        Date createdIssue;
        Date resolutionDate;
        Release ovRelease;
        Release fixVersion;
        Release ivVersion;
        String resolutionDateString;
        String ovString;
        boolean released;

        do {
            //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
            j = i + 1000;
            String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                    + project + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
                    + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,fixVersions,versions,created&startAt="
                    + i + "&maxResults=" + j;
            json = readJsonFromUrl(url);
            issues = json.getJSONArray("issues");
            total = json.getInt("total");                       //Number of Issues
            Issue issue;
            for (; i < total && i < j; i++) {
                //Get new Issue
                //Opening version
                ovRelease=null;
                //Fix version on JIRA
                fixVersion = null;
                ivVersion = null;
                //Affected version on JIRA
                affectedVersions = new ArrayList<>();
                key = issues.getJSONObject(i%1000).get("key").toString();
                resolutionDateString = issues.getJSONObject(i % 1000).getJSONObject(MACRO).getString("resolutiondate");
                resolutionDateString = resolutionDateString.substring(0,10);
                resolutionDate = Date.valueOf(resolutionDateString);
                for(Release release: releaseList){
                    if(release.getDate().compareTo(resolutionDate) > 0){
                        fixVersion = release;
                        break;
                    }
                }
                if(fixVersion!=null){
                    //create issue object
                    //Opening version
                    ovString = issues.getJSONObject(i % 1000).getJSONObject(MACRO).getString("created");
                    ovString = ovString.substring(0, 10);                                                                    //get date string
                    createdIssue = Date.valueOf(ovString);                                                                       //created date of issue
                    for (Release release : releaseList) {
                        if (release.getDate().compareTo(createdIssue) > 0) {
                            ovRelease = release;
                            break;
                        }
                    }
                    if(ovRelease==null)
                        continue;                               //There is not Opening version
                    //Affected version
                    affectedVersionsJSONArray = issues.getJSONObject(i % 1000).getJSONObject(MACRO).getJSONArray("versions");
                    for (int u = 0; u < affectedVersionsJSONArray.length(); u++) {
                        released = affectedVersionsJSONArray.getJSONObject(u).getBoolean("released");
                        if (released) {
                            try {
                                affectedVersions.add(new Release(affectedVersionsJSONArray.getJSONObject(u).getString("name"),
                                        Date.valueOf(affectedVersionsJSONArray.getJSONObject(u).getString(MACRO1))));
                            } catch (Exception e) {
                                //no releaseDate
                            }
                        }
                    }

                    //Get IV of issue if AV is consistent
                    if(!affectedVersions.isEmpty()){
                        Release olderRelease = affectedVersions.get(0);
                        for(Release release: affectedVersions){
                            if(olderRelease.getDate().compareTo(release.getDate())>0){
                                olderRelease = release;
                            }
                        }
                        for(Release release: releaseList){
                            if(release.getDate().compareTo(olderRelease.getDate())==0){
                                ivVersion = release;
                                break;
                            }
                        }
                    }

                    issue = new Issue(key, ivVersion, fixVersion, ovRelease, affectedVersions, resolutionDate);

                    //OV and FV are on JIRA I can not calculate them
                    if(issue.ov.getDate().compareTo(issue.fv.getDate())>=0){
                        //OV >= FV
                        continue;
                    }


                    //Exclude defects that are not post-release
                    if(issue.iv!=null && issue.iv.getDate().compareTo(issue.fv.getDate())==0
                            && issue.iv.getDate().compareTo(issue.ov.getDate())==0)
                    {
                        continue;
                    }

                    issueList.add(issue);
                }
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
            already=false;                                       //I suppose that release is not in list
            JSONObject jsonObject = json.getJSONObject(count);
            released = jsonObject.getBoolean("released");
            if(released){
                try {
                    //check if there is already one release in the list with same date
                    for(Release release1: releaseList){
                        if(release1.getDate().compareTo(Date.valueOf(jsonObject.getString(MACRO1)))==0){
                            already = true;         //there is a release with same date
                            break;
                        }
                    }
                    if(!already)
                    {
                        release = new Release(jsonObject.getString("name"), Date.valueOf(jsonObject.getString(MACRO1)));
                        releaseList.add(release);
                    }
                }catch (Exception e){
                    //no released date
                }

            }
        }

        //sorting
        releaseList.sort(Comparator.comparing(o -> o.getDate().toString()));

        //Set index of release
        int count = 0;
        for(Release release1: releaseList){
            release1.index = count;
            count ++;
        }

        return releaseList;
    }

}
