import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;
import java.awt.*;
import javax.swing.*;
import java.util.Scanner;

public class AStar
{
    //Stores an arraylist of arraylists of location classes which are expanded and added to
    //in order to create the states
    ArrayList<ArrayList<location>> states = new ArrayList<ArrayList<location>>();
		//Stores an individual state which is an array of locations
		ArrayList<location> stateArray;
		//Stores the ASCII map as a 2 dimentional array of locations
		location[][] ASCIImap;
		//Stores the
		File ASCIIF;
		//Stores the size of the frontier to be output once the
		int frontierSize = 0;
		String line = "";
    //Are varibles which are used to be able to save where
    //the goal and the start positions will be for the path finding
    int startX = -1;
    int startY = -1;
    int goalX = -1;
    int goalY = -1;
    //Is varibles which are able to go through the height and width
    int i = 0;
    int j = 0;
    //Stores the Euclidean distance calculation
    double EdistCal;
    //Is used to be able to store the bounds of the map
    int xMax;
    int yMax;
    //Is a float value which is used to be able to calculate the Euclidean distance
    //from the goal
    double dist;
    //Is a boolean which is used to be able to know if a pair of "+" are encountered
    //when going down the height of the map
    boolean plusFound;
    //Is a class which is used to be able to store a location in creating the map and
    //keeping track of the locations which are met when a state is being created / expanded
    public class location {
      //Stores the Euclidean distance
      double Edist;
      //Stores the X and Y cordinates of the
      int x;
      int y;
      //Is a boolean which is used for the A* search which is set to true
      //when it has been passed over to prevent loops or less effecient states
      boolean passed = false;
      //Is a boolean which is used to be able to determine what locations are used
      //in creating the path from the start to the goal
      boolean path = false;
      //Stores the number of were the location is in order to show the sequence
      //which the final path is navigated
      int pos = 0;
      //Sets the values as passed in
      public location(int locationX, int locationY, double calulatedEdist) {
        x = locationX;
        y = locationY;
        Edist = calulatedEdist;
      }
    }

    public static void main(String[] args)
    {
			AStar program = new AStar();
			program.start(args[0]);
    }

