package visualizer;

import java.util.*;

public class GraphAlgorithms {

    private GraphAlgorithms() {
        throw new IllegalStateException("Utility class");
    }

    public static List<String> depthFirstSearch(Vertex startVertex, List<Vertex> vertexList, List<Edge> edgeList) {
        System.out.println("DFS started with: " + startVertex.getVertexId());

        Map<Vertex, List<Edge>> map = (buildAdjacencyMap(vertexList, edgeList));//vertex-uri si listele de muchii al
        //fiecărui vertex.

        Stack<Vertex> orderVisitedVertices = new Stack<>();//Stack(lifo) - pentru adâncime.
        orderVisitedVertices.push(startVertex);

        Set<Vertex> listVisitedVertices = new HashSet<>();//Set - ajuta sa prevenim duplicatele.
        listVisitedVertices.add(startVertex);

        List<String> resultList = new ArrayList<>();//adaugăm vertex-uri in lista când sunt scoase din Stack.

        while (!orderVisitedVertices.isEmpty()) {

            Vertex currentVertex = orderVisitedVertices.pop();
            resultList.add(currentVertex.getVertexId());//adaugăm in lista cu rezultatul final.
            List<Edge> edges = map.get(currentVertex);//cream lista de muchii care corespunde unui anumit vertex.
            Collections.reverse(edges);

            for (Edge edge : edges) {
                Vertex v = edge.toVertex;//găsim vertexul spre care merge muchia, vertex nevizitat

                if (!listVisitedVertices.contains(v)) {//verificam lista, ca sa nu adaugăm unul si același vertex.
                    orderVisitedVertices.push(v);//adaug un nou vecin (vertex) in Stack.
                    listVisitedVertices.add(v);//adaugăm in lista vertex-urile vizitate, in Set.
                }
            }
        }
        System.out.println("DFS result: " + resultList);
        return resultList;
    }

