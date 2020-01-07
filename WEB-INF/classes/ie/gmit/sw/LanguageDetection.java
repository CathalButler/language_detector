package ie.gmit.sw;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Class that handles making a language prediction. Requests are read in from the in queue in Service Handler class
 * and are then processed return a return to the out queue in the Service handler
 *
 * @author Cathal Butler
 * Reference: John Healy - Lecture of the module in GMIT. Online tutorial videos and lecture content.
 */

public class LanguageDetection implements Runnable, Resizeable {

    // === M e m b e r V a r i a b l e s ============================
    private int kmar;
    private DatabaseImpl database;
    private static final Logger LOGGER = Logger.getLogger(ServiceHandler.class.getName());
    private Map<Integer, Kmer> db = new TreeMap<>();
    private Map<Integer, Kmer> queryList = new TreeMap<>();
    private int kmarListSize = 300;

    /**
     * Constructor
     *
     * @param kmar     kmar size
     * @param database database instance
     */
    public LanguageDetection(int kmar, DatabaseImpl database) {
        this.kmar = kmar;
        this.database = database;
    }

    /**
     * Method to read in a string query which is then broken into kmars/n-grams then converted into hashcode and the
     * frequency updated in the list.
     *
     * @param query user query
     * @return language predication
     */
    public Language analysisQuery(String query) {
        //Variables
        int km;
        int frequency = 1;

        for (int i = 0; i <= query.length() - kmar; i++) {

            CharSequence charSequence = query.substring(i, i + kmar);
            km = charSequence.hashCode();

            //Increment the frequency
            if (queryList.containsKey(km)) {
                frequency += queryList.get(km).getFrequency();
            }//End if
            //Update the list
            queryList.put(km, new Kmer(km, frequency));
        }//End for loop

        //Resize the list
        resize(kmarListSize);

        //Return the language
        return database.getLanguage(db);
    }//End method

    @Override
    public void resize(int max) {
        Set<Integer> keys = queryList.keySet();

        for (Integer entiers : keys) {
            Map<Integer, Kmer> top = getTopOccurrence(max);
            db.put(entiers, top.get(entiers));

        }//End for loop
        System.out.println("Resize complete : " + queryList.size());
    }//End resize

    /**
     * Method to get the top given number of frequency occurring kmars in a language map.
     *
     * @param max size of the kmars in a list
     * @return frequency occurring list
     */
    public Map<Integer, Kmer> getTopOccurrence(int max) {
        ConcurrentHashMap<Integer, Kmer> temp = new ConcurrentHashMap<>();
        List<Kmer> les = new ArrayList<>(queryList.values());
        Collections.sort(les);

        int rank = 1;
        for (Kmer le : les) {
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
    }//End method

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("Assigning request from queue");
                //Run a return from the in queue
                Request languageDetection = ServiceHandler.inQueue.take();

                //Add to the out queue with job id and the language predication that came back from the language detection
                System.out.println("Result complete, adding to the complete queue");
                ServiceHandler.outQueue.put(languageDetection.getJobNumber(), analysisQuery(languageDetection.getQuery()));
            } catch (Exception exception) {
                exception.printStackTrace();
            }//End try catch
        }//end while loop
    }//end run method
}//End class
