/******************************************************************************
  *  Name:    Nick Barnett
  *  NetID:   nrbarnet
  *  Precept: P04
  *
  *  Partner Name:    N/A
  *  Partner NetID:   N/A
  *  Partner Precept: N/A
  * 
  *  Description: Implements a range() and nearest() method for a RedBlackBST
******************************************************************************/
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

public class PointST<Value> {
    
    private RedBlackBST<Point2D, Value> bst;
    
    // conbstruct an empty symbol table of points
    public PointST()                                 
    {
        this.bst = new RedBlackBST<Point2D, Value>();
    }
    
    // is the symbol table empty? 
    public boolean isEmpty()                      
    {
        return bst.isEmpty();
    }
    
    // number of points 
    public int size()                        
    {
        return bst.size();
    }
    
    // associate the value val with point p
    public void put(Point2D p, Value val)      
    {
        if (p == null || val == null) throw new NullPointerException();
        bst.put(p, val);
    }
    
    // value associated with point p 
    public Value get(Point2D p)                 
    {
        if (p == null) throw new NullPointerException();
        return bst.get(p);
    }
    
    // does the symbol table contain point p? 
    public boolean contains(Point2D p)           
    {
        if (p == null) throw new NullPointerException();
        return bst.contains(p);
    }
    
    // all points in the symbol table
    public Iterable<Point2D> points()                        
    {
        return bst.keys();
    }
    
    // all points that are inside the rectangle 
    public Iterable<Point2D> range(RectHV rect)             
    {
        if (rect == null) throw new NullPointerException();
        //create a queue with Key, Point2D, to hold all the points contained in
        //the argument rectangle, and that will be returned at the end of the 
        //method
        //iterate through  all points in bst by using points()
        //for each point, call rect.contains(p). If this condition is met, add 
        //that point to the queue
        Queue<Point2D> q = new Queue<Point2D>();
        for (Point2D p: points()) {
            if (rect.contains(p)) q.enqueue(p);
        }
        return q;
    }
    
    // a nearebst neighbor to point p; null if the symbol table is empty
    public Point2D nearest(Point2D p)             
    {
        if (isEmpty() || p == null) throw new NullPointerException();
        //iterate through  all points in bst by using points()
        //for each point, calculate its squared dibstance to the argument point
        //if this dibstance is the smallebst dibstance calculated thus far, set 
        //the value of minDibst to this new value, and set  minP equal to the 
        //point being evaluated
        //return minP
        Point2D minP = null; 
        double minDist = Double.POSITIVE_INFINITY;
        double dist;
        for (Point2D otherP: points()) {
            dist = p.distanceSquaredTo(otherP);
            if (dist < minDist) {
                minDist = dist;
                minP = otherP;
            }
        }
        return minP; 
    }
    
    // unit testing (not graded) 
    public static void main(String[] args)                  
    {
        PointST<Double> brute = new PointST<Double>();
        In in = new In(args[0]);
        double x, y;
        Point2D p;
        Double val = 0.0;
        while (!in.isEmpty()) {
            x = in.readDouble();
            y = in.readDouble();
            p = new Point2D(x, y);
            brute.put(p, val);
        }
        double randX = StdRandom.uniform();
        double randY = StdRandom.uniform();
        Point2D petey = new Point2D(randX, randY);
        Stopwatch stopwatch = new Stopwatch();
        System.out.println(brute.nearest(petey));
        double time = stopwatch.elapsedTime();
        System.out.println(time);
        
        //points() works
//        for (Point2D pt: brute.points()) {
//            System.out.println("(" + pt.x() + ", " + pt.y() + ")");
//        }
        
        //contains() works
//        Point2D p3 = new Point2D(.679523, .615082);
//        System.out.println(brute.contains(p3));
        
        //put() works
//        Point2D p4 = new Point2D(.5666, .5666);
//        brute.put(p4, val);
//        System.out.println(brute.contains(p4));
        
        //size() works and correctly returns the number of points in kd tree
//        System.out.println(kd.size());
        
        //nearest() works
//        Point2D p6 = new Point2D(.75, .75);
//        Point2D p5 = brute.nearest(p6);
//        System.out.println("(" + p5.x() + ", " + p5.y() + ")");
        
        //range() works
//        RectHV poop = new RectHV(.1,.1,.25,.25);
//        for (Point2D pps: brute.range(poop)) {
//            System.out.println("(" + pps.x() + ", " + pps.y() + ")");
//        }
    }
}