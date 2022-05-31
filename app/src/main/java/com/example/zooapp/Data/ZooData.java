package com.example.zooapp.Data;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.zooapp.Ultility.IdentifiedWeightedEdge;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.nio.json.JSONImporter;

/**
 * This class loads in data about the zoo graph from JSON files
 */
public class ZooData {

    /**
     * This class loads in data about the zoo exhibits from JSON files
     */
    public static class VertexInfo {

        /**
         * This method sets up how to convert from JSON strings into type Enum
         */
        public static enum Kind {
            // The SerializedName annotation tells GSON how to convert
            // from the strings in our JSON to this Enum.
            @SerializedName("gate") GATE,
            @SerializedName("exhibit") EXHIBIT,
            @SerializedName("intersection") INTERSECTION
        }

        //Public fields
        public String id;
        public Kind kind;
        public String name;
        public List<String> tags;
    }

    /**
     * This class handles storing information about each edge in the zoo graph
     */
    public static class EdgeInfo {
        public String id;
        public String street;
    }

    /**
     * Loads information about each vertex in the graph from a JSON file
     *
     * @param context
     * @param path the path to the information file
     *
     * @return Map of edges and their information
     */
    public static Map<String, ZooData.VertexInfo> loadVertexInfoJSON(Context context, String path) {

        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Reader reader = new InputStreamReader(inputStream);

        Gson gson = new Gson();
        Type type = new TypeToken<List<ZooData.VertexInfo>>(){}.getType();
        List<ZooData.VertexInfo> zooData = gson.fromJson(reader, type);

        // This code is equivalent to:
        //
        // Map<String, ZooData.VertexInfo> indexedZooData = new HashMap();
        // for (ZooData.VertexInfo datum : zooData) {
        //   indexedZooData[datum.id] = datum;
        // }
        //
        Map<String, ZooData.VertexInfo> indexedZooData = zooData
                .stream()
                .collect(Collectors.toMap(v -> v.id, datum -> datum));

        return indexedZooData;
    }

    /**
     * Loads information about each edge in the graph from a JSON file
     *
     * @param context
     * @param path the path to the information file
     *
     * @return Map of edges and their information
     */
    public static Map<String, ZooData.EdgeInfo> loadEdgeInfoJSON(Context context, String path) {
        //InputStream inputStream = App.class.getClassLoader().getResourceAsStream(path);
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Reader reader = new InputStreamReader(inputStream);

        Gson gson = new Gson();
        Type type = new TypeToken<List<ZooData.EdgeInfo>>(){}.getType();
        List<ZooData.EdgeInfo> zooData = gson.fromJson(reader, type);

        Map<String, ZooData.EdgeInfo> indexedZooData = zooData
                .stream()
                .collect(Collectors.toMap(v -> v.id, datum -> datum));

        return indexedZooData;
    }

    /**
     * Loads the zoo graph from a JSON file
     *
     * @param context
     * @param path the path to the information file
     *
     * @return Graph
     */
    public static Graph<String, IdentifiedWeightedEdge> loadZooGraphJSON(Context context, String path) {
        // Create an empty graph to populate.
        Graph<String, IdentifiedWeightedEdge> g = new DefaultUndirectedWeightedGraph<>(IdentifiedWeightedEdge.class);

        // Create an importer that can be used to populate our empty graph.
        JSONImporter<String, IdentifiedWeightedEdge> importer = new JSONImporter<>();

        // We don't need to convert the vertices in the graph, so we return them as is.
        importer.setVertexFactory(v -> v);

        // We need to make sure we set the IDs on our edges from the 'id' attribute.
        // While this is automatic for vertices, it isn't for edges. We keep the
        // definition of this in the IdentifiedWeightedEdge class for convenience.
        importer.addEdgeAttributeConsumer(IdentifiedWeightedEdge::attributeConsumer);

        // On Android, you would use context.getAssets().open(path) here like in Lab 5.
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        //App.class.getClassLoader().getResourceAsStream(path);
        Reader reader = new InputStreamReader(inputStream);

        // And now we just import it!
        importer.importGraph(g, reader);

        return g;
    }
}
