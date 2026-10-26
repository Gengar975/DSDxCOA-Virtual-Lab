package lab.content;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** All lessons of the application, in sidebar order. */
public final class Catalog {

    private static List<Topic> all;

    private Catalog() { }

    public static synchronized List<Topic> all() {
        if (all == null) {
            List<Topic> l = new ArrayList<>();
            l.addAll(BasicsContent.topics());
            l.addAll(CircuitsContent.topics());
            l.addAll(ComputerOrgContent.topics());
            all = List.copyOf(l);
        }
        return all;
    }

    public static Topic byId(String id) {
        for (Topic t : all()) if (t.getId().equals(id)) return t;
        throw new IllegalArgumentException("Unknown topic: " + id);
    }

    /** Topics grouped for the sidebar, in the order the groups are declared. */
    public static Map<Topic.Group, List<Topic>> byGroup() {
        Map<Topic.Group, List<Topic>> m = new LinkedHashMap<>();
        for (Topic.Group g : Topic.Group.values()) m.put(g, new ArrayList<>());
        for (Topic t : all()) m.get(t.getGroup()).add(t);
        return m;
    }
}
