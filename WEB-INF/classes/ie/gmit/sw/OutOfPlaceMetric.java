package ie.gmit.sw;

/**
 * The sum of all the Out-of-Place values for all n-grams is
 * the distance measure of the document for that language
 */
public class OutOfPlaceMetric implements Comparable<OutOfPlaceMetric> {
    // === M e m b e r V a r i a b l e s ============================
    private Language lang;
    private int distance;

    //Contractor
    public OutOfPlaceMetric(Language lang, int distance) {
        super();
        this.lang = lang;
        this.distance = distance;
    }

    /**
     * get language method
     * @return language
     */
    public Language getLanguage() {
        return lang;
    }

    /**
     * get absolute distance method
     * @return distance
     */
    public int getAbsoluteDistance() {
        return Math.abs(distance);
    }

    @Override
    public int compareTo(OutOfPlaceMetric o) {
        return Integer.compare(this.getAbsoluteDistance(), o.getAbsoluteDistance());
    }

    @Override
    public String toString() {
        return "[lang=" + lang + ", distance=" + getAbsoluteDistance() + "]";
    }
}//End method
