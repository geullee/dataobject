package kr.geul.dataobject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.text.BadLocationException;

import kr.geul.console.Console;

public class DataClass {

	private static ArrayList<DataClassArray> dataClassArrayHanger = new ArrayList<DataClassArray>();

	public static void addDataClass(DataClassArray dataClassArray) throws ClassNotFoundException, 
	InstantiationException, IllegalAccessException, IllegalArgumentException, 
	InvocationTargetException, NoSuchMethodException, SecurityException, 
	BadLocationException {

		DataClassInfoHolder infoHolder = dataClassArray.getInfoHolder();
		String dataClassName = infoHolder.getClassName();
		ArrayList<String> nameArray = infoHolder.getVariableNames();
		ArrayList<String> typeArray = infoHolder.getVariableTypes();
		boolean doesDataClassExist = checkDataClassExistence(dataClassName);

		if (doesDataClassExist == false) {
			dataClassArrayHanger.add(dataClassArray);
			printDataClassAdditionResults(dataClassName, nameArray, typeArray);
		}

	}

	public static void addDataClass(String dataClassName, String nameAlias,
			String typeAlias) throws ClassNotFoundException, InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException, BadLocationException {

		boolean doesDataClassExist = checkDataClassExistence(dataClassName);

		if (doesDataClassExist == false) {

			DataClassInfoHolder infoHolder = new DataClassInfoHolder();
			ArrayList<String> nameArrayList = Console.getAliasContentsList(nameAlias),
							  typeArrayList = Console.getAliasContentsList(typeAlias);

			if (nameArrayList != null && typeArrayList != null && 
					checkTypeArray(typeArrayList) == true) {

				infoHolder.setClassName(dataClassName);
				infoHolder.setVariableNames(nameArrayList);
				infoHolder.setVariableTypes(typeArrayList);

				DataClassArray dataClassArray = new DataClassArray(infoHolder);
				dataClassArrayHanger.add(dataClassArray);
				printDataClassAdditionResults(dataClassName, nameArrayList, typeArrayList);

			}	

		}

	}

	private static boolean checkDataClassExistence(String dataClassName) throws ClassNotFoundException, 
	InstantiationException, IllegalAccessException, IllegalArgumentException, 
	InvocationTargetException, NoSuchMethodException, SecurityException, 
	BadLocationException {

		for (int i = dataClassArrayHanger.size() - 1; i >= 0 ; i--) {

			DataClassInfoHolder holder = dataClassArrayHanger.get(i).getInfoHolder();

			if (holder.getClassName().equals(dataClassName)) {

				if (dataClassName.equals("RawObservation")) { 
					removeDataClass("RawObservation");
					break;
				}

				else {
					Console.printErrorMessage("Class '" + dataClassName + "' already does exist.", 
							"DataClass");
					return true;
				}

			}			

		}	

		return false;

	}

	private static boolean checkTypeArray(ArrayList<String> typeArray) throws ClassNotFoundException, 
	InstantiationException, IllegalAccessException, IllegalArgumentException, 
	InvocationTargetException, NoSuchMethodException, SecurityException, 
	BadLocationException {

		for (int i = 0; i < typeArray.size(); i++) {

			String type = typeArray.get(i);

			if (type.equals("date") == false && type.equals("double") == false && 
					type.equals("int") == false && type.equals("list") == false && 
					type.equals("oblist") == false && type.equals("obs") == false && 
					type.equals("string") == false) {

				Console.printErrorMessage("There exists an invalid variable type '" + type + "' " +
						"in the variable type array.", "DataClass");
				return false;

			}

		}

		return true;

	}

	public static void clearDataClassHanger() {
		
		System.out.println("The class list has been cleared.");
		dataClassArrayHanger = new ArrayList<DataClassArray>();
		
	}
	
