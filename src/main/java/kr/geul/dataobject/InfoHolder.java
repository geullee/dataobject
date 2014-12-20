package kr.geul.dataobject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.text.BadLocationException;

import kr.geul.console.Console;

public class InfoHolder {

	private static ArrayList<DataClassInfoHolder> infoHolders = new ArrayList<DataClassInfoHolder>();
	private static ArrayList<String> infoHolderTags = new ArrayList<String>();

	public static void addInfoHolder(DataClassInfoHolder infoHolder, String infoHolderTag) 
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, 
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, 
			SecurityException, BadLocationException {

		boolean doesExist = checkExistence(infoHolderTag);

		if (doesExist == false) {
			infoHolders.add(infoHolder);
			infoHolderTags.add(infoHolderTag);
			printAdditionResults(infoHolder, infoHolderTag);
		}

	}

	private static boolean checkExistence(String infoHolderTag) throws ClassNotFoundException, 
	InstantiationException, IllegalAccessException, IllegalArgumentException, 
	InvocationTargetException, NoSuchMethodException, SecurityException, 
	BadLocationException {

		for (int i = infoHolderTags.size() - 1; i >= 0 ; i--) {

			String tag = infoHolderTags.get(i);

			if (tag.equals(infoHolderTag)) {

				Console.printErrorMessage("Information holder '" + infoHolderTag + "' already does exist.",
						"InfoHolder");
				return true;

			}			

		}	

		return false;

	}

	public static void clearAll() {
		
		System.out.println("The information holder list has been cleared.");
		infoHolders = new ArrayList<DataClassInfoHolder>();
		infoHolderTags = new ArrayList<String>();
		
	}
	
	public static DataClassInfoHolder getInfoHolder(String infoHolderTag) throws BadLocationException, 
	ClassNotFoundException, InstantiationException, IllegalAccessException, 
	IllegalArgumentException, InvocationTargetException, NoSuchMethodException, 
	SecurityException {

		DataClassInfoHolder infoHolder = null;
		boolean doesExist = false;

		for (int i = 0; i < infoHolders.size(); i++) {

			String tag = infoHolderTags.get(i);

			if (tag.equals(infoHolderTag)) {

				infoHolder = infoHolders.get(i);
				doesExist = true;

			}

		}

		if (doesExist == false)
			Console.printErrorMessage("Unable to find information holder '" + infoHolderTag + "'.",
					"InfoHolder");

		return infoHolder;

	}	
	
	public static void loadInfoHolder(DataClassInfoHolder infoHolder, String infoHolderTag) 
			throws BadLocationException, ClassNotFoundException, 
			InstantiationException, IllegalAccessException, IllegalArgumentException, 
			InvocationTargetException, NoSuchMethodException, SecurityException {
		
		for (int i = infoHolderTags.size() - 1; i >= 0 ; i--) {

			String tag = infoHolderTags.get(i);

			if (tag.equals(infoHolderTag)) 
					Console.printErrorMessage("Information holder '" + infoHolderTag + 
							"' does already exist.", "InfoHolder");

		}	
		
		infoHolders.add(infoHolder);
		infoHolderTags.add(infoHolderTag);

	}
	
	private static void printAdditionResults
	(DataClassInfoHolder infoHolder, String infoHolderTag) 
			throws BadLocationException {

		System.out.println
		("Stored a new information holder '" + infoHolderTag + "'.");
		System.out.print("Variables: ");

		ArrayList<String> nameArray = infoHolder.getVariableNames();
		ArrayList<String> typeArray = infoHolder.getVariableTypes();
		
		for (int i = 0; i < nameArray.size(); i++) {	
			System.out.print(nameArray.get(i) + "(" + typeArray.get(i) + ") ");		
		}

		System.out.println("");

	}
	
	public static void remove(String infoHolderTag) {

		for (int i = infoHolderTags.size() - 1; i > -1 ; i--) {

			String tag = infoHolderTags.get(i);

			if (tag.equals(infoHolderTag)) {				

				infoHolders.remove(i);
				infoHolderTags.remove(i);
				System.out.println("Information holder '" + infoHolderTag
						+ "' has been successfully removed.");
				
			}

		}		

	}
	
	public static void showList() {

		System.out.println("<< Beginning of information holder list >>\n");

		if (infoHolders.size() == 0) 
			System.out.println("--- No class has been found ---");

		else {

			for (int i = 0; i < infoHolders.size(); i++) {

				DataClassInfoHolder infoHolder = infoHolders.get(i);
				String infoHolderTag = infoHolderTags.get(i);
				String dataClassName = infoHolder.getClassName();
				ArrayList<String> nameArray = infoHolder.getVariableNames();
				ArrayList<String> typeArray = infoHolder.getVariableTypes();

				System.out.print(infoHolderTag + ": {Classname: " + dataClassName + ", Variables: ");
				
				if (nameArray.size() > 0) {
				
					for (int j = 0; j < nameArray.size(); j++) {

						System.out.print(nameArray.get(j) + "(" + typeArray.get(j) + ")");
						
						if (j < nameArray.size() - 1)
							System.out.print(", ");

					}
					
				}

				else
					System.out.print("NULL");
				
				System.out.println("}");

			}

		}

		System.out.println("\n<< End of information holder list >>");

	}
	
}
