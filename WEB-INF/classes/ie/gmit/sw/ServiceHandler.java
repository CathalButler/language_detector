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
 * To compile this servlet, open a command prompt in the web application directory and execute the following commands:
 *
 * Linux/Mac																	Windows
 * ---------																	---------
 * cd WEB-INF/classes/															cd WEB-INF\classes\
 * javac -cp .:/usr/share/tomcat9/lib/servlet-api.jar ie/gmit/sw/*.java		    javac -cp .:%TOMCAT_HOME%/lib/servlet-api.jar ie/gmit/sw/*.java
 * cd ../../																	cd ..\..\
 * jar -cf ngrams.war *															jar -cf ngrams.war *
 *
 * Drag and drop the file ngrams.war into the webapps directory of Tomcat to deploy the application. It will then be
 * accessible from http://localhost:8080.
 *
 * NOTE: the text file containing the 253 different languages needs to be placed in /data/wili-2018-Edited.txt. This means
 * that you must have a "data" directory in the root of your file system that contains a file called "wili-2018-Edited.txt".
 * Do NOT submit the wili-2018 text file with your assignment!
 *
 */

public class ServiceHandler extends HttpServlet {
    private String languageDataSet = null; //This variable  is shared by all HTTP requests for the servlet
    private static UUID jobNumber = null; //The number of the task in the async queue
    private File file;
    static BlockingQueue<Request> inQueue = new LinkedBlockingQueue<>();
    static Map<UUID, Language> outQueue = new ConcurrentHashMap<>();
    private int kmar = 1;
    private DatabaseImpl database = DatabaseImpl.getInstance();
    private static final Logger LOGGER = Logger.getLogger(ServiceHandler.class.getName());


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

        LOGGER.info("PARSER COMPLETE, DATABASE BUILT");

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
        String option = req.getParameter("cmbOptions"); //Change options to whatever you think adds value to your assignment...
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
                //Add job to in-queue
                if (query != null && query.length() > 0) {
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
                LOGGER.info("Language: " + outQueue.get(jobNumber));
                //Remove job from out-queue
                outQueue.remove(jobNumber);
            }//end if
        }//end if else


        out.print("<H1>Processing request for Job UUID: " + taskNumber + "</H1>");
        out.print("<div id=\"r\"></div>");

        out.print("<font color=\"#993333\"><b>");
        out.print("Language Dataset is located at " + languageDataSet + " and is <b><u>" + file.length() + "</u></b> bytes in size");
        out.print("<br>Option(query): " + option);
        out.print("<br>Query Text : " + query);
        out.print("</font><p/>");

        out.print("<br>This servlet should only be responsible for handling client request and returning responses. Everything else should be handled by different objects. ");
        out.print("Note that any variables declared inside this doGet() method are thread safe. Anything defined at a class level is shared between HTTP requests.");
        out.print("</b></font>");

        out.print("<P> Next Steps:");
        out.print("<OL>");
        out.print("<LI>Generate a big random number to use a a job number, or just increment a static long variable declared at a class level, e.g. jobNumber.");
        out.print("<LI>Create some type of an object from the request variables and jobNumber.");
        out.print("<LI>Add the message request object to a LinkedList or BlockingQueue (the IN-queue)");
        out.print("<LI>Return the jobNumber to the client web browser with a wait interval using <meta http-equiv=\"refresh\" content=\"10\">. The content=\"10\" will wait for 10s.");
        out.print("<LI>Have some process check the LinkedList or BlockingQueue for message requests.");
        out.print("<LI>Poll a message request from the front of the queue and pass the task to the language detection service.");
        out.print("<LI>Add the jobNumber as a key in a Map (the OUT-queue) and an initial value of null.");
        out.print("<LI>Return the result of the language detection system to the client next time a request for the jobNumber is received and the task has been complete (value is not null).");
        out.print("</OL>");

        out.print("<form method=\"POST\" name=\"frmRequestDetails\">");
        out.print("<input name=\"cmbOptions\" type=\"hidden\" value=\"" + option + "\">");
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