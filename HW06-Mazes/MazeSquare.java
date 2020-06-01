/**
* MazeSquare represents a single square within a Maze.
* @author Anna Rafferty
*/ 
public class MazeSquare {
    //Wall variables
    private boolean hasTopWall = false;
    private boolean hasRightWall = false;
		
    //Location of this square in a larger maze.
    private int row;
    private int col;

    private boolean visited = false;
    private boolean inSolution = false;
		
    public MazeSquare(String data, int _col, int _row)
    {
      col = _col;
      row = _row;
      if (data.equals("7"))
      {
        hasTopWall = true;
        hasRightWall = true;
      }
      else if (data.equals("_"))
      {
        hasTopWall = true;
      }
      else if (data.equals("|"))
      {
        hasRightWall = true;
      }
    }
		
    /**
     * Returns true if this square has a top wall.
     */
    public boolean hasTopWall() {
        return hasTopWall;
    }
		
    /**
     * Returns true if this square has a right wall.
     */
    public boolean hasRightWall() {
        return hasRightWall;
    }
		
    /**
     * Returns the row this square is in.
     */
    public int getRow() {
        return row;
    }
		
    /**
     * Returns the column this square is in.
     */
    public int getColumn() {
        return col;
    }

    public void visit()
    {
      visited = true;
    }
   
    public boolean isVisited()
    {
     return visited;
    }
    
    public void isInSolution()
    {
      inSolution = true;
    }
    public boolean checkSolution()
    {
     return inSolution;
    }
    
} 