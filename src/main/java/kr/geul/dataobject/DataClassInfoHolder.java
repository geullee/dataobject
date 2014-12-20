package kr.geul.dataobject;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import kr.geul.console.Console;

public class DataClassInfoHolder implements Cloneable, Serializable {

	private static final long serialVersionUID = 3519182502719945520L;
	private String className;
	private ArrayList<String> baseVariables;
	private ArrayList<String> variableNames;
	private ArrayList<String> variableTypes;
	
	public DataClassInfoHolder() {
		
		className = "";
		baseVariables = new ArrayList<String>();
		variableNames = new ArrayList<String>();
		variableTypes = new ArrayList<String>();
		
	}
	
	public String getClassName() {
		return className;		
	}

	public ArrayList<String> getBaseVariables() {	
		return baseVariables;		
	}
	
	public ArrayList<String> getVariableNames() {	
		return variableNames;		
	}

	public String getVariableType(String variableName) throws ClassNotFoundException, 
	InstantiationException, IllegalAccessException, IllegalArgumentException, 
	InvocationTargetException, NoSuchMethodException, SecurityException {
		
		boolean doesExist = false;
		String variableType = null;
		
		for (int i = 0; i < variableNames.size(); i++) {
			
			if (variableNames.get(i).equals(variableName)) {
				variableType = variableTypes.get(i);
				doesExist = true;
			}
			
		}
		
		if (doesExist == true)
			return variableType;
		
		else 
			Console.printErrorMessage("There does not exist a variable called '"
					+ variableName + "' in class '" + className + "'.",  
					Thread.currentThread().getStackTrace()[1].getClassName());	
			return "ERROR"; 
		
	}
	
	public ArrayList<String> getVariableTypes() {	
		return variableTypes;	
	}
	
	public void setClassName(String className) {	
		this.className = className;		
	}
	
	public void setBaseVariables(ArrayList<String> baseVariables) {	
		this.baseVariables = baseVariables;	
	}
	
	public void setVariableNames(ArrayList<String> variableNames) {	
		this.variableNames = variableNames;	
	}
	
	public void setVariableTypes(ArrayList<String> variableTypes) {	
		this.variableTypes = variableTypes;	
	}
	
	public String toString() {
		String string = "className: " + className + ", VariableNames: ";
		
		for (int i = 0; i < variableNames.size(); i++) {			
			string += variableNames.get(i) + "(" + variableTypes.get(i) + "), ";		
		}
		
		string += "baseVariables: ";
		
		for (int i = 0; i < baseVariables.size(); i++) {			
			
			string += baseVariables.get(i);
			if (i < baseVariables.size() - 1)
				string += ", ";
			
		}	
		
		return string;
	}

	public DataClassInfoHolder getClone() throws CloneNotSupportedException {
		return (DataClassInfoHolder)this.clone();
	}
	
}
