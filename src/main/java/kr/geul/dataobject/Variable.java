package kr.geul.dataobject;

import java.lang.reflect.InvocationTargetException;

import kr.geul.console.Console;

public class Variable {

	String name, type;
	Object value;
	
	public Variable(String name, String value) {
		this.name = name;
		this.type = "String";
		this.value = value;
	}

	public Variable(String name, int value) {
		this.name = name;
		this.type = "int";
		this.value = value;
	}

	public Variable(String name, double value) {
		this.name = name;
		this.type = "double";
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public double getDoubleValue() throws ClassNotFoundException, InstantiationException, 
	IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
	NoSuchMethodException, SecurityException {
		
		if (type.equals("double"))
			return (Double) value;
		
		else {
			Console.printErrorMessage("Variable '" + name + "' is not a double type variable.", 
					Thread.currentThread().getStackTrace()[1].getClassName());			
			return 9999999;
		}
		
	}
	
	public int getIntValue() throws ClassNotFoundException, InstantiationException, 
	IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
	NoSuchMethodException, SecurityException {
		
		if (type.equals("int"))
			return (Integer) value;
		
		else {
			Console.printErrorMessage("Variable '" + name + "' is not a integer type variable.", 
					Thread.currentThread().getStackTrace()[1].getClassName());			
			return 9999999;
		}
		
	}
	
	public String getStringValue() throws ClassNotFoundException, InstantiationException, 
	IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
	NoSuchMethodException, SecurityException {
		
		if (type.equals("String"))
			return (String) value;
		
		else {
			Console.printErrorMessage("Variable '" + name + "' is not a string type variable.", 
					Thread.currentThread().getStackTrace()[1].getClassName());			
			return null;
		}
		
	}
	
}
