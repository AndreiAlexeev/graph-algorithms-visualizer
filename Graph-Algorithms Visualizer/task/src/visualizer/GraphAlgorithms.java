package visualizer;

import java.util.*;

public class GraphAlgorithms {

    private GraphAlgorithms() {
        throw new IllegalStateException("Utility class");
    }

    public static List<String> depthFirstSearch(Vertex startVertex,
                                                List<Vertex> vertexList,
                                                List<Edge> edgeList) {
        System.out.println("DFS started with: " + startVertex.getVertexId());

        Map<Vertex, List<Edge>> map = (buildAdjacencyMap(vertexList, edgeList));//vertex-uri si listele de muchii al
        //fiecÄƒrui vertex.

        Stack<Vertex> orderVisitedVertices = new Stack<>();//Stack(lifo) - pentru adÃ¢ncime.
        orderVisitedVertices.push(startVertex);

        Set<Vertex> listVisitedVertices = new HashSet<>();//Set - ajuta sa prevenim duplicatele.
        listVisitedVertices.add(startVertex);

        List<String> resultList = new ArrayList<>();//adaugÄƒm vertex-uri in lista cind sunt scoase din Stack.

        while (!orderVisitedVertices.isEmpty()) {

            Vertex currentVertex = orderVisitedVertices.pop();
            resultList.add(currentVertex.getVertexId());//adaugÄƒm in lista cu rezultatul final.
            List<Edge> edges = map.get(currentVertex);//cream lista de muchii care corespunde unui anumit vertex.
            Collections.reverse(edges);

            for (Edge edge : edges) {
                Vertex v = edge.toVertex;//gÄƒsim vertexul spre care merge muchia, vertex nevizitat

                if (!listVisitedVertices.contains(v)) {//verificam lista, ca sa nu adaugÄƒm unul si acelaÈ™i vertex.
                    orderVisitedVertices.push(v);//adaug un nou vecin (vertex) in lista.
                    listVisitedVertices.add(v);//adaugÄƒm in lista vertex-urile vizitate.
                }
            }
        }
        System.out.println("DFS result: " + resultList);
        return resultList;
    }

    public static List<String> breadthFirstSearch(Vertex startVertex,
                                                  List<Vertex> vertexList,
                                                  List<Edge> edgeList) {
        System.out.println("BFS started with: " + startVertex.getVertexId());

        Map<Vertex, List<Edge>> map = buildAdjacencyMap(vertexList, edgeList);

        Queue<Vertex> orderVisitedVertices = new LinkedList<>();
        orderVisitedVertices.add(startVertex);

        Set<Vertex> listVisitedVertices = new HashSet<>();
        listVisitedVertices.add(startVertex);

        List<String> resultList = new ArrayList<>();

        while (!orderVisitedVertices.isEmpty()) {

            Vertex currentVertex = orderVisitedVertices.poll();
            resultList.add(currentVertex.getVertexId());
            List<Edge> edges = map.get(currentVertex);
            for (Edge edge : edges) {
                Vertex v = edge.toVertex;
                if (!listVisitedVertices.contains(v)) {
                    orderVisitedVertices.add(v);
                    listVisitedVertices.add(v);
                }
            }
        }

        return resultList;
    }

    public static Map<Vertex, List<Edge>> buildAdjacencyMap(List<Vertex> vertexList, List<Edge> edgeList) {

        Map<Vertex, List<Edge>> map = new HashMap<>();

        //initialize empty list for each vertex
        for (Vertex vertex : vertexList) {
            map.put(vertex, new ArrayList<>());
        }

        //add edges in the list
        for (Edge edge : edgeList) {
            Vertex fromVertex = edge.fromVertex;
            List<Edge> listNeighbor = map.get(fromVertex);
            listNeighbor.add(edge);
        }

        //print the list once
        for (Vertex vertex : vertexList) {
            Collections.sort(map.get(vertex), new Comparator<Edge>() {
                @Override
                public int compare(Edge o1, Edge o2) {
                    return Integer.compare(o1.getEdgeWeight(), o2.getEdgeWeight());
                }
            });
        }
        return map;
    }

    public static Map<Vertex, String> dijkstraAlgorithm(Vertex startVertex, List<Vertex> vertexList, List<Edge> edges) {

        Map<Vertex, List<Edge>> map = buildAdjacencyMap(vertexList, edges);


        PriorityQueue<Vertex> distancePriorityQueue = new PriorityQueue<>();
        return null;
    }

    private static class NodeDistance implements Comparable<NodeDistance> {
        Vertex vertex;
        int vertexDistance;

        public NodeDistance(Vertex vertexId, int vertexDistance) {
            this.vertex = vertexId;
            this.vertexDistance = vertexDistance;
        }

        @Override
        public int compareTo(NodeDistance other) {

            return this.vertexDistance - other.vertexDistance;
        }
    }
}