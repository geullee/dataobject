package kr.geul.dataobject.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;


import kr.geul.console.command.Command;
import kr.geul.dataobject.DataClass;
import kr.geul.dataobject.DataClassArray;

public class LoadClass extends Command {

	public LoadClass (ArrayList<String> arguments) {
		super(arguments);	
	}

	@Override
	protected String getCommandName() {
		return "loadclass";
	}

	@Override
	protected int[] getValidNumberOfArguments() {
		int[] numbers = {1};
		return numbers;
	}

	@Override
	protected void runCommand() throws Exception {
		
		File file = new File(arguments.get(0));

		System.out.print("Loading " + file.getCanonicalPath() + "...");
		
		tic();
		
		ObjectInputStream input = new ObjectInputStream(
				new FileInputStream(file));

		DataClassArray dataClassArray = (DataClassArray) (input
				.readObject());

		DataClass.loadDataClassArray(dataClassArray);

		input.close();

		toc();
		
	}
	
}
