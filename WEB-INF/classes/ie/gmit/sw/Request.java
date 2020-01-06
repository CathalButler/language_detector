package ie.gmit.sw;

import java.util.UUID;

public class Request {
    private UUID jobNumber;
    private String query;

    public Request(UUID jobNumber, String query) {
        this.jobNumber = jobNumber;
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public UUID getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(UUID jobNumber) {
        this.jobNumber = jobNumber;
    }

    @Override
    public String toString() {
        return "Request{" +
                "query='" + query + '\'' +
                ", jobNumber=" + jobNumber +
                '}';
    }
}//End class
