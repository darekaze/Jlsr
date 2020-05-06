package dev.darekaze.utils;

public class Edge {
  public final Vertex target;
  public final int cost;

  public Edge(Vertex target, int cost) {
    this.target = target;
    this.cost = cost;
  }

  @Override
  public String toString() {
    return "Edge{" + target.name + ", " + cost + '}';
  }
}
