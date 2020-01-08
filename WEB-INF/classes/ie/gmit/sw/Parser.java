package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * Class that handles parsing the wili-2018 language file. The file is parsed breaking the language sentence up into
 * k-mars/n-grams and adding them and the language name to a subject database/list
 * @author Cathal Butler
 * Reference: John Healy - Lecture of the module in GMIT. Online tutorial videos and lecture content.
 */


public class Parser implements Runnable {

    // === M e m b e r V a r i a b l e s ============================
    private DatabaseImpl db;
    private String file;
    private int kmar;
    private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());

    /**
     * Constructor
     *
     * @param file     this file is used to build subject database
     * @param kmar     n-gram
     * @param database instance
     */
    public Parser(String file, int kmar, DatabaseImpl database) {
        this.file = file;
        this.kmar = kmar;
        this.db = database;
    }//End Constructor

    /**
     * Method which splits a string of text into a set Char Sequences.
     *
     * @param text string which will be made into a Char Sequence to make k-mars
     * @param lang name of the language
     */
    public void parser(String text, String lang) {
        Language language = Language.valueOf(lang);

        for (int i = 0; i <= text.length() - kmar; i++) {
            CharSequence kmer = text.substring(i, i + kmar);
            db.add(kmer, language);
        }//End for loop
    }//End parser method

    @Override
    public void run() {
        try {
            LOGGER.info(file);
            //Load file
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            // Read file
            while ((line = br.readLine()) != null) {
                // Trim once a `@` as been hit, this is the delimiter for where the language name is
                String[] record = line.trim().split("@");
                if (record.length != 2) continue;
                parser(record[0], record[1]);
            }//End while
            br.close();
            LOGGER.info("PARSER COMPLETE, DATABASE BUILT");
        } catch (Exception e) {
            e.printStackTrace();
        }//End try catch
    }//End override run method
}//End class
