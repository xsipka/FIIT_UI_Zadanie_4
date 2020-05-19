package application;

import java.util.ArrayList;


// singleton class used for storing an array list of array lists
// containing modified facts after every change
public class Facts {

	private static Facts instance;
    private ArrayList<ArrayList<String> >  list = null;

    public static Facts getInstance() {
        if(instance == null)
        	instance = new Facts();

        return instance;
    }

    
    private Facts() {
    	list = new ArrayList<ArrayList<String> > ();
    }
    
    
    public ArrayList<ArrayList<String>> getArray() {
    	return this.list;
    }
    
    
    public void addToArray(ArrayList<String> value) {
    	list.add(value);
    }
} 
