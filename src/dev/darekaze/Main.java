package dev.darekaze;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import dev.darekaze.utils.*;

public class Main {
  public static void computePaths(Vertex source, String mode) {
    source.setMinDistance(0);

    PriorityQueue<Vertex> vertexQueue = new PriorityQueue<>();
    vertexQueue.add(source);

    while (!vertexQueue.isEmpty()) {
      Vertex src = vertexQueue.poll();

      for (Edge e : src.neighbors) {
        Vertex target = e.target;
        int cost = e.cost;

        int distance = src.getMinDistance() + cost;
        if (distance < target.getMinDistance()) {
          // Remove current target
          vertexQueue.remove(target);

          // Update target
          target.setMinDistance(distance);
          target.setPrevious(src);
          vertexQueue.add(target);

          // Output for ss mode
          if (mode.equals("SS")) {
            System.out.print("Found " + target.name + ": ");
            printPath(target);
            promptContinue();
          }
        }
      }
    }
  }

  public static List<Vertex> getShortestPathTo(Vertex target) {
    List<Vertex> path = new ArrayList<>();

    for (Vertex v = target; v != null; v = v.getPrevious()) {
      path.add(v);
    }
    Collections.reverse(path);
    return path;
  }

  public static Map<String, Vertex> readVertexList(String fileName) {
    Map<String, Vertex> vList = new HashMap<>();

    try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
      stream.forEach((line) -> {
        String nodeName = line.split(":")[0];
        vList.put(nodeName, new Vertex(nodeName));
      });
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }

    try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
      stream.forEach((line) -> {
        String[] attributes = line.split("\\s+");
        String nodeName = attributes[0].split(":")[0];
        ArrayList<Edge> neighbors = new ArrayList<>();

        for (int n = 1; n < attributes.length; n++) {
          String[] kv = attributes[n].split(":");
          String name = kv[0];
          int cost = Integer.parseInt(kv[1]);

          neighbors.add(new Edge(vList.get(name), cost));
        }
        vList.get(nodeName).neighbors = neighbors;
      });
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
    return vList;
  }

  public static void printPath(Vertex target) {
    List<Vertex> path = getShortestPathTo(target);

    System.out.print("Path: ");
    for (int i = 0; i < path.size(); i++) {
      System.out.print(path.get(i).name);
      System.out.print(i < path.size() - 1 ? ">" : ", ");
    }
    System.out.println("Cost: " + target.getMinDistance());
  }

  public static void printNetwork(Map<String, Vertex> vList) {
    System.out.println();
    System.out.println("======= LSA of the current network =======");

    for (Vertex v : vList.values()) {
      System.out.print(v.name + ": ");
      v.neighbors.forEach(n -> System.out.print(n.target.name + ":" + n.cost + " "));
      System.out.println();
    }
  }

  private static void promptContinue() {
    System.out.println("  [Press any key to continue]");
    try {
      System.in.read();
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    String fileName = args[0];
    String mode = args[2].toUpperCase();

    Map<String, Vertex> vList = readVertexList(fileName);
    Vertex src = vList.get(args[1]);

    while (true) {
      // Compute the shortest paths for all nodes
      computePaths(src, mode);

      System.out.println("=========== Summary table ===========");
      System.out.println("Source " + src.name + ":");
      for (Vertex v : vList.values()) {  // display all shortest paths of each node
        if (v != src) {
          System.out.print(v.name + ": ");
          printPath(v);
        }
      }

      printNetwork(vList);
      modifyNetwork(vList); // for add or delete nodes
    }
  }

  private static void modifyNetwork(Map<String, Vertex> vList) {
    System.out.print("Do you want to add / delete node (y/n)? ");
    Scanner sc = new Scanner(System.in);
    String ans = sc.next().toUpperCase(); // for Y or N

    if (ans.toUpperCase().equals("Y")) {
      System.out.print("Add or delete (add/del)? ");
      sc = new Scanner(System.in);
      ans = sc.next().toUpperCase();

      if (ans.equals("ADD")) { // add node
        System.out.println("Please input the new node relation ([newNode]: [...existingNode:cost]):");
        sc = new Scanner(System.in);
        String newNode = sc.nextLine().toUpperCase();

        String[] attributes = newNode.split("\\s+");
        String nodeName = attributes[0].split(":")[0];
        ArrayList<Edge> neighbors = new ArrayList<>();

        // Add new node to vertex list
        vList.put(nodeName, new Vertex(nodeName));

        // Add neighbors in both sides
        for (int n = 1; n < attributes.length; n++) {
          String[] kv = attributes[n].split(":");
          String name = kv[0];
          int cost = Integer.parseInt(kv[1]);

          neighbors.add(new Edge(vList.get(name), cost));
          vList.get(name).neighbors.add(new Edge(vList.get(nodeName), cost));
        }
        vList.get(nodeName).neighbors = neighbors;

      } else if (ans.equals("DEL")) {  // delete node
        System.out.println("which of the following node you want to delete? ");
        printNetwork(vList);
        System.out.print("please enter one node only: ");
        sc = new Scanner(System.in);
        ans = sc.next().toUpperCase(); // get the node to be removed

        if (vList.containsKey(ans)) {
          for (Edge e : vList.get(ans).neighbors) {
            List<Edge> nb = e.target.neighbors;
            for (int i = 0; i < nb.size(); i++) {
              if (nb.get(i).target.name.equals(ans)) {
                nb.remove(i);
              }
            }
          }
          vList.remove(ans);
        }
      }
      // Reset all distance in the vertex list
      for (Vertex v : vList.values()) {
        v.setMinDistance(Integer.MAX_VALUE);
      }
    } else {
      System.exit(0);
    }
  }
}
