package kr.geul.dataobject;

import java.util.ArrayList;

public class ObList extends ArrayList<Observation> {

	private static final long serialVersionUID = 1034527757544297822L;
	DataClassInfoHolder infoHolder;
	
	public ObList() {}
	
	public DataClassInfoHolder getInfoHolder() {
		return infoHolder;
	}
	
	public void setInfoHolder(DataClassInfoHolder infoHolder) {
		this.infoHolder = infoHolder;
	}

}
