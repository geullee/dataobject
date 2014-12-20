package kr.geul.dataobject.command;

import java.util.ArrayList;

import kr.geul.console.Console;
import kr.geul.console.command.Command;
import kr.geul.dataobject.DataClass;
import kr.geul.dataobject.DataClassArray;
import kr.geul.dataobject.DataClassInfoHolder;
import kr.geul.dataobject.DataObject;
import kr.geul.dataobject.Observation;

public class SetObs extends Command {

	public SetObs(ArrayList<String> arguments) {
		super(arguments);	
	}

	@Override
	protected String getCommandName() {
		return "setobs";
	}

	@Override
	protected int[] getValidNumberOfArguments() {
		int[] numbers = {3, 4};
		return numbers;
	}

	@Override
	protected void runCommand() throws Exception {
		
		// Setup
		int amountOfAddedObservations = 0;
		boolean isNoFilter = false;
		boolean isSorted = false;
		
		String dataClassName = arguments.get(0);
		ArrayList<String> variableNames =
				Console.getAliasContentsList(arguments.get(1));
		ArrayList<String> rawVariableNames =
				Console.getAliasContentsList(arguments.get(2));
	
		DataClassArray dataClassArray = DataClass.getDataClassArray(dataClassName);
		DataClassArray rawObservationArray = DataClass.getDataClassArray("RawObservation");
		DataClassInfoHolder dataClassInfoHolder = dataClassArray.getInfoHolder();
		DataClassInfoHolder rawObservationInfoHolder = rawObservationArray.getInfoHolder();
		
		ArrayList<String> dataClassVariables =
				dataClassArray.getInfoHolder().getVariableNames();		
		ArrayList<String> rawObservationVariables =
				rawObservationArray.getInfoHolder().getVariableNames();		
		
		// Check the option argument
		if (arguments.size() == 4) {

			if (arguments.get(3).equals("nofilter"))
				isNoFilter = true;
			else if (arguments.get(3).equals("sorted")) {
				isNoFilter = true;
				isSorted = true;
			}
			
			else
				Console.printErrorMessage("'" + arguments.get(3) + "' is not a valid option.", 
						Thread.currentThread().getStackTrace()[1].getClassName());

		}	
		
		// Check existence
		String[] doesExist = DataObject.checkExistence
				(variableNames, dataClassVariables);

		if (doesExist[0].equals("false"))
			Console.printErrorMessage("There does not exist a variable '" + 
					doesExist[1] + "' in class '" + dataClassName + "'.",
					Thread.currentThread().getStackTrace()[1].getClassName());

		doesExist = DataObject.checkExistence
				(rawVariableNames, rawObservationVariables);

		if (doesExist[0].equals("false")) 
			Console.printErrorMessage("There does not exist a variable '" + 
					doesExist[1] + "' in class 'RawObservation'.",
					Thread.currentThread().getStackTrace()[1].getClassName());
		
		// Check ArrayList size
		if (variableNames.size() != rawVariableNames.size())
			Console.printErrorMessage("The number of variables are different for " +
					"the two variable lists.",
					Thread.currentThread().getStackTrace()[1].getClassName());
		
		// Start operation	
		tic();
		
		System.out.print("Setting observations for class '"
				+ dataClassName + "'...");
		
		for (int i = 0; i < rawObservationArray.size(); i++) {
			
			updateProgress(i, rawObservationArray.size());

			Observation rawObservation = rawObservationArray.get(i);
			boolean doesAlreadyExist = false;
			
			if (isNoFilter == false) {

				int k = 0;

				while (k < dataClassArray.size() && doesAlreadyExist == false) {

					Observation classObservation = dataClassArray.get(k);
					boolean isIdentical = true;

					for (int l = 0; l < variableNames.size(); l++) {

						String rawValueCompared = 
								rawObservation.getStringTypeVariable
								(rawObservationInfoHolder, rawVariableNames.get(l));
						String classValueCompared =
								classObservation.getStringTypeVariable
								(dataClassInfoHolder, variableNames.get(l));

						if (rawValueCompared != null &&
								classValueCompared != null &&
								rawValueCompared.equals(classValueCompared) == false)
							isIdentical = false;

					}

					if (isIdentical == true) 
						doesAlreadyExist = true;

					k++;

				}

			}
			
			if (isSorted == true && i > 0) {

				Observation lastRawObservation = rawObservationArray.get(i - 1);

				doesAlreadyExist = true;

				for (int j = 0; j < rawVariableNames.size(); j++) {

					String thisRawObservationValue =
							rawObservation.getStringTypeVariable
							(rawObservationInfoHolder, rawVariableNames.get(j));	
					String lastRawObservationValue =
							lastRawObservation.getStringTypeVariable
							(rawObservationInfoHolder, rawVariableNames.get(j));	

					if (thisRawObservationValue.equals(lastRawObservationValue) == false)
						doesAlreadyExist = false;

				}

			}
			
			if (doesAlreadyExist == false) {

				Observation newObservation = new Observation(dataClassInfoHolder);

				for (int j = 0; j < rawVariableNames.size(); j++) {

					String portedValue = 
							rawObservation.getStringTypeVariable
							(rawObservationInfoHolder, rawVariableNames.get(j));
					String portingVariable = variableNames.get(j);
					newObservation.setVariable
					(dataClassInfoHolder, portingVariable, portedValue);

				}

				amountOfAddedObservations++;
				dataClassArray.add(newObservation.getClone());

			}
			
		}
		
		toc();
		System.out.println(amountOfAddedObservations
				+ " new observations are added.");
		setProgress(0.0);
		
	}

}
