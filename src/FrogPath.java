/**This is a class to determine Freddy's movement path on a grid of hexagons in order to get
 * to the other frog.
 * March 20 2024
 */

public class FrogPath {
    private Pond pond;

    //Constructor, takes from a file to determine Freddy's grid.
    public FrogPath (String filename) {

        try {
            pond = new Pond(filename);
        } catch (Exception e) {
            System.out.print("Exception");
        }
    }
    //Make sure that the neighboring and other accessible cells aren't next to an alligator
    private boolean isValid(Hexagon currCell) {
        //Check for if the cell is offGrid, if it's the starting cell, or if it's marked.
        if (currCell == null || currCell.isStart() || currCell.isMarked()) {
            return false;
        }
        //Check for alligator from all directions
        for (int i = 0; i < 6; i++) {
            Hexagon neighbourCell = currCell.getNeighbour(i);
            // Proceed only if the neighbor cell is not null.
            if (neighbourCell != null && neighbourCell.isAlligator()) {
                // If any of the neighbor cells is an alligator, return false.
                return false;}

            // If no adjacent alligators were found, no mud, current cell is not null, return false.
        }  return true;
    }


    // Determines if the hexagon grid is a fly grid, return 0 if no flies are found.
    private int numFlies(Hexagon currCell) {
        if (currCell instanceof FoodHexagon) {
            // Downcast to access FoodHexagon-specific method
            FoodHexagon foodHexagon = (FoodHexagon) currCell;
            int flies = foodHexagon.getNumFlies();
            return flies;
        } else {
            return 0;
        }
    }

    /**
     * @param neighborCell - the neighbour to the current cell that will be analyzed
     * @param doubleMove - true if Freddy is doing a double jump
     * @param line - true if Freddy is double jumping AND in a straight line
     * @return the priority of the cell given all other circumstances. Priority is 99.0 if
     * 			the cell is not valid (according to isValid()).
     */
    private double findPriority (Hexagon neighborCell, boolean doubleMove, boolean line) {

        double prio = 99.0;

        int numFlies = numFlies(neighborCell);

        //if neighboring cell is valid:
        if (isValid(neighborCell)) {

            if(neighborCell.isWaterCell()) {
                prio = 6.0;
            }
            //If adjacent reeds cell does not adjacent an alligator:
            else if (neighborCell.isReedsCell()) {
                prio = 5.0;
            }

            else if (neighborCell.isLilyPadCell()) {
                prio = 4.0;
            }
            //If the cell is a fly cell:
            else if (numFlies != 0) {
                if (numFlies == 1) {
                    prio = 2.0 ;
                }
                else if (numFlies == 2) prio = 1.0;
                else prio = 0.0;
            }
        }
        //If neighbor cell has reeds next to an alligator:
        else if (neighborCell.isReedsCell()) prio = 10.0;

        else {
            prio = 99.0;
        }
        //If double moving:
        if (doubleMove == true && line == true && prio != 99.0) {
            prio += 0.5;
        }
        else if (doubleMove == true && line == false && prio != 99.0) {
            prio += 1.0;
        }
        //If no changes are made to priority, return number of 99.0
        return prio;
    }

    //Finds the best step for Freddy to take, according to calculated priority. Put the priority of
    //steps into the priority queue.
    public Hexagon findBest(Hexagon currCell) {
        ArrayUniquePriorityQueue<Hexagon> priorityQueue = new ArrayUniquePriorityQueue<Hexagon>();
        //Start with the innermost sides 0-5
        for (int i = 0; i<6; i++) {

            if (currCell.getNeighbour(i) != null && currCell.getNeighbour(i).isValid() ) {
                Hexagon neighborCell = currCell.getNeighbour(i);
                double prio = findPriority(neighborCell, false, false);

                //add neighbor cell if returning a valid priority
                if (prio != 99.0) {
                    priorityQueue.add(neighborCell, prio);
                }
            }

            //If Freddy is on a lilypad, use findPriority to find priority when double jumping and moving in a straight line.
            if (currCell.isLilyPadCell()) {
                for (int x = 0; x<6; x++) {
                    //Find neighbourCell's neighbour
                    if (x < 5 && isValid(currCell.getNeighbour(x))) {
                        Hexagon farCell = (currCell.getNeighbour(x)).getNeighbour(x);

                        //If farCell is neighbourCell's same index to currCell, then Freddy will move
                        //in a straight line.
                        if (isValid(farCell)) {
                            double prio = findPriority(farCell, true, true);
                            if (prio != 99.0) priorityQueue.add(farCell, prio);
                        }

                        //If farCell is neighbourCell's index + 1, then Freddy will be moving in
                        //double-jump and not in a straight line
                        farCell = (currCell.getNeighbour(x)).getNeighbour(x + 1);
                        if (isValid(farCell)) {
                            double prio = findPriority(farCell, true, false);
                            if (prio != 99.0) priorityQueue.add(farCell, prio);
                        }

                        //If neighbour index is 5, index+1 does not exist.
                    } 	else if (x == 5) {
                        if (currCell.getNeighbour(x) != null) {
                            if ((currCell.getNeighbour(x)).getNeighbour(x) != null) {
                                Hexagon farCell = (currCell.getNeighbour(x)).getNeighbour(x);
                                double prio = findPriority(farCell, true, true);
                                if (prio != 99.0) priorityQueue.add(farCell, prio);
                            }
                        }
                    }

                }
            }
        } //Return hexagon object
        if (!priorityQueue.isEmpty()) {
            return priorityQueue.peek();
        } else return null;
    }

    //Find the path Freddy will be taking, backtrack to marked tiles if Freddy meets a dead end.
    public String findPath() {
        ArrayStack<Hexagon> stack = new ArrayStack<>();
        Hexagon start = pond.getStart();
        stack.push(start);
        start.markInStack();

        int fliesEaten = 0;
        String string = "";

        while (!stack.isEmpty()) {
            Hexagon current = stack.peek();

            string += current.getID() + " ";
            if (current.isEnd()) {
                break;

            } if (current instanceof FoodHexagon ) {
                int numFlies = ((FoodHexagon) current).getNumFlies();
                fliesEaten += numFlies;
                ((FoodHexagon) current).removeFlies();
            } Hexagon next = findBest(current);
            //If Freddy is at no possible solution, backtrack
            if (next == null) {
                stack.pop();
                current.markOutStack();
            } else {
                stack.push(next);
                next.markInStack();
            }

        }if (stack.isEmpty()) {
            string = "No solution";
        } else {
            string += "ate " + fliesEaten + " flies";
        }
        return string;

    }


}

