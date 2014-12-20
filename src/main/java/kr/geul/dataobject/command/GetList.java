package kr.geul.dataobject.command;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.text.BadLocationException;

import kr.geul.console.Console;
import kr.geul.console.command.Command;
import kr.geul.dataobject.DataClass;
import kr.geul.dataobject.DataClassArray;
import kr.geul.dataobject.DataClassInfoHolder;
import kr.geul.dataobject.DataObject;
import kr.geul.dataobject.Observation;

public class GetList extends Command {

	ArrayList<DataClassInfoHolder> obListSubInfoHolders;
	ArrayList<String> criteriaVariableNames, sourceDataClassVariableNames, 
	targetListAddress, targetDataClassVariableNames, targetDataClassVariableTypes,
	obListSubInfoHolderTags;
	DataClassArray sourceDataClassArray, targetDataClassArray;
	DataClassInfoHolder sourceDataClassInfoHolder, targetDataClassInfoHolder, newInfoHolder;
	int observationsIncluded;
	String targetListName;

	public GetList(ArrayList<String> arguments) {
		super(arguments);
	}

	@Override
	protected String getCommandName() {
		return "getlist";
	}

	@Override
	protected int[] getValidNumberOfArguments() {
		int[] numbers = {3, 4};
		return numbers;
	}

	@Override
	protected void runCommand() throws Exception {
		
		targetListAddress = DataObject.getPartitionedTokens(arguments.get(0));
		targetListName = targetListAddress.get(1);
		targetDataClassArray = DataClass.getDataClassArray(targetListAddress.get(0));
		sourceDataClassArray = DataClass.getDataClassArray(arguments.get(1));
		criteriaVariableNames = Console.getAliasContentsList(arguments.get(2));

		sourceDataClassInfoHolder = sourceDataClassArray.getInfoHolder();
		targetDataClassInfoHolder = targetDataClassArray.getInfoHolder();
		sourceDataClassVariableNames = sourceDataClassInfoHolder.getVariableNames();
		targetDataClassVariableNames = targetDataClassInfoHolder.getVariableNames();
		targetDataClassVariableTypes = targetDataClassInfoHolder.getVariableTypes();
		
		obListSubInfoHolders = new ArrayList<DataClassInfoHolder>();
		obListSubInfoHolderTags = new ArrayList<String>();
		
		checkTargetListAppropriateness();
		checkCriteriaVariables();
		
		System.out.print("Building list '" + targetListName +
				"' for each '" + targetDataClassArray.getInfoHolder().getClassName() +
				"' observation...");

		tic();

		observationsIncluded = 0;		
		reduceInfoHolder();
		constructSubInfoHolders(newInfoHolder, sourceDataClassArray, targetListName);
		
		if (obListSubInfoHolders.size() > 0) {

			for (int i = 0; i < obListSubInfoHolders.size(); i++) {
				targetDataClassArray.addSubInfoHolder
				(obListSubInfoHolders.get(i), obListSubInfoHolderTags.get(i));
			}

		}
		
		if (arguments.size() == 4) {

			if (arguments.get(3).equals("sorted"))
				doQuickSingleOperation();

			else if (arguments.get(3).equals("rep"))
				doNormalRepeatedOperation();

			else if (arguments.get(3).equals("sortedrep")) 
				doQuickRepeatedOperation();

			else if (arguments.get(3).equals("asortedrep")) 
				doAscendingQuickRepeatedOperation();

			else
				Console.printErrorMessage("'" + arguments.get(3) + "' is not a valid option.", 
						Thread.currentThread().getStackTrace()[1].getClassName());
		}

		else
			doNormalSingleOperation();

		toc();

		System.out.println(observationsIncluded	+ " '" +
				sourceDataClassArray.getInfoHolder().getClassName() +
				"' observations are included in list variables.");
		
	}

	private void checkCriteriaVariables() throws ClassNotFoundException, 
	InstantiationException, IllegalAccessException, IllegalArgumentException, 
	InvocationTargetException, NoSuchMethodException, SecurityException {

		DataObject.checkExistence(criteriaVariableNames, targetDataClassVariableNames);
		DataObject.checkExistence(criteriaVariableNames, sourceDataClassVariableNames);

		for (int i = 0; i < criteriaVariableNames.size(); i++) {

			String targetCriteriaVariableType = targetDataClassArray.getInfoHolder().
					getVariableType(criteriaVariableNames.get(i));
			String sourceCriteriaVariableType = sourceDataClassArray.getInfoHolder().
					getVariableType(criteriaVariableNames.get(i));

			if (targetCriteriaVariableType.equals(sourceCriteriaVariableType) == false) {
				Console.printErrorMessage("Type of criterion variable '" + 
						criteriaVariableNames.get(i) + 
						"' is different in the source and target classes.", 
						Thread.currentThread().getStackTrace()[1].getClassName());
			}

		}		

	}

