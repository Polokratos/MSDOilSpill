package msdoilspill;

import java.util.ArrayList;

public class CIV {
    private ArrayList<OilParticle> currentParticles = new ArrayList<>();
    
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
        return currentParticles.size()*OilParticle.mass_kg;
    }
}
