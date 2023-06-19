package msdoilspill;
//Oil point State <=> Oil Particle State <=> Wartosci wewnetrzne jednej kropelki.
public class OPS {
    public int mass_kg;
    public double water_mass_kg;

    public OPS(int oil_mass){
        mass_kg=oil_mass;
        water_mass_kg=0;
    }
    public double get_density(){
        double Y = water_mass_kg/(water_mass_kg+mass_kg);
        return (1-Y)*Globals.oilDensity_kg_over_m3 + Y * Globals.waterDensity_kg_over_m3;
    }


}
