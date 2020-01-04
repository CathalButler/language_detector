package ie.gmit.sw;

public class Runner {

    public static void main(String[] args) throws InterruptedException {
        Parser p = new Parser("wili-2018-Small-11750-Edited.txt", 1);

        Database db = new Database();
        p.setDb(db);
        Thread t = new Thread(p);
        t.start();
        t.join();

        //Starts the Databases class
        db.resize(300);
        //String s = "Éiríonn le Biolbó agus Gandalf iad a shábháil agus faigheann siad claimhte i bpoll na dtroll, i ndiaidh cuardaigh";
        //String s = "In 1978 Johnson was awarded an American Institute of Architects Gold Medal. In 1979 he became the first recipient of the Pritzker Architecture Prize the most prestigious international architectural award";
        //String s = "Is cathair i gContae Baldwin, Alabama, Stáit Aontaithe Mheiriceá é Spanish Fort. Bhí 8,327 duine a bheith ina gcónaí sa chathair sa bhliain 2016.";
        String s = "Η Αφροδίτη Γραμμέλη αναφέρει μετά την πρεμιέρα ότι το πρώτο δείγμα τηλεθέασης, αν και νωρίς, είναι πολύ καλό. \"Ο Καπουτζίδης έχει αποδείξει ότι έχει στόφα ταλαντούχου σεναριογράφου που δεν γράφει απλά για να υπάρχει αλλά για";
        p.analysisQuery(s);

    }//End main class
}//End class
