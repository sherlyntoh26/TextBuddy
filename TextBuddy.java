
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
import java.util.Collections;
import java.util.Scanner;

public class TextBuddy {

	private static final String MESSAGE_WELCOME = "Welcome to TextBuddy. %1$s is ready for use";
	private static final String MESSAGE_COMMAND = "command: ";
	private static final String MESSAGE_INVALID_FORMAT = "invalid command format: %1$s";
	private static final String MESSAGE_ADDED = "added to %1$s: \"%2$s\"";
	private static final String MESSAGE_DELETE = "deleted from %1$s: \"%2$s\"";
	private static final String MESSAGE_DELETE_FAIL = "Line %1$s does not exist in %2$s";
	private static final String MESSAGE_DISPLAY = "%1$s. %2$s";
	private static final String MESSAGE_CLEAR = "all content deleted from %1$s";
	private static final String MESSAGE_EMPTY = "%1$s is empty";
	private static final String MESSAGE_ERROR = "Error: %s\n";
	private static final String MESSAGE_SORTED = "Contents in %1$s have been sorted";
	private static final String MESSAGE_SORTED_FAIL = "%1$s has nothing to sort";
	private static final String MESSAGE_SEARCH_FAIL = "\"%1$s\" cannot be found in %2$s";

	// These are the possible command types
	enum CommandTypes {
		ADD_TEXT, DISPLAY_TEXT, DELETE_TEXT, CLEAR_TEXT, INVALID, EXIT, SORT, SEARCH
	}

	// This is the correct number of parameters for delete command
	private static final int PARAM_SIZE_FOR_DELETE_TEXT = 1;

	// This is the position where the parameters will appear in a command
	private static final int PARAM_POSITION_DELETE_VALUE = 0;

	// This is the indication of empty storage
	private static final int STORAGE_EMPTY_VALUE = 0;

	private static final String DEFAULT_FILE = "default.txt";

	public static String fileName = DEFAULT_FILE;
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

