package msdoilspill.delegating;

import java.util.ArrayList;
/**
 * Observer design pattern implementation.
 * 
 * A class representing a possible.. well event. let's call it X.
 * If a module requires to know that X happened, it may add an observer function, and if X happens, it will make a method call to the function.
 */
public class Event {
    private ArrayList<Action> observers;

    public Event()
    {
        observers = new ArrayList<Action>();
    }

    /**
     * Call this when the event happens.
     * @param args
     */
    public void raise()
    {
        for (Action obs : observers)
            obs.invoke();
    }

    public void addObserver(Action obs)
    {
        observers.add(obs);
    }
    public void removeObserver(Action obs)
    {
        observers.remove(obs);
    }
}