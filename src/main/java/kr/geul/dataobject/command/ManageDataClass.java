package kr.geul.dataobject.command;

import java.util.ArrayList;

import kr.geul.console.Console;
import kr.geul.console.command.Command;
import kr.geul.dataobject.DataClass;

public class ManageDataClass extends Command {

	public ManageDataClass(ArrayList<String> arguments) {
		super(arguments);	
	}

	@Override
	protected String getCommandName() {
		return "class";
	}

	@Override
	protected int[] getValidNumberOfArguments() {
		return null;
	}

	@Override
	protected void runCommand() throws Exception {
		
		if (arguments.size() == 0)
			DataClass.showList();

		else if (arguments.size() == 1) {
			
			if (arguments.get(0).equals("*")) 
				DataClass.clearDataClassHanger();
			else
				DataClass.removeDataClass(arguments.get(0));
		}
		
		else if (arguments.size() == 2 || arguments.size() > 3)
			Console.printErrorMessage("Two aliases, each for variable names and types,"
					+ " are needed to define a class.", 
					Thread.currentThread().getStackTrace()[1].getClassName());		

		else {

			String dataClassName = arguments.get(0);
			String nameAlias = arguments.get(1);
			String typeAlias = arguments.get(2);

			DataClass.addDataClass(dataClassName, nameAlias, typeAlias);

		}
		
	}

}