	private void checkTargetListAppropriateness() throws ClassNotFoundException, 
	InstantiationException, IllegalAccessException, IllegalArgumentException, 
	InvocationTargetException, NoSuchMethodException, SecurityException { 

		@SuppressWarnings("unused")
		boolean doesExist = false;

		for (int i = 0; i < targetDataClassVariableNames.size(); i++) {

			String variableName = targetDataClassVariableNames.get(i);
			String variableType = targetDataClassVariableTypes.get(i);

			if (targetListName.equals(variableName)) {

				if (variableType.equals("list") || variableType.equals("oblist")) 
					doesExist = true;
				else
					Console.printErrorMessage("The target variable '" + variableName + 
							"' is not a list variable.", 
							Thread.currentThread().getStackTrace()[1].getClassName());

			}

		}

		if (doesExist = false) 
			Console.printErrorMessage("Unable to find the target variable '" + targetListName + 
					"' in class '" + targetDataClassArray.getInfoHolder().getClassName() + "'.", 
					Thread.currentThread().getStackTrace()[1].getClassName());

	}

	private void constructSubInfoHolders
	(DataClassInfoHolder infoHolder, DataClassArray dataClassArray, String address) 
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, 
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, 
			SecurityException, BadLocationException {

		ArrayList<String> variableNames = infoHolder.getVariableNames();
		ArrayList<String> variableTypes = infoHolder.getVariableTypes();

		for (int i = 0; i < variableTypes.size(); i++) {

			String variableType = variableTypes.get(i);

			if (variableType.equals("obs") || variableType.equals("oblist")) {
			
				DataClassInfoHolder obListSubInfoHolder = 
						dataClassArray.getSubInfoHolder(variableNames.get(i));
				
				if (obListSubInfoHolder.getClassName().equals("NULL") == false) {
				
					DataClassArray nextDataClassArray =
							DataClass.getDataClassArray
							(obListSubInfoHolder.getClassName());
					String obListSubInfoHolderTag = 
							address + "/" + variableNames.get(i);

					obListSubInfoHolders.add(obListSubInfoHolder);
					obListSubInfoHolderTags.add(obListSubInfoHolderTag);
					constructSubInfoHolders
					(obListSubInfoHolder, nextDataClassArray, obListSubInfoHolderTag);
					
				}

			}

		}

	}

	private void doAscendingQuickRepeatedOperation() throws ClassNotFoundException, 
	InstantiationException, IllegalAccessException, IllegalArgumentException, 
	InvocationTargetException, NoSuchMethodException, SecurityException, 
	BadLocationException, NoSuchFieldException {

		ArrayList<Integer> startPoints = getStartPoints(sourceDataClassArray);

		for (int i = 0; i < targetDataClassArray.size(); i++) {

			updateProgress(i, targetDataClassArray.size());
			Observation targetObservation = targetDataClassArray.get(i);

			int startPointIndex = getOptimizedStartPointForASortedRep(targetObservation, startPoints);

			boolean isDone = false;
			
			do {

				Observation startPointObservation = 
						sourceDataClassArray.get(startPoints.get(startPointIndex));

				if (isEquivalent(targetObservation, startPointObservation) == true) {

					for (int j = startPoints.get(startPointIndex); 
							j < (startPoints.size() > 1 ? startPoints.get(startPointIndex + 1) : 
								sourceDataClassArray.size()); j++) {

						targetObservation.getObListVariable(targetDataClassInfoHolder, targetListName).
						add(getReducedObservation(sourceDataClassArray.get(j), 
								sourceDataClassInfoHolder, newInfoHolder));
						observationsIncluded++;

					}

					isDone = true;

				}

				else				
					startPointIndex++;

			} while (isDone == false && startPointIndex < startPoints.size() - 1);

			if (isDone == false) {

				Observation startPointObservation = 
						sourceDataClassArray.get(startPoints.get(startPoints.size() - 1));

				if (isEquivalent(targetObservation, startPointObservation) == true) {

					for (int j = startPoints.get(startPoints.size() - 1); 
							j < sourceDataClassArray.size(); j++) {

						targetObservation.getObListVariable(targetDataClassInfoHolder, targetListName).
						add(getReducedObservation(sourceDataClassArray.get(j), 
								sourceDataClassInfoHolder, newInfoHolder));
						observationsIncluded++;

					}

					isDone = true;

				}

			}

		}		

	}

