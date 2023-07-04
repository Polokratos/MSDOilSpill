# MSDOilSpill

## About
This project was made for a "Discreete Systems Modeling" course at the
**AGH UST**. The aim was to simulate a spill of oil into the ocean,
taking into consideration multiple natural phenomena and weather
conditions (such as speed of current) in a given area. For testing
purposes we chose a reactangular region with dimensions 799x227 [km]
in the Gulf of Mexico.

## Technologies used
The application is written in Java we used the Gradle build system
for managing and running the project. Additionally we utilized: 

- Java Swing library for the GUI
- Python for data preprocessing

## Running the project
Building and running the code is entirely managed by Gradle. 
You can use your editor's tools if it has support for it or
simply run the script provided by gradle. Enter the project directory
and type
```
./gradlew run
```
on a Unix-like system or

```
.\gradlew run
```
on Windows (in both cases substitule run for build if you just want
to compile the project).


