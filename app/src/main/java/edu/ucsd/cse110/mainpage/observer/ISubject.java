package edu.ucsd.cse110.mainpage.observer;

public interface ISubject<ObserverT> {

    //register a new listener
    void register(ObserverT observer);

    //unregister a listener
    void unregister(ObserverT observer);
}
