package org.capotombolo.metrics;

import java.io.*;

public class Size {

    public final String path;

    public Size(String path){
        this.path = path;
    }

    public int getLOC() throws IOException {
        int lines = 0;

        FileInputStream fileInputStream = new FileInputStream(this.path);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        while(reader.readLine() !=null){
            lines++;
        }
        reader.close();
        return lines;
    }

}
