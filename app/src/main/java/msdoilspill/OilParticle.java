package msdoilspill;

import java.util.ArrayList;

public class OilParticle {
    public static ArrayList<OilParticle> allParticles = new ArrayList<>();
    public static int mass_kg = 20; 
    
    public int location_x_m; // X coordinate, in meters.
    public int location_y_m; // Y coorddinate, in meters.
    public Cell occupying;

    public int locationDelta_x_m;
    public int locationDelta_y_m;

    public OilParticle(int x_m,int y_m)
    {
        location_x_m = x_m;
        location_y_m = y_m;
        occupying = cacheoccupiedCell();
        occupying.CIV.addParticle(this);
        allParticles.add(this);
    }

    public void advectionMovement(int deltaX_m, int deltaY_m)
    {
        locationDelta_x_m += deltaX_m;
        locationDelta_y_m += deltaY_m;
        
    }

    public void confirmMovement()
    {
        location_x_m += locationDelta_x_m;
        location_y_m += locationDelta_y_m;
        locationDelta_x_m = locationDelta_y_m = 0;
        occupying.CIV.removeParticle(this);
        occupying = cacheoccupiedCell();
        if(occupying != null)
            occupying.CIV.addParticle(this);
    }

    private Cell cacheoccupiedCell()
    {
        int x = location_x_m / CEV.cellSize_m;
        int y = location_y_m / CEV.cellSize_m;
        if( x >= 0 && y>=0 && x < Board.getInstance().cells.length && y < Board.getInstance().cells[0].length)
            return Board.getInstance().cells[x][y];
        //out of AO, wa can stop modeling this particle.
        return null;
    }
}
