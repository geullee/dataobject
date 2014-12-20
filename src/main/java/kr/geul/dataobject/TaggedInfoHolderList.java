package kr.geul.dataobject;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.text.BadLocationException;

public class TaggedInfoHolderList implements Cloneable, Serializable {

	private static final long serialVersionUID = -3423278932920513452L;
	private ArrayList<DataClassInfoHolder> infoHolders;
	private ArrayList<String> tags;
	
	public TaggedInfoHolderList() {
		infoHolders = new ArrayList<DataClassInfoHolder>();
		tags = new ArrayList<String>();
	}
	
	public void addInfoHolder(DataClassInfoHolder infoHolder, String tag) {
		infoHolders.add(infoHolder);
		tags.add(tag);
	}
	
	public DataClassInfoHolder getInfoHolder(String tag) throws ClassNotFoundException, 
	InstantiationException, IllegalAccessException, IllegalArgumentException, 
	InvocationTargetException, NoSuchMethodException, SecurityException, 
	BadLocationException {
		
		for (int i = 0; i < tags.size(); i++) {
			
			if (tags.get(i).equals(tag))
				return infoHolders.get(i);
			
		}
		
		DataClassInfoHolder nullInfoHolder = new DataClassInfoHolder();
		nullInfoHolder.setClassName("NULL");
		return nullInfoHolder;
		
	}
	
	public ArrayList<String> getTags() {
		return tags;
	}
	
}
