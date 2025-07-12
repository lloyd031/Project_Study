package application;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Pipe {
	private Stack<PathNode> pnList=new Stack<PathNode>();
	private Queue<PathNode> queue=new LinkedList<PathNode>();
	private LinkedList<PathNode> visited=new LinkedList<PathNode>();
	private PathNode start;
	private Block as;
	int selectedwall=0;
	int selectedwall2=0;
	char[] pattern= new char[4];
	double w;
	double l;
	public Pipe(PathNode start, PathNode end,double w, double l) {
		this.start=start;
		
		this.w=w;
		this.l=l;
		
	}
	public void setselectedwall(int selectedwall, int selectedwall2) {
		this.selectedwall=selectedwall;
		this.selectedwall2=selectedwall2;
	}
	public void addNeighbor(PathNode a, PathNode end) {
		double adder=100;
		double adderX1=defNxtPnt(a.getX(),adder,end.getX(),0,'x');
		double adderX2=defNxtPnt(a.getX(),adder,end.getX(),1,'x');
		double adderZ1=defNxtPnt(a.getZ(),adder,end.getZ(),0,'z');
		double adderZ2=defNxtPnt(a.getZ(),adder,end.getZ(),1,'z');
		double adderY1=defNxtPnt(a.getY(),adder, end.getY(),0,'y');
		double adderY2=defNxtPnt(a.getY(),adder,end.getY(),1,'y');
		
	
			if(this.selectedwall2<2 && this.selectedwall<2 ) {
				if(!validate(new PathNode(a.getX()-adderX1,a.getY(),a.getZ()) )) {
				    createNode(a,new PathNode(a.getX()-adderX1,a.getY(),a.getZ()));	
					
				}
				if(!validate(new PathNode(a.getX()+adderX2,a.getY(),a.getZ()))) {
					createNode(a,new PathNode(a.getX()+adderX2,a.getY(),a.getZ()));
					
				}
				if(!validate(new PathNode(a.getX(),a.getY(),a.getZ()-adderZ1)) ) {
					createNode(a,new PathNode(a.getX(),a.getY(),a.getZ()-adderZ1));
				}
				if(!validate(new PathNode(a.getX(),a.getY(),a.getZ()+adderZ2)) ) {
					createNode(a,new PathNode(a.getX(),a.getY(),a.getZ()+adderZ2));
					
				}
			}else {
				if(!validate(new PathNode(a.getX()-adderX1,a.getY(),a.getZ()) )) {
					createNode(a,new PathNode(a.getX()-adderX1,a.getY(),a.getZ()));
				}
				if(!validate(new PathNode(a.getX()+adderX2,a.getY(),a.getZ()))) {
					createNode(a,new PathNode(a.getX()+adderX2,a.getY(),a.getZ()));
				}
				if(!validate(new PathNode(a.getX(),a.getY(),a.getZ()-adderZ1)) ) {
					createNode(a,new PathNode(a.getX(),a.getY(),a.getZ()-adderZ1));
				}
				if(!validate(new PathNode(a.getX(),a.getY(),a.getZ()+adderZ2)) ) {
					createNode(a,new PathNode(a.getX(),a.getY(),a.getZ()+adderZ2));
				}
				
			}
			if(!validate(new PathNode(a.getX(),a.getY()-adderY1,a.getZ())) ) {
				createNode(a,new PathNode(a.getX(),a.getY()-adderY1,a.getZ()));
			}
			if(!validate(new PathNode(a.getX(),a.getY()+adderY2,a.getZ()))) {
				createNode(a,new PathNode(a.getX(),a.getY()+adderY2,a.getZ()));
			}
		
		
	}
	
	
	
	public double defNxtPnt(double nextPnt, double add, double end, int op,char axis) {
		
		double adder=0;
		if(op==0) {
			
			if(nextPnt>end) {
				adder=(nextPnt-add<end)?nextPnt-end:add;
			}else {
				adder=add;
			}
			
		}else {
			if(nextPnt<end) {
				adder=(nextPnt+add<end)?add:end-nextPnt ;
				
			}else {
				adder=add;
					
			}
		}
		
		return adder;
	}
	
	public void createNode(PathNode p, PathNode ch) {
		ch.setParent(p);
		queue.add(ch);
	}

	public void BFS(PathNode a, PathNode end) {
		visited.add(a);
		addNeighbor(a,end);
		if(a.getX()==end.getX() && a.getY()==end.getY() && a.getZ()==end.getZ()) {
			trackPath(a);
		}else {
			BFS(queue.poll(),end);
		
		}
	}
	public void trackPath(PathNode a) {
		pnList.push(a);
		if(a.getParent()!=null) {
			trackPath(a.getParent());
		}
	}
	public boolean validate(PathNode a) {
		boolean res=false;
		for(PathNode x:queue)
		{
			if(a.getX()==x.getX() && a.getY()==x.getY() && a.getZ()==x.getZ()  )
			{
				res=true;
			}
		}
		
		for(PathNode x:visited)
		{
			if(a.getX()==x.getX() && a.getY()==x.getY() && a.getZ()==x.getZ())
			{
				res=true;
			}
		}
		
		
		
		return res;
		
	}

	public Stack<PathNode> getPath() {
		
		return this.pnList;
	}
}

