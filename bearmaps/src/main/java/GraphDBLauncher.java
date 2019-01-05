import java.util.List;
import java.util.ArrayList;

/**
 * This class provides a main method for experimenting with GraphDB construction.
 * You could also use MapServer, but this class lets you play around with
 * GraphDB in isolation from all the rest of the parts of this assignment.
 */
public class GraphDBLauncher {
    private static final String OSM_DB_PATH = "../library-su18/bearmaps/tiny-clean.osm.xml";

    public static void main(String[] args) {
        GraphDB g = new GraphDB(OSM_DB_PATH);
        List<Long> vertices = new ArrayList<>();
        for (long v : g.vertices()) {
            vertices.add(v);
        }

        System.out.println("There are " + vertices.size() + " vertices in the graph.");


        for (Long id: vertices) {
            System.out.println(id);
            System.out.print(GraphDB.projectToX(g.lon(id), g.lat(id)));
            System.out.println(GraphDB.projectToY(g.lon(id), g.lat(id)));
        }

        long v = g.closest(-122.258207, 37.875352);
        System.out.print("The vertex number closest to -122.258207, 37.875352 is " + v + ", which");
        System.out.println(" has longitude, latitude of: " + g.lon(v) + ", " + g.lat(v));

        System.out.println("To get started, uncomment print statements in GraphBuildingHandler.");
    }
}
