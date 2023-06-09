package msdoilspill;

import java.awt.Color;
import java.util.ArrayList;

public class Cell{
	
    private static final int LAND = 1, SHORE = 2, WATER = 3;

	public ArrayList<Cell> neighbors = new ArrayList<>();
	//Required by GUI (jak jest menu wyboru typu to stąd bierze opcje)
	public static Integer []types ={0,1,2,3};
	public int type;

	public CEV cev = new CEV();
	public CIV civ = new CIV();

	public Cell(int type) {
        this.type = type;
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
        switch (type) {
            case LAND: 
                return new Color(230, 230, 174);
            case SHORE:
                return new Color(92, 212, 165);
            case WATER:
                int intensity = civ.getTotalMass_kg() / 10;
                intensity = Math.max(intensity,0);
                intensity = Math.min(intensity,255);
                return new Color(53,166,255-intensity);
            default:
                return new Color (255, 255, 255);
        }
	}
	
	public void calculateSpreading(Cell with, int dX, int dY, int iter_num) /*dX, dY ==> FROM THIS TO WITH AT -1,0,1 scale*/
	{
		double kinematic_viscosity_avg = (this.civ.getKinematicViscosity_kg_over_ms() + with.civ.getKinematicViscosity_kg_over_ms())/2;
		double volume_total = this.civ.getTotalVolume_m3() + with.civ.getTotalVolume_m3();
//		double density_delta = (this.civ.getDensityDelta()+with.civ.getDensityDelta())/2;
		double density_delta = (Globals.waterDensity_kg_over_m3-Globals.oilDensity_kg_over_m3)/Globals.waterDensity_kg_over_m3;
		double t = (2*(double)iter_num+Globals.simulationStep_s)/2;
		double n = 3; // From "calibrations" - wartosc z dupy
		double D = (0.48/(n*n))*Math.pow(volume_total*volume_total*Globals.Earth_gravitation_constant*density_delta/Math.pow(kinematic_viscosity_avg,0.5),1/3)*Math.pow(t,-0.5);

		double delta_mass = 0.5*(with.civ.getTotalMass_kg() - this.civ.getTotalMass_kg())*(1-Math.pow(Math.E,-2*D/(CEV.cellSize_m*CEV.cellSize_m)*Globals.simulationStep_s));

		Cell src;
		dX *= CEV.cellSize_m; //rescale to cell size;
		dY *= CEV.cellSize_m;
		if(delta_mass >= 0) {src = this;}
		else { src = with; dX = -dX; dY = -dY;}
		double move_probability = Math.abs(delta_mass)/src.civ.getTotalMass_kg(); //Randomization coefficient

		for (var op : src.civ.currentParticles) 
			if(RNG.getInstance().nextDouble() < move_probability)
			{
				op.locationDelta_x_m += dX;
				op.locationDelta_y_m += dY;
			}
	}
}