	private void doNormalRepeatedOperation() throws ClassNotFoundException, InstantiationException, 
	IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
	NoSuchMethodException, SecurityException, NoSuchFieldException, 
	BadLocationException {

		for (int i = 0; i < targetDataClassArray.size(); i++) {

			updateProgress(i, targetDataClassArray.size());
			Observation targetObservation = targetDataClassArray.get(i);

			int sourceDataClassArrayIndex = 0;
			boolean isDone = false;

			do {

				Observation sourceObservation = 
						sourceDataClassArray.get(sourceDataClassArrayIndex);

				if (isEquivalent(targetObservation, sourceObservation) == true) {

					targetObservation.getObListVariable(targetDataClassInfoHolder, targetListName).
					add(getReducedObservation(sourceDataClassArray.get(sourceDataClassArrayIndex), 
							sourceDataClassInfoHolder, newInfoHolder));
					observationsIncluded++;

					isDone = true;

				}

				else				
					sourceDataClassArrayIndex++;

			} while (isDone == false && sourceDataClassArrayIndex < sourceDataClassArray.size());

		}		

	}

	private void doNormalSingleOperation() throws NoSuchFieldException, SecurityException, 
	ClassNotFoundException, InstantiationException, IllegalAccessException, 
	IllegalArgumentException, InvocationTargetException, NoSuchMethodException, 
	BadLocationException {

		for (int i = 0; i < sourceDataClassArray.size(); i++) {

			updateProgress(i, sourceDataClassArray.size());
			Observation sourceObservation = sourceDataClassArray.get(i);

			int targetDataClassArrayIndex = 0;
			boolean isDone = false;

			do {

				Observation targetObservation = 
						targetDataClassArray.get(targetDataClassArrayIndex);

				if (isEquivalent(targetObservation, sourceObservation) == true) {

					targetObservation.getObListVariable(targetDataClassInfoHolder, targetListName).
					add(getReducedObservation(sourceDataClassArray.get(i), 
							sourceDataClassInfoHolder, newInfoHolder));
					observationsIncluded++;
					isDone = true;

				}

				else
					targetDataClassArrayIndex++;

			} while (isDone == false && targetDataClassArrayIndex < targetDataClassArray.size());

		}

	}

	private void doQuickRepeatedOperation() throws ClassNotFoundException, InstantiationException, 
	IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
	NoSuchMethodException, SecurityException, BadLocationException, NoSuchFieldException {

		ArrayList<Integer> startPoints = getStartPoints(sourceDataClassArray);

		for (int i = 0; i < targetDataClassArray.size(); i++) {

			updateProgress(i, targetDataClassArray.size());
			Observation targetObservation = targetDataClassArray.get(i);

			int startPointIndex = 0;
			boolean isDone = false;

			do {

				Observation startPointObservation = 
						sourceDataClassArray.get(startPoints.get(startPointIndex));

				if (isEquivalent(targetObservation, startPointObservation) == true) {

					for (int j = startPoints.get(startPointIndex); 
							j < startPoints.get(startPointIndex + 1); j++) {

						targetObservation.getObListVariable(targetDataClassInfoHolder, targetListName).
						add(getReducedObservation(sourceDataClassArray.get(j), 
								sourceDataClassInfoHolder, newInfoHolder));
						observationsIncluded++;

					}

					isDone = true;

				}

				else				
					startPointIndex++;

			} while (isDone == false && startPointIndex < startPoints.size() - 1);

			if (isDone == false) {

				Observation startPointObservation = 
						sourceDataClassArray.get(startPoints.get(startPoints.size() - 1));

				if (isEquivalent(targetObservation, startPointObservation) == true) {

					for (int j = startPoints.get(startPoints.size() - 1); 
							j < sourceDataClassArray.size(); j++) {

						targetObservation.getObListVariable(targetDataClassInfoHolder, targetListName).
						add(getReducedObservation(sourceDataClassArray.get(j), 
								sourceDataClassInfoHolder, newInfoHolder));
						observationsIncluded++;

					}

					isDone = true;

				}

			}

		}

	}

