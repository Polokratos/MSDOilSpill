package msdoilspill;

import java.awt.Color;
import java.util.ArrayList;

public class Cell{
	
	public ArrayList<Cell> neighbors = new ArrayList<>();
	//Required by GUI (jak jest menu wyboru typu to stąd bierze opcje)
	public static Integer []types ={0,1,2,3};
	public int type;

	public CEV CEV = new CEV();
	public CIV CIV = new CIV();

	public Cell() {
	}
	
	public void clear() {
	}

	public void iteration(){
	}

	public void addNeighbor(Cell nei) {
		neighbors.add(nei);
	}

	/*
	 * What color should this cell be represened by?
	 */
	public Color getColor()
	{
		int intensity = CIV.getTotalMass_kg() / 100;
		intensity = Math.max(intensity,0);
		intensity = Math.min(intensity,255);
		return new Color(0,0,255-intensity);
	}
	
}