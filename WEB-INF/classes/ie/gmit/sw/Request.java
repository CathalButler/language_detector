package ie.gmit.sw;

public class Request {
    private int jobNumber;
    private String query;

    public Request(int jobNumber, String query) {
        this.jobNumber = jobNumber;
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(int jobNumber) {
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