	private void doQuickSingleOperation() throws ClassNotFoundException, InstantiationException, 
	IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
	NoSuchMethodException, SecurityException, NoSuchFieldException, 
	BadLocationException {

		ArrayList<Integer> startPoints = getStartPoints(targetDataClassArray);

		for (int i = 0; i < sourceDataClassArray.size(); i++) {

			updateProgress(i, sourceDataClassArray.size());
			Observation targetObservation = sourceDataClassArray.get(i);

			int startPointIndex = 0;
			boolean isDone = false;

			do {

				Observation startPointObservation = 
						targetDataClassArray.get(startPoints.get(startPointIndex));

				if (isEquivalent(targetObservation, startPointObservation) == true) {

					for (int j = startPoints.get(startPointIndex); 
							j < startPoints.get(startPointIndex + 1); j++) {

						targetObservation.getObListVariable(targetDataClassInfoHolder, targetListName).
						add(getReducedObservation(sourceDataClassArray.get(j), 
								sourceDataClassInfoHolder, newInfoHolder));
						observationsIncluded++;

					}

					isDone = true;

				}

				else				
					startPointIndex++;

			} while (isDone == false && startPointIndex < startPoints.size() - 1);

			if (isDone == false) {

				Observation startPointObservation = 
						targetDataClassArray.get(startPoints.get(startPoints.size() - 1));

				if (isEquivalent(targetObservation, startPointObservation) == true) {

					for (int j = startPoints.get(startPoints.size() - 1); 
							j < targetDataClassArray.size(); j++) {

						targetObservation.getObListVariable(targetDataClassInfoHolder, targetListName).
						add(getReducedObservation(sourceDataClassArray.get(j), 
								sourceDataClassInfoHolder, newInfoHolder));
						observationsIncluded++;

					}

					isDone = true;

				}

			}

		}

	}

	private int getOptimizedStartPointForASortedRep(
			Observation targetObservation, ArrayList<Integer> startPoints) 
					throws BadLocationException, ClassNotFoundException, 
					InstantiationException, IllegalAccessException, IllegalArgumentException, 
					InvocationTargetException, NoSuchMethodException, SecurityException {

		if (startPoints.size() == 1)
			return 0;
		
		else {
		
			long location = Math.round((double) startPoints.size() / 2.0);
			long distance = Math.round((double) startPoints.size() / 2.0);

			Observation startPointObservation;

			do {

				startPointObservation = sourceDataClassArray.get(startPoints.get((int) location));

				if (isLarger(targetObservation, startPointObservation) == true) {
					if (location + Math.round((double) distance / 2.0) >= startPoints.size()) 
						return (int) location;
					else
						location += Math.round((double) distance / 2.0);
				}

				else {
					if (location - Math.round((double) distance / 2.0) < 0)
						return 0;
					else
						location -= Math.round((double) distance / 2.0);
				}

				distance = Math.round((double) distance / 2.0);

			} while (distance > 1000);

			if (isLarger(targetObservation, startPointObservation) == false) {

				if (location - (distance * 4) > 0)
					return (int) (location - (distance * 4));		
				else 
					return 0;	

			}

			else {

				if (location - (distance * 2) > 0)
					return (int) (location - (distance * 2));
				else 
					return 0;

			}
			
		}

	}

	private Observation getReducedObservation(Observation oldObservation,
			DataClassInfoHolder oldInfoHolder, DataClassInfoHolder newInfoHolder) 
					throws ClassNotFoundException, InstantiationException, IllegalAccessException, 
					IllegalArgumentException, InvocationTargetException, NoSuchMethodException, 
					SecurityException, BadLocationException, NoSuchFieldException {

		Observation newObservation = new Observation(newInfoHolder);

		for (int i = 0; i < newInfoHolder.getVariableNames().size(); i++) {

			String variableName = newInfoHolder.getVariableNames().get(i);
			String variableType = newInfoHolder.getVariableTypes().get(i);

			if (variableType.equals("oblist"))
				newObservation.setVariable
				(newInfoHolder, variableName, 
						oldObservation.getObListVariable(oldInfoHolder, variableName));
			else if (variableType.equals("list"))
				newObservation.setVariable
				(newInfoHolder, variableName, 
						oldObservation.getListVariable(oldInfoHolder, variableName));
			else
				newObservation.setVariable
				(newInfoHolder, variableName, 
						oldObservation.getStringTypeVariable(oldInfoHolder, variableName));

		}

		return newObservation;

	}

