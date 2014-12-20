package kr.geul.dataobject.lib;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.text.BadLocationException;

import kr.geul.console.Console;

public class MergeSort {

	ArrayList<String> criteriaVariableTypesList, directions;
	int current = -1;
	int total;

	public MergeSort(ArrayList<String> criteriaVariableTypesList, ArrayList<String> directions) {
		this.criteriaVariableTypesList = criteriaVariableTypesList;
		this.directions = directions;
	}

	private boolean compare(ArrayList<Object> valuesCompared,
			ArrayList<Object> valuesPivot, int index) throws BadLocationException,
			ClassNotFoundException, InstantiationException, IllegalAccessException, 
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, 
			SecurityException {

		Object valueCompared = valuesCompared.get(index);
		Object valuePivot = valuesPivot.get(index);
		String type = criteriaVariableTypesList.get(index);		
		Boolean isLarger = null, isSmaller = null; 

		if (type.equals("int")) {		
			isLarger = (int) valueCompared > (int) valuePivot;
			isSmaller = (int) valueCompared < (int) valuePivot;		
		}

		else if (type.equals("double")) {
			isLarger = (double) valueCompared > (double) valuePivot;
			isSmaller = (double) valueCompared < (double) valuePivot;
		}

		else if (type.equals("string")) {
			isLarger = ((String) valueCompared).
					compareToIgnoreCase((String) valuePivot) > 0;
					isSmaller = ((String) valueCompared).
							compareToIgnoreCase((String) valuePivot) < 0;			
		}

		else if (type.equals("date")) {
			isLarger = (long) valueCompared > (long) valuePivot;
			isSmaller = (long) valueCompared < (long) valuePivot;
		}

		else 			
			Console.printErrorMessage("Unable to compare values.", "MergeSort");

		if (directions.get(index).equals("d")) {
			boolean temp = isLarger;
			isLarger = isSmaller;
			isSmaller = temp;
		}

		if (directions.get(index).equals("d") == false && directions.get(index).equals("a") == false)
			Console.printErrorMessage
			("'" + directions.get(index) + "' is not a valid direction option.", "MergeSort");

		if (isLarger == true)
			return true;

		else if (isSmaller == true)
			return false;

		else {

			if (index < criteriaVariableTypesList.size() - 1)
				return compare(valuesCompared, valuesPivot, index + 1);
			else
				return false;	

		}

	}

	private TaggedValues[] merge(TaggedValues[] firstHalf, TaggedValues[] secondHalf) 
			throws BadLocationException, ClassNotFoundException, 
			InstantiationException, IllegalAccessException, IllegalArgumentException, 
			InvocationTargetException, NoSuchMethodException, SecurityException {

		current++;
		TaggedValues[] temp = new TaggedValues[firstHalf.length + secondHalf.length];

		int firstHalfIndex = 0;
		int secondHalfIndex = 0;
		int tempIndex = 0;

		while (firstHalfIndex < firstHalf.length && secondHalfIndex < secondHalf.length) {

			ArrayList<Object> leftValues = secondHalf[secondHalfIndex].getValues();
			ArrayList<Object> rightValues = firstHalf[firstHalfIndex].getValues();

			boolean isLeftValueLarger = compare(leftValues, rightValues, 0);

			if (isLeftValueLarger == true) {
				temp[tempIndex++] = firstHalf[firstHalfIndex++];
			}

			else {
				temp[tempIndex++] = secondHalf[secondHalfIndex++];
			}

		}

		while (firstHalfIndex < firstHalf.length) {
			temp[tempIndex++] = firstHalf[firstHalfIndex++];
		}

		while (secondHalfIndex < secondHalf.length) {
			temp[tempIndex++] = secondHalf[secondHalfIndex++];
		}

		return temp;

	}

	public void sort(TaggedValues[] list) throws BadLocationException, 
	ClassNotFoundException, InstantiationException, IllegalAccessException, 
	IllegalArgumentException, InvocationTargetException, NoSuchMethodException, 
	SecurityException {	

		total = getTotal(list.length);
		doSort(list);

	}

	private int getTotal(int length) throws BadLocationException {

		int total = 0;
		int fullLength = length;

		if (length % 2 == 1)
			length -= 1;

		while (length / 2.0 >= 1) {

			length /= 2.0;
			if (length % 2.0 == 1.0)
				length -= 1;

			total += ((int) Math.round((double) fullLength / (double) length));

		}

		return total * 2;

	}

	public void doSort(TaggedValues[] list) throws BadLocationException, 
	ClassNotFoundException, InstantiationException, IllegalAccessException, 
	IllegalArgumentException, InvocationTargetException, NoSuchMethodException, 
	SecurityException {	

		if (list.length > 1) {

			current++;

			TaggedValues[] firstHalf = new TaggedValues[list.length / 2];
			System.arraycopy(list, 0, firstHalf, 0, list.length / 2);
			doSort(firstHalf);

			int secondHalfLength = list.length - (list.length / 2);
			TaggedValues[] secondHalf = new TaggedValues[secondHalfLength];
			System.arraycopy(list, list.length / 2, secondHalf, 0, secondHalfLength);
			doSort(secondHalf);

			TaggedValues[] temp = merge(firstHalf, secondHalf);
			System.arraycopy(temp, 0, list, 0, temp.length);

		}

	}

}