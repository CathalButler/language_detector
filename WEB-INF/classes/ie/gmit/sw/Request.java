package ie.gmit.sw;

import java.util.UUID;

/**
 * Request object class. Requests are added by the Service Handler class when client sends
 * a request and the Language Detection class handles processing and removing the request.
 *
 * @author Cathal Butler
 * Referance: John Healy - Lecture of the module in GMIT. Online tutorial videos and lecture content.
 */


public class Request {
    // === M e m b e r V a r i a b l e s ============================
    private UUID jobNumber;
    private String query;

    //Constructor
    public Request(UUID jobNumber, String query) {
        this.jobNumber = jobNumber;
        this.query = query;
    }

    /**
     * method to get query string
     *
     * @return query
     */
    public String getQuery() {
        return query;
    }

    /**
     * method to get job number
     *
     * @return jobNumber
     */
    public UUID getJobNumber() {
        return jobNumber;
    }

    @Override
    public String toString() {
        return "Request{" +
                "query='" + query + '\'' +
                ", jobNumber=" + jobNumber +
                '}';
    }//End toString
}//End class
