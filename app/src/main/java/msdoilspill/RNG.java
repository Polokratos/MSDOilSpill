package msdoilspill;

import java.util.Random;

public class RNG {
    private static Random instance = null;
    public static Random getInstance()
    {
        if(instance == null)
            instance = new Random();
        return instance;
    }
}
