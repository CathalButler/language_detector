package ie.gmit.sw;

/**
 * Interface to allow for implementation of resizing of a list.
 */
public interface Resizeable {
    /**
     * method to resize by inputted amount in the max param
     *
     * @param max the amount by which to resize
     */
    void resize(int max);
}//End interface
