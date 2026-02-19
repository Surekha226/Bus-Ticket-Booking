package com.busbooking.model;

public class Route {
    private int routeId;
    private String origin;
    private String destination;
    private int distance;
    private int duration;
    
    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public int getDistance() { return distance; }
    public void setDistance(int distance) { this.distance = distance; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
}
