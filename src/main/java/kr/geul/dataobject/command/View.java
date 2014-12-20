package kr.geul.dataobject.command;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.text.BadLocationException;

import kr.geul.console.Console;
import kr.geul.console.command.Command;
import kr.geul.dataobject.DataClass;
import kr.geul.dataobject.DataClassArray;
import kr.geul.dataobject.DataClassInfoHolder;
import kr.geul.dataobject.ObList;
import kr.geul.dataobject.Observation;

public class View extends Command {
	
	DataClassArray dataClassArray;

	public View(ArrayList<String> arguments) {
		super(arguments);	
	}

	@Override
	protected String getCommandName() {
		return "view";
	}

	@Override
	protected int[] getValidNumberOfArguments() {
		int[] numbers = {2, 3};
		return numbers;
	}

	@Override
	protected void runCommand() throws Exception {
		
		String dataClassName = arguments.get(0);
		int startingPoint = Integer.parseInt(arguments.get(1));
		int numberOfObservations = 0;

		if (arguments.size() == 3)
			numberOfObservations = Integer.parseInt(arguments.get(2));

		if (startingPoint < 0 || numberOfObservations < 0)
			Console.printErrorMessage("Observation point cannot be negative.",
					Thread.currentThread().getStackTrace()[1].getClassName());

		dataClassArray = DataClass.getDataClassArray(dataClassName);	

		if (dataClassArray.size() < startingPoint - 1 || 
				dataClassArray.size() + numberOfObservations < startingPoint - 1)
			Console.printErrorMessage("Requested observation point exceeds the amount of observation.",
					Thread.currentThread().getStackTrace()[1].getClassName());

		DataClassInfoHolder infoHolder = dataClassArray.getInfoHolder();			

		for (int i = startingPoint; i < startingPoint + numberOfObservations + 1; i++) {

			Observation observation = dataClassArray.get(i);
			printObservationInformation(observation, infoHolder, i);

		}
		
	}

	private void printObservationInformation(Observation observation,
			DataClassInfoHolder infoHolder, int point) throws NoSuchFieldException, 
			SecurityException, ClassNotFoundException, InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, BadLocationException {

		System.out.println("\n<< Beginning of observation #" + point + 
				" of class '" + infoHolder.getClassName() + "' >>\n");

		printInformation(observation, infoHolder, "");

		System.out.println("\n<< End of observation #" + point + 
				" of class '" + infoHolder.getClassName() + "' >>");

	}

	private void printInformation(Observation observation,
			DataClassInfoHolder infoHolder, String address) 
					throws NoSuchFieldException, SecurityException, ClassNotFoundException, 
					InstantiationException, IllegalAccessException, IllegalArgumentException, 
					InvocationTargetException, NoSuchMethodException, BadLocationException {

		ArrayList<String> variableNames = infoHolder.getVariableNames();
		ArrayList<String> variableTypes = infoHolder.getVariableTypes();
		String newAddress; 

		for (int i = 0; i < variableTypes.size(); i++) {

			if (variableTypes.get(i).equals("oblist")) {

				if (address.equals(""))
					newAddress = variableNames.get(i);
				else 
					newAddress = address + "/" + variableNames.get(i);  

				ObList obList = observation.getObListVariable(infoHolder, variableNames.get(i));
				DataClassInfoHolder obListInfoHolder = 
						dataClassArray.getSubInfoHolder(newAddress);

				if (i == 0 || (variableTypes.get(i - 1).equals("oblist") == false &&
						variableTypes.get(i - 1).equals("obs") == false))
					System.out.println("\n");

				System.out.println("< Beginning of ObList variable '" + variableNames.get(i)
						+ "' >");

				if (obListInfoHolder.getClassName().equals("NULL")) 
					System.out.println("--- No data ---");	

				else {
				
					if (obList.size() > 0)		
						System.out.println("");

					for (int j = 0; j < obList.size(); j++) {

						Observation obListObservation = obList.get(j);
						System.out.println("[ Beginning of ObList observation #" + j
								+ " in '" + variableNames.get(i) + "' ]");
						
						printInformation(obListObservation, obListInfoHolder, newAddress);

						System.out.println("[ End of ObList observation #" + j
								+ " in '" + variableNames.get(i) + "' ]\n");

					}
					
				}

				System.out.print("< End of ObList variable '" + variableNames.get(i)
						+ "' >");
				if (i < variableTypes.size() - 1)
					System.out.println("\n");
				
			}

			else if (variableTypes.get(i).equals("obs")) {

				if (address.equals(""))
					newAddress = variableNames.get(i);
				else 
					newAddress = address + "/" + variableNames.get(i);  

				Observation subObservation = 
						observation.getObservationVariable(infoHolder, variableNames.get(i));
				DataClassInfoHolder subObsInfoHolder = 
						dataClassArray.getSubInfoHolder(newAddress);
				
					if (i == 0 || (variableTypes.get(i - 1).equals("oblist") == false &&
							variableTypes.get(i - 1).equals("obs") == false))
						System.out.println("\n");

					System.out.println("[ Beginning of Observation variable '" + variableNames.get(i)
							+ "' ]");				

					if (subObsInfoHolder.getClassName().equals("NULL") || subObservation == null) 
						System.out.println("--- No data ---");	
					else
						printInformation(subObservation, subObsInfoHolder, newAddress); 

					System.out.print("[ End of Observation variable '" + variableNames.get(i)
							+ "' ]");
					if (i < variableTypes.size() - 1)
						System.out.println("\n");
					

				
			}

			else {

				System.out.print(variableNames.get(i) + ": " + observation.getStringTypeVariable
						(infoHolder, variableNames.get(i)));

				if (i < variableTypes.size() - 1)
					System.out.print(", ");

			}

		}

		System.out.println("");

	}

}
