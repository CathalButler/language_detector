package ie.gmit.sw;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

public class LanguageDetection implements Runnable {

    //Member Variables
    private int kmar;
    private DatabaseImpl database;
    private static final Logger LOGGER = Logger.getLogger(ServiceHandler.class.getName());

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

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("Assigning job from queue...");

                Request languageDetection = ServiceHandler.inQueue.take();
                LOGGER.info("fuck you from the language detector");

                //Add to the out queue with job id and the language predication that came back from the language detection
                ServiceHandler.outQueue.put(languageDetection.getJobNumber(), analysisQuery(languageDetection.getQuery()));
                System.out.println(ServiceHandler.outQueue.toString());
            } catch (Exception exception) {
                exception.printStackTrace();
            }//End try catch
        }//end while loop
    }//end run method


    public Language analysisQuery(String query) {
        //Variables
        int k = 1;
        int km;
        int frequency = 1;
        Map<Integer, LanguageEntry> queryList = new TreeMap<>();

        for (int i = 0; i <= query.length() - k; i++) {
            CharSequence charSequence = query.substring(i, i + k);
            km = charSequence.hashCode();

            //Increment the frequency
            if (queryList.containsKey(km)) {
                frequency += queryList.get(km).getFrequency();
            }//End if

            queryList.put(km, new LanguageEntry(km, frequency));
        }//End for loop


//        //For printing out Map content
//        for (Map.Entry<Integer, LanguageEntry> entry : queryList.entrySet()) {
//            Integer key = entry.getKey();
//            LanguageEntry value = entry.getValue();
//            System.out.printf("%s : %s\n", key, value);
//        }//End for
//        //PRINT LANGUAGE
//        System.out.println(db.getLanguage(queryDb));

        return database.getLanguage(queryList);
    }//End method
}//End class