    public static List<String> breadthFirstSearch(Vertex startVertex, List<Vertex> vertexList, List<Edge> edgeList) {
        System.out.println("BFS started with: " + startVertex.getVertexId());

        Map<Vertex, List<Edge>> map = buildAdjacencyMap(vertexList, edgeList);//vertex-uri si listele de muchii
        // ale fiecărui vertex.

        Queue<Vertex> orderVisitedVertices = new LinkedList<>();//Queue(fifo) - pentru lățime.
        orderVisitedVertices.add(startVertex);

        Set<Vertex> listVisitedVertices = new HashSet<>();//Set - ajuta sa prevenim duplicatele.
        listVisitedVertices.add(startVertex);

        List<String> resultList = new ArrayList<>();//adaugăm vertex-uri când sunt scoase din Queue.

        while (!orderVisitedVertices.isEmpty()) {

            Vertex currentVertex = orderVisitedVertices.poll();
            resultList.add(currentVertex.getVertexId());
            List<Edge> edges = map.get(currentVertex);//cream lista de muchii care corespund unui anumit vertex.

            for (Edge edge : edges) {
                Vertex v = edge.toVertex;//găsim vertex-ul spre care merge muchia, vertex nevizitat
                if (!listVisitedVertices.contains(v)) {//verificam lista ca sa nu apară unul si același vertex
                    orderVisitedVertices.add(v);//adaugăm un nou vecin (vertex) in Queue.
                    listVisitedVertices.add(v);//adaugăm in lista vertex-urile vizitate, in Set.
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

    private static Map<Vertex, Integer> initializeDistances(List<Vertex> vertexList, Vertex startVertex) {
        final int INFINITY_DISTANCE = Integer.MAX_VALUE;

        Map<Vertex, Integer> distances = new HashMap<>();

        for (Vertex vertex : vertexList) {
            distances.put(vertex, INFINITY_DISTANCE);
        }
        distances.put(startVertex, 0);
        return distances;
    }

    private static void processVertex(VertexDistance currentVertexDistance, Set<Vertex> unprocessedVertices,
                                      Map<Vertex, Integer> distances, Map<Vertex, List<Edge>> adjacencyMap,
                                      PriorityQueue<VertexDistance> priorityQueue) {

        Vertex currentVertex = currentVertexDistance.vertex; //extract vertex

        if (unprocessedVertices.contains(currentVertex)) {
            unprocessedVertices.remove(currentVertex);

            List<Edge> neighbors = adjacencyMap.get(currentVertex); //get the edges adjacent to the current vertex

            for (Edge edge : neighbors) {
                Vertex neighbour = edge.toVertex;
                if (unprocessedVertices.contains(neighbour)) {
                    int newDistance = distances.get(currentVertex) + edge.getEdgeWeight();

                    if (newDistance < distances.get(neighbour)) {
                        distances.put(neighbour, newDistance);
                        priorityQueue.add(new VertexDistance(neighbour, newDistance));
                    }
                }
            }
        }
    }

    public static Map<Vertex, Integer> dijkstraAlgorithm(Vertex startVertex, List<Vertex> vertexList, List<Edge> edges) {

        Map<Vertex, Integer> distances = initializeDistances(vertexList, startVertex);
        Set<Vertex> unprocessedVertices = new HashSet<>(vertexList);
        PriorityQueue<VertexDistance> distancePriorityQueue = new PriorityQueue<>();
        Map<Vertex, List<Edge>> adjacencyMap = buildAdjacencyMap(vertexList, edges);

        distancePriorityQueue.add(new VertexDistance(startVertex, 0));

        while (!unprocessedVertices.isEmpty()) {

            VertexDistance currentVertexDistance = distancePriorityQueue.poll(); //remove the nearest vertex

            if (currentVertexDistance != null) { //if queue is empty
                processVertex(currentVertexDistance,
                        unprocessedVertices,
                        distances,
                        adjacencyMap,
                        distancePriorityQueue);
            }
        }
        return distances;
    }

    private static void processVertexPrim(VertexDistance currentVertexDistance,
                                          Set<Vertex> unprocessedVertices,
                                          Map<Vertex, Integer> distances,
                                          Map<Vertex, List<Edge>> adjacencyMap,
                                          PriorityQueue<VertexDistance> priorityQueue,
                                          Map<Vertex, Vertex> parents/**/) {

        Vertex currentVertex = currentVertexDistance.vertex; //extract vertex

        if (unprocessedVertices.contains(currentVertex)) {
            unprocessedVertices.remove(currentVertex);

            List<Edge> neighbors = adjacencyMap.get(currentVertex); //get the edges adjacent to the current vertex

            for (Edge edge : neighbors) {
                Vertex neighbour = edge.toVertex;
                if (unprocessedVertices.contains(neighbour)) {
                    int newDistance = edge.getEdgeWeight();//

                    if (newDistance < distances.get(neighbour)) {
                        distances.put(neighbour, newDistance);
                        priorityQueue.add(new VertexDistance(neighbour, newDistance));
                        parents.put(neighbour, currentVertex);
                    }
                }
            }
        }
    }

    public static Map<String, String> primAlgorithm(Vertex startVertex,
                                                    List<Vertex> vertexList,
                                                    List<Edge> edges) {

        Map<Vertex, Integer> distances = initializeDistances(vertexList, startVertex);
        Map<Vertex, Vertex> parents = new HashMap<>();

        Set<Vertex> unprocessedVertices = new HashSet<>(vertexList);
        PriorityQueue<VertexDistance> distancePriorityQueue = new PriorityQueue<>();
        Map<Vertex, List<Edge>> adjacencyMap = buildAdjacencyMap(vertexList, edges);

        distancePriorityQueue.add(new VertexDistance(startVertex, 0));

        while (!unprocessedVertices.isEmpty()) {

            VertexDistance currentVertexDistance = distancePriorityQueue.poll(); //remove the nearest vertex

            if (currentVertexDistance != null) { //if queue is empty
                processVertexPrim(currentVertexDistance,
                        unprocessedVertices,
                        distances,
                        adjacencyMap,
                        distancePriorityQueue,
                        parents);
            }
        }

        Map<String, String> result = new HashMap<>();

        for (Map.Entry<Vertex, Vertex> map : parents.entrySet()){
            result.put(map.getKey().getVertexId(), map.getValue().getVertexId());
        }

        return result;
    }

    private static class VertexDistance implements Comparable<VertexDistance> {
        Vertex vertex;
        int distance;

        public VertexDistance(Vertex vertex, int vertexDistance) {
            this.vertex = vertex;
            this.distance = vertexDistance;
        }

        @Override
        public int compareTo(VertexDistance other) {

            return this.distance - other.distance;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            VertexDistance that = (VertexDistance) o;
            return distance == that.distance && Objects.equals(vertex, that.vertex);
        }

        @Override
        public int hashCode() {
            return Objects.hash(vertex, distance);
        }
    }
}