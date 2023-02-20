import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Astar {
    public static Node AstarAlg(long startID, long endID, RoadNetwork graph, Boolean debug) {
        PriQueue<Node, Double> frontier = new PriQueue<>();
        Map<Long, Node> reached = new HashMap<>();
        Location destination = graph.getLocationForId(endID);
        Location firstLocation = graph.getLocationForId(startID);
        double g = 0;
        double h = hfunction(firstLocation.latitude(), firstLocation.longitude(), destination.latitude(), destination.longitude());
        double f = g + h;
        int totNodesVisited = 0;
        Node start = new Node(startID, null, g, h, f);
        frontier.add(start, start.f());
        reached.put(start.locationID(), start);
        while (!frontier.isEmpty()) {
            Node node = frontier.remove();
            totNodesVisited+=1;
            if (debug)
                if (node.parent() == null)
                    System.out.println("\nVisiting [State=" + node.locationID() + ", parent=null, g=" + node.g() + ", h=" + node.h() + ", f=" + node.f() + " to frontier.");
                else
                    System.out.println("\nVisiting [State=" + node.locationID() + ", parent=" + node.parent().locationID() + ", g=" + node.g() + ", h=" + node.h() + ", f=" + node.f() + " to frontier.");

            if (node.locationID() == endID) {
                System.out.println("\nTotal travel time in seconds: " + node.f());
                System.out.println("Number of nodes visited: " + totNodesVisited);

                return node;
            }
            for (Node child : Expand(node, graph, destination)) {
                long childID = child.locationID();
                if (!reached.containsKey(childID) || child.f() < reached.get(childID).f()) {
                    reached.put(childID, child);
                    frontier.add(child, child.f());
                    if (debug)
                        System.out.println("  Adding [State=" + child.locationID() + ", parent=" + child.parent().locationID() + ", g=" + child.g() + ", h=" + child.h() + ", f=" + child.f() + " to frontier.");
                } else {
                    if (debug)
                        System.out.println("  Skipping [State=" + child.locationID() + ", parent=" + child.parent().locationID() + ", g=" + child.g() + ", h=" + child.h() + ", f=" + child.f() + "] (already on frontier with lower cost).");
                }
            }
        }


        return null;
    }

    public static ArrayList<Node> Expand(Node node, RoadNetwork graph, Location destination) {
        ArrayList<Node> childNodes = new ArrayList<>();
        Long nodeState = node.locationID();
        List<Road> roads = graph.getAdjacentRoads(nodeState);
        for (Road road : roads) {
            Location childLoc = graph.getLocationForId(road.endId());
            double child_gcost = node.g() + Geometry.getDriveTimeInSeconds(road, graph);
            double child_hcost = hfunction(childLoc.latitude(), childLoc.longitude(), destination.latitude(), destination.longitude());
            Node child = new Node(childLoc.id(), node, child_gcost, child_hcost, child_gcost + child_hcost);
            childNodes.add(child);
        }
        return childNodes;
    }

    public static double hfunction(double lat1, double long1, double lat2, double long2) {
        return (Geometry.getDistanceInMiles(lat1, long1, lat2, long2) / 65) * 3600;
    }
}
