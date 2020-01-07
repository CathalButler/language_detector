package ie.gmit.sw;

import java.util.Map;

/**
 * Database interface to be implemented when creating a subject database out of the languages and kmars
 */

public interface Database {
    void add(CharSequence charSequence, Language language);
    Map<Integer, Kmer> getTopOccurrence(int max, Language language);
    Language getLanguage(Map<Integer, Kmer> query);
}//End interface
