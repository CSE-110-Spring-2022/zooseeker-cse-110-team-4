package com.example.zooapp;

import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.nio.Attribute;

/**
 * Exactly like a DefaultWeightedEdge, but has an id field we
 * can use to look up the information about the edge with.
 */
public class IdentifiedWeightedEdge extends DefaultWeightedEdge {
    // Private fields
    private String id = null;

    /**
     * Returns the id of the edge
     *
     * @return Id of the edge
     */
    public String getId() { return id; }

    /**
     * Sets the id of the edge
     *
     * @param id Id to set for the edge
     */
    public void setId(String id) { this.id = id; }

    /**
     * String for the edge
     *
     * @return String for displaying the edge
     */
    @Override
    public String toString() {
        return "(" + getSource() + " :" + id + ": " + getTarget() + ")";
    }

    public static void attributeConsumer(Pair<IdentifiedWeightedEdge, String> pair,
                                         Attribute attr) {
        IdentifiedWeightedEdge edge = pair.getFirst();
        String attrName = pair.getSecond();
        String attrValue = attr.getValue();

        if (attrName.equals("id")) {
            edge.setId(attrValue);
        }
    }
}
