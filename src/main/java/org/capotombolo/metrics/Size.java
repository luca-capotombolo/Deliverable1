package org.capotombolo.metrics;

import org.capotombolo.weka.WalkForward;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Size {

    public final String path;

    public Size(String path){
        this.path = path;
    }

    public int getLOC() throws IOException {
        int lines = 0;
        Logger logger = Logger.getLogger(WalkForward.class.getName());
        String row;
        FileInputStream fileInputStream = new FileInputStream(this.path);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        while((row = reader.readLine()) !=null){
            logger.log(Level.INFO, row);
            lines++;
        }
        reader.close();
        return lines;
    }

}
