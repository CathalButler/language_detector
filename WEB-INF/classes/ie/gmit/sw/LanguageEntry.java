package ie.gmit.sw;

public class LanguageEntry implements Comparable<LanguageEntry> {

    //Member Variables
    private int kmer;        // Not using strings, calling hashcode on string
    private int frequency;    // Frequency of occurrence
    private int rank;        // The Frequency of the kmer in the language

    //Constructor
    public LanguageEntry(int kmer, int frequency) {
        super();
        this.kmer = kmer;
        this.frequency = frequency;
    }//End Constructor

    public int getKmer() {
        return kmer;
    }

    public void setKmer(int kmer) {
        this.kmer = kmer;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public int compareTo(LanguageEntry next) {
        return -Integer.compare(frequency, next.getFrequency());
    }

    @Override
    public String toString() {
        return "[" + kmer + "/" + frequency + "/" + rank + "]";
    }
}//End class