import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Project1 {
    public static void main(String[] args) {
        RoadNetwork graph = readGraph("memphis-medium.txt");
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter a starting location ID: ");
        long lStart = Long.parseLong(scan.nextLine());
        System.out.print("Enter a destination ID: ");
        long lEnd = Long.parseLong(scan.nextLine());
        System.out.print("Do you want debugging information (y/n): ");
        String debug = scan.nextLine();
        boolean debugStatus = false;
        if (debug.equals("y")) {
            debugStatus = true;
        }
        Node destination = Astar.AstarAlg(lStart, lEnd, graph, debugStatus);
        Node currentNode = destination;
        System.out.println("Route found is:");
        ArrayList<Long> path = new ArrayList<>();

        while (currentNode != null) {
            path.add(currentNode.locationID());
            currentNode = currentNode.parent();
        }

        System.out.println(path.get(path.size() - 1) + " (starting location)");
        for (int j = path.size() - 1; j > 0; j--) {
            List<Road> adjacentRoads = graph.getAdjacentRoads(path.get(j));
            for (Road road : adjacentRoads) {
                if (road.endId() == path.get(j - 1)) {
                    System.out.println(path.get(j - 1) + " (" + road.name() + ")");
                }
            }
        }

    }

    public static RoadNetwork readGraph(String filename) {
        InputStream is = Project1.class.getResourceAsStream(filename);
        if (is == null) {
            System.err.println("Bad filename: " + filename);
            System.exit(1);
        }
        Scanner scan = new Scanner(is);

        RoadNetwork graph = new RoadNetwork();

        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            String[] pieces = line.split("\\|");

            if (pieces[0].equals("location")) {
                long id = Long.parseLong(pieces[1]);
                double lat = Double.parseDouble(pieces[2]);
                double longi = Double.parseDouble(pieces[3]);
                Location loc = new Location(id, lat, longi);
                graph.addLocation(loc);
            } else if (pieces[0].equals("road")) {
                long startId = Long.parseLong(pieces[1]);
                long endId = Long.parseLong(pieces[2]);
                int speed = Integer.parseInt(pieces[3]);
                String name = pieces[4];
                Road r1 = new Road(startId, endId, speed, name);
                Road r2 = new Road(endId, startId, speed, name);
                graph.addRoad(r1);
                graph.addRoad(r2);
            }
        }
        scan.close();

        return graph;
    }
}
