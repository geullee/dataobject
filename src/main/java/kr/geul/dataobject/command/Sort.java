package kr.geul.dataobject.command;

import java.lang.reflect.InvocationTargetException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.text.BadLocationException;

import kr.geul.console.Console;
import kr.geul.console.command.Command;
import kr.geul.dataobject.DataClass;
import kr.geul.dataobject.DataClassArray;
import kr.geul.dataobject.DataClassInfoHolder;
import kr.geul.dataobject.DataObject;
import kr.geul.dataobject.ObList;
import kr.geul.dataobject.Observation;
import kr.geul.dataobject.lib.MergeSort;
import kr.geul.dataobject.lib.TaggedValues;

public class Sort extends Command {

	ArrayList<String> criteriaVariableNamesList, criteriaVariableTypesList,
	directions, targetVariableAddress;
	boolean isSubArray = false, isRawObservation = false;
	DataClassArray dataClassArray;

	public Sort (ArrayList<String> arguments) {
		super(arguments);
	}

	@Override
	protected String getCommandName() {
		return "sort";
	}

	@Override
	protected int[] getValidNumberOfArguments() {
		int[] numbers = {3, 4};
		return numbers;
	}

	@Override
	protected void runCommand() throws Exception {
		
		targetVariableAddress =
				DataObject.getPartitionedTokens(arguments.get(0));
		dataClassArray = 
				DataClass.getDataClassArray(targetVariableAddress.get(0));
		criteriaVariableNamesList =
				Console.getAliasContentsList(arguments.get(1));
		directions = Console.getAliasContentsList(arguments.get(2));

		if (targetVariableAddress.size() > 1)
			isSubArray = true;

		if (arguments.size() == 4) {

			String option = arguments.get(3);

			if (option.equals("raw"))
				isRawObservation = true;

		}

		System.out.print("Sorting ");

		for (int i = 0; i < targetVariableAddress.size(); i++) {

			System.out.print(targetVariableAddress.get(i));
			if (i < targetVariableAddress.size() - 1)
				System.out.print("/");

		}

		System.out.print("...");

		tic();

		loopedSort();

		toc();
		
	}

	protected String getVariableType(String variableName, 
			DataClassInfoHolder infoHolder) throws ClassNotFoundException, 
			InstantiationException, IllegalAccessException, IllegalArgumentException, 
			InvocationTargetException, NoSuchMethodException, SecurityException {
		
		ArrayList<String> variableNameList = 
				infoHolder.getVariableNames();
		ArrayList<String> variableTypeList = 
				infoHolder.getVariableTypes();
		
		boolean doesExist = false;
		String variableType = null;
		
		for (int i = 0; i < variableNameList.size(); i++) {
			
			if (variableNameList.get(i).equals(variableName)) {
				
				variableType = variableTypeList.get(i);				
				doesExist = true;
				
			}		
			
		}
		
		if (doesExist == false) {
			Console.printErrorMessage("There does not exist a variable '" + variableName + 
					"' in class '" + infoHolder.getClassName() + ".",
					Thread.currentThread().getStackTrace()[1].getClassName());
			return null;
		}
		
		else
			return variableType;
		
	}
	
	private void loopedSort() throws BadLocationException, 
	NoSuchFieldException, SecurityException, ClassNotFoundException, 
	InstantiationException, IllegalAccessException, IllegalArgumentException, 
	InvocationTargetException, NoSuchMethodException {

		if (isSubArray == true) {

			for (int i = 0; i < dataClassArray.size(); i++) {
				Observation observation = dataClassArray.get(i);
				loopedSort(observation, 1);
			}

		}

		else {
			DataClassArray newDataClassArray = (DataClassArray) sort(dataClassArray, true); 
			DataClass.resetDataClass(newDataClassArray);
		}


	}

	private void loopedSort(Observation observation, int index) 
			throws NoSuchFieldException, SecurityException, BadLocationException, 
			ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {

		ObList observationList = 
				observation.getObListVariable
				(dataClassArray.getInfoHolder(), targetVariableAddress.get(index));

		if (targetVariableAddress.size() > index + 1) {

			for (int i = 0; i < observationList.size(); i++) {
				Observation subObservation = observationList.get(i);
				loopedSort(subObservation, index + 1);
			}

		}

		else {

			ObList newObservationList = 
					(ObList) sort(observationList, false);
			observation.setVariable
			(dataClassArray.getInfoHolder(), targetVariableAddress.get(index), newObservationList);

		}		

	}

	private AbstractList<?> sort(AbstractList<?> array, boolean isDataClassArray) 
			throws BadLocationException, ClassNotFoundException, 
			InstantiationException, IllegalAccessException, IllegalArgumentException, 
			InvocationTargetException, NoSuchMethodException, SecurityException {	

		if (array != null && array.size() > 0) {

			AbstractList<?> newArray;
			criteriaVariableTypesList = new ArrayList<String>();
			DataClassInfoHolder infoHolder = null;
			
			if (array.getClass().getCanonicalName().equals("kr.geul.dataobject.DataClassArray")) {
				infoHolder = ((DataClassArray) array).getInfoHolder();
			}
			
			else if (array.getClass().getCanonicalName().equals("kr.geul.dataobject.ObList")) {
				infoHolder = ((ObList) array).getInfoHolder();
			}
			
			for (int i = 0; i < criteriaVariableNamesList.size(); i++) {

				String criterion = criteriaVariableNamesList.get(i);
				
				String criterionType = getVariableType(criterion, infoHolder);		
				criteriaVariableTypesList.add(criterionType);

				if (criterionType.equals("list") || criterionType.equals("oblist") || 
						criterionType.equals("obs"))
					Console.printErrorMessage
						("A list or observation variable cannot be a criterion", criterion);

			}


			if (isDataClassArray == true) {		
				newArray = new DataClassArray(((DataClassArray)array).getInfoHolder()); 
			}

			else {	
				newArray = new ObList(); 			
			}

			TaggedValues[] taggedValues = new TaggedValues[array.size()];				

			for (int i = 0; i < array.size(); i++) {

				taggedValues[i] = new TaggedValues();
				taggedValues[i].setIndex(i);

				for (int j = 0; j < criteriaVariableNamesList.size(); j++) {

					if (criteriaVariableTypesList.get(j).equals("date")) {
						Calendar calendar = ((Observation)array.get(i)).
								getDateVariable(infoHolder, criteriaVariableNamesList.get(j));
						taggedValues[i].addValue(calendar.getTimeInMillis());
					}

					else 
						taggedValues[i].addValue(((Observation)array.get(i)).
								getVariable(infoHolder, criteriaVariableNamesList.get(j)));

				} 

			}

			MergeSort mergeSort = new MergeSort(criteriaVariableTypesList, directions);
			mergeSort.sort(taggedValues);


			if (isDataClassArray == true) {		

				for (int i = 0; i < ((DataClassArray)array).size(); i++) {
					((DataClassArray)newArray).add
					((Observation)(array.get(taggedValues[i].getIndex())));

				} 			

			}
	
			else {
				for (int i = 0; i < ((ObList)array).size(); i++) {	
					((ObList)newArray).add
					((Observation)(array.get(taggedValues[i].getIndex())));
				}			

			}

			return newArray;

		}

		else
			return array;

	}
	
}
