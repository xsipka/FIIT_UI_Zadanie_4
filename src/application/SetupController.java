package application;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SetupController {

    @FXML
    private TextArea txMyFacts;
    
    @FXML
    private TextArea txMessages;
    
    @FXML
    private TextField txFactsFile;

    @FXML
    private TextField txRulesFile;

    @FXML
    private Button btnDoStuff;
    
    @FXML
    private Button btnNextStep;
    
    @FXML
    private Button btnLoadFacts;
    
    SetupScene setupScene;
    boolean firstStep = true;
    int stepsCounter = 0;
    
    
    // saves facts from the text area into an array list, gets the name of
    // rules.txt file (not yet implemented) and does main stuff
    public void doStuffButtonClicked(ActionEvent event) throws Exception {
    	String rulesFileName;
    	ArrayList<String> facts = new ArrayList<String>();
    	ArrayList<String> messages = new ArrayList<String>();
    	
    	if (!txMyFacts.getText().isEmpty()) {
    		for (String line : txMyFacts.getText().split("\\n")) {
    			facts.add(line);
    		}
    	}
    	else {
        	JOptionPane.showMessageDialog(null, "Give me some facts!");
        	return;
    	}
    	rulesFileName = txRulesFile.getText();
    	if (rulesFileName.isEmpty()) {
    		JOptionPane.showMessageDialog(null, "Give me some rules!");
        	return;
    	}
    	setupScene = new SetupScene();
    	facts = setupScene.doStuff(facts, rulesFileName, false);
    	messages = setupScene.getMessages();
    	displayFacts(facts);
    	displayMessages(messages);
    }
    
    
    
    public void nextStepButtonClicked(ActionEvent event) throws Exception {
    	String rulesFileName;
    	ArrayList<String> facts = new ArrayList<String>();
    	//ArrayList<String> messages = new ArrayList<String>();
    	ArrayList<ArrayList<String>> factsBySteps = new ArrayList<ArrayList<String>>();
    	
    	if (firstStep == true) {
    		firstStep = false;
    		if (!txMyFacts.getText().isEmpty()) {
        		for (String line : txMyFacts.getText().split("\\n")) {
        			facts.add(line);
        		}
        	}
        	else {
            	JOptionPane.showMessageDialog(null, "Give me some facts!");
            	return;
        	}
        	rulesFileName = txRulesFile.getText();
        	if (rulesFileName.isEmpty()) {
        		JOptionPane.showMessageDialog(null, "Give me some rules!");
            	return;
        	}
        	setupScene = new SetupScene();
        	facts = setupScene.doStuff(facts, rulesFileName, true);
        	//messages = setupScene.getMessages();
        	factsBySteps = Facts.getInstance().getArray(); 
            displayFacts(factsBySteps.get(stepsCounter));
            stepsCounter += 1;
        	//displayMessages(messages);
    	}
    	else {
    		factsBySteps = Facts.getInstance().getArray(); 
    		if (stepsCounter < factsBySteps.size()) {
    			displayFacts(factsBySteps.get(stepsCounter));
    			stepsCounter += 1;
    		}
    	}
    }
    
    
    // gets the name of a .txt file we want to use and then loads
    // facts from the file and displays them in a text area
    public void loadFactsButtonClicked(ActionEvent event) throws Exception {
    	String factsFileName;
    	ArrayList<String> facts;
    	
    	txMyFacts.clear();
    	txMessages.clear();
    	factsFileName = txFactsFile.getText();
		if (factsFileName.isEmpty()) {
    		JOptionPane.showMessageDialog(null, "Give me some facts!");
    		return;
    	}
		setupScene = new SetupScene();
    	facts = setupScene.loadFacts(factsFileName);
    	displayFacts(facts);
    }
    
    
    // displays an array list of facts into a text area
    public void displayFacts(ArrayList<String> facts) {
    	
    	if (!txMyFacts.getText().isEmpty()) { 
    		txMyFacts.clear();
    	}
    	for (int i = 0; i < facts.size(); i++) {
    		txMyFacts.appendText(facts.get(i));
    		txMyFacts.appendText("\n");
    	}
    }
    
    
    // displays an array list of messages into a text area
    public void displayMessages(ArrayList<String> messages) {
    	
    	if (!txMessages.getText().isEmpty()) { 
    		txMessages.clear();
    	}
    	for (int i = 0; i < messages.size(); i++) {
    		txMessages.appendText(messages.get(i));
    		txMessages.appendText("\n");
    	}
    }
    
}