	public static DataClassArray getDataClassArray(String className) throws BadLocationException, 
	ClassNotFoundException, InstantiationException, IllegalAccessException, 
	IllegalArgumentException, InvocationTargetException, NoSuchMethodException, 
	SecurityException {

		DataClassArray dataClassArray = null;
		boolean doesExist = false;

		for (int i = 0; i < dataClassArrayHanger.size(); i++) {

			String name = dataClassArrayHanger.get(i).getInfoHolder().getClassName();

			if (name.equals(className)) {

				dataClassArray = dataClassArrayHanger.get(i);
				doesExist = true;

			}

		}

		if (doesExist == false)
			Console.printErrorMessage("Unable to find class '" + className + "'.",
					"DataClass");

		return dataClassArray;

	}	


	public static void loadDataClassArray(DataClassArray dataClassArray) 
			throws BadLocationException, ClassNotFoundException, 
			InstantiationException, IllegalAccessException, IllegalArgumentException, 
			InvocationTargetException, NoSuchMethodException, SecurityException {

		DataClassInfoHolder infoHolder = dataClassArray.getInfoHolder();
		String className = infoHolder.getClassName();
		
		for (int i = dataClassArrayHanger.size() - 1; i >= 0 ; i--) {

			DataClassInfoHolder holder = dataClassArrayHanger.get(i).getInfoHolder();

			if (holder.getClassName().equals(className)) {

				if (className.equals("RawObservation")) 
					removeDataClass("RawObservation");				

				else 
					Console.printErrorMessage("Class '" + className + "' does already exist.",
							"DataClass");

			}			

		}	
		
		dataClassArrayHanger.add(dataClassArray);

	}
	
	private static void printDataClassAdditionResults
	(String dataClassName, ArrayList<String> nameArray, ArrayList<String> typeArray) 
			throws BadLocationException {

		System.out.println
		("Defined a new class '" + dataClassName + "'.");
		System.out.print("Variables: ");

		for (int i = 0; i < nameArray.size(); i++) {	
			System.out.print(nameArray.get(i) + "(" + typeArray.get(i) + ") ");		
		}

		System.out.println("");

	}

	public static void removeDataClass(String className) {

		for (int i = dataClassArrayHanger.size() - 1; i > -1 ; i--) {

			DataClassInfoHolder infoHolder = dataClassArrayHanger.get(i).getInfoHolder();

			if (infoHolder.getClassName().equals(className)) {				
				dataClassArrayHanger.remove(i);
				System.out.println("class '" + className
						+ "' has been successfully removed.");				
			}

		}		

	}

	public static void resetDataClass(DataClassArray classArray) throws BadLocationException {

		DataClassInfoHolder infoHolder = classArray.getInfoHolder();
		String className = infoHolder.getClassName();

		for (int i = 0; i < dataClassArrayHanger.size(); i++) {

			String name = dataClassArrayHanger.get(i).getInfoHolder().getClassName();

			if (name.equals(className)) {
				dataClassArrayHanger.set(i, classArray);
				return;
			}

		}

	}

	public static void showList() {

		System.out.println("<< Beginning of class list >>\n");

		if (dataClassArrayHanger.size() == 0) 
			System.out.println("--- No class has been found ---");

		else {

			for (int i = 0; i < dataClassArrayHanger.size(); i++) {

				DataClassArray dataClassArray = dataClassArrayHanger.get(i);
				DataClassInfoHolder infoHolder = dataClassArray.getInfoHolder();
				String dataClassName = infoHolder.getClassName();
				ArrayList<String> nameArray = infoHolder.getVariableNames();
				ArrayList<String> typeArray = infoHolder.getVariableTypes();

				System.out.print(dataClassName + " (" + dataClassArray.size() + " obs): {");

				for (int j = 0; j < nameArray.size(); j++) {

					System.out.print(nameArray.get(j) + "(" + typeArray.get(j) + ")");

					if (j < nameArray.size() - 1)
						System.out.print(", ");;

				}

				System.out.println("}");

			}

		}

		System.out.println("\n<< End of class list >>");

	}

}
