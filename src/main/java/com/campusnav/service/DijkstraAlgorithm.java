package com.campusnav.service;

import com.campusnav.entity.CampusLocation;
import com.campusnav.entity.RouteConnection;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * High-performance implementation of Dijkstra's Shortest Path algorithm
 * tailored for campus navigational graphs with accessibility filtering.
 */
@Component
public class DijkstraAlgorithm {

    public static class PathResult {
        private final List<CampusLocation> pathNodes;
        private final List<RouteConnection> edgesUsed;
        private final double totalDistance;

        public PathResult(List<CampusLocation> pathNodes, List<RouteConnection> edgesUsed, double totalDistance) {
            this.pathNodes = pathNodes;
            this.edgesUsed = edgesUsed;
            this.totalDistance = totalDistance;
        }

        public List<CampusLocation> getPathNodes() {
            return pathNodes;
        }

        public List<RouteConnection> getEdgesUsed() {
            return edgesUsed;
        }

        public double getTotalDistance() {
            return totalDistance;
        }
    }

    private static class NodeDistance implements Comparable<NodeDistance> {
        final Long locationId;
        final double distance;

        NodeDistance(Long locationId, double distance) {
            this.locationId = locationId;
            this.distance = distance;
        }

        @Override
        public int compareTo(NodeDistance o) {
            return Double.compare(this.distance, o.distance);
        }
    }

    /**
     * Finds the shortest path between startLocation and targetLocation.
     *
     * @param startLocation  origin node
     * @param targetLocation destination node
     * @param allConnections all navigable pathways in campus
     * @param accessibleOnly whether to restrict path to wheelchair-accessible connections
     * @return PathResult containing path nodes, edges, and total distance
     */
    public Optional<PathResult> findShortestPath(CampusLocation startLocation,
                                                 CampusLocation targetLocation,
                                                 List<RouteConnection> allConnections,
                                                 boolean accessibleOnly) {
        if (startLocation.getId().equals(targetLocation.getId())) {
            return Optional.of(new PathResult(List.of(startLocation), Collections.emptyList(), 0.0));
        }

        // Build adjacency list representation of the campus graph
        Map<Long, CampusLocation> locationMap = new HashMap<>();
        locationMap.put(startLocation.getId(), startLocation);
        locationMap.put(targetLocation.getId(), targetLocation);

        Map<Long, List<RouteConnection>> adjacencyList = new HashMap<>();

        for (RouteConnection edge : allConnections) {
            if (accessibleOnly && !edge.isWheelchairAccessible()) {
                continue; // Skip stairs or steep inclines if accessibility is required
            }

            CampusLocation u = edge.getFromLocation();
            CampusLocation v = edge.getToLocation();

            locationMap.put(u.getId(), u);
            locationMap.put(v.getId(), v);

            adjacencyList.computeIfAbsent(u.getId(), k -> new ArrayList<>()).add(edge);
        }

        Map<Long, Double> distances = new HashMap<>();
        Map<Long, Long> previousNodes = new HashMap<>();
        Map<Long, RouteConnection> previousEdges = new HashMap<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>();

        distances.put(startLocation.getId(), 0.0);
        pq.add(new NodeDistance(startLocation.getId(), 0.0));

        Set<Long> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            Long currentId = current.locationId;

            if (visited.contains(currentId)) {
                continue;
            }
            visited.add(currentId);

            if (currentId.equals(targetLocation.getId())) {
                break; // Reached destination
            }

            List<RouteConnection> neighbors = adjacencyList.getOrDefault(currentId, Collections.emptyList());
            for (RouteConnection edge : neighbors) {
                Long neighborId = edge.getToLocation().getId();

                if (visited.contains(neighborId)) {
                    continue;
                }

                double newDist = distances.get(currentId) + edge.getDistanceMeters();
                if (newDist < distances.getOrDefault(neighborId, Double.MAX_VALUE)) {
                    distances.put(neighborId, newDist);
                    previousNodes.put(neighborId, currentId);
                    previousEdges.put(neighborId, edge);
                    pq.add(new NodeDistance(neighborId, newDist));
                }
            }
        }

        if (!distances.containsKey(targetLocation.getId())) {
            return Optional.empty(); // No navigable path found
        }

        // Reconstruct path
        LinkedList<CampusLocation> path = new LinkedList<>();
        LinkedList<RouteConnection> edges = new LinkedList<>();
        Long curr = targetLocation.getId();

        while (curr != null) {
            path.addFirst(locationMap.get(curr));
            RouteConnection edge = previousEdges.get(curr);
            if (edge != null) {
                edges.addFirst(edge);
            }
            curr = previousNodes.get(curr);
        }

        return Optional.of(new PathResult(path, edges, distances.get(targetLocation.getId())));
    }
}
