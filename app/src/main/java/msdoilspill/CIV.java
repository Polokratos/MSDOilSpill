package msdoilspill;

import java.util.ArrayList;

import javax.management.RuntimeErrorException;

public class CIV {
    public ArrayList<OilParticle> currentParticles = new ArrayList<>();
    
    public ArrayList<OilParticle> getParticles() { return currentParticles;}
    //FIXME: Add guards against random shit.
    public void addParticle(OilParticle toAdd)
    {
        currentParticles.add(toAdd);
    }
    //FIXME: Add guards against random shit.
    public void removeParticle(OilParticle toRemove)
    {
        currentParticles.remove(toRemove);
    }

    public int getTotalMass_kg()
    {
        int retval = 0;
        for (OilParticle oilParticle : currentParticles) {
            retval += oilParticle.OPS.mass_kg;
        }
        return retval;
    }

    public double getTotalVolume_m3() {
        double retval = 0;
        for (OilParticle oilParticle : currentParticles) {
            retval+=oilParticle.OPS.mass_kg/Globals.oilDensity_kg_over_m3 + oilParticle.OPS.water_mass_kg/Globals.waterDensity_kg_over_m3;
        }
        return retval;
    }
    public double getKinematicViscosity_kg_over_ms()
    {
        double water = 0;
        double oil = 0;
        for (OilParticle oilParticle : currentParticles) {
            water+=oilParticle.OPS.water_mass_kg;
            oil+=oilParticle.OPS.mass_kg;
        }
        double density = (oil*Globals.oilDensity_kg_over_m3 + water*Globals.waterDensity_kg_over_m3)/(oil+water);
        double kv = (oil * Globals.oilDynamicViscosity + water*Globals.waterDynamicViscosity)/(density * (oil+water));
        return kv;
    }

    public int getDensityDelta()
    {
        throw new RuntimeErrorException(null);
    }
}
