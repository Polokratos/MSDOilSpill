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

    public int getTotalVolume_m3()
    {
        throw new RuntimeErrorException(null);
    }

    public int getKinematicViscosity_kg_over_ms()
    {
        throw new RuntimeErrorException(null);
    }

    public int getDensityDelta()
    {
        throw new RuntimeErrorException(null);
    }
}
