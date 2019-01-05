import java.util.Comparator;
import java.util.List;

import static java.lang.Math.abs;

public class KdTree {
    private Node root;

    public KdTree(List<Point> pointList) {
        if (pointList.isEmpty()) {
            root = null;
        } else {
            root = creatnode(pointList, 0);
        }

    }

    public Node getroot() {
        return root;
    }

    public static class Node {
        private Node left;
        private Node right;
        private int axis;
        private Point item;

        public Node(Point item, int axis) {
            this.item = item;
            this.axis = axis;
        }

        public Point getitem() {
            return item;
        }
    }

    private static Node creatnode(List<Point> pointList, int depth) {
        //axis 0 = x
        //axis 1 = y
        if (pointList.isEmpty()) {
            return null;
        }
        int axis = depth % 2;

        if (axis == 0) {
            pointList.sort(Comparator.comparingDouble(x ->
                    x.x));
        } else if (axis == 1) {
            pointList.sort(Comparator.comparingDouble(x ->
                    x.y));
        }

        //choose pivot as median
        Point mid = pointList.get(pointList.size() / 2);
        Node n = new Node(mid, axis);

        List<Point> beforemid = pointList.subList(0, pointList.size() / 2);
        List<Point> aftermid = pointList.subList(pointList.size() / 2 + 1, pointList.size());

        //construct subtree
        n.left = creatnode(beforemid, depth + 1);
        n.right = creatnode(aftermid, depth + 1);
        return n;

    }


    private static double dist(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static Point closest(double x, double y, Point best, Node n) {
        if (n == null) {
            return best;
        } else {
            double ndist = dist(x, y, n.item.x, n.item.y);
            double bestdist = dist(x, y, best.x, best.y);
            if (ndist < bestdist) {
                best = n.item;
            }
            //d = projectX A - x
            double d;
            if (n.axis == 0) {
                d = n.item.x - x;
            } else {
                d = n.item.y - y;
            }
            //query on the left/bottom
            if (d > 0) {
                best = closest(x, y, best, n.left);
                if (bestdist > abs(d)) {
                    best = closest(x, y, best, n.right);
                    return best;
                } else {
                    return best;
                }
                //query on the right/top
            } else {
                best = closest(x, y, best, n.right);
                if (bestdist > abs(d)) {
                    best = closest(x, y, best, n.left);
                    return best;
                } else {
                    return best;
                }
            }
        }
    }



}
