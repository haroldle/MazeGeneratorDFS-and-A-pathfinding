At first the program is going to run a star path first. 
After A star path is finished it will start run dfs path. 
However, the bread crumbs from Astar will not be erase
In the terminal it will print in a format
DONE!!!!
Number of how many moves in Astar
DONE!!!!
Number of how many moves in dfs

Installation:
Linux: Ubuntu 18.04
1. Make sure to install java version 11.0.6 and javac version 11.0.6 and
install sdk application to ubuntu
to install kotlin type:
	 curl -s https://get.sdkman.io | bash
after download is finished type:
	sdk install kotlin
If the installation of kotlin is unsuccessful please go to kotlinlang.org to look for download tutorial

2. put two file Maze.kt and runMaze.kt in the same folder
3. run these commands:
First command: kotlinc runMaze.kt Maze.kt -include-runtime -d runMaze.jar
Second command: java -jar runMaze.jar

Windows 10:
Install IntelliJ software.
Create new project.
Add two file runMaze.kt and Maze.kt into the scr folder which is inside the project folder.
follow IntelliJ suggestion to install all necessary packages.