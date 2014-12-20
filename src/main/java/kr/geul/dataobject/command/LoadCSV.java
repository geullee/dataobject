package kr.geul.dataobject.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import kr.geul.console.Console;
import kr.geul.console.command.Command;
import kr.geul.dataobject.DataClass;
import kr.geul.dataobject.DataClassArray;
import kr.geul.dataobject.DataClassInfoHolder;
import kr.geul.dataobject.Observation;

public class LoadCSV extends Command {

	public LoadCSV(ArrayList<String> arguments) {
		super(arguments);	
	}

	@Override
	protected String getCommandName() {
		return "loadcsv";
	}

	@Override
	protected int[] getValidNumberOfArguments() {
		int[] numbers = {1, 2};
		return numbers;
	}

	@Override
	protected void runCommand() throws Exception {
		String fileName = arguments.get(0);
		ArrayList<String> variablesRequested = null;

		if (arguments.size() == 2) 
			variablesRequested = Console.getAliasContentsList(arguments.get(1));
		
		File file = getFileName(fileName);
		String line;

		if (file != null) {

			BufferedReader bufferedReader = 
					new BufferedReader(new FileReader(file));
			DataClassArray rawObservations = null;
			DataClassInfoHolder infoHolder = null;
			ArrayList<String> variableNames = null;
			ArrayList<Integer> variableLocations = null;
			int totalNumbers = 0;

			tic();

			System.out.print("Loading file " + fileName + "...");

			while ((line = bufferedReader.readLine()) != null) {
				totalNumbers++;
			}

			bufferedReader.close();
			bufferedReader = 
					new BufferedReader(new FileReader(file));

			setProgress(0.0);

			for (int i = 0; i < totalNumbers; i++) {

				line = bufferedReader.readLine();
				String[] rawValues = line.split(",", -1);

				if (i == 0) {

					variableNames = getVariables(rawValues);
					variableLocations = new ArrayList<Integer>();
					for (int j = 0; j < variableNames.size(); j++) {
						variableLocations.add(j);
					}
					
					if (arguments.size() == 2) {

						variableLocations = getRequestedVariableLocations
								(variableNames, variablesRequested);
						variableNames = pickRequestedVariables
								(variableNames, variablesRequested);
								
					}
					
					infoHolder = getRawObservationInfoHolder(variableNames);
					rawObservations = new DataClassArray(infoHolder);
					
				}

				else {

					Observation observation = new Observation(infoHolder);

					for (int j = 0; j < variableNames.size(); j++) {
						observation.setVariable
						(infoHolder, variableNames.get(j), rawValues[variableLocations.get(j)]);							
					}

					rawObservations.add((Observation) observation.getClone());

				}

				updateProgress(i, totalNumbers);

			}

			toc();

			System.out.println(rawObservations.size()
					+ " observations retrieved.");
			DataClass.addDataClass(rawObservations);

			bufferedReader.close();

		}
		
	}

	private static File getFileName(String fileName) {
		
		File file = null;
		File rawArgumentFile = new File(fileName);
		File fullAddressFile = new File(Console.getDefaultPath() + fileName);
		
		if (rawArgumentFile.exists() == true && fullAddressFile.exists() == true)
			file = fullAddressFile;
		else if (rawArgumentFile.exists() == false && fullAddressFile.exists() == true)
			file = fullAddressFile;
		else if (rawArgumentFile.exists() == true && fullAddressFile.exists() == false)
			file = rawArgumentFile;
		else
			System.out.println("File does not exist.");	
		
		return file;
		
	}
	
	private DataClassInfoHolder getRawObservationInfoHolder(
			ArrayList<String> variableNames) {

		DataClassInfoHolder rawObservationInfoHolder = new DataClassInfoHolder();
		ArrayList<String> variableTypes = new ArrayList<String>();

		for (int i = 0; i < variableNames.size(); i++) {
			variableTypes.add("string");
		}

		rawObservationInfoHolder.setClassName("RawObservation");
		rawObservationInfoHolder.setVariableNames(variableNames);
		rawObservationInfoHolder.setVariableTypes(variableTypes);

		return rawObservationInfoHolder;

	}

	private ArrayList<Integer> getRequestedVariableLocations(
			ArrayList<String> variableNames,
			ArrayList<String> variablesRequested) {

		ArrayList<Integer> result = new ArrayList<Integer>();
		
		for (int i = 0; i < variablesRequested.size(); i++) {
			
			String variableRequested = variablesRequested.get(i);
			
			for (int j = 0; j < variableNames.size(); j++) {
				
				String variableName = variableNames.get(j);
				
				if (variableName.equals(variableRequested)) {
					result.add(j);
					break;
				}
				
			}
			
		}
		
		return result;
		
	}
	
	private ArrayList<String> getVariables(String[] token) {

		ArrayList<String> variables = new ArrayList<String>();

		for (int i = 0; i < token.length; i++) {
			variables.add(token[i]);
		}

		return variables;

	}

	private ArrayList<String> pickRequestedVariables(
			ArrayList<String> variableNames,
			ArrayList<String> variablesRequested) {
		
		ArrayList<String> result = new ArrayList<String>();
		
		for (int i = 0; i < variablesRequested.size(); i++) {
			
			String variableRequested = variablesRequested.get(i);
			
			for (int j = 0; j < variableNames.size(); j++) {
				
				String variableName = variableNames.get(j);
				
				if (variableName.equals(variableRequested)) {
					result.add(variableName);
					break;
				}
				
			}
			
		}
		
		return result;
		
	}

}





