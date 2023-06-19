package msdoilspill;

/*
 * Globals: For all values global to the simulation that have no other place as of yet.
 */
public class Globals {
    public static final int simulationStep_s = 20; //One step of simulation simulates this amt of seconds IRL.
    public static final double AdvectionAlpha = 1.1;
    public static final double AdvectionBeta = 0.03;

//    public static final double EvaporationTb = 341.87;
//    public static final double EvaporationTbI = 413;

    public static final double waterDensity_kg_over_m3 = 1000;
    public static final double oilDensity_kg_over_m3 = 835;
    public static final double Earth_gravitation_constant = 9.81;
}
