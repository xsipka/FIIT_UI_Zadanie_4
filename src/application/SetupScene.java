package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SetupScene {
	
	
	Rules rule;
	String X;
	String Y;
	String Z;
	SetupController controller;
	ArrayList<String> messages = new ArrayList<String>();
	ArrayList<String> newFacts = new ArrayList<String>();
	boolean modified;
	boolean nextStep;
	
	
	public ArrayList<String> doStuff(ArrayList<String> facts, String fileName, boolean bySteps) {
		
		ArrayList<Rules> rules = loadRules(fileName);
		String condition;
		modified = true;
		nextStep = bySteps;
		
		while (modified) {
			modified = false;
			for (Rules rule : rules) {
				X = "";
				Y = "";
				Z = "";
				condition = rule.getCondition();
				checkRules(condition, rule, facts);
			}
		}
		return facts;
	}
	

	// goes through every fact for every subcondition and checks if they are the same
	// calls itself if there are unchecked subconditions, if not, then executes actions
	public void checkRules(String condition, Rules rule, ArrayList<String> facts) {
		
		String[] subconditions = condition.split(",");				// splits condition into multiple subconditions
		String[] toCheck = replaceWithValues(subconditions[0]);		// replace one subcondition with values
		String x=X, y=Y, z=Z;										// saves main variables as helping ones 
		
		
		for (int i = 0; i < facts.size(); i++) {					// goes through every fact and compares it with
			String[] factToCheck = facts.get(i).split(" ");			// current subcondition, continues only if true
			if (factChecker(toCheck, factToCheck)) {				// if true, then check if last subcondition
				if (subconditions.length > 1) {
					condition = condition.replace(subconditions[0] + ",", "");	// creates new subcondition and calls itself
					checkRules(condition, rule, facts);
				} else {
					createActions(rule, facts);			// if there are no more subcondition, then executes actions
				}
			}
			X=x;
			Y=y;
			Z=z;
		}		
		if (nextStep == true) {							// used only if we want step by step output
			if (newFacts.size() != facts.size()) {		// if sizes are same, then we have no reason to continue
				stepByStepFacts(facts);					// because we know nothing has changed 
			}
		}
	}
	
	
	// saves facts into array list of array lists, used only for step by step output
	public void stepByStepFacts(ArrayList <String> facts) {
		if (newFacts.size() < facts.size()) {					// if new fact was added
        	for (int i = 0; i < facts.size(); i++) {			// adds empty strings to adjust sizes 
        		if (i >= newFacts.size()) {
        			newFacts.add("");
        		}
        		if (!facts.get(i).equals(newFacts.get(i))) {
        			newFacts.set(i, facts.get(i));
        		}
        	}
        	saveFacts(facts);									
        	return;
        }
		if (newFacts.size() > facts.size()) {					// if old fact was removed
        	for (int i = 0; i < newFacts.size(); i++) {			// finds old fact in newFacts and removes it
        		if (!facts.get(i).equals(newFacts.get(i))) {
        			newFacts.remove(i);
        		}
        	}
        	saveFacts(facts);
        	return;
		}
	}
	
	
	// creates a string array of condition in which are variables replaced with values 
	public String[] replaceWithValues(String toReplace) {
			
		String[] replaced = toReplace.split(" ");			// splits condition into array of strings
		for (int i = 0; i < replaced.length; i++) {			// checks every part for variables
			if (replaced[i].equals("?X")) {					// if there are some, then replace with values
				if (!X.isEmpty()) { replaced[i] = X; }
			}
			if (replaced[i].equals("?Y")) {
				if (!Y.isEmpty()) { replaced[i] = Y; }
			}
			if (replaced[i].equals("?Z")) {
				if (!Z.isEmpty()) { replaced[i] = Z; }
			}
		}
		return replaced;
	}
	

	// compares fact with condition with variables replaced with values
	// returns true if they are the same, else return false
	public boolean factChecker(String[] toCheck, String[] fact) {
		
		if (toCheck.length == fact.length) {				// checks if fact and rule have the same length
			for (int i = 0; i < fact.length; i++) {			// if true, then continue, else check another condition
				if (!toCheck[i].equals(fact[i])) {
					if (toCheck[i].equals("?X")) {
						if (X.isEmpty()) { X = fact[i]; }	// if condition contains variables and current variables
					}										// are empty, then assign them values from current fact
					else if (toCheck[i].equals("?Y")) {		// we do this for all variables
						if (Y.isEmpty()) { Y = fact[i]; }
					}
					else if (toCheck[i].equals("?Z")) {
						if (Z.isEmpty()) { Z = fact[i]; }
					}
					else { return false; }
				}
			}
			return true;		
		}
		if (toCheck[0].contentEquals("<>")) {						// checks if this is not equal condition
			if (!toCheck[1].equals(toCheck[2])) { return true; }	// if true, then checks if values are equal or not
		}															// if not, return true, else return false
		return false;
	}
	
	
	// creates individual actions from current Rules object and executes them
	public void createActions(Rules rule, ArrayList<String> facts) {
		
		String[] oneAction = rule.getActions().split(",");			// splits actions into individual string arrays 
		
		for (int curr = 0; curr < oneAction.length; curr++) {		// does following for every individual action
			String actionByWords[] = oneAction[curr].split(" ");	// splits action into individual words
			executeAction(actionByWords, oneAction[curr], facts);	// executes action
		}
	}
	
	
	// executes current action depending on action name
	public void executeAction(String[] actionByWords, String action, ArrayList<String> facts) {
		
		String toExecute;
		if (actionByWords[0].equals("pridaj")) {		// checks if current action is valid or not
			toExecute = action.substring(7);			// removes action name from action call
			toExecute = createActionCall(toExecute);	// creates action call with replaced values
			for (String fact : facts) {
				if (toExecute.equals(fact)) { return; }		// return if fact already exists
			}												// else add to the list of facts
			modified = true;
			facts.add(toExecute);
		}
		else if (actionByWords[0].equals("vymaz")) {
			toExecute = action.substring(6);
			toExecute = createActionCall(toExecute);
			for (int i = 0; i < facts.size(); i++) {
				if (toExecute.equals(facts.get(i))) {		// if fact is found delete it
					modified = true;
					facts.remove(i);
				}
			}
		}
		else if (actionByWords[0].equals("sprava")) {
			toExecute = action.substring(7);
			toExecute = createActionCall(toExecute);
			for (String message : messages) {					// goes through messages, if new message exists return
				if (toExecute.equals(message)) { return; }		// else add a new message to the message list
			}	
			messages.add(toExecute);
		}
		else { return; }	// if unknown action, then return
	}
	
	
	
	// creates action call by replacing variables with previously obtained values
	public String createActionCall(String toExecute) {
		
		String replaced[] = toExecute.split(" ");				// creates a string array out of action call
		for (int i = 0; i < replaced.length; i++) {				// goes through every word and replace values if possible
			if (!X.isEmpty() && replaced[i].equals("?X")) {
				replaced[i] = X;
			}
			if (!Y.isEmpty() && replaced[i].equals("?Y")) {
				replaced[i] = Y;
			}
			if (!Z.isEmpty() && replaced[i].equals("?Z")) {
				replaced[i] = Z;
			}
		}
		return String.join(" ", replaced);
	}
	
	
	// returns an array list of messages, called after everything else
	public ArrayList<String> getMessages() {
		return messages;
	}
	
	
	// saves an array list into array list of array lists
	public void saveFacts(ArrayList<String> facts) {
		ArrayList<String> bySteps = new ArrayList<String>();
		for (String fact : facts) {
			bySteps.add(fact);						// creates temporary array list, adding original doesn't work
		}											
		Facts.getInstance().addToArray(bySteps);	// adding an array list into array list of array lists
	}
	
	
	// loads facts from .txt file into an array list
	public ArrayList<String> loadFacts(String fileName) {
		
		ArrayList<String> facts = new ArrayList<String>();	
		
		try {
			File file = new File("src/TextFiles/"+fileName);
			Scanner fileScanner = new Scanner(file);	
			
			while (fileScanner.hasNextLine()) {
				facts.add(fileScanner.nextLine());
			}			
			fileScanner.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found...");
			e.printStackTrace();
		}
		return facts;
	}
	
	
	// loads rules from .txt file into an array list
	public ArrayList<Rules> loadRules(String fileName) {
		
		ArrayList<Rules> rules = new ArrayList<Rules>();
		String rowContent;
		int rowCounter = 0;
		rule = new Rules();
		
		try {
			File file = new File("src/TextFiles/"+fileName);
			Scanner fileScanner = new Scanner(file);
		      
			while (fileScanner.hasNextLine()) {
				rowContent = fileScanner.nextLine();			
				if (rowCounter % 4 == 0) {
					rule.setName(rowContent);
				}
				if (rowCounter % 4 == 1) {
					rule.setCondition(rowContent);
				}
				if (rowCounter % 4 == 2) {
					rule.setActions(rowContent);
					rules.add(rule);
					rule = new Rules();
				}
				rowCounter++;
			}			
			fileScanner.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found...");
			e.printStackTrace();
		}
		return rules;
	}
	
}