		public void start(String file){
		  //First try read in the ASCII map
      try {
        //Uses the passed in argument to open the file
        ASCIIF = new File(file);
        //First goes through to find the bondarys of the map
        Scanner scan = new Scanner(ASCIIF);
        //If there is lines which can be read
        if (scan.hasNextLine()) {
          //Read in the first line
          line = scan.nextLine();
          //Checks that the first char is a "+"
          if (Character.compare(line.charAt(i), '+') != 0) {
            //Give an error if not
            System.out.println("Map width does not start with a '+' to show the boundary");
            //Terminate the program
            System.exit(0);
          }
          //Moves on from the '+'
					i++;

          //For each of the characters in the top row check that they are -
          while (i < (line.length()-1)) {
            //Checks to make sure that the values are all "-"
            if (Character.compare(line.charAt(i), '-') != 0) {
              System.out.println("Invalid char " + line.charAt(i) + " encountered at X: " + i + " Y: " + j);
              //Terminate the program
              System.exit(0);
            }
            //Moves on to the next character in the width
            i++;
          }
          //Determines that there is a "+" at the end of the line
          if (Character.compare(line.charAt(line.length()-1), '+') != 0) {
            //Give an error to say that the map does not
            //end with a "+" to show the boundary
            System.out.println("Map width does not end with a '+' to show the boundary");
            //Terminate the program
            System.exit(0);
          }
					//Adds 1 to j to go inside the map boundarys
					j++;

          //Goes through the steps to find if the
          //map being passed in has a character which
          //is valid, if it encounters anything which
          //is not a "S", "G", "X" or " " to
          //give an error and returns from the program
          while (scan.hasNextLine()) {
            //Reads the line
            line = scan.nextLine();
            //Breaks the loop if pair of "+" are encountered
            if ((Character.compare(line.charAt(0), '+') == 0) && (Character.compare(line.charAt(i), '+') == 0)) {
              //Checks to make sure that everything beween is "-"
              int g = 1;
              //For each of the characters in the bottom row check that they are -
              while (g < (line.length()-1)) {
                //Checks to make sure that the values are all "-"
                if (Character.compare(line.charAt(g), '-') != 0) {
                  System.out.println("Invalid char " + line.charAt(i) + " encountered at X: " + i + " Y: " + j);
                  //Terminate the program
                  System.exit(0);
                }
                //Moves on to the next character in the width
                g++;
              }
              //If all correct than set plusFound to be true
              plusFound = true;
              break;
            }
            //Checks to see if the value is any character other than "|"
            else if ((Character.compare(line.charAt(0), '|') != 0) && (Character.compare(line.charAt(i), '|') != 0)) {
              System.out.println("Invalid char encounted for border or non matching '+' / '|'");
              //Terminate the program
              System.exit(0);
            }
            //Goes through to try and find a start "S" or goal "G" between the bounds (0 = '|', i = '|')
            for (int f = 1; f < i; f++) {
              //Provides ways of storing the goal corodinates
              if(Character.compare(line.charAt(f), 'G') == 0) {
                //Checks to make sure that there is only one goal by checking
                //that the x and y values for goal are currently null
                if (goalX == -1 && goalY == -1) {
                  //Stores the values of the goal cordinates
                  goalX = f;
                  goalY = j;
                }
                //Otherwise give error as there can not be 2 goals
                else {
                  System.out.println("More than 1 goal 'G' present in the map");
                  //Terminate the program
                  System.exit(0);
                }
              }
              //Provides ways of storing the start corodinates
              else if(Character.compare(line.charAt(f), 'S') == 0) {
                //Checks to make sure that there is only one start by checking
                //that the x and y values for start are currently null
                if (startX == -1 && startY == -1) {
                  //Stores the values of the goal cordinates
                  startX = f;
                  startY = j;
                }
                //Otherwise give error as there can not be 2 start positions
                else {
                  System.out.println("More than 1 start 'S' present in the map");
                  //Terminate the program
                  System.exit(0);
                }
              }
              //If not Goal or Start check that the character is an invalid
              //option which is not a "X" or " "
              else if ((Character.isWhitespace(line.charAt(f)) == false) && Character.compare(line.charAt(f), 'X') != 0) {
                System.out.println("Invalid Character '" + line.charAt(f) + "' Encountered inside bounds of map at X: " + f + " Y: " + j);
                //Terminate the program
                System.exit(0);
              }
            }
            //Increments j to record the mapHeight
            j++;
          }
          //Checks to make sure that a pair of "+" have been met once
          if (plusFound != true) {
            System.out.println("Map height completed but does not end with a pair of '+' to show the boundary");
            //Terminate the program
            System.exit(0);
          }
          //Checks to make sure that if the while loop has broke once a pair of "+"
          // is met that it is at the end of the ASCII map file
          else if (scan.hasNextLine()) {
            System.out.println("Map height goes past bottom pair of '+'");
            //Terminate the program
            System.exit(0);
          }
          //Checks that there are values for the start and goal
          if (startX == -1 || startY == -1 || goalX == -1 || goalY == -1) {
            System.out.println("Goal or start point never found inside map");
            //Terminate the program
            System.exit(0);
          }
          //Otherwise a valid map is found
          System.out.println("Map bounds found! ");
          //Prints out the start and goal cordinates
  				System.out.println("Goal location is: X: " + goalX + " Y: " + goalY);
          System.out.println("Start location is: X: " + startX + " Y: " + startY);
        }

        //Creates a array which are used to be able to store the locations
        ASCIImap = new location[i][j];

        //Sets up the bounds using the recorded i and j values and subtracting 1 to keep
        //inside the bounds of what is setup
        xMax = i-1;
        yMax = j-1;
        //Print out the size of the map (From within the bounds)
        System.out.println("Size of map is X: " + xMax + " Y: " + yMax);


        //Begins the process of putting the map into a 2 dimentional array

        //Sets up the scanner for reading in the lines of the map
        //for what is inside the bounds
        scan = new Scanner(ASCIIF);
				//Goes past the "+----+" line
				scan.nextLine();
				//Sets j (for determining if yMax is reached) to start at 1
				j = 1;
        //Sets i to be within the bounds of the map
        i = 1;
        //while there are still lines which can be read which are within the yMax range
        while(scan.hasNextLine() && j <= yMax) {
          //Reads in the next line from scanner
          line = scan.nextLine();
          //While reading everything between the "| |"
          while (i <= xMax) {
            //If its is an obsticle
            if(Character.compare(line.charAt(i), 'X') == 0) {
                //Sets location to be an obsticle by having
                //Euclidean value as being -1
                EdistCal = -1;
            }
            //Otherwise is either a Start, Goal or Space location
            //calculate the Euclidean distace from the goal co-ordinates
            else {
              //First calculate the difference between x goal and current pos
              //and y goal and current pos
              int Xdis = i - goalX;
              int Ydis = j - goalY;
              //Calculate the Euclidean distace
              EdistCal = Math.sqrt((Xdis*Xdis) + (Ydis*Ydis));
            }
            //Adds the location to the ASCIImap
            ASCIImap[i][j] = new location(i, j, EdistCal);
            //Prints out the datails of the location added
            //System.out.println(line.charAt(i) + "(X:" + i + "),(Y:" + j + ") Edist = " + EdistCal);
            //Increments i to go to the next location in the line
            i++;
          }
          //Goes down one row and puts i back to the start
					j++;
          i = 1;
        }

        //Once the 2d array of states has been created start the A* search

        //Adds the start location to an array and adds it to the states array
        stateArray = new ArrayList<location>();
        stateArray.add(ASCIImap[startX][startY]);
        states.add(stateArray);
        //Sets the inital start state as having been passed
        ASCIImap[startX][startY].passed = true;
        //Goes through the process of expanding, removing and adding states
        //to the array while printing out the frontier size during the process
        //while the stateArray still contains possible states
        while(states.isEmpty() != true){
          //Starts with the first value / index of the arrayList of states
          stateArray = states.get(0);
          //Calculates the f value through first cost + Euclidean distance of the first location
          //(stateArray.size()-1) +
          double f = stateArray.size() + stateArray.get(stateArray.size()-1).Edist;
          double fx = stateArray.get(stateArray.size()-1).x;
          double fy = stateArray.get(stateArray.size()-1).y;
          //Goes through each of the currentCosts to find the lowest value when
          //adding together the Euclidean distance and the current cost
          for(int a = 1; a < states.size(); a++){
            //calculates the 2ndf value
            ArrayList<location> stateArray2 = states.get(a);
            double f2 = stateArray.size() + stateArray.get(stateArray.size()-1).Edist;
            double fx2 = stateArray.get(stateArray.size()-1).x;
            double fy2 = stateArray.get(stateArray.size()-1).y;
            //Compare it to the f value of the previous calculated to see if f2 is lower
            if (f2 < f) {
              //Sets the corrosponding varibles for f2 as the new lowest f
              stateArray = stateArray2;
              f = f2;
            }
          }
          //Once the lowest value has been found, find the location which needs to
          //be expanded from (Last one added from get(stateArray.size()-1);)
          location expand = stateArray.get(stateArray.size()-1);
          int ExpX = expand.x;
          int ExpY = expand.y;
          //Prints out the current location being expanded
          //System.out.println("Location being expanded (X:" + ExpX + "),(Y:" + ExpY + ")");
          //Removes the state from the stateArray (If there were no possible moves
          //then no extended versions would have been added, if there were possible
          //moves then the old state is now redundant)
          states.remove(stateArray);
          //Try to expand the corresponding state by checking which location that
          //is Up = ExpY-1, Down = ExpY+1, Left = ExpX-1, Right = ExpX+1
          // the current location and that the result is not a obsticle (-1)

          //If possible to check Left
          if ((ExpX - 1) >= 1){
            //Calls the method which will add a expanded state and set
            //check made to be true if it is made, uses the stateArray
            //for the methoid to determine which state is being expanded
            canBeMade(ExpX - 1, ExpY, stateArray);
          }
          //If possible to check Right
          if ((ExpX + 1) <= xMax) {
            canBeMade(ExpX + 1, ExpY, stateArray);
          }
          //If possible to check Down
          if ((ExpY - 1) >= 1){
            canBeMade(ExpX, ExpY - 1, stateArray);
          }
          //If possible to check Up
          if((ExpY + 1) <= yMax) {
            canBeMade(ExpX, ExpY + 1, stateArray);
          }

          /*System.out.println("Contents of Expanded");
          for (int i = 0; i < stateArray.size(); i++){
            System.out.println(i + "  " + stateArray.get(i).Edist + "  x:" + stateArray.get(i).x + "  y:" + stateArray.get(i).y);
          }*/
          //Prints out the size of the frontier
          System.out.println("Frontier Size: " + states.size());
          //For each of the values in the Frontier print them out to the console

        }
        //If all states have been removed then let the user know that it is
        //no possible to reach the goal using the passed in map (Could be
        //due to obticles making a barrier around the goal)
        System.out.println("There is no possible way to reach the goal when naviaging the map");
        //Terminate the program
        System.exit(0);
      }
      //If there is an error reading in the ASCII map
      catch(Exception e)
      {
          System.out.println("Error processing ASCII map");
          e.printStackTrace();
          System.exit(0);
      }
		}

