# Zombie Apocalypse

### Solution
The solution is provided in scala (2.13.2) using a functional approach without using any advanced libraries (like cats/scalaz). One library I used is circe for JSON parsing.

In designing the solution I decided to adapt the functional approach of working with data and a series of transformations without mutating state.
I have created some Data Types to model the domain, but did not follow the object oriented approach of modelling the whole domain as objects sending messages to collaborators and mutating state.
The World is described by bounds and data structures that represent zombies, creatures and movements. Both zombies and creatures are really just positions in the grid.   

The solution accepts 


### Running
You'll need JDK11 (OpenJDK is fine), sbt 1.3.10 and scala 2.13.2.
 - to run unit tests type `sbt test` in the root directory 
 - to run 'integration' tests `sbt it:test`
 - to create an executable (jar) invoke: `./scripts/build.sh`
 - to run the programme (java -jar ...) invoke `./scripts/run.sh` passing environment variables, eg: `INPUT=sample.txt OUTPUT=json ./scripts/run.sh`
 

 
     
