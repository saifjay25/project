package structures;

import java.util.ArrayList;

/**
 * Encapsulates an interval tree.
 * 
 * @author runb-cs112
 */
public class IntervalTree {
	
	/**
	 * The root of the interval tree
	 */
	IntervalTreeNode root;
	
	/**
	 * Constructs entire interval tree from set of input intervals. Constructing the tree
	 * means building the interval tree structure and mapping the intervals to the nodes.
	 * 
	 * @param intervals Array list of intervals for which the tree is constructed
	 */
	public IntervalTree(ArrayList<Interval> intervals) {
		
		// make a copy of intervals to use for right sorting
		ArrayList<Interval> intervalsRight = new ArrayList<Interval>(intervals.size());
		for (Interval iv : intervals) {
			intervalsRight.add(iv);
		}
		
		// rename input intervals for left sorting
		ArrayList<Interval> intervalsLeft = intervals;
		
		// sort intervals on left and right end points
		sortIntervals(intervalsLeft, 'l');
		sortIntervals(intervalsRight,'r');
		
		// get sorted list of end points without duplicates
		ArrayList<Integer> sortedEndPoints = 
							getSortedEndPoints(intervalsLeft, intervalsRight);
		
		// build the tree nodes
		root = buildTreeNodes(sortedEndPoints);
		
		// map intervals to the tree nodes
		mapIntervalsToTree(intervalsLeft, intervalsRight);
	}
	
	/**
	 * Returns the root of this interval tree.
	 * 
	 * @return Root of interval tree.
	 */
	public IntervalTreeNode getRoot() {
		return root;
	}
	
	/**
	 * Sorts a set of intervals in place, according to left or right endpoints.  
	 * At the end of the method, the parameter array list is a sorted list. 
	 * 
	 * @param intervals Array list of intervals to be sorted.
	 * @param lr If 'l', then sort is on left endpoints; if 'r', sort is on right endpoints
	 */
	public static void sortIntervals(ArrayList<Interval> intervals, char lr) {
		if (intervals.size()==0){
			return;
		}
		if (lr=='l'){
		for (int i=0;i<intervals.size();i++){
			for (int k=i;k<intervals.size();k++){
				if (intervals.get(i).leftEndPoint>intervals.get(k).leftEndPoint){
					Interval temp=intervals.get(i);
					intervals.set(i,intervals.get(k));
					intervals.set(k,temp);
				}
			}
		}
		}
		if (lr=='r'){
			for (int i=0;i<intervals.size();i++){
				for (int k=i;k<intervals.size();k++){
					if (intervals.get(i).rightEndPoint>intervals.get(k).rightEndPoint){
						Interval temp=intervals.get(i);
						intervals.set(i,intervals.get(k));
						intervals.set(k,temp);
					}
				}
			}
			}
		}
	
	/**
	 * Given a set of intervals (left sorted and right sorted), extracts the left and right end points,
	 * and returns a sorted list of the combined end points without duplicates.
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 * @return Sorted array list of all endpoints without duplicates
	 */
	public static ArrayList<Integer> getSortedEndPoints(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		if (leftSortedIntervals.size()==0&&rightSortedIntervals.size()==0){
			return null;
		}
		ArrayList <Integer> sorted=new ArrayList <Integer>();
		for (int i=0;i<leftSortedIntervals.size();i++){
			Interval left=leftSortedIntervals.get(i);
			if (sorted.size()==0){
				sorted.add(left.leftEndPoint);
			}
			for (int j=0;j<sorted.size();j++){
				if (left.leftEndPoint==sorted.get(j)){
					break;
				}
				if (j+1==sorted.size()){
					sorted.add(left.leftEndPoint);
				}
			}
		}
		for (int j=0;j<rightSortedIntervals.size();j++){
			Interval right=rightSortedIntervals.get(j);
			for (int k=0;k<sorted.size();k++){
				if (right.rightEndPoint==sorted.get(k)){
					break;
				}
				if (k+1==sorted.size()){
					sorted.add(right.rightEndPoint);
					for (int h=0;h<sorted.size();h++){
						if (right.rightEndPoint>sorted.get(h)){
							continue;
						}
						while(h!=sorted.size()){
						int temp=sorted.get(h);
						sorted.set(h, sorted.get(sorted.size()-1));
						sorted.set(sorted.size()-1, temp);
						h++;
						}
						k=0;
						break;
					}
				}
			}
		}
		return sorted;
	}
	
