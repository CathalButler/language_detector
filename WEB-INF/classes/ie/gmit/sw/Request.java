package ie.gmit.sw;

import java.util.UUID;

public class Request {
    private String query;
    private long jobNumber;

    public Request(String query, long jobNumber) {
        this.query = query;
        this.jobNumber = jobNumber;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public long getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(long jobNumber) {
        this.jobNumber = jobNumber;
    }
}//End class
