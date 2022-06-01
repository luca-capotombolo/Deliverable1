package org.capotombolo.filesystem;

import org.capotombolo.metrics.Size;
import org.capotombolo.utils.MyFile;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Luca Capotombolo
 */

public class FoundAllJavaFiles {

    public final String localPath;
    public final File directory;
    public final int length;

    public FoundAllJavaFiles(String localPath) {
        this.localPath = localPath;
        this.length = localPath.length();
        this.directory = new File(localPath);
    }


    /**
     * @author Luca Capotombolo
     *
     * Ritorna la lista dei file e delle directory contenute
     * all'interno della directory passata in input.
     *
     * @param dir Directory in cui cercare i file e le directory.
     * @return La lista dei file e delle directory.
     */
    public File [] listFiles(File dir){

        File [] files = null;           //Lista dei files contenuti in dir

        if(!dir.isDirectory())
        {
            return new File[0];
        }

        //dir è una directory
        try {
            files = dir.listFiles();
        }catch (Exception e){
            e.printStackTrace();
        }


        return files;
    }

    /**
     * @author Luca Capotombolo
     *
     *
     *
     *
     *
     * @return La lista di tutti i file contenuti nella directory
     */

    public List<MyFile> foundAllFiles() throws IOException {

        File[] files;
        File dir;
        List<MyFile> filenames = new ArrayList<>();     //Lista dei filename corrispondenti ai file .java
        String filename;
        //Stack<File> stack = new Stack<>();              //Contiene le directory che devono essere ancora esplorate
        Deque<File> stack = new ArrayDeque<>();

        if(!this.directory.isDirectory()){
            return Collections.emptyList();
        }

        //this.directory è un file directory
        Size size;
        int lines;

        //tutti i file contenuti nella directory root
        files = this.listFiles(this.directory);

        for(File file: files){
            if(file.isDirectory()){
                stack.push(file);
            }else {
                filename = file.getAbsolutePath();
                if (filename.contains(".java")) {
                    size = new Size(filename);
                    lines = size.getLOC();
                    filenames.add(new MyFile(filename, MyFile.StateFile.NO_BUG, lines));
                }
            }
        }

        //while(!stack.empty()){      //finché c'è una directory che ancora non è stata esplorata
        while(!stack.isEmpty()){
            dir = stack.pop();
            files = this.listFiles(dir);
            if(dir.getName().equals("target") || dir.getName().equals("test") || files.length == 0) continue;
            for(File file: files){
                if(file.isDirectory()){
                    stack.push(file);
                }else {
                    filename = file.getAbsolutePath();
                    if (filename.contains(".java")) {
                        size = new Size(filename);
                        lines = size.getLOC();
                        filenames.add(new MyFile(filename, MyFile.StateFile.NO_BUG, lines));
                    }
                }
            }
        }

        return filenames;
    }


}
