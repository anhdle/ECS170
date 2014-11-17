
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Stack;
import java.util.HashMap;

/// A* Division
/**
 * @author Casey Wilson, Anh Le
 */

public class AStarDiv_997995396_998513706 implements AIModule
{
    /// Creates the path to the goal.
    public List<Point> createPath(final TerrainMap map) {
        
        // Holds the resulting closed
        final HashMap<Point, Pair> closed = new HashMap<Point, Pair>();
        final HashMap<Point, Pair> openH = new HashMap<Point, Pair>();
        
        // Holds Open list
        Comparator<Pair> comparator = new PairComparator();
        final PriorityQueue<Pair> open = new PriorityQueue<Pair>(1, comparator);
        
        // add start point
        open.add(new Pair( map.getStartPoint(), null, 0.0, getHeuristic(map, map.getStartPoint(), map.getEndPoint()) ));
        openH.put(map.getStartPoint(), new Pair( map.getStartPoint(), null, 0.0, getHeuristic(map, map.getStartPoint(), map.getEndPoint()) ));
        
        while( !open.isEmpty() ) { // while frontier is not empty
            Pair u = open.poll();  // select best node for expansion
            closed.put(u.point, u);
            
            if( u.point.equals(map.getEndPoint() )) // if we've hit the goal node, return path
                return retracePath(u);
                  
            Point[] neighbors = map.getNeighbors(u.point);
            for(Point v : neighbors) {
                
                // Improve
                Pair vp = openH.get(v);
                Pair vc = closed.get(v);
                if( vp != null ){ // if v in open
                    if( u.gValue + map.getCost(u.point, v) < vp.gValue ) { // and the new path is better
                        open.remove(vp);
                        openH.remove(vp.point);
                        open.add( new Pair( v, u, u.gValue + map.getCost(u.point, v), getHeuristic(map, v, map.getEndPoint()) ));
                        openH.put( v, new Pair( v, u, u.gValue + map.getCost(u.point, v), getHeuristic(map, v, map.getEndPoint()) ));
                    }
                } else if( vc != null ) { // if v in closed
                    if( u.gValue + map.getCost(u.point, v) < vc.gValue ) { // and the new path is better
                        closed.remove(v);
                        open.add( new Pair( v, u, u.gValue + map.getCost(u.point, v), getHeuristic(map, v, map.getEndPoint()) ));
                        openH.put( v, new Pair( v, u, u.gValue + map.getCost(u.point, v), getHeuristic(map, v, map.getEndPoint()) ));
                    }
                } else { // if is unexplored
                    open.add( new Pair( v, u, u.gValue + map.getCost(u.point, v), getHeuristic(map, v, map.getEndPoint()) ));
                    openH.put( v, new Pair( v, u, u.gValue + map.getCost(u.point, v), getHeuristic(map, v, map.getEndPoint()) ));
                }
            }
        }
        
        return null;
    }
    
    private double getHeuristic(final TerrainMap map, final Point pt1, final Point pt2) {
        
        int xDiff = Math.abs(pt2.x - pt1.x);
        int yDiff = Math.abs(pt2.y - pt1.y);
        double c = 0.50;
        
        if( xDiff < yDiff ) { return yDiff*c; } else { return xDiff*c; }
        
    }
    
    private ArrayList<Point> retracePath( Pair endPoint ) {
        ArrayList<Point> path = new ArrayList<Point>();
        Stack<Point> stack = new Stack<Point>();
        
        Pair p = endPoint;
        while( p.parent != null ) {
            stack.push(p.point);
            p = p.parent;
        }
        stack.push(p.point);
        
        while( !stack.empty() )
            path.add( stack.pop() );
        
        return path;
    } 
    
    public class Pair{
        private Point point;
        private Pair parent;
        private double gValue;
        private double hValue;
        private double fValue;

        public Pair(Point p, Pair pt, double g, double h) {
            point = p;
            parent = pt;
            gValue = g;
            hValue = h;
            fValue = g+h;
        }
        
        public String toString()
        { 
            return "(" + point.x + ", " + point.y + ") " + "f: " + fValue;
        }
    }
    
    public class PairComparator implements Comparator<Pair> {
        
        public int compare(Pair pt1, Pair pt2) {
            
            double f1 = pt1.fValue;
            double f2 = pt2.fValue;

            if (f1 > f2 ){
                return 1;
            }
            else if (f1 < f2){
                return -1;
            }
            else
                return 0;
        }
    }
    
}
