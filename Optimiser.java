package sjdb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Optimiser implements PlanVisitor{
	
	private Catalogue catalogue;

	public Set<Attribute> attributes = new HashSet<Attribute>();
	
	public Estimator es = new Estimator();
	public Optimiser(Catalogue c) {
		
		this.catalogue = c;
		
	}
	
	public ArrayList<Operator> operators = new ArrayList<Operator>();
	public ArrayList<Scan> scans = new ArrayList<Scan>();

	private List<Attribute> projectAttributes = new ArrayList<Attribute>();
	

	public void visit(Scan op) {
		scans.add(new Scan((NamedRelation) op.getRelation()));
		//operators.add(new Scan((NamedRelation) op.getRelation()));
	}


	
	
	
	public ArrayList<Predicate> predicates = new ArrayList<Predicate>();

	public void visit(Select op) {
		predicates.add(op.getPredicate());
		
		
		
	}

	public void visit(Product op) {
			
		//predicates.add(new Predicate(op.getLeft(),op.getRight()))
	}
	public void visit(Project op) {
		
		projectAttributes.addAll(op.getAttributes());

		
		
	}
	public void visit(Join op) {
		
		//wpredicates.add(new Predicate(op.getLeft(),op.getRight()))
	}
	
	
	public Operator optimise(Operator op) {				
		
		
		op.accept(this);
		
		ArrayList<Operator> scanList = moveSelects(op);
		
		ArrayList<Operator> f = orderAndCreateJoins(scanList);
		
		//Operator res = pushProject(f.get(0));
		
		
		Project p = new Project(f.get(0),projectAttributes);
		//return f.get(0);
		return p;
		
	}
	
	
	//moves selets operators down and returns a list of selector operators
	public ArrayList<Operator> moveSelects(Operator root) {
		
		Operator r = null;
		ArrayList<Operator> scanList = new ArrayList<Operator>();
		for (Scan s: scans) {
			List<Attribute> scanAttributes = s.getOutput().getAttributes();
			r = s;
			for(Predicate p: predicates) {
				
				if(p.equalsValue()&& scanAttributes.contains(p.getLeftAttribute())) {
					
					r = new Select(r,p);
					predicates.remove(p);
				}
				else if(!p.equalsValue()&& scanAttributes.contains (p.getLeftAttribute()) && scanAttributes.contains(p.getRightAttribute())) {
					r = new Select(r,p);
					predicates.remove(p);
				}
				
				
			}			
			
			scanList.add(r);
		}		
		
		
		return scanList;		
	}
	
	
	//orders the joins form smallest to largest and creates new joins
	public ArrayList<Operator> orderAndCreateJoins(ArrayList<Operator> scanList) {
		ArrayList<Join> tempJoins = new ArrayList<Join>();
		ArrayList<Join> finalJoins = new ArrayList<Join>();
		int highest = Integer.MAX_VALUE;
		Predicate hp = null;
		Join h = null;
		while(!predicates.isEmpty()) {
			highest = Integer.MAX_VALUE;;

			for(Predicate p: predicates) {

				for(Operator o: scanList) {					

					if(o.getOutput().getAttributes().contains(p.getLeftAttribute())) {						

						for(Operator o1: scanList) {
							if(o1.getOutput().getAttributes().contains(p.getRightAttribute())) {
								
								Join j = new Join(o,o1,p);
								tempJoins.add(j);
								
								if(es.getJoinCost(j)<highest) {
									
									highest = es.getJoinCost(j);
									h = j;
									hp = p;
									
								}
							}
						}
					}
				
				}
			
			
			}
			
			finalJoins.add(h);
			scanList.remove(h.getLeft());
			scanList.remove(h.getRight());
			scanList.add(h);
			predicates.remove(hp);
			
		}
				
		return scanList;		
	}
	
//	public Operator pushProject(Operator r) {
//	
//		
//		
//		return r;
//	}
	
	
	

}