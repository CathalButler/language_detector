package ie.gmit.sw;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    //Member Variables
    private Map<Language, Map<Integer, LanguageEntry>> db = new TreeMap<>();

    /**
     * Method to add or update the frequency of a kmer in the map
     * @param s
     * @param lang
     */
    public void add(CharSequence s, Language lang) {
        Map<Integer, LanguageEntry> langDb = getLanguageEntries(lang);
        int kmer = s.hashCode();
        int frequency = 1;

        //Increment the frequency
        if (langDb.containsKey(kmer)) {
            frequency += langDb.get(kmer).getFrequency();
        }//End if
        //Override the existing entry with the newly updated one
        langDb.put(kmer, new LanguageEntry(kmer, frequency));

    }//End method

    /**
     * @param lang
     * @return language database
     */
    private Map<Integer, LanguageEntry> getLanguageEntries(Language lang) {
        Map<Integer, LanguageEntry> langDb = null;
        if (db.containsKey(lang)) {
            langDb = db.get(lang);
        } else {
            langDb = new TreeMap<Integer, LanguageEntry>();
            db.put(lang, langDb);
        }
        return langDb;
    }//End method

    /**
     * Method to resize the top most frequent kmars in a language map.
     * @param max
     */
    public void resize(int max) {
        Set<Language> keys = db.keySet();

        for (Language lang : keys) {
            Map<Integer, LanguageEntry> top = getTop(max, lang);
            db.put(lang, top);
        }//End for loop
    }//End method

    /**
     * Method to get the top given number of frequency occurring kmars in a language map.
     * @param max
     * @param lang
     * @return frequency occurring list
     */
    public Map<Integer, LanguageEntry> getTop(int max, Language lang) {
        //TODO: Look into concurrent hash map OR concurrent skip lists as we will be using threads
        ConcurrentHashMap<Integer, LanguageEntry> temp = new ConcurrentHashMap<>();
        List<LanguageEntry> les = new ArrayList<>(db.get(lang).values());
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
    }//End method

    /**
     * @param query
     * @return
     */
    public Language getLanguage(Map<Integer, LanguageEntry> query) {
        TreeSet<OutOfPlaceMetric> oopm = new TreeSet<>();

        Set<Language> langs = db.keySet();
        for (Language lang : langs) {
            oopm.add(new OutOfPlaceMetric(lang, getOutOfPlaceDistance(query, db.get(lang))));
        }
        return oopm.first().getLanguage();
    }//End method

    /**
     * @param query
     * @param subject
     * @return
     */
    private int getOutOfPlaceDistance(Map<Integer, LanguageEntry> query, Map<Integer, LanguageEntry> subject) {
        //Member Variables
        int distance = 0;
        Set<LanguageEntry> les = new TreeSet<>(query.values());

        for (LanguageEntry q : les) {
            LanguageEntry s = subject.get(q.getKmer());
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

            Collection<LanguageEntry> m = new TreeSet<>(db.get(lang).values());
            kmerCount += m.size();
            for (LanguageEntry le : m) {
                sb.append("\t" + le + "\n");
            }
        }
        sb.append(kmerCount + " total k-mers in " + langCount + " languages");
        return sb.toString();
    }//End method
}//End class