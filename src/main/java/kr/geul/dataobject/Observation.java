package kr.geul.dataobject;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.text.BadLocationException;

import kr.geul.console.Console;

public class Observation implements Cloneable, Serializable {

	private static final long serialVersionUID = 5112138744000227619L;
	private ArrayList<Object> variableValues = new ArrayList<Object>();

	public Observation(DataClassInfoHolder infoHolder) {

		for (int i = 0; i < infoHolder.getVariableNames().size(); i++) {

			switch (infoHolder.getVariableTypes().get(i)) {

			case "list":
				variableValues.add(new ArrayList<Object>());
				break;

			case "oblist":
				variableValues.add(new ObList());
				break;
				
			default:
				variableValues.add(null);
				break;

			}

		}

	}

	public void addVariableValue(Object object) {
		variableValues.add(object);
	}

	public Observation getClone() throws CloneNotSupportedException {
		return (Observation) this.clone();
	}

	public Calendar getDateVariable(DataClassInfoHolder infoHolder, String variableName)
			throws BadLocationException, ClassNotFoundException, InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException {

		String variableType = getVariableType(infoHolder, variableName);

		if (variableType.equals("date") == false) {

			Console.printErrorMessage("Variable '" + variableName + "' is not a date Variable.",
					Thread.currentThread().getStackTrace()[1].getClassName());
			return null;

		}

		else {

			int variablePosition = getVariablePosition(infoHolder, variableName);
			String originalValue = (String) variableValues
					.get(variablePosition);

			return toCalendar(originalValue);

		}

	}

	@SuppressWarnings("unchecked")
	public ArrayList<Observation> getListVariable(DataClassInfoHolder infoHolder, String variableName)
			throws BadLocationException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException {

		String variableType = getVariableType(infoHolder, variableName);

		if (variableType.equals("list") == false) {

			Console.printErrorMessage("Variable '" + variableName + "' is not a list variable.",
					Thread.currentThread().getStackTrace()[1].getClassName());
			return null;

		}

		else {

			int variablePosition = getVariablePosition(infoHolder, variableName);
			return (ArrayList<Observation>) variableValues
					.get(variablePosition);

		}

	}

	public ObList getObListVariable(DataClassInfoHolder infoHolder, String variableName)
			throws BadLocationException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException {

		String variableType = getVariableType(infoHolder, variableName);

		if (variableType.equals("oblist") == false) {

			Console.printErrorMessage
				("Variable '" + variableName + "' is not a oblist variable.",
				Thread.currentThread().getStackTrace()[1].getClassName());
			return null;

		}

		else {

			int variablePosition = getVariablePosition(infoHolder, variableName);
			return (ObList) variableValues.get(variablePosition);

		}

	}
	
	public Observation getObservationVariable(DataClassInfoHolder infoHolder, String variableName)
			throws BadLocationException, ClassNotFoundException, InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException {

		String variableType = getVariableType(infoHolder, variableName);

		if (variableType.equals("obs") == false) {

			Console.printErrorMessage
				("Variable '" + variableName + "' is not an observation variable.",
				Thread.currentThread().getStackTrace()[1].getClassName());
			return null;

		}

		else {
			int variablePosition = getVariablePosition(infoHolder, variableName);
			return (Observation) variableValues.get(variablePosition);
		}

	}

	public String getStringTypeVariable(DataClassInfoHolder infoHolder, String variableName)
			throws BadLocationException, ClassNotFoundException, InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException {

		String variableType = getVariableType(infoHolder, variableName);
		if (variableType.equals("list") || variableType.equals("obs")) {

			Console.printErrorMessage("Variable '" + variableName + "' does not have a string form.",
					Thread.currentThread().getStackTrace()[1].getClassName());
			return null;

		}

		else {
			int variablePosition = getVariablePosition(infoHolder, variableName);

			if (variableValues.size() <= variablePosition) {
				Console.printErrorMessage("abnormal variableValues length: "
						+ "found while searching for variable '" + variableName + "'",
						Thread.currentThread().getStackTrace()[1].getClassName());
				return null;
			}
			else
				return (String) variableValues.get(variablePosition);
		}

	}

	public Object getVariable(DataClassInfoHolder infoHolder, String variableName) throws BadLocationException, 
	ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, 
	InvocationTargetException, NoSuchMethodException, SecurityException {

		Object variable = null;
		int variablePosition = getVariablePosition(infoHolder, variableName);
		String variableType = getVariableType(infoHolder, variableName);

		if (variableValues.get(variablePosition) != null) {

			switch (variableType) {

			case "list":

				variable = variableValues.get(variablePosition);
				break;

			case "obs":

				variable = variableValues.get(variablePosition);
				break;

			case "oblist":

				variable = variableValues.get(variablePosition);
				break;
				
			case "string":

				variable = variableValues.get(variablePosition);
				break;

			case "int":

				variable = Integer.parseInt((String) variableValues
						.get(variablePosition));
				break;

			case "double":

				variable = Double.parseDouble((String) variableValues
						.get(variablePosition));
				break;

			case "date":

				String originalValue = (String) variableValues
				.get(variablePosition);

				variable = toCalendar(originalValue);
				break;

			}

		}

		return variable;

	}

