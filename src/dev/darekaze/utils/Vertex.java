package dev.darekaze.utils;

import java.util.ArrayList;

public class Vertex implements Comparable<Vertex> {
  public final String name;
  public ArrayList<Edge> neighbors;
  private int minDistance = Integer.MAX_VALUE;
  private Vertex previous;

  public Vertex(String name) {
    this.name = name;
    this.neighbors = new ArrayList<Edge>();
  }

  @Override
  public int compareTo(Vertex peer) {
    int peerMinDistance = peer.getMinDistance();
    return Integer.compare(minDistance, peerMinDistance);
  }

  @Override
  public String toString() {
    return "Vertex{" +
        "name='" + name + '\'' +
        ", neighbors=" + neighbors +
        ", minDistance=" + minDistance +
        ", previous=" + previous +
        '}';
  }

  // Getters
  public int getMinDistance() {
    return minDistance;
  }

  public Vertex getPrevious() {
    return previous;
  }

  // Setters
  public void setMinDistance(int minDistance) {
    this.minDistance = minDistance;
  }

  public void setPrevious(Vertex previous) {
    this.previous = previous;
  }
}