	/**
	 * Builds the interval tree structure given a sorted array list of end points
	 * without duplicates.
	 * 
	 * @param endPoints Sorted array list of end points
	 * @return Root of the tree structure
	 */
	public static IntervalTreeNode buildTreeNodes(ArrayList<Integer> endPoints) {
		if (endPoints.size()==0){
			return null;
		}
		Queue<IntervalTreeNode> t=new Queue<IntervalTreeNode>();
		IntervalTreeNode tree=new IntervalTreeNode(0, 0, 0);
		for (int i=0;i<endPoints.size();i++){
			IntervalTreeNode temp=new IntervalTreeNode(endPoints.get(i), endPoints.get(i), endPoints.get(i));
			t.enqueue(temp);
		}
		if (t.size==0){
			return null;
		}
		if (t.size==1){
			tree=t.dequeue();
			return tree;
		}
		int count=t.size;
		while(t.size>1){
			IntervalTreeNode node=t.dequeue();
			IntervalTreeNode node2=t.dequeue();
			float split=(node.maxSplitValue+node2.minSplitValue)/2;
			tree=new IntervalTreeNode(split,node.minSplitValue,node2.maxSplitValue);
			tree.rightChild=node2;
			tree.leftChild=node;
			t.enqueue(tree);
			count--;
			count--;
			if (count==1){
				IntervalTreeNode num1=t.dequeue();
				t.enqueue(num1);
				count=t.size;
			}
		}
		tree=t.dequeue();
		return tree;
	}
	
	/**
	 * Maps a set of intervals to the nodes of this interval tree. 
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 */
	public void mapIntervalsToTree(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		if (leftSortedIntervals.size()==0&&rightSortedIntervals.size()==0){
			return;
		}
		IntervalTreeNode ptr=root;
		for (int i=0;i<leftSortedIntervals.size();i++){
			Interval e=leftSortedIntervals.get(i);
			if (e.leftEndPoint<=ptr.splitValue&&e.rightEndPoint>=ptr.splitValue){
				ptr.leftIntervals=this.checkfornull(ptr.leftIntervals,e);
				ptr=root;
			}
			while (e.leftEndPoint>ptr.splitValue&&e.rightEndPoint>ptr.splitValue||e.leftEndPoint<ptr.splitValue&&e.rightEndPoint<ptr.splitValue){
				while (e.leftEndPoint<ptr.splitValue&&e.rightEndPoint<ptr.splitValue){
					ptr=ptr.leftChild;
				}
				while (e.leftEndPoint>ptr.splitValue&&e.rightEndPoint>ptr.splitValue){
					ptr=ptr.rightChild;
				}
				if (e.leftEndPoint<=ptr.splitValue&&e.rightEndPoint>=ptr.splitValue){
					ptr.leftIntervals=this.checkfornull(ptr.leftIntervals,e);
					ptr=root;
					break;
				}
			}
		}
		for (int i=0;i<rightSortedIntervals.size();i++){
			Interval e=rightSortedIntervals.get(i);
			if (e.leftEndPoint<=ptr.splitValue&&e.rightEndPoint>=ptr.splitValue){
				ptr.rightIntervals=this.checkfornull(ptr.rightIntervals,e);
				System.out.println(ptr.rightIntervals);
				System.out.println(ptr.splitValue);
				ptr=root;
			}
			while (e.leftEndPoint>ptr.splitValue&&e.rightEndPoint>ptr.splitValue||e.leftEndPoint<ptr.splitValue&&e.rightEndPoint<ptr.splitValue){
				while (e.leftEndPoint<ptr.splitValue&&e.rightEndPoint<ptr.splitValue){
					ptr=ptr.leftChild;
				}
				while (e.rightEndPoint>ptr.splitValue&&e.leftEndPoint>ptr.splitValue){
					ptr=ptr.rightChild;
				}
				if (e.leftEndPoint<=ptr.splitValue&&e.rightEndPoint>=ptr.splitValue){
					ptr.rightIntervals=this.checkfornull(ptr.rightIntervals,e);
					ptr=root;
					break;
				}
			}
		}
	}
	private ArrayList<Interval> checkfornull(ArrayList<Interval> Intervals, Interval e) {
		if (Intervals==null){
			Intervals=new ArrayList <Interval>();
		}
		Intervals.add(e);
		return Intervals;
	}

	/**
	 * Gets all intervals in this interval tree that intersect with a given interval.
	 * 
	 * @param q The query interval for which intersections are to be found
	 * @return Array list of all intersecting intervals; size is 0 if there are no intersections
	 */
	public ArrayList<Interval> findIntersectingIntervals(Interval q) {
		if (q==null){
			return null;
		}
		ArrayList<Interval> list=new ArrayList<Interval>();
		IntervalTreeNode node=root;
		printInorder(node,q,list);
		return list;
	}
	private void printInorder(IntervalTreeNode node,Interval q,ArrayList<Interval> list) {
		 if (node==null)
	            return;
		 	printInorder(node.leftChild,q,list);
			if (node.leftIntervals!=null){
	        	checkIntervals(node,q,list);
			}
	        printInorder(node.rightChild,q,list);
	}
	private void checkIntervals(IntervalTreeNode node, Interval q,ArrayList<Interval> list) {
		for (int i=0;i<node.leftIntervals.size();i++){
			Interval e=node.leftIntervals.get(i);
			if (q.leftEndPoint>=e.leftEndPoint&&q.leftEndPoint<=e.rightEndPoint||q.rightEndPoint>=e.leftEndPoint&&q.rightEndPoint<=e.rightEndPoint){
				list.add(e);
			}
		}
	}
}