    //Is a methoid which is used to take in cordinates for a potental move
    //and determine if it is an obsticle or a move made in the past.
    //It can be made it will then check if the state is at the goal
    // or not, if it has reached the goal then call a
    //methoid which uses the passed in stateArray to construct the
    //final path to the goal
    public void canBeMade(int x, int y, ArrayList<location> stateArray){
      //Makes a clone of the stateArray so that the original is not altered
      ArrayList<location> state = (ArrayList<location>) stateArray.clone();
      //Checks to see if the what is pointed at is a location which can be expanded
      //(Is not already passed and is not a obsticle with a -1 Euclidean distance)
      if (ASCIImap[x][y].passed == false) {
        //Sets the passed boolean value to be true to indicate that this state
        //has been passed at the most efficent cost already through using the
        //current state values x and y cordinates
        ASCIImap[x][y].passed = true;
        if(ASCIImap[x][y].Edist != -1){
          //Adds the cost of the move as being
          //First checks if current x / y cordinates are the goal
          if((x == goalX)&&(y == goalY)) {
            //Calls the method to show the path through GUI Buttons
            displayPath(x, y, state);
            System.out.println("press Enter to finish viewing program");
            //Waits for the user to enter a key before closing the application
            try {
              System.in.read();
            }
            catch(Exception e) {
              System.out.println("Cant read from System.in");
            }
            System.exit(0);

          }
          //Makes a extended copy of the state made, adds the move and adds back
          //to the states array
          state.add(ASCIImap[x][y]);
          states.add(state);
        }
      }
    }

    //Is a methoid which is called once the shortest path is found, uses the passed
    //in finishedStateArray to detemine what path it needs to include the goal cordinates with
    public void displayPath(int x, int y, ArrayList<location> finishedStateArray) {
      //Let user know that a path has been found
      System.out.println("Shortest path to goal mapped successfully! Showing path");
      //adds the goal location to the end of the finishedStateArray
      finishedStateArray.add(ASCIImap[x][y]);
      //Goes through each of the locations in the array to change the
      //locations from the finishedStateArray
      for(int p = 0; p < finishedStateArray.size(); p++)
      {
          location pathFinished = finishedStateArray.get(p);
          //Sets the path boolean to true corrosponding cordinates
          //for the location from the finishedStateArray
          ASCIImap[pathFinished.x][pathFinished.y].path = true;
          //Sets the position of the location in the path
          ASCIImap[pathFinished.x][pathFinished.y].pos = p;
          //Prints out the cordinates to the system so that the user can read the steps
          System.out.println("Step " + p + ": X " + pathFinished.x + " Y " + pathFinished.y);
      }
      //Initialises the JFrame which is used to be show the output
    	JFrame output = new JFrame();
  		output.setLayout(new GridLayout(yMax,xMax));
      //Creates an array of JButtons to show the map
  		JButton[][] map = new JButton[xMax+1][yMax+1];
      //For each of the locations in the height of the map
  		for(int fY = 1; fY <= yMax; fY++){
        //For each of the locations in the width of the map
  			for(int fX = 1; fX <= xMax; fX++){
          //Checks what colour button needs to be added to represent the location

          //if it is a obsticle
          if(ASCIImap[fX][fY].Edist == -1) {
            map[fX][fY] = new JButton("");
            map[fX][fY].setBackground(Color.BLACK);
          }
          //Else if it is part of the path
          else if(ASCIImap[fX][fY].path == true) {
            map[fX][fY] = new JButton("");
            map[fX][fY].setBackground(Color.GREEN);
          }
          //Otherwise it is a space
          else {
            map[fX][fY] = new JButton("");
            map[fX][fY].setBackground(Color.WHITE);
          }
          //Adds the JButton to the grid
  				output.add(map[fX][fY]);
  			}
  		}
      //Sets the behaviour once the window is closed
  		output.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //adjusts the size to pack the buttons together
  		output.pack();
      //After everything has been added then set the output to be visible
  		output.setVisible(true);
  	}
}
