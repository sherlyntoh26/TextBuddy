/**
 * This class is to write a Command Line Interface (CLI) program called TextBuddy
 * using Java to manipulate text in a file. 
 * The user can interact with the program through entering command.
 * The user can specify which file to be used, add value, display content, delete value,
 * clear whole content, exit the program and automatically saved into the file specify.
 * 
 * Author: Sherlyn Toh Hui Hsian
 * Matrix No: A0126288
 * 
 * Reasonable assumption about program behavior
 * 1. The program checks for any existing file with the same filename
 * 2. The end result overwrites the content in the any found existing file.
 * 3. The file is saved/overwrite when the user exit the program. 
 * 	  - the exit command trigger the saved method. 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class TextBuddy {

	private static final String MESSAGE_WELCOME = "Welcome to TextBuddy. %1$s is ready for use";
	private static final String MESSAGE_COMMAND = "command: ";
	private static final String MESSAGE_INVALID_FORMAT = "invalid command format: %1$s";
	private static final String MESSAGE_ADDED = "added to %1$s: \"%2$s\"";
	private static final String MESSAGE_DELETE = "deleted from %1$s: \"%2$s\"";
	private static final String MESSAGE_DISPLAY = "%1$s. %2$s";
	private static final String MESSAGE_CLEAR = "all content deleted from %1$s";
	private static final String MESSAGE_EMPTY = "%1$s is empty";
	private static final String MESSAGE_ERROR = "Error: %s\n";
	
	// These are the possible command types
	enum COMMAND_TYPE {
		ADD_TEXT, DISPLAY_TEXT, DELETE_TEXT, CLEAR_TEXT, INVALID, EXIT
	}
	
	// This is the correct number of parameters for delete command
	private static final int PARAM_SIZE_FOR_DELETE_TEXT = 1;
	
	// This is the position where the parameters will appear in a command
	private static final int PARAM_POSITION_DELETE_VALUE = 0;
	
	// This is the indication of empty storage
	private static final int STORAGE_EMPTY_VALUE = 0;
	
	private static final String DEFAULT_FILE = "default.txt";
	
	public static String fileName;
	private static ArrayList<String> storages = new ArrayList<String>();
	
	private static Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) throws IOException {
		String msg = getFileName(args);
		showToUser(msg);
		while (true) {
			System.out.print(MESSAGE_COMMAND);
			String userCommand = scanner.nextLine();
			String feedback = executeCommand(userCommand);
			showToUser(feedback);
		}
	}
	
	private static String getFileName(String[] input) throws IOException {
		
		if(input.length != 0) {
			fileName = input[0];
			
			File getFile = new File(fileName);
			if(getFile.exists()) {
				BufferedReader inputFile = new BufferedReader(new FileReader(fileName));
				readFromFile(inputFile);
			} else {
				getFile.createNewFile();
			}
		} else {
			fileName = DEFAULT_FILE;
		}
		return String.format(MESSAGE_WELCOME, fileName);
	}
	
	private static void readFromFile(BufferedReader file) throws FileNotFoundException, IOException {
		String content;
		while((content = file.readLine()) != null) {
			storages.add(content);
		}
	}
	
	private static void showToUser(String text) {
		System.out.println(text);
	}
	
	private static String executeCommand(String userCommand) {
		if(userCommand.trim().equals(""))
			return String.format(MESSAGE_INVALID_FORMAT, userCommand);
		
		String commandTypeString = getFirstWord(userCommand);
		
		COMMAND_TYPE commandType = determineCommandType(commandTypeString);
		
		switch(commandType) {
		case ADD_TEXT:
			return addText(userCommand);
		case DELETE_TEXT:
			return deleteText(userCommand);
		case DISPLAY_TEXT:
			return displayText();
		case CLEAR_TEXT:
			return clearText();
		case INVALID:
			return String.format(MESSAGE_INVALID_FORMAT, userCommand);
		case EXIT:
			exitProgram();
		default:
			throw new Error("Unrecognized command type");
		}
	}
	
	/**
	 * This operation determines which of the supported command types the user
	 * wants to perform
	 * 
	 * @param commandTypeString
	 *            is the first word of the user command
	 */
	private static COMMAND_TYPE determineCommandType(String commandTypeString) {
		if(commandTypeString == null)
			throw new Error("command type string cannot be null");
		
		if(commandTypeString.equalsIgnoreCase("add")) {
			return COMMAND_TYPE.ADD_TEXT;
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			return COMMAND_TYPE.DELETE_TEXT;
		} else if (commandTypeString.equalsIgnoreCase("display")) {
			return COMMAND_TYPE.DISPLAY_TEXT;
		} else if (commandTypeString.equalsIgnoreCase("clear")) {
			return COMMAND_TYPE.CLEAR_TEXT;
		} else if (commandTypeString.equalsIgnoreCase("exit")) {
			return COMMAND_TYPE.EXIT;
		} else {
			return COMMAND_TYPE.INVALID;
		}
	}
	
	private static String addText(String userCommand) {
		
		String textValue = removeFirstWord(userCommand);
		storages.add(textValue);
		
		return String.format(MESSAGE_ADDED, fileName, textValue);
	}
	
	/**
	 * This operation finds the value to be deleted. 
	 * @param userCommand
	 * @return
	 */
	private static String deleteText(String userCommand) {
		String[] parameters = splitParameters(removeFirstWord(userCommand));
		
		if(parameters.length != PARAM_SIZE_FOR_DELETE_TEXT) {
			return String.format(MESSAGE_INVALID_FORMAT, userCommand);
		}
		
		String deleteValue = parameters[PARAM_POSITION_DELETE_VALUE];
		
		if(!isPositiveNonZeroInt(deleteValue)) {
			return String.format(MESSAGE_INVALID_FORMAT, userCommand);
		}
		
		String deletedText = deleteTextAtPosition(deleteValue);
		
		return String.format(MESSAGE_DELETE, fileName, deletedText);
	}
	
	private static String deleteTextAtPosition(String deleteValue) {
		
		int deleteIntValue = Integer.parseInt(deleteValue) - 1 ;
		String deleteText = storages.get(deleteIntValue);
		storages.remove(deleteIntValue);
		return deleteText;
	}
	
	/**
	 * This operation display the data that are saved in the Arraylist of the program
	 */
	private static String displayText() {
		
		if(storages.size() == STORAGE_EMPTY_VALUE) {
			return String.format(MESSAGE_EMPTY, fileName);
		}
		
		String printingMsg = "";
		
		for (int i = 0; i < storages.size(); i++) {
			String currentMsg = String.format(MESSAGE_DISPLAY, i+1, storages.get(i));
			printingMsg += currentMsg;
			if(i != storages.size()-1){
				printingMsg += "\n";
			}
		}
		
		return printingMsg;
	}
	
	/**
	 * This operation clear the whole data being saved in the Arraylist of the program
	 */
	private static String clearText() {
		
		storages.clear();
		return String.format(MESSAGE_CLEAR, fileName);
	}
	
	/**
	 * This operation overwrites the content of the file and exit the program
	 */
	private static void exitProgram(){
		try {
			PrintWriter out = new PrintWriter(fileName,"UTF-8");
			
			for(int i = 0; i < storages.size(); i++){
				out.println(storages.get(i));
			}
			out.close();
			System.exit(0);
		} catch (IOException e) {
			System.out.printf(MESSAGE_ERROR, e.getMessage());
		}
	}
	
	private static String getFirstWord(String userCommand) {
		String commandTypeString = userCommand.trim().split("\\s+")[0];
		return commandTypeString;
	}

	private static String removeFirstWord(String userCommand) {
		return userCommand.replace(getFirstWord(userCommand), "").trim();
	}
	
	private static String[] splitParameters(String commandParametersString) {
		String[] parameters = commandParametersString.trim().split("\\s+");
		return parameters;
	}
	
	private static boolean isPositiveNonZeroInt(String s){
		try {
			int intValue = Integer.parseInt(s);
			// return turn if intValue is greater than 0
			return (intValue > 0 ? true : false);
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
}
