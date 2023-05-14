package msdoilspill;

import java.util.ArrayList;

public class Point{
	
	public ArrayList<Point> neighbors;
	public static Integer []types ={0,1,2,3};
	public int type;
	public int staticField;
	public boolean isPedestrian;

	public Point() {
	}
	
	public void clear() {
	}

	public void move(){
	}

	public void addNeighbor(Point nei) {
		neighbors.add(nei);
	}
	
}