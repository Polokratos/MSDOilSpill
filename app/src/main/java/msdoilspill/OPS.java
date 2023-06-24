package msdoilspill;
//Oil point State <=> Oil Particle State <=> Wartosci wewnetrzne jednej kropelki.
public class OPS {
    public double mass_kg;
    public double water_mass_kg;
    public double dynamic_viscosity;

    public OPS(int oil_mass){
        mass_kg=oil_mass;
        water_mass_kg=0;
        dynamic_viscosity = Globals.oilDynamicViscosity;
    }
    public double get_density(){
        double Y = water_mass_kg/(water_mass_kg+mass_kg);
        return (1-Y)*Globals.oilDensity_kg_over_m3 + Y * Globals.waterDensity_kg_over_m3;
    }
    public double getVolume(){
        return mass_kg/Globals.oilDensity_kg_over_m3 + water_mass_kg/Globals.waterDensity_kg_over_m3;
    }
    public double getDynamicViscosity(){
        return dynamic_viscosity;
    }


}