	private ArrayList<Integer> getStartPoints(DataClassArray dataClassArray) throws BadLocationException, 
	ClassNotFoundException, InstantiationException, 
	IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
	NoSuchMethodException, SecurityException {

		ArrayList<Integer> startPoints = new ArrayList<Integer>();

		for (int i = 1; i < dataClassArray.size(); i++) {

			Observation thisObservation = dataClassArray.get(i);
			Observation lastObservation = dataClassArray.get(i - 1);

			if (i == 1)
				startPoints.add(0);

			if (isEquivalent(thisObservation, lastObservation) == false) 
				startPoints.add(i);		

		}

		return startPoints;	

	}

	private boolean isEquivalent(Observation sourceObservation,
			Observation targetObservation) throws BadLocationException,
			ClassNotFoundException, InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException {

		boolean isEquivalent = true;

		int baseVariableLocator = 0;

		do {

			String sourceValue = sourceObservation
					.getStringTypeVariable(sourceDataClassInfoHolder, 
							criteriaVariableNames.get(baseVariableLocator));
			String targetValue = targetObservation
					.getStringTypeVariable(targetDataClassInfoHolder,
							criteriaVariableNames.get(baseVariableLocator));

			if (sourceValue == null || targetValue == null
					|| sourceValue.equals(targetValue) == false)
				isEquivalent = false;

			baseVariableLocator++;

		} while (baseVariableLocator < criteriaVariableNames.size()
				&& isEquivalent == true);

		return isEquivalent;

	}

	private boolean isLarger(Observation targetObservation,
			Observation startPointObservation) throws BadLocationException, 
			ClassNotFoundException, InstantiationException, IllegalAccessException, 
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, 
			SecurityException {

		String firstCriterionName = criteriaVariableNames.get(0);
		String targetFirstCriterionValue = 
				targetObservation.getStringTypeVariable(targetDataClassInfoHolder, firstCriterionName);
		String startPointFirstCriterionValue = 
				startPointObservation.getStringTypeVariable(targetDataClassInfoHolder, firstCriterionName);
		String variableType = 
				targetObservation.getVariableType(targetDataClassInfoHolder, firstCriterionName);

		if (variableType.equals("int")) {	

			if (Integer.parseInt(targetFirstCriterionValue) >
			Integer.parseInt(startPointFirstCriterionValue))
				return true;
			else
				return false;

		}

		else if (variableType.equals("double")) {	

			if (Double.parseDouble(targetFirstCriterionValue) >
			Double.parseDouble(startPointFirstCriterionValue))
				return true;
			else
				return false;

		}	

		else if (variableType.equals("string")) {	

			if (targetFirstCriterionValue.
					compareToIgnoreCase(startPointFirstCriterionValue) > 0)
				return true;
			else
				return false;

		}	

		else if (variableType.equals("date")) {	

			if (Integer.parseInt(targetFirstCriterionValue) >
			Integer.parseInt(startPointFirstCriterionValue))
				return true;
			else
				return false;

		}	

		else 		
			Console.printErrorMessage("Unable to compare values.",
					Thread.currentThread().getStackTrace()[1].getClassName());	
		return false;

	}

	@SuppressWarnings("unchecked")
	private void reduceInfoHolder() {

		DataClassInfoHolder infoHolder = new DataClassInfoHolder();
		infoHolder.setClassName(sourceDataClassInfoHolder.getClassName());

		ArrayList<String> variableNames = 
				(ArrayList<String>) sourceDataClassInfoHolder.getVariableNames().clone();
		ArrayList<String> variableTypes = 
				(ArrayList<String>)	sourceDataClassInfoHolder.getVariableTypes().clone();

		for (int i = variableNames.size() - 1; i > -1; i--) {

			String variableName = variableNames.get(i);

			for (int j = 0; j < criteriaVariableNames.size(); j++) {

				if (variableName.equals(criteriaVariableNames.get(j))) {
					variableNames.remove(i);
					variableTypes.remove(i);
					break;
				}

			}

		}

		infoHolder.setVariableNames(variableNames);
		infoHolder.setVariableTypes(variableTypes);

		newInfoHolder = infoHolder;
		targetDataClassArray.addSubInfoHolder(newInfoHolder, targetListName);
		
	}

}
