package msdoilspill.delegating;

import java.util.ArrayList;
import java.util.function.Consumer;
/**
 * Observer design pattern implementation.
 * 
 * A class representing a possible.. well event. let's call it X.
 * If a module requires to know that X happened, it may add an observer function, and if X happens, it will make a method call to the function.
 */
public class ArgEvent<EventArgs> {
    private ArrayList<Consumer<EventArgs>> observers;

    public ArgEvent()
    {
        observers = new ArrayList<Consumer<EventArgs>>();
    }

    /**
     * Call this when the event happens.
     * @param args
     */
    public void raise(EventArgs args)
    {
        for (Consumer<EventArgs> obs : observers)
            obs.accept(args);
    }

    public void addObserver(Consumer<EventArgs> obs)
    {
        observers.add(obs);
    }
    public void removeObserver(Consumer<EventArgs> obs)
    {
        observers.remove(obs);
    }
}
