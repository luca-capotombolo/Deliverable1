package org.capotombolo.filesystem;

import org.capotombolo.utils.MyFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Luca Capotombolo
 */

public class FoundAllJavaFiles {

    public static void main(String[] args) {
        FoundAllJavaFiles foundAllJavaFiles = new FoundAllJavaFiles("C:\\Users\\lucac\\maven\\Zookkeeper\\zookeeper");
        System.out.println("File contenuti nella cartella: " + foundAllJavaFiles.localPath);
        foundAllJavaFiles.foundAllFiles();

    }

    public final String localPath;
    public final File directory;

    public FoundAllJavaFiles(String localPath) {
        this.localPath = localPath;
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
            System.out.println("Il parametro passato non è una directory...");
            return null;
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

    public List<MyFile> foundAllFiles(){

        File[] files;
        File dir;
        List<MyFile> filenames = new ArrayList<>();     //Lista dei filename corrispondenti ai file .java
        String filename;
        Stack<File> stack = new Stack<>();              //Contiene le directory che devono essere ancora esplorate

        if(!this.directory.isDirectory()){
            System.out.println("Il file passato non è una directory...");
            return null;
        }

        //this.directory è un file directory

        //tutti i file contenuti nella directory root
        files = this.listFiles(this.directory);

        for(File file: files){
            if(file.isDirectory()){
                stack.push(file);
            }else {
                filename = file.getAbsolutePath();
                if (filename.contains(".java")) {
                    //System.out.println(filename);
                    filenames.add(new MyFile(filename, MyFile.StateFile.NO_BUG));
                }
            }
        }

        /**
         * Ho inserito tutti i file .java che si trovavano
         * all'interno della root Directory
         * Adesso vado ad esaminamire i file contenuti
         * all'interno delle Directory che si trovano
         * nella root directory
         */
        while(!stack.empty()){      //finché c'è una directory che ancora non è stata esplorata
            dir = stack.pop();
            //System.out.println("Dir parent: " +dir.getName());
            if(dir.getName().equals("target")) continue;
            files = this.listFiles(dir);
            if (files.length == 0) continue;
            for(File file: files){
                if(file.isDirectory()){
                    stack.push(file);
                }else {
                    filename = file.getAbsolutePath();
                    if (filename.contains(".java")) {
                        filenames.add(new MyFile(filename, MyFile.StateFile.NO_BUG));
                    }
                }
            }
        }

        /*
        if(!filenames.isEmpty()){
            System.out.println("Numero di file .java trovati: " + filenames.size());
            for(String name: filenames){
                System.out.println(name);
            }
        }*/

        return filenames;
    }


}
