package com.busbooking.model;

import java.math.BigDecimal;

public class Route {
    private int id;
    private String origin;
    private String destination;
    private BigDecimal price;
    private int busCompanyId;
    private String busName;
    private int totalSeats;
    private String departureTime;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getBusCompanyId() { return busCompanyId; }
    public void setBusCompanyId(int busCompanyId) { this.busCompanyId = busCompanyId; }
    public String getBusName() { return busName; }
    public void setBusName(String busName) { this.busName = busName; }
    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    @Override
    public String toString() {
        return busName + " | " + origin + " → " + destination + " | KES " + price + " | " + departureTime;
    }
}
