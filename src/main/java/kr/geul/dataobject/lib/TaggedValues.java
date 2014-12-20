package kr.geul.dataobject.lib;

import java.util.ArrayList;

public class TaggedValues {

	int index;
	ArrayList<Object> values;
	
	public TaggedValues() {
		values = new ArrayList<Object>();
	}
	
	public void addValue(Object value) {
		this.values.add(value);
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public ArrayList<Object> getValues() {
		return values;
	}
	
}
