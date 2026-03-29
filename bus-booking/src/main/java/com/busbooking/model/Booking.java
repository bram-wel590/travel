package com.busbooking.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Booking {
    private int id;
    private int userId;
    private int routeId;
    private int seatNumber;
    private LocalDate bookingDate;
    private LocalDate travelDate;
    private String mpesaCode;
    private String phonePaid;
    private BigDecimal amountPaid;
    private String status;
    private String passengerName;
    private String busName;
    private String origin;
    private String destination;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }
    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }
    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
    public LocalDate getTravelDate() { return travelDate; }
    public void setTravelDate(LocalDate travelDate) { this.travelDate = travelDate; }
    public String getMpesaCode() { return mpesaCode; }
    public void setMpesaCode(String mpesaCode) { this.mpesaCode = mpesaCode; }
    public String getPhonePaid() { return phonePaid; }
    public void setPhonePaid(String phonePaid) { this.phonePaid = phonePaid; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getBusName() { return busName; }
    public void setBusName(String busName) { this.busName = busName; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
}
