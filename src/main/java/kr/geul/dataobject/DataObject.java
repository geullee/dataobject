package kr.geul.dataobject;

import java.util.ArrayList;

import kr.geul.console.CommandLibrary;
import kr.geul.console.ExpansionPack;

public class DataObject extends ExpansionPack {

	protected CommandLibrary getCommandLibrary() {
		
		CommandLibrary library = new CommandLibrary("DataObject");
		library.addCommand("class", "kr.geul.dataobject.command.ManageDataClass");
		library.addCommand("getlist", "kr.geul.dataobject.command.GetList");
		library.addCommand("getobs", "kr.geul.dataobject.command.GetObs");
		library.addCommand("infoholder", "kr.geul.dataobject.command.ManageInfoHolder");
		library.addCommand("loadclass", "kr.geul.dataobject.command.LoadClass");
		library.addCommand("loadcsv", "kr.geul.dataobject.command.LoadCSV");
		library.addCommand("saveclass", "kr.geul.dataobject.command.SaveClass");
		library.addCommand("setobs", "kr.geul.dataobject.command.SetObs");
		library.addCommand("sort", "kr.geul.dataobject.command.Sort");
		library.addCommand("view", "kr.geul.dataobject.command.View");
		
		return library;
		
	}

	public static String[] checkExistence(ArrayList<String> checkedArray,
			ArrayList<String> targetArray) {

		String[] returnValue = new String[2];
		String checkedElement = null;		

		for (int i = 0; i < checkedArray.size(); i++) {

			boolean doesExist = false;
			checkedElement = checkedArray.get(i);

			for (int j = 0; j < targetArray.size(); j++) {

				String targetElement = targetArray.get(j);

				if (checkedElement.equals(targetElement))
					doesExist = true;

			}

			if (doesExist == false) {

				returnValue[0] = "false";
				returnValue[1] = checkedElement;

				return returnValue;

			}

		}

		returnValue[0] = "true";
		returnValue[1] = "";

		return returnValue;

	}

	protected boolean checkExistence(String checkedString,
			ArrayList<String> targetArrayList) {

		boolean doesExist = false;

		for (int j = 0; j < targetArrayList.size(); j++) {

			String targetElement = targetArrayList.get(j);

			if (checkedString.equals(targetElement))
				doesExist = true;

		}

		return doesExist;

	}

	public static boolean checkExistence(int checkedInt,
			ArrayList<Integer> targetArray) {

		boolean doesExist = false;

		for (int j = 0; j < targetArray.size(); j++) {

			int targetElement = targetArray.get(j);

			if (checkedInt == targetElement)
				doesExist = true;

		}

		return doesExist;

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
