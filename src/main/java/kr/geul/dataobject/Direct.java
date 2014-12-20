package kr.geul.dataobject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;

import javax.swing.text.BadLocationException;

public class Direct {

	static long startTime, endTime;
	static double progress;
	
	public static DataClassArray loadDataClass(String fileName) throws ClassNotFoundException, InstantiationException, 
	IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
	NoSuchMethodException, SecurityException, FileNotFoundException, IOException, 
	BadLocationException {

		File file = new File(fileName);
		ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
		
		System.out.print("Loading " + file.getCanonicalPath() + "...");

		tic();

		DataClassArray dataClassArray = (DataClassArray) (input.readObject());
		
		toc();
		
		input.close();
		
		return dataClassArray;

	}
	
	public static void saveDataClass(DataClassArray dataClassArray, String fileName) throws ClassNotFoundException, InstantiationException, 
	IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
	NoSuchMethodException, SecurityException, FileNotFoundException, IOException, BadLocationException {

		File file = new File(fileName);

		System.out.print("Saving class 'day' in " + file.getCanonicalPath() + "...");

		tic();

		ObjectOutputStream output =
				new ObjectOutputStream(new FileOutputStream(file));
		output.writeObject(dataClassArray);	

		toc();
		
		output.close();

	}
	
	private static void tic() {	
		progress = 0.0;
		startTime = System.currentTimeMillis();		
	}

	private static void toc() throws BadLocationException {

		endTime = System.currentTimeMillis();
		System.out.println(" DONE, Elapsed time: "
				+ (double) (((double) endTime - startTime) / 1000.00)
				+ " seconds");

	}
	
}
