package msdoilspill;

import java.util.ArrayList;

public class OilParticle {
    public static int initial_mass_kg = 200;

    public static ArrayList<OilParticle> allParticles = new ArrayList<>();
    
    public OPS OPS;

    public double location_x_m; // X coordinate, in meters.
    public double location_y_m; // Y coorddinate, in meters.
    public Cell occupying;

    public double locationDelta_x_m;
    public double locationDelta_y_m;

    public double Y;
    public double dY=0;

    public OilParticle(int x_m,int y_m)
    {
        OPS = new OPS(initial_mass_kg);
        location_x_m = x_m;
        location_y_m = y_m;
        occupying = cacheoccupiedCell();
        occupying.civ.addParticle(this);
        allParticles.add(this);
    }

    public void advectionMovement(double deltaX_m, double deltaY_m)
    {
        locationDelta_x_m += deltaX_m;
        locationDelta_y_m += deltaY_m;
        
    }

    public void confirmMovement()
    {
        if (OPS.mass_kg<=0){
            occupying.civ.removeParticle(this);
            return;
        }
        location_x_m += locationDelta_x_m;
        location_y_m += locationDelta_y_m;
        locationDelta_x_m = locationDelta_y_m = 0;
        occupying.civ.removeParticle(this);
        occupying = cacheoccupiedCell();
        if(occupying != null) //out of AO
            occupying.civ.addParticle(this);
    }

    private Cell cacheoccupiedCell()
    {
        double x = location_x_m / CEV.cellSize_m;
        double y = location_y_m / CEV.cellSize_m;
        if( x > 0 && y>0 && x < Board.getInstance().cells.length && y < Board.getInstance().cells[0].length)
            return Board.getInstance().cells[(int)x][(int)y];
        return null;
    }

    public double getdY() {
        return dY;
    }

    public double getY() {
        return Y;
    }
}
