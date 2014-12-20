package kr.geul.dataobject.command;

import java.util.ArrayList;

import kr.geul.console.command.Command;
import kr.geul.dataobject.DataClass;
import kr.geul.dataobject.DataClassArray;
import kr.geul.dataobject.DataClassInfoHolder;
import kr.geul.dataobject.InfoHolder;

public class ManageInfoHolder extends Command {

	public ManageInfoHolder(ArrayList<String> arguments) {
		super(arguments);	
	}

	public static ArrayList<Integer> checkPartitions(String token) {

		ArrayList<Integer> locations = new ArrayList<Integer>();
		boolean isPartitioned = false;

		for (int i = 0; i < token.length(); i++) {

			if (token.charAt(i) == '/') {
				locations.add(i);
				isPartitioned = true;		
			}

		}

		if (isPartitioned == false)
			locations.add(-1);

		return locations;

	}

	@Override
	protected String getCommandName() {
		return "infoholder";
	}

	@Override
	protected int[] getValidNumberOfArguments() {
		int[] numbers = {0, 1, 2};
		return numbers;
	}

	protected void runCommand() throws Exception {
		if (arguments.size() == 0)
			InfoHolder.showList();

		else if (arguments.size() == 1) { 

			if (arguments.get(0).equals("*")) 
				InfoHolder.clearAll();
			else
				InfoHolder.remove(arguments.get(0));

		}

		else {

			String infoHolderAddress = arguments.get(0);
			String infoHolderTag = arguments.get(1);

			ArrayList<String> addressTokens = getPartitionedTokens(infoHolderAddress);
			DataClassArray dataClassArray = DataClass.getDataClassArray(addressTokens.get(0));
			DataClassInfoHolder infoHolder;

			if (addressTokens.size() == 1)
				infoHolder = dataClassArray.getInfoHolder();

			else {

				String subInfoHolderAddress = "";

				for (int i = 1; i < addressTokens.size(); i++) {

					subInfoHolderAddress += addressTokens.get(i);
					if (i < addressTokens.size() - 1)
						subInfoHolderAddress += "/";

				}

				infoHolder = dataClassArray.getSubInfoHolder(subInfoHolderAddress);

			}

			InfoHolder.addInfoHolder(infoHolder, infoHolderTag);

		}

	}

	public static ArrayList<String> getPartitionedTokens(String rawToken) {

		ArrayList<String> tokens = new ArrayList<String>();

		ArrayList<Integer> partitionLocations = 
				checkPartitions(rawToken);

		if (partitionLocations.get(0) > -1) {

			int leftPoint = 0, rightPoint = 0;

			for (int i = 0; i < partitionLocations.size(); i++) {

				rightPoint = partitionLocations.get(i);
				tokens.add(rawToken.substring(leftPoint, rightPoint));
				leftPoint = rightPoint + 1;

			}

			tokens.add(rawToken.substring(leftPoint, rawToken.length()));

		}

		else
			tokens.add(rawToken);

		return tokens;

	}	

}
