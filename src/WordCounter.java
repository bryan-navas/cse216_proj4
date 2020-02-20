import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class WordCounter {

    // The following are the ONLY variables we will modify for grading.
    // The rest of your code must run with no changes.
    public static final Path FOLDER_OF_TEXT_FILES  = Paths.get("src/FOLDER_OF_TEXT_FILES"); // path to the folder where input text files are located
    public static final Path WORD_COUNT_TABLE_FILE = Paths.get("src/WORD_COUNT_TABLE_FILE.txt"); // path to the output plain-text (.txt) file
    public static final int  NUMBER_OF_THREADS     = 8;                // max. number of threads to spawn

    public static void main(String... args) {
        try{    //this makes it so that all the console output will be printed to a file
            //PrintStream out = new PrintStream(new FileOutputStream(WORD_COUNT_TABLE_FILE.toString()));
            //System.setOut(out);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        long start = System.currentTimeMillis();



        Map<String, List<Integer>> allWords = new ConcurrentHashMap<>();        //the HashMap that will contain all the words and list of occurrences
        List<String> listOfFiles = getFilesFromDirectory();         //the list of files from the given folder
        List<Thread> threadList = new LinkedList<>();

        runWithThreads(allWords, listOfFiles, NUMBER_OF_THREADS);

        TreeMap<String, List<Integer>> sortedMap = new TreeMap<>(allWords);
        Set<Entry<String, List<Integer>>> sortedEntries = sortedMap.entrySet();

        long end = System.currentTimeMillis();

        System.out.print(Table.makeTable(sortedEntries, listOfFiles));
        System.out.println("\nTime: " + (end-start));



    }

    private static void runWithThreads(Map<String, List<Integer>> allWords, List<String> listOfFiles, int numThreads) {
        int currentFile = 0;

        if(numThreads <= 0 || numThreads ==1){     //Code runs with just the main thread
            for(String file : listOfFiles){
                readFileToMap(allWords, file, listOfFiles.size(), currentFile);
                currentFile++;
            }

        }
        else{
            if(numThreads >= listOfFiles.size()){
                numThreads = listOfFiles.size()-1;
            }

            List<ReadingThread> threads = new LinkedList<>();
            int threadIndex = 0;

            for(int threadsUsed = 1; threadsUsed < numThreads; threadsUsed++){
                for(String file : listOfFiles){
                    //readFileToMap(allWords, file, listOfFiles.size(), currentFile);
                    threads.add(new ReadingThread(allWords, file, listOfFiles.size(), currentFile));
                    threads.get(threadIndex).start();
                    threadIndex++;
                    currentFile++;
                }

             //join all the newly made threads
             for(Thread thr : threads){
                 try {
                     thr.join();
                 }
                 catch (Exception e){
                     e.printStackTrace();
                 }
             }

            }
        }



    }

    /*
    hashMap: the map that the words in this file will be placed into
    path: a string of the path for the current text file
    numFiles: how many files in the given directory
    thisFileNum: the current file inside the directory this method will work with. Example: the first file in the directory is file 0.

    This method takes a HashMap and places the occurrence of every word onto it. The key will be a unique String, while the value will
    hold an arrayList of Integers to indicate where that certain word appeared. Example: there are 3 files (text-a,text-b,text,c) therefore
    each value will be (int, int, int) with element 0 being word occurrence in text-a, element 1 being occurrence in text-b, and element 2 being
    occurrence in text-c.

     */
    public static void readFileToMap(Map<String, List<Integer>> hashMap, String path, int numFiles, int thisFileNum){
        try{
            FileReader file = new FileReader(path);
            BufferedReader textFile = new BufferedReader(file);

            String fileLine = "";
            String[] wordsInLine;
            List<Integer> occurences;
            List<Integer> temp;

            while((fileLine = textFile.readLine()) != null){
                wordsInLine = fileLine.split(" ");
                cleanLine(wordsInLine);
                //add them to map
                for(String word : wordsInLine){

                    if(word.length() > Table.maxWordLength){    //keeps track of the longest character
                        Table.setMaxWordLength(word.length());
                    }

                    if(!hashMap.containsKey(word)){
                        occurences = new ArrayList<>();     //creating a new value makes it so that one change wont affect every HashMap element
                        occurences = setZerosList(numFiles);
                        occurences.set(thisFileNum, 1);
                        hashMap.put(word, occurences);
                    }
                    else{
                        temp = hashMap.get(word);                               //this code makes a new list, and increments the word appearance by 1
                        temp.set(thisFileNum, temp.get(thisFileNum)+1);
                        hashMap.put(word, temp );
                    }
                }
            }
        }
        catch (Exception e){
            //e.printStackTrace();
            return;
        }
    }

    /*
    Returns an array filled with zeroes of size(numFiles)
     */
    public static ArrayList<Integer> setZerosList(int numFiles){
        ArrayList<Integer> zerosList = new ArrayList<>();
        for(int i = 0; i < numFiles; i++)
            zerosList.add(0);

        return zerosList;

    }

    /*
    Removes all the special characters from an array of Strings
     */
    public static void cleanLine(String[] wordsInLine){
        int i = 0;
        for(String word : wordsInLine){
            wordsInLine[i] = word.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]","");
            i++;
        }
    }

    /*
    Returns a list that contains all the file paths inside of the folder in the final variable
    FOLDER_OF_TEXT_FILES
     */
    public static List<String> getFilesFromDirectory(){
        File wordTextFolder = new File(FOLDER_OF_TEXT_FILES.toString());
        File[] filesInsideFolder = wordTextFolder.listFiles();
        List<String> paths = new ArrayList<>();
        String name;

        for (File txtFile : filesInsideFolder){
           paths.add( txtFile.getPath() );
            name = txtFile.getName();
            name = name.substring(0, name.lastIndexOf('.'));

            if(name.length() > Table.maxFileNameLength){
                Table.setMaxFileNameLength(name.length());
            }
        }
        return paths;
    }

}

