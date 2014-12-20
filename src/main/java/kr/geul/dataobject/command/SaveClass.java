package kr.geul.dataobject.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import kr.geul.console.command.Command;
import kr.geul.dataobject.DataClass;
import kr.geul.dataobject.DataClassArray;

public class SaveClass extends Command {

	public SaveClass (ArrayList<String> arguments) {
		super(arguments);	
	}

	@Override
	protected String getCommandName() {
		return "saveclass";
	}

	@Override
	protected int[] getValidNumberOfArguments() {
		int[] numbers = {1, 2};
		return numbers;
	}

	@Override
	protected void runCommand() throws Exception {
		
		File file;
		
		if (arguments.size() == 2)
			file = new File(arguments.get(1));
		else
			file = new File(arguments.get(0) + ".cl");
		
		System.out.print("Saving class '" + arguments.get(0)
				+ "' in " + file.getCanonicalPath() + "...");
		
		tic();
		
		ObjectOutputStream output =
			new ObjectOutputStream(new FileOutputStream(file)); 
		
		DataClassArray classArray =
			DataClass.getDataClassArray(arguments.get(0));

			output.writeObject(classArray);							
		
		output.close();
		
		toc();	
		
	}

}