	private int getVariablePosition(DataClassInfoHolder infoHolder, String variableName)
			throws BadLocationException, ClassNotFoundException, InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException {

		int variablePosition = 0;
		boolean doesExist = false;

		ArrayList<String> variableNames = infoHolder.getVariableNames();

		for (int i = 0; i < variableNames.size(); i++) {

			if (variableNames.get(i).equals(variableName)) {

				variablePosition = i;
				doesExist = true;

			}

		}

		if (doesExist == false) {

			Console.printErrorMessage("Variable '" + variableName + "' does not exist in class '" 
					+ infoHolder.getClassName() + "'.",
					Thread.currentThread().getStackTrace()[1].getClassName());
			return 99999;

		}

		return variablePosition;

	}

	public String getVariableType(DataClassInfoHolder infoHolder, String variableName)
			throws BadLocationException, ClassNotFoundException, InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException {

		int variablePosition = getVariablePosition(infoHolder, variableName);
		String type = infoHolder.getVariableTypes().get(variablePosition);
		return type;

	}

	public void setVariable(DataClassInfoHolder infoHolder, String variableName, ArrayList<?> variableValue)
			throws BadLocationException, ClassNotFoundException, InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException {

		int variablePosition = getVariablePosition(infoHolder, variableName);
		String variableType = infoHolder.getVariableTypes().get(
				variablePosition);

			if (variableType.equals("list")) 
				variableValues.set(variablePosition, variableValue);

			else 				
				Console.printErrorMessage("A list must be stored as a list variable.",
						Thread.currentThread().getStackTrace()[1].getClassName());

	}

	public void setVariable(DataClassInfoHolder infoHolder, String variableName, ObList variableValue)
			throws BadLocationException, ClassNotFoundException, InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException {

		int variablePosition = getVariablePosition(infoHolder, variableName);
		String variableType = infoHolder.getVariableTypes().get(
				variablePosition);

			if (variableType.equals("oblist")) 
				variableValues.set(variablePosition, variableValue);

			else 				
				Console.printErrorMessage("An observation list must be stored as a oblist variable.",
						Thread.currentThread().getStackTrace()[1].getClassName());

	}
	
	public void setVariable(DataClassInfoHolder infoHolder, String variableName, Observation variableValue)
			throws BadLocationException, ClassNotFoundException, InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException {

		int variablePosition = getVariablePosition(infoHolder, variableName);
		String variableType = infoHolder.getVariableTypes().get(
				variablePosition);

			if (variableType.equals("obs")) 
				variableValues.set(variablePosition, variableValue);

			else 
				Console.printErrorMessage
					("An observation must be stored as an observation variable.",
					Thread.currentThread().getStackTrace()[1].getClassName());

	}

	public void setVariable(DataClassInfoHolder infoHolder, String variableName, String variableValue)
			throws BadLocationException, ClassNotFoundException, InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException {

		int variablePosition = getVariablePosition(infoHolder, variableName);
		variableValues.set(variablePosition, variableValue);

	}

	private static Calendar toCalendar(String string) {

		if (string != null && string.length() == 8) {

			Calendar result = Calendar.getInstance();

			int year = Integer.parseInt(string.substring(0, 4));
			int month = Integer.parseInt(string.substring(4, 6));
			int day = Integer.parseInt(string.substring(6, 8));

			result.set(year, month - 1, day);

			return result;

		}

		else
			return null;
		
	}
	
	public String toString(DataClassInfoHolder infoHolder) {

		String className = infoHolder.getClassName();
		ArrayList<String> variableNames = infoHolder.getVariableNames();
		ArrayList<String> variableTypes = infoHolder.getVariableTypes();

		String string = className + " class Observation, ";

		for (int i = 0; i < variableNames.size(); i++) {

			string += variableNames.get(i) + ": ";

			if (variableValues.get(i) == null)
				string += "null";

			else {

				switch (variableTypes.get(i)) {
				
				case "list":
					string += ((ArrayList<?>) variableValues.get(i)).size()
					+ " instance(s)";
					break;

				case "oblist":
					string += ((ObList) variableValues.get(i)).size()
					+ " instance(s)";
					break;
					
				case "obs":
					string += infoHolder.getClassName() + " instance";
					break;

				default:
					string += ((String) variableValues.get(i));

				}

			}

			if (i < variableNames.size() - 1)
				string += ", ";

		}

		return string;
	}

}
