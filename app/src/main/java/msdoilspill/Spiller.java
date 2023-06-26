package msdoilspill;

public class Spiller {
    
    /*
     * Stub. Actual spill would be governed by external data.
     */
    public static void spill()
    {
        spillInPlace(224,116,1000);
    }

    /*
     * There may be spillage in differenct cells simultaneously. This method adds OPs to a single cell.
     */
    private static void spillInPlace(int x, int y, int spillMass_kg)
    {
        int OPamt = spillMass_kg / OilParticle.initial_mass_kg;
        for (int i = 0; i < OPamt; i++) {
            int x_m = x*CEV.cellSize_m + RNG.getInstance().nextInt(CEV.cellSize_m);
            int y_m = y*CEV.cellSize_m + RNG.getInstance().nextInt(CEV.cellSize_m);
            new OilParticle(x_m, y_m); //FIXME: Refactor via factory method.
        }
    }
}