	private static String getFileName(String[] input) {
		if (input.length != 0) {
			fileName = input[0];

			File getFile = new File(fileName);
			if (getFile.exists()) {
				openingFile();
			} else {
				try {
					getFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return String.format(MESSAGE_WELCOME, fileName);
	}

	private static void openingFile() {
		BufferedReader inputFile;
		try {
			inputFile = new BufferedReader(new FileReader(fileName));
			readFromFile(inputFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void readFromFile(BufferedReader file) {
		String content;
		try {
			while ((content = file.readLine()) != null) {
				storages.add(content);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void showToUser(String text) {
		System.out.println(text);
	}

	public static String executeCommand(String userCommand) {
		if (userCommand.trim().equals("")) {
			return String.format(MESSAGE_INVALID_FORMAT, userCommand);
		}

		String commandTypeString = getFirstWord(userCommand);

		CommandTypes commandType = determineCommandType(commandTypeString);

		switch (commandType) {
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
		case SORT:
			return sortText();
		case SEARCH:
			return searchText(userCommand);
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
	private static CommandTypes determineCommandType(String commandTypeString) {
		if (commandTypeString == null) {
			throw new Error("command type string cannot be null");
		}

		if (commandTypeString.equalsIgnoreCase("add")) {
			return CommandTypes.ADD_TEXT;
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			return CommandTypes.DELETE_TEXT;
		} else if (commandTypeString.equalsIgnoreCase("display")) {
			return CommandTypes.DISPLAY_TEXT;
		} else if (commandTypeString.equalsIgnoreCase("clear")) {
			return CommandTypes.CLEAR_TEXT;
		} else if (commandTypeString.equalsIgnoreCase("exit")) {
			return CommandTypes.EXIT;
		} else if (commandTypeString.equalsIgnoreCase("sort")) {
			return CommandTypes.SORT;
		} else if (commandTypeString.equalsIgnoreCase("search")) {
			return CommandTypes.SEARCH;
		} else {
			return CommandTypes.INVALID;
		}
	}

	private static String sortText() {
		if (storages.size() == 0) {
			return String.format(MESSAGE_SORTED_FAIL, fileName);
		}

		Collections.sort(storages, String.CASE_INSENSITIVE_ORDER);
		return String.format(MESSAGE_SORTED, fileName);
	}

	private static String searchText(String userCommand) {
		String searchValue = removeFirstWord(userCommand).trim();
		
		if(storages.isEmpty()) {
			return String.format(MESSAGE_SEARCH_FAIL, searchValue, fileName);
		}
		
		String searchOutput = getSearchOutput(searchValue);

		if (searchOutput.equals("")) {
			return String.format(MESSAGE_SEARCH_FAIL, searchValue, fileName);
		}
		return searchOutput;
	}

	private static String getSearchOutput(String searchValue) {
		ArrayList<String> searchResult = getSearchResult(searchValue);
		String searchOutput = "";

		for (int i = 0; i < searchResult.size(); i++) {
			String currentMsg = String.format(MESSAGE_DISPLAY, i + 1, searchResult.get(i));
			searchOutput += currentMsg;
			if (i != searchResult.size() - 1) {
				searchOutput += "\n";
			}
		}
		return searchOutput;
	}

	private static ArrayList<String> getSearchResult(String searchValue) {
		ArrayList<String> results = new ArrayList<String>();

		for (int i = 0; i < storages.size(); i++) {
			String text = storages.get(i);
			if (text.toLowerCase().contains(searchValue.toLowerCase())) {
				results.add(text);
			}
		}
		return results;
	}

	private static String addText(String userCommand) {
		String textValue = removeFirstWord(userCommand);
		storages.add(textValue);

		return String.format(MESSAGE_ADDED, fileName, textValue);
	}

	/**
	 * This operation finds the value to be deleted.
	 * 
	 * @param userCommand
	 * @return message in String
	 */
	private static String deleteText(String userCommand) {
		String[] parameters = splitParameters(removeFirstWord(userCommand));

		if (parameters.length != PARAM_SIZE_FOR_DELETE_TEXT) {
			return String.format(MESSAGE_INVALID_FORMAT, userCommand);
		}

		String deleteValue = parameters[PARAM_POSITION_DELETE_VALUE];

		if (!isPositiveNonZeroInt(deleteValue)) {
			return String.format(MESSAGE_INVALID_FORMAT, userCommand);
		}

		if (!isValidIndex(deleteValue)) {
			return String.format(MESSAGE_DELETE_FAIL, userCommand, fileName);
		}

		String deletedText = deleteTextAtPosition(deleteValue);

		return String.format(MESSAGE_DELETE, fileName, deletedText);
	}

	private static String deleteTextAtPosition(String deleteValue) {

		int deleteIntValue = Integer.parseInt(deleteValue) - 1;
		String deleteText = storages.get(deleteIntValue);
		storages.remove(deleteIntValue);
		return deleteText;
	}

	/**
	 * This operation display the data that are saved in the Arraylist of the
	 * program
	 */
	private static String displayText() {

		if (storages.size() == STORAGE_EMPTY_VALUE) {
			return String.format(MESSAGE_EMPTY, fileName);
		}

		String msgToDisplay = getStringText();
		return msgToDisplay;
	}
	
	private static String getStringText() {
		String printingMsg = "";

		for (int i = 0; i < storages.size(); i++) {
			String currentMsg = String.format(MESSAGE_DISPLAY, i + 1, storages.get(i));
			printingMsg += currentMsg;
			if (i != storages.size() - 1) {
				printingMsg += "\n";
			}
		}
		return printingMsg;
	}

	/**
	 * This operation clear the whole data being saved in the Arraylist of the
	 * program
	 * @return cleared message
	 */
	private static String clearText() {
		storages.clear();
		return String.format(MESSAGE_CLEAR, fileName);
	}

	/**
	 * This operation overwrites the content of the file and exit the program
	 */
	private static void exitProgram() {
		try {
			PrintWriter out = new PrintWriter(fileName, "UTF-8");
			for (int i = 0; i < storages.size(); i++) {
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

	private static boolean isPositiveNonZeroInt(String deletingValue) {
		try {
			int intValue = Integer.parseInt(deletingValue);
			// return turn if intValue is greater than 0
			return (intValue > 0);
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	private static boolean isValidIndex(String deleteValue) {
		int intValue = Integer.parseInt(deleteValue);
		if (intValue >= storages.size()) {
			return false;
		}
		return true;
	}
}
