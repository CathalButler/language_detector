package ie.gmit.sw;


/**
 * Kmer object class. This class is for the creation for kmer objects with hold the kmer which is a hashcode of a string,
 * frequency of occurrence and the rank in a given language.
 *
 * @author Cathal Butler
 * Referance: John Healy - Lecture of the module in GMIT. Online tutorial videos and lecture content.
 */


public class Kmer implements Comparable<Kmer> {

    // === M e m b e r V a r i a b l e s ============================
    private int kmer;        // Not using strings, calling hashcode on string
    private int frequency;    // Frequency of occurrence
    private int rank;        // The Frequency of the k-mer in the language

    //Constructor
    public Kmer(int kmer, int frequency) {
        super();
        this.kmer = kmer;
        this.frequency = frequency;
    }//End Constructor

    /**
     * method to get kmer/n-grams
     *
     * @return kmer
     */
    public int getKmer() {
        return kmer;
    }

    /**
     * method to set kmer/n-gram
     *
     * @param kmer
     */
    public void setKmer(int kmer) {
        this.kmer = kmer;
    }

    /**
     * method to get frequency
     *
     * @return frequency
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * method to set frequency
     *
     * @param frequency
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * method to get rank
     *
     * @return rank
     */
    public int getRank() {
        return rank;
    }

    /**
     * method to set rank
     *
     * @param rank
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public int compareTo(Kmer next) {
        return -Integer.compare(frequency, next.getFrequency());
    }

    @Override
    public String toString() {
        return "[" + kmer + "/" + frequency + "/" + rank + "]";
    }
}//End class