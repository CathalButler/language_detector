package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Parser implements Runnable {

    //Member Variables
    private Database db = null;
    private String file;
    private int kmar;
    private Map<Integer, LanguageEntry> queryDb = new TreeMap<>();

    /**
     * Constructor
     *
     * @param file
     * @param kmar
     */
    public Parser(String file, int kmar) {
        this.file = file;
        this.kmar = kmar;
    }//End Constructor

    /**
     * Method which is used to set a database.
     *
     * @param database
     */
    public void setDb(Database database) {
        this.db = database;
    }//End

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;

            while ((line = br.readLine()) != null) {
                String[] record = line.trim().split("@");
                if (record.length != 2) continue;
                parser(record[0], record[1]);
            }//End while
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }//End try catch
    }//End run method

    /**
     * Method which splits a string of text into a set Char Sequence.
     *
     * @param text
     * @param lang
     */
    public void parser(String text, String lang) {
        Language language = Language.valueOf(lang);

        for (int i = 0; i <= text.length() - kmar; i++) {
            CharSequence kmer = text.substring(i, i + kmar);
            db.add(kmer, language);
        }//End for loop
    }//End parser method

    /**
     * Method to read in user input and make a char sequence of it
     *
     * @param input
     */
    public void analysisQuery(String input) {
        //Variables
        int k = 1;

        for (int i = 0; i <= input.length() - k; i++) {
            CharSequence kmer = input.substring(i, i + k);
            System.out.println(kmer);
            add(kmer);
        }//End for loop

        //Start this class up to process query
        resize(400);

        //For printing out Map content
        for (Map.Entry<Integer, LanguageEntry> entry : queryDb.entrySet()) {
            Integer key = entry.getKey();
            LanguageEntry value = entry.getValue();
            System.out.printf("%s : %s\n", key, value);
        }//End for
        //PRINT LANGUAGE
        System.out.println(db.getLanguage(queryDb));
    }//End method


    public void add(CharSequence s) {
        //Variables
        int kmer = s.hashCode();
        int frequency = 1;
        Map<Integer, LanguageEntry> inputDb = getEntries(kmer);

        //Increment the frequency
        if (inputDb.containsKey(kmer)) {
            frequency += inputDb.get(kmer).getFrequency();
        }//End if
        //Override the existing entry with the newly updated one
        inputDb.put(kmer, new LanguageEntry(kmer, frequency));
    }//End method


    private Map<Integer, LanguageEntry> getEntries(Integer kmar) {
        //Variables
        Map<Integer, LanguageEntry> tempDb;
        if (queryDb.containsKey(kmar)) {
            tempDb = queryDb;
        } else {
            tempDb = new TreeMap<>();
            LanguageEntry languageEntry = new LanguageEntry(kmar, 1);
            // Add new entry to queryMapDb
            queryDb.put(kmar, languageEntry);
        }//End if else
        return tempDb;
    }//End method

    //TODO: REMEMBER YOU DID A THING THAT MADE A THING WORK HERE

    /**
     * Method which is used to resize the amount of
     * @param max
     */
    public void resize(int max) {
        Set<Integer> keys = queryDb.keySet();
        for (Integer entries : keys) {
            Map<Integer, LanguageEntry> top = getTopOccurrence(max);
            // add entry to list with newly updated rank from getTop method
            queryDb.put(entries, top.get(entries));
        }//End for loop
    }//End method


    public Map<Integer, LanguageEntry> getTopOccurrence(int max) {
        ConcurrentHashMap<Integer, LanguageEntry> temp = new ConcurrentHashMap<>();
        List<LanguageEntry> les = new ArrayList<>(queryDb.values());
        Collections.sort(les);

        int rank = 1;
        for (LanguageEntry le : les) {
            // Assign rank, the tree map is ordered so the first entry will be the most fragrant
            le.setRank(rank);
            //Add it to the temp map
            temp.put(le.getKmer(), le);
            //Break at whatever the max value is
            if (rank == max) break;
            //else increment rank
            rank++;
        }//End for loop
        return temp;
    }//End class
}//End class
