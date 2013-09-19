package br.com.bb.qos;

import java.util.ArrayList;
import java.util.List;

public class AttributeSpecification {
	List<Goal> goals = new ArrayList<Goal>();
	
	
	
	public AttributeSpecification(List<Goal> goals) {
		super();
		this.goals = new ArrayList<Goal>(goals);
	}



	public List<Goal> getGoals() {
		return new ArrayList<Goal>(goals);
	}
}
