package kr.geul.dataobject;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.text.BadLocationException;

public class DataClassArray extends ArrayList<Observation> implements Serializable {

	private static final long serialVersionUID = -942082353671455135L;
	private DataClassInfoHolder infoHolder;
	private TaggedInfoHolderList subInfoHolderArray;
	
	public DataClassArray(DataClassInfoHolder infoHolder) {	
		this.infoHolder = infoHolder;	
		subInfoHolderArray = new TaggedInfoHolderList();
	}

	public DataClassInfoHolder getInfoHolder() {		
		return infoHolder;		
	}

	public void setInfoHolder(DataClassInfoHolder infoHolder) {	
		this.infoHolder = infoHolder;		
	}
	
	public void addSubInfoHolder(DataClassInfoHolder infoHolder, String tag) {
		subInfoHolderArray.addInfoHolder(infoHolder, tag);
	}

	public DataClassInfoHolder getSubInfoHolder(String tag) throws ClassNotFoundException, 
	InstantiationException, IllegalAccessException, IllegalArgumentException, 
	InvocationTargetException, NoSuchMethodException, SecurityException, 
	BadLocationException {
		return subInfoHolderArray.getInfoHolder(tag);
	}
	
	public ArrayList<String> getSubInfoHolderTags() throws ClassNotFoundException, 
	InstantiationException, IllegalAccessException, IllegalArgumentException, 
	InvocationTargetException, NoSuchMethodException, SecurityException, 
	BadLocationException {
		return subInfoHolderArray.getTags();
	}
	
}
