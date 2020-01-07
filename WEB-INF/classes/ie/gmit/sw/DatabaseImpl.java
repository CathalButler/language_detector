package ie.gmit.sw;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class that handles parsing the wili-2018 language file. The file is parsed breaking the language sentence up into
 * kmars/n-grams and adding them and the language name to a subject database/list
 * Reference: John Healy - Lecture of the module in GMIT. Online tutorial videos and lecture content.
 */

//https://www.java67.com/2015/09/thread-safe-singleton-in-java-using-double-checked-locking-pattern.html

public class DatabaseImpl implements Database, Resizeable {
    // === M e m b e r V a r i a b l e s ============================
    private volatile static DatabaseImpl database;
    public Map<Language, Map<Integer, Kmer>> db = new ConcurrentHashMap<>();

    /**
     * This is used to get an instance of this class when called.
     * Implementation of Singleton design pattern
     *
     * @return database
     */
    public static DatabaseImpl getInstance() {
        if (database == null) {
            synchronized (DatabaseImpl.class) {
                if (database == null) {
                    return database = new DatabaseImpl();
                }
            }
        }//end if
        return database;
    }//End method

    /**
     * Method to add or update the frequency of a kmer in the map
     *
     * @param charSequence a sequence of chars
     * @param language     object
     */
    @Override
    public void add(CharSequence charSequence, Language language) {
        Map<Integer, Kmer> langDb = getLanguageEntries(language);
        int kmer = charSequence.hashCode();
        int frequency = 1;

        //Increment the frequency
        if (langDb.containsKey(kmer)) {
            frequency += langDb.get(kmer).getFrequency();
        }//End if
        //Override the existing entry with the newly updated one
        langDb.put(kmer, new Kmer(kmer, frequency));

    }//End method

    /**
     * Method which returns a list of all language entries
     *
     * @param language object
     * @return language database
     */
    private Map<Integer, Kmer> getLanguageEntries(Language language) {
        Map<Integer, Kmer> langDb;
        if (db.containsKey(language)) {
            langDb = db.get(language);
        } else {
            langDb = new TreeMap<>();
            db.put(language, langDb);
        }//End if else
        return langDb;
    }//End method

    /**
     * Method to resize the amount of top most frequent kmars in a language map.
     *
     * @param max size of the kmars in a list
     */
    @Override
    public void resize(int max) {
        Set<Language> keys = db.keySet();

        for (Language lang : keys) {
            Map<Integer, Kmer> top = getTopOccurrence(max, lang);
            db.put(lang, top);

        }//End for loop
        System.out.println("Resize complete : " + db.size());
    }//End method

    /**
     * Method to get the top given number of frequency occurring kmars in a language map.
     *
     * @param max      size of the kmars in a list
     * @param language object
     * @return frequency occurring list
     */
    @Override
    public Map<Integer, Kmer> getTopOccurrence(int max, Language language) {
        ConcurrentHashMap<Integer, Kmer> temp = new ConcurrentHashMap<>();
        List<Kmer> les = new ArrayList<>(db.get(language).values());
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

    /**
     * Method which will return the first language in the list
     *
     * @param query user query entry
     * @return first language in the Out of place metric list
     */
    public Language getLanguage(Map<Integer, Kmer> query) {
        TreeSet<OutOfPlaceMetric> oopm = new TreeSet<>();

        Set<Language> langs = db.keySet();
        for (Language lang : langs) {
            oopm.add(new OutOfPlaceMetric(lang, getOutOfPlaceDistance(query, db.get(lang))));
        }
        return oopm.first().getLanguage();
    }//End method

    /**
     * Method which calculates Out-of-Place Measure as a distance metric.
     *
     * @param query   user query entry
     * @param subject language
     * @return distance
     */
    private int getOutOfPlaceDistance(Map<Integer, Kmer> query, Map<Integer, Kmer> subject) {
        //Member Variables
        int distance = 0;
        Set<Kmer> les = new TreeSet<>(query.values());

        for (Kmer q : les) {
            Kmer s = subject.get(q.getKmer());
            if (s == null) {
                distance += subject.size() + 1;
            } else {
                distance += s.getRank() - q.getRank();
            }
        }//End for loop
        return distance;
    }//End method

    @Override
    public String toString() {

        //Member Variables
        StringBuilder sb = new StringBuilder();
        int langCount = 0;
        int kmerCount = 0;
        Set<Language> keys = db.keySet();


        for (Language lang : keys) {
            langCount++;
            sb.append(lang.name() + "->\n");

            Collection<Kmer> m = new TreeSet<>(db.get(lang).values());
            kmerCount += m.size();
            for (Kmer le : m) {
                sb.append("\t" + le + "\n");
            }//End for loop
        }//End for loop

        sb.append(kmerCount + " total k-mers in " + langCount + " languages");
        return sb.toString();
    }//End method
}//End class