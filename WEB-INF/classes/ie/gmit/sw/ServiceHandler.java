package ie.gmit.sw;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;


/*
 * Service handler class which handles request made on the ngram application when served.
 */

public class ServiceHandler extends HttpServlet {
    // === M e m b e r V a r i a b l e s ============================
    private String languageDataSet = null; //This variable  is shared by all HTTP requests for the servlet
    private static UUID jobNumber = null; //The number of the task in the async queue
    private File file;
    static BlockingQueue<Request> inQueue = new LinkedBlockingQueue<>(); //In Queue returns from client
    static Map<UUID, Language> outQueue = new ConcurrentHashMap<>(); // Out Queue, completed returns with results
    private DatabaseImpl database = DatabaseImpl.getInstance(); //Instance of database class to gain access to the db
    private static final Logger LOGGER = Logger.getLogger(ServiceHandler.class.getName());
    private int kmar;

    /**
     * Method which is used to init() the servlet
     */
    public void init() {
        ServletContext ctx = getServletContext(); //Get a handle on the application context
        languageDataSet = ctx.getInitParameter("LANGUAGE_DATA_SET"); //Reads the value from the <context-param> in web.xml

        //You can start to build the subject database at this point. The init() method is only ever called once during the life cycle of a servlet
        file = new File(languageDataSet);

        //Start parser on a thread
        Thread thread = new Thread(new Parser(file.toString(), kmar, database));
        thread.start();

        // Wait on parser to complete it process before processing the next step
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Resize the database to the top 300 n-grams
        int databaseNgramSize = 300;
        database.resize(databaseNgramSize);
    }//End init()

    /**
     * Method for GET Requests
     *
     * @param req  HTTP Request
     * @param resp HTTP Response
     * @throws IOException
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOGGER.info("GET REQUEST");
        resp.setContentType("text/html"); //Output the MIME type
        PrintWriter out = resp.getWriter(); //Write out text. We can write out binary too and change the MIME type...

        //Initialise some request valuables with the submitted form info. These are local to this method and thread safe...
        kmar = Integer.parseInt(req.getParameter("cmbOptions")); //Change options to whatever you think adds value to your assignment...
        String query = req.getParameter("query");
        String taskNumber = req.getParameter("frmTaskNumber");

        LOGGER.info("TASK NUMBER " + taskNumber);


        out.print("<html><head><title>Advanced Object Oriented Software Development Assignment</title>");
        out.print("</head>");
        out.print("<body>");


        if (taskNumber == null) {
            //Generate a UUID for each job/task
            jobNumber = UUID.randomUUID();
            //Assign a new job to the list
            taskNumber = new String("T" + jobNumber.toString());

            System.out.println(jobNumber);

            try {
                if (query != null && query.length() > 0) {
                    //Add job to in-queue
                    inQueue.add(new Request(jobNumber, query));
                    //Start a thread to run the languageDetection class
                    new Thread(new LanguageDetection(kmar, database)).start();
                } else {
                    out.print("<h3>Please enter a query in the input box.</h3>");
                }//End if else
            } catch (Exception ie) {
                ie.printStackTrace();
            }//End try catch

            //Make a class which will be able to return if the process is complete, try have that class run threads
        } else {
            //Check out-queue for finished job
            if (outQueue.containsKey(jobNumber)) {
                out.print("Language: " + outQueue.get(jobNumber));
                //Log result
                LOGGER.info("Language: " + outQueue.get(jobNumber));
                //Remove job from out-queue
                outQueue.remove(jobNumber);
            }//end if
        }//end if else


        out.print("<H1>Processing request for Job UUID: " + taskNumber + "</H1>");
        out.print("<div id=\"r\"></div>");

        out.print("<font color=\"#993333\"><b>");
        out.print("Language Dataset is located at " + languageDataSet + " and is <b><u>" + file.length() + "</u></b> bytes in size");
        out.print("<br>K-mar amount: " + kmar);
        out.print("<br>Query Text : " + query);
        out.print("</font><p/>");

        out.print("<br>This servlet should only be responsible for handling client request and returning responses. Everything else should be handled by different objects. ");
        out.print("Note that any variables declared inside this doGet() method are thread safe. Anything defined at a class level is shared between HTTP requests.");
        out.print("</b></font>");

        out.print("<form method=\"POST\" name=\"frmRequestDetails\">");
        out.print("<input name=\"cmbOptions\" type=\"hidden\" value=\"" + kmar + "\">");
        out.print("<input name=\"query\" type=\"hidden\" value=\"" + query + "\">");
        out.print("<input name=\"frmTaskNumber\" type=\"hidden\" value=\"" + taskNumber + "\">");
        out.print("</form>");
        out.print("</body>");
        out.print("</html>");

        out.print("<script>");
        out.print("var wait=setTimeout(\"document.frmRequestDetails.submit();\", 10000);");
        out.print("</script>");
    }//End method

    /**
     * Method for POST Request
     *
     * @param req  HTTP Request
     * @param resp HTTP Response
     * @throws IOException
     */
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }//End method
}//End class