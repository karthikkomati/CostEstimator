package sjdb;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Estimator implements PlanVisitor {


	public Estimator() {
		// empty constructor
	}

	/* 
	 * Create output relation on Scan operator
	 *
	 * Example implementation of visit method for Scan operators.
	 */
	public void visit(Scan op) {
		Relation input = op.getRelation();
		Relation output = new Relation(input.getTupleCount());
		
		Iterator<Attribute> iter = input.getAttributes().iterator();
		while (iter.hasNext()) {
			output.addAttribute(new Attribute(iter.next()));
		}
		
		op.setOutput(output);
	}

	public void visit(Project op) {
		
		Relation input = op.getInput().getOutput();
		Relation output = new Relation(input.getTupleCount());
		
		for(Attribute a : op.getAttributes()) {

			try {
				output.addAttribute(new Attribute(input.getAttribute(a)));
			}catch(Exception e) {}
			
		}
		
		op.setOutput(output);
		
		
	}
	
	public void visit(Product op) {
		
		Relation left  = op.getLeft().getOutput();
		Relation right = op.getRight().getOutput();
		Relation output = new Relation(left.getTupleCount()*right.getTupleCount());
		
		for(Attribute a: left.getAttributes()) {
			output.addAttribute(new Attribute(a));
		}
		for(Attribute a: right.getAttributes()) {
			output.addAttribute(new Attribute(a));
		}
		
		op.setOutput(output);
	}
	
	public void visit(Select op) {
		
		Relation input = op.getInput().getOutput();
		Relation output;
		
		
		if(op.getPredicate().equalsValue()) {
			
					
			output = new Relation(input.getTupleCount()/input.getAttribute(op.getPredicate().getLeftAttribute()).getValueCount());
						
			for(Attribute a: input.getAttributes()) {
				if(a.equals(input.getAttribute(op.getPredicate().getLeftAttribute()))) {
					output.addAttribute(new Attribute(a.getName(),1));
				}
				else {
					
					
					output.addAttribute(new Attribute(a));
				}
			}
		}		
		else {
			
			output = new Relation(input.getTupleCount()/(Math.max(input.getAttribute(op.getPredicate().getLeftAttribute()).getValueCount(),input.getAttribute(op.getPredicate().getRightAttribute()).getValueCount())));
			int value = Math.min(input.getAttribute(op.getPredicate().getLeftAttribute()).getValueCount(),input.getAttribute(op.getPredicate().getRightAttribute()).getValueCount());
			
			for (Attribute a: input.getAttributes()) {
				if(a.equals(input.getAttribute(op.getPredicate().getLeftAttribute()))|| a.equals(input.getAttribute(op.getPredicate().getRightAttribute()))) {
					output.addAttribute(new Attribute(a.getName(),value));
				}
				else {
					
					
					output.addAttribute(new Attribute(a));
				}
			}
		}
		
		op.setOutput(output);
	}
	
	
	
	
	
	public void visit(Join op) {
		Relation left  = op.getLeft().getOutput();
		Relation right = op.getRight().getOutput();
		
		Relation output = new Relation((left.getTupleCount()*right.getTupleCount())/(Math.max(left.getAttribute(op.getPredicate().getLeftAttribute()).getValueCount(),right.getAttribute(op.getPredicate().getRightAttribute()).getValueCount())));
		
		for(Attribute a: left.getAttributes()) {
			if(a.equals(op.getPredicate().getLeftAttribute())) {
				
				output.addAttribute(new Attribute(a.getName(),Math.min(left.getAttribute(op.getPredicate().getLeftAttribute()).getValueCount(),right.getAttribute(op.getPredicate().getRightAttribute()).getValueCount())));
				
			}else {
				output.addAttribute(new Attribute(a));
			}
		}
		
		for(Attribute a: right.getAttributes()) {
			if(a.equals(op.getPredicate().getRightAttribute())) {
				output.addAttribute(new Attribute(a.getName(),Math.min(left.getAttribute(op.getPredicate().getLeftAttribute()).getValueCount(),right.getAttribute(op.getPredicate().getRightAttribute()).getValueCount())));
			}else {
				output.addAttribute(new Attribute(a));
			}
		}
		
		
		op.setOutput(output);
	}
	
	public int getJoinCost(Join op) {
		
		Relation left  = op.getLeft().getOutput();
		Relation right = op.getRight().getOutput();
		
		Relation output = new Relation((left.getTupleCount()*right.getTupleCount())/(Math.max(left.getAttribute(op.getPredicate().getLeftAttribute()).getValueCount(),right.getAttribute(op.getPredicate().getRightAttribute()).getValueCount())));
		
		for(Attribute a: left.getAttributes()) {
			if(a.equals(op.getPredicate().getLeftAttribute())) {
				
				output.addAttribute(new Attribute(a.getName(),Math.min(left.getAttribute(op.getPredicate().getLeftAttribute()).getValueCount(),right.getAttribute(op.getPredicate().getRightAttribute()).getValueCount())));
				
			}else {
				output.addAttribute(new Attribute(a));
			}
		}
		
		for(Attribute a: right.getAttributes()) {
			if(a.equals(op.getPredicate().getRightAttribute())) {
				output.addAttribute(new Attribute(a.getName(),Math.min(left.getAttribute(op.getPredicate().getLeftAttribute()).getValueCount(),right.getAttribute(op.getPredicate().getRightAttribute()).getValueCount())));
			}else {
				output.addAttribute(new Attribute(a));
			}
		}
		
		
		return output.getTupleCount();
		
	}
}