class ReadingThread extends Thread{
    private Map hashMap;
    private String filePath;
    private int numFiles;
    private int currentFile;

    public ReadingThread(Map hashMap, String filePath, int numFiles, int currentFile){
    super();
    this.hashMap = hashMap;
    this.filePath =filePath;
    this.numFiles = numFiles;
    this.currentFile = currentFile;

    }

    public void run() {
        WordCounter.readFileToMap(hashMap, filePath, numFiles, currentFile);
    }
}


class Table{
    public static int maxWordLength = 0;
    public static int maxFileNameLength = 0;

    public static void setMaxWordLength(int newLength){
        maxWordLength = newLength;
    }

    public static void setMaxFileNameLength(int newLength){
        maxFileNameLength = newLength;
    }


    public static String makeTable(Set<Entry<String, List<Integer>>> sortedEntries, List<String> listOfFiles) {
        StringBuilder output = new StringBuilder();
        addTableHeader(listOfFiles, output);
        addTableBody(sortedEntries, output);
        return output.toString();
    }

    private static void addTableBody(Set<Entry<String, List<Integer>>> sortedEntries, StringBuilder output) {
        List<Integer> currentValues;
        int sum;
        for(Entry<String, List<Integer>> entry : sortedEntries){
            currentValues = entry.getValue();
            sum = currentValues.stream().mapToInt(Integer::intValue).sum();;
            output.append(String.format("%-"+ (maxWordLength+1) + "s",entry.getKey()));
            for(Integer occ : currentValues){
                output.append(String.format("%-" + (maxFileNameLength+5) +"d", occ));
            }
            output.append(String.format("%-5d", sum));
            output.append("\n");
        }
    }

    private static void addTableHeader(List<String> listOfFiles, StringBuilder output) {
        File file;
        String fileName;
        output.append(String.format("%-" + (maxWordLength+1)+ "s"," "));
        for(String path : listOfFiles){
            file = new File(path);
            fileName = file.getName();
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            output.append(String.format("%-" + (maxFileNameLength+5) +"s", fileName));
        }
        output.append("total");
        output.append("\n");
    }
}