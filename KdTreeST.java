/******************************************************************************
  *  Name:    Nick Barnett
  *  NetID:   nrbarnet
  *  Precept: P04
  *
  *  Partner Name:    N/A
  *  Partner NetID:   N/A
  *  Partner Precept: N/A
  * 
  *  Description: Uses a 2-D tree that implements classic BST methods, such as 
  *  put() and get(), as well as range() and nearest()
******************************************************************************/
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.algs4.StdRandom;

public class KdTreeST<Value> {
     
    private double xmin; //minimum x-coordinate of rectangle
    private double ymin; //minimum y-coordinate of rectangle
    private double xmax; //maximum x-coordinate of rectangle
    private double ymax; //maximum y-coordinate of rectangle
    private Node root;   //root of KdTree
    private int N;       //number of nodes in KdTree
    //public int operations;
    
    private class Node {
        private Point2D p;           // the point
        private Value val;           // the symbol table maps the point to this
        // value
        private RectHV rect;         // the axis-aligned rectangle 
        // corresponding to this node
        private Node left;           // the left/bottom subtree
        private Node right;          // the right/top subtree
        private boolean vertical;    // identifies whether the key is the 
        // x-coordinate of a point (1), or the
        //y-coordinate of a point (0)
        
        public Node(Point2D p, Value val, RectHV rect, Node left, Node right,
                    boolean vertical) {
            this.p = p;
            this.val = val;
            this.rect = rect;
            this.left = left;
            this.right = right;
            this.vertical = vertical;
        }
    }
    
    // construct an empty symbol table of points
    public KdTreeST()                                 
    {
        root = null;
        N = 0;
    }
    
    // is the symbol table empty? 
    public boolean isEmpty()                      
    {
        return N == 0;
    }
    
    // number of points 
    public int size()                        
    {
        return N;
    }
    
    // associate the value val with point p
    public void put(Point2D p, Value val)      
    {
        if (p == null || val == null) throw new NullPointerException();
        
        //restart the dimensions of the RectHV
        xmin = 0.0;
        ymin = 0.0;
        xmax = 1.0;
        ymax = 1.0; 
        // The key of root is always the x-coordinate of its point
        // (vertical == true)
        root = put(root, p, val, true);
    }
    
    private Node put(Node node, Point2D p, Value val, boolean vertical) {
        // if there isn't a node, add one
        if (node == null) {
            N++;
            RectHV rect = new RectHV(xmin, ymin, xmax, ymax);
            return new Node(p, val, rect, null, null, vertical);
        }
        
        // if the point is equal to the point at the node being inspected, 
        // overwrite the node's value and return the node
        if (p.equals(node.p)) {
            node.val = val;
            return node;
        }
        
        // If vertical is true for the node, then that means we should be using
        // the x-coordinate of its point as the key. Otherwise, we should be
        // using the y-coordinate of its point as the key
        double cmp;
        if (node.vertical) cmp = Double.compare(p.x(), node.p.x());
        else               cmp = Double.compare(p.y(), node.p.y());
        
        // if the point is less than the point of the node, go left
        // if the point is greater than or equal to the point of the node, go
        // right
        if      (cmp < 0) {
            // help to make the bounding rectangles for each point
            if (node.vertical) xmax = node.p.x();
            else               ymax = node.p.y();
            node.left = put(node.left, p, val, !node.vertical);
        }
        else if (cmp >= 0) {
            // help to make the bounding rectangles for each point
            if (node.vertical) xmin = node.p.x();
            else               ymin = node.p.y();
            node.right = put(node.right, p, val, !node.vertical);
        }
        
        //IDK why I need this
        return node; 
    }
    
    // value associated with point p 
    public Value get(Point2D p)                 
    {
        if (p == null) throw new NullPointerException();
        return get(root, p);
    }
    
    private Value get(Node node, Point2D p) {
        // Is the node we're looking at null? Either the tree is empty, or we 
        // have reached the end of a branch. Either way, the point-value pair 
        // did not exist in the tree 
        if (node == null) return null;
        
        // We have found the point we have been looking for. Return the point's
        // corresponding value
        if (p.equals(node.p)) return node.val;
        
        double cmp;
        if (node.vertical) cmp = Double.compare(p.x(), node.p.x());
        else               cmp = Double.compare(p.y(), node.p.y());
        
        if      (cmp < 0) return get(node.left, p);
        else if (cmp >= 0) return get(node.right, p);
        
        //Again, why do I need this?
        return node.val; 
    }
    
    
    // does the symbol table contain point p? 
    public boolean contains(Point2D p)           
    {
        if (p == null) throw new NullPointerException();
        return get(p) != null;
    }
    
    // all points in the symbol table
    // level-order traversal
    public Iterable<Point2D> points()                        
    {
        Queue<Node> q = new Queue<Node>();
        Queue<Point2D> q2 = new Queue<Point2D>();
        q.enqueue(root);
        while (!q.isEmpty())
        {
            Node x = q.dequeue();
            if (x == null) continue;
            q2.enqueue(x.p);
            q.enqueue(x.left);
            q.enqueue(x.right);
        }
        return q2;
    }
    
    // all points that are inside the rectangle 
    // To find all points contained in a given query rectangle, start at the 
    //root and recursively search for
    // points in both subtrees using the following pruning rule:
    // if the query rectangle does not intersect the rectangle corresponding to 
    //a node,
    // there is no need to explore that node (or its subtrees).
    // A subtree is searched only if it might contain a point contained in the 
    //query rectangle.
    public Iterable<Point2D> range(RectHV queryRect)             
    {
        if (queryRect == null) throw new NullPointerException();
        Queue<Point2D> pointsInQueryRect = new Queue<Point2D>();
        pruner(root, queryRect, pointsInQueryRect);
        return pointsInQueryRect;
    }
    
    private void pruner(Node node, RectHV queryRect, Queue<Point2D> q) {
        if (node == null) return;
        
        if (queryRect.intersects(node.rect)) {
            if (queryRect.contains(node.p)) q.enqueue(node.p);
            pruner(node.left, queryRect, q);
            pruner(node.right, queryRect, q);
        }
    }
    
    // a nearest neighbor to point p; null if the symbol table is empty
    public Point2D nearest(Point2D p)             
    {
        if (isEmpty() || p == null) throw new NullPointerException();
        //operations++;
        return nearest(root, p, root.p, true);
    }
    
    private Point2D nearest(Node node, Point2D p, Point2D n, boolean vertical) {
        //avoid duplicate variable names
        Point2D nearest = n;
        
        if (node == null) return nearest;
        
        if (node.p.distanceSquaredTo(p) < nearest.distanceSquaredTo(p))
            nearest = node.p;
        
        // If the nearest point discovered so far is closer than the 
        // distance between the query point and the rectangle corresponding 
        // to a node, there is no need to explore that node, and its 
        // subtrees (a.k.a. do nothing).
        // The converse of this is that if the rectangle corresponding to 
        // a node is closer to the query point than the nearest point 
        // discovered, then I need to explore that node, and its subtrees
        if (node.rect.distanceSquaredTo(p) < nearest.distanceSquaredTo(p)) {
            
            double cmp;
            if (node.vertical) cmp = Double.compare(p.x(), node.p.x());
            else               cmp = Double.compare(p.y(), node.p.y());
            
            if (cmp < 0) {
                // When there are two possible subtrees to go down, search the
                // subtree that is on the same side of the splitting line as 
                // the query point first
                // By key, the query point is less than the node point, so
                // explore the left subtree first
                //operations++;
                nearest = nearest(node.left, p, nearest, vertical);
                //operations++;
                nearest = nearest(node.right, p, nearest, vertical);
            }
            else if (cmp >= 0) {
                // When there are two possible subtrees to go down, search the
                // subtree that is on the same side of the splitting line as 
                // the query point first
                // By key, the query point is greater than or equal to the node
                // point, so explore the left subtree first
                //operations++;
                nearest = nearest(node.right, p, nearest, vertical);
                //operations++;
                nearest = nearest(node.left, p, nearest, vertical);
            }
        }
        return nearest;
    }
    
// unit testing (not graded) 
    public static void main(String[] args)                  
    {
        KdTreeST<Double> kd = new KdTreeST<Double>();
        In in = new In(args[0]);
        double x, y;
        Point2D p;
        Double val = 0.0;
        while (!in.isEmpty()) {
            x = in.readDouble();
            y = in.readDouble();
            p = new Point2D(x, y);
            kd.put(p, val);
        }
        double randX = StdRandom.uniform();
        double randY = StdRandom.uniform();
        Point2D petey = new Point2D(randX, randY);
        Stopwatch stopwatch = new Stopwatch();
        System.out.println(kd.nearest(petey));
        double time = stopwatch.elapsedTime();
        System.out.println(time);
        System.out.printf("%.10f", time);
        System.out.println();
        //System.out.println("operations: " + kd.operations);
        
        //points() works
//        for (Point2D pt: kd.points()) {
//            System.out.println("(" + pt.x() + ", " + pt.y() + ")");
//        }
        
        //contains() works
//        Point2D p3 = new Point2D(.679523, .615082);
//        System.out.println(kd.contains(p3));
        
        //put() works
//        Point2D p4 = new Point2D(.5666, .5666);
//        kd.put(p4, val);
//        System.out.println(kd.contains(p4));
        
        //size() works and correctly returns the number of points in kd tree
//        System.out.println(kd.size());
        
        //nearest works
//        Point2D p6 = new Point2D(.75, .75);
//        Point2D p5 = kd.nearest(p6);
//        System.out.println("(" + p5.x() + ", " + p5.y() + ")");
        
//        range() works
//        RectHV poop = new RectHV(.1,.1,.25,.25);
//        for (Point2D pps: kd.range(poop)) {
//            System.out.println("(" + pps.x() + ", " + pps.y() + ")");
//        }
        
        //Testing to make sure RectHVs were set correctly
//        Point2D a = new Point2D(.2, .3);
//        Point2D b = new Point2D(.4, .2);
//        Point2D c = new Point2D(.4, .5);
//        Point2D d = new Point2D(.3, .3);
//        Point2D e = new Point2D(.1, .5);
//        Point2D f = new Point2D(.4, .4);
//        kd.put(a, val);
//        kd.put(b, val);
//        kd.put(c, val);
//        kd.put(d, val);
//        kd.put(e, val);
//        kd.put(f, val);
//        System.out.println(kd.root.rect.toString());
//        System.out.println(kd.root.vertical);
//        System.out.println(kd.root.left.rect.toString());
//        System.out.println(kd.root.left.vertical);
//        System.out.println(kd.root.right.rect.toString());
//        System.out.println(kd.root.right.vertical);  
    }
}