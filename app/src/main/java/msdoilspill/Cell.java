package msdoilspill;

import java.util.ArrayList;

public class Cell{
	
	public ArrayList<Cell> neighbors;
	//Required by GUI (jak jest menu wyboru typu to stąd bierze opcje)
	public static Integer []types ={0,1,2,3};
	public int type;

	public Cell() {
	}
	
	public void clear() {
	}

	public void move(){
	}

	public void addNeighbor(Cell nei) {
		neighbors.add(nei);
	}
	
}