import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.*;

/**
* Maze represents a maze that can be navigated. The maze
* should indicate its start and end squares, and where the
* walls are. 
*
* Eventually, this class will be able to load a maze from a
* file, and solve the maze.
* The starter code has part of the implementation of load, but
* it does not read and store the information about where the walls of the maze are.
*
*/
public class Maze { 
    //Number of rows in the maze.
    private int numRows;
    
    //Number of columns in the maze.
    private int numColumns;
    
    //Grid coordinates for the starting maze square
    private int startRow;
    private int startColumn;
    
    //Grid coordinates for the final maze square
    private int finishRow;
    private int finishColumn;
    
    //**************YOUR CODE HERE******************
    //You'll likely want to add one or more additional instance variables
    //to store the squares of the maze
    ArrayList<MazeSquare> squaresInMaze;
    ArrayList<MazeSquare> squaresInSolution = new ArrayList<MazeSquare>();
    /**
     * Creates an empty maze with no squares.
     */
    public Maze() {
        //You can add any code you need to initialize instance 
        //variables you've added.
    } 
    
    /**
     * Loads the maze that is written in the given fileName.
     * Returns true if the file in fileName is formatted correctly
     * (meaning the maze could be loaded) and false if it was formatted
     * incorrectly (meaning the maze could not be loaded). The correct format
     * for a maze file is given in the assignment description. Ways 
     * that you should account for a maze file being incorrectly
     * formatted are: one or more squares has a descriptor that doesn't
     * match  *, 7, _, or | as a descriptor; the number of rows doesn't match
     * what is specified at the beginning of the file; or the number of
     * columns in any row doesn't match what's specified at the beginning
     * of the file; or the start square or the finish square is outside of
     * the maze. You can assume that the file does start with the number of
     * rows and columns.
     * 
     */
    public boolean load(String fileName) { 
      try {
      File file = new File(fileName);
      Scanner reader = new Scanner(file);
      String data = reader.nextLine();
      int row = 0; 
      String[] sizeData = data.split(" ");
      numColumns = Integer.parseInt(sizeData[0]);
      numRows = Integer.parseInt(sizeData[1]);
      if (reader.hasNextLine())
      {
        data = reader.nextLine();
        String[] startData = data.split(" ");
        startColumn = Integer.parseInt(startData[0]);
        startRow = Integer.parseInt(startData[1]);
      }
      if (reader.hasNextLine())
      {
        data = reader.nextLine();
        String[] endData = data.split(" ");
        finishColumn = Integer.parseInt(endData[0]);
        finishRow = Integer.parseInt(endData[1]);
      }
      squaresInMaze = new ArrayList<MazeSquare>();
      while (reader.hasNextLine()) 
      {
        int col = 0;
        data = reader.nextLine();
        String[] rowData = data.split(" ");
        char[] squares = data.toCharArray();

	    	for (char square : squares) {
          String each = Character.toString(square);
          if (each.equals("*") || each.equals("7") || each.equals("_") || each.equals("|"))
          {
            MazeSquare newSquare = new MazeSquare(each, col, row);
            squaresInMaze.add(newSquare);
          }
          else
          {
            return false;
          }
         col++;
        }
        row++;
      }
      reader.close();
    } 
    catch (FileNotFoundException e) 
    {
      return false;
    }
    return true;
    } 
    
    /**
     * Returns true if number is greater than or equal to lower bound
     * and less than upper bound. 
     * @param number
     * @param lowerBound
     * @param upperBound
     * @return true if lowerBound ≤ number < upperBound
     */
    private static boolean isInRange(int number, int lowerBound, int upperBound) {
        return number < upperBound && number >= lowerBound;
    }
    
    /**
     * Prints the maze with the start and finish squares marked. Does
     * not include a solution.
     */
    public void print() {
        //We'll print off each row of squares in turn.
        for(int row = 0; row < numRows; row++) {
            //Print each of the lines of text in the row
            for(int charInRow = 0; charInRow < 4; charInRow++) {
                //Need to start with the initial left wall.
                if(charInRow == 0) {
                    System.out.print("+");
                } else {
                    System.out.print("|");
                }
                
                for(int col = 0; col < numColumns; col++) {
                    MazeSquare curSquare = this.getMazeSquare(row, col);
                    if(charInRow == 0) {
                        //We're in the first row of characters for this square - need to print
                        //top wall if necessary.
                        if(curSquare.hasTopWall()) {
                            System.out.print(getTopWallString());
                        } else {
                            System.out.print(getTopOpenString());
                        }
                    } else if(charInRow == 1 || charInRow == 3) {
                        //These are the interior of the square and are unaffected by
                        //the start/final state.
                        if(curSquare.hasRightWall()) {
                            System.out.print(getRightWallString());
                        } else {
                            System.out.print(getOpenWallString());
                        }
                    } else {
                        //We must be in the second row of characters.
                        //This is the row where start/finish should be displayed if relevant
                        
                        //Check if we're in the start or finish state
                        if(startRow == row && startColumn == col) {
                            System.out.print("  S  ");
                        } else if(finishRow == row && finishColumn == col) {
                            System.out.print("  F  ");
                        } else if (squaresInSolution.contains(curSquare)){
                            System.out.print("  *  ");
                        } else {
                            System.out.print("     ");
                        }
                        if(curSquare.hasRightWall()) {
                            System.out.print("|");
                        } else {
                            System.out.print(" ");
                        }
                    } 
                }
                
                //Now end the line to start the next
                System.out.print("\n");
            }           
        }
        
        //Finally, we have to print off the bottom of the maze, since that's not explicitly represented
        //by the squares. Printing off the bottom separately means we can think of each row as
        //consisting of four lines of text.
        printFullHorizontalRow(numColumns);
    }
    
    /**
     * Prints the very bottom row of characters for the bottom row of maze squares (which is always walls).
     * numColumns is the number of columns of bottom wall to print.
     */
    private static void printFullHorizontalRow(int numColumns) {
        System.out.print("+");
        for(int row = 0; row < numColumns; row++) {
            //We use getTopWallString() since bottom and top walls are the same.
            System.out.print(getTopWallString());
        }
        System.out.print("\n");
    }
    
    /**
     * Returns a String representing the bottom of a horizontal wall.
     */
    private static String getTopWallString() {
        return "-----+";
    }
    
    /**
     * Returns a String representing the bottom of a square without a
     * horizontal wall.
     */
    private static String getTopOpenString() {
        return "     +";
    }
    
    /**
     * Returns a String representing a left wall (for the interior of the row).
     */
    private static String getRightWallString() {
        return "     |";
    }
    
    /**
     * Returns a String representing no left wall (for the interior of the row).
     */
    private static String getOpenWallString() {
        return "      ";
    }
    
    /**
     * Implement me! This method should return the MazeSquare at the given 
     * row and column. The line "return null" is added only to make the
     * code compile before this method is implemented. Delete that line and
     * replace it with your own code.
     */
    public MazeSquare getMazeSquare(int row, int col) {
        //**************YOUR CODE HERE******************
        return squaresInMaze.get(row*numColumns + col%numColumns);
    }

    
    public Stack<MazeSquare> getSolution()

    {
      MysteryStackImplementation<MazeSquare> stack = new MysteryStackImplementation<MazeSquare>();
      stack.push(squaresInMaze.get(startRow*numColumns + startColumn%numColumns));
      squaresInMaze.get(startRow*numColumns + startColumn%numColumns).visit();
      while(!stack.isEmpty())
      {
        //is finished?
        if (stack.peek().getRow() == finishRow && stack.peek().getColumn() == finishColumn)
        {
          return stack;
        }
        //check to add left
        if (stack.peek().getColumn() % numColumns > 0 && !(squaresInMaze.get(stack.peek().getRow()*numColumns + (stack.peek().getColumn()-1)%numColumns).hasRightWall()) && !squaresInMaze.get(stack.peek().getRow()*numColumns + (stack.peek().getColumn()-1)%numColumns).isVisited())
        {
            squaresInMaze.get(stack.peek().getRow()*numColumns + (stack.peek().getColumn()-1)%numColumns).visit();
           
            stack.push(squaresInMaze.get(stack.peek().getRow()*numColumns + (stack.peek().getColumn()-1)%numColumns));
        }
        // add right
        else if ((stack.peek().getColumn() % numColumns) + 1 < numColumns && !stack.peek().hasRightWall() && !squaresInMaze.get(stack.peek().getRow()*numColumns + (stack.peek().getColumn()+1)%numColumns).isVisited())
        {
          squaresInMaze.get(stack.peek().getRow()*numColumns + (stack.peek().getColumn()+1)%numColumns).visit();
          stack.push(squaresInMaze.get(stack.peek().getRow()*numColumns + (stack.peek().getColumn()+1)%numColumns));
        }
        // add up
        else if (stack.peek().getRow() > 0 && !stack.peek().hasTopWall() && !squaresInMaze.get((stack.peek().getRow()-1)*numColumns + (stack.peek().getColumn())%numColumns).isVisited())
        {
          squaresInMaze.get((stack.peek().getRow()-1)*numColumns + (stack.peek().getColumn())%numColumns).visit();
          stack.push(squaresInMaze.get((stack.peek().getRow()-1)*numColumns + (stack.peek().getColumn())%numColumns));
        }
        // add down
        else if (stack.peek().getRow() + 1 < numRows && !(squaresInMaze.get((stack.peek().getRow()+1)*numColumns + (stack.peek().getColumn())%numColumns).hasTopWall()) && !(squaresInMaze.get((stack.peek().getRow()+1)*numColumns + (stack.peek().getColumn())%numColumns).isVisited()))
        {
            squaresInMaze.get((stack.peek().getRow()+1)*numColumns + (stack.peek().getColumn())%numColumns).visit();
            stack.push(squaresInMaze.get((stack.peek().getRow()+1)*numColumns + (stack.peek().getColumn())%numColumns));
        }
        else
        {
          stack.pop();
        }
      }
      return null;
    }
    /**
     * You should modify main so that if there is only one
     * command line argument, it loads the maze and prints it
     * with no solution. If there are two command line arguments
     * and the second one is --solve,
     * it should load the maze, solve it, and print the maze
     * with the solution marked. No other command lines are valid.
     */ 
    public static void main(String[] args) { 
        Maze mainMaze = new Maze();
        mainMaze.load(args[0]);
        mainMaze.print();
        if (args.length > 1)
        {
          if (args[1].equals("--solve"))
          {
            Stack<MazeSquare> solutions = mainMaze.getSolution();
            if (solutions != null)
            {
              while(!solutions.isEmpty())
              {
                mainMaze.squaresInSolution.add(solutions.pop());
                System.out.println();
                mainMaze.print();
              }
            }
            else
              {
                System.out.print("Unsolvable.");
              }
          }
        }

        
    } 
}