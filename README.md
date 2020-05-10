# Zombie Apocalypse

### Solution
The solution is provided in scala (2.13.2) using a functional approach without using any advanced libraries (like cats/scalaz). One library I used is circe for JSON parsing.

In designing the solution I decided to adapt the functional approach of working with data and a series of transformations without mutating state.
I have created some Data Types to model the domain, but did not follow the object oriented approach of modelling the whole domain as objects sending messages to collaborators and mutating state.
The World is described by bounds and data structures that represent zombies, creatures and movements. Both zombies and creatures are really just positions in the grid.   

The programme accepts input in text and JSON formats (as specified in problem statements), but can esily read another format.

### Running
You'll need JDK11 (OpenJDK is fine), sbt 1.3.10 and scala 2.13.2.
 - to run unit tests type `sbt test` in the root directory 
 - to run 'integration' tests `sbt it:test`
 - to create an executable (jar) invoke: `./scripts/build.sh`
 - to run the programme (java -jar ...) invoke `./scripts/run.sh` passing environment variables, eg: `INPUT=sample.txt OUTPUT=json ./scripts/run.sh`
 
### Problem Description

After the nuclear war, a strange and deadly virus has infected the planet. Living creatures are becoming zombies that spread their zombiness by an unfriendly bite. The world consists of an ​n​ x​ n​ grid on which ​zombies​ ​and ​creatures​ live.

Both of these occupy a single square on the grid and can be addressed using zero-indexed x-y coordinates. Top left corner is ​(x: 0, y: 0)​ with x represent horizontal coordinate, y represent vertical coordinate. Any number of zombies and creatures may occupy the same grid square.

At the beginning of the program, a single zombie awakes and begins to move around the grid. It is given an initial x-y coordinate and a list of movements, up, down, left and right. E.g. (​U​,​D​,​L​,​R​).

The ordered sequence of movements needs to be represented somehow as input, for example: DLUURR​ (down, left, up, up, right, right). Zombies can move through the edge of the grid, appearing on the directly opposite side. For a 10x10 grid, a zombie moving left for (0,4) will move to (9,4); a zombie moving down from (3,9) will move to (3,0).

The poor creatures in the area are the zombie’s victims. They also have an initial x-y coordinate. The creatures are aware of zombie presence but are so frightened that they never move.

If a zombie moves so that it end up on the same location as a creature, the creature is transformed into another zombie and zombies score one ​point​. The zombie continues moving and infecting creatures until has performed all its moves.

Once it has completed its movement, the first newly created zombie moves using the same sequence as the original zombie. Once it has completed its move, the second newly created zombie moves, and so in order of infection with each zombie performing the same sequence of moves. Once no new zombies have been created and all the zombies have completed moving the program ends.

Your task is to write a program that takes input that describes the following parameters:
- dimensions of the area (N)
- the initial position of the zombie
- a list of positions of poor creatures
- and a list of moves zombies will make

to produce an output that shows:
- the number of points scored by the zombies
- the final position of the zombies

The input and output are not limited to a particular format. I.e. it can be json, txt, visual input etc.

Example input and output using txt file:

|Example input:   | Example output:                       |
|-----------------|---------------------------------------|
|4                |zombies score: 3                       |
|(2,1)            |zombies positions: (3,0)(2,1)(1,0)(0,0)|
|(0,1)(1,2)(3,1)  |                                       |
|DLUURR           |                                       |
 

 

 
     
