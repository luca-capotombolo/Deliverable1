package org.capotombolo.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.capotombolo.jira.Jira.readJsonFromUrl;

public class Commit {
    public final String sha;
    public final Date date;
    public final String comment;
    public final List<String> files;
    public Release release;

    public Commit(Date date, String comment, List<String> files, Release release, String sha) {
        this.date = date;
        this.comment = comment;
        this.files = files;
        this.release = release;
        this.sha = sha;
    }
}
