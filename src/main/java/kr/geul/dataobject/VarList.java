package kr.geul.dataobject;

import java.util.ArrayList;

public class VarList extends ArrayList<Variable> {

	private static final long serialVersionUID = 1924011225418375975L;

	public Variable get(String name) {
		
		for (int i = 0; i < this.size(); i++) {
			
			Variable variable = this.get(i);
			
			if (variable.getName().equals(name))
				return variable;
			
		}
		
		throw new NullPointerException();
		
	}
	
}
