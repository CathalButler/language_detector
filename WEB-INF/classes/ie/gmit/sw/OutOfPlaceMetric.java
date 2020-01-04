package ie.gmit.sw;

/**
 *
 */
class OutOfPlaceMetric implements Comparable<OutOfPlaceMetric> {
    //Member Variables
    private Language lang;
    private int distance;

    public OutOfPlaceMetric(Language lang, int distance) {
        super();
        this.lang = lang;
        this.distance = distance;
    }

    public Language getLanguage() {
        return lang;
    }

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
