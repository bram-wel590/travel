# 🚌 Kenya Bus Booking System

A Java Swing desktop application for booking bus tickets across Kenya, with M-Pesa STK Push integration, PDF receipt generation, and an admin dashboard.

## Bus Companies
| Bus | Seats |
|-----|-------|
| Maseno Speeder Bus | 42 |
| Kisumu Express | 48 |
| Green Safaris | 46 |
| Dualipa | 40 |
| Honkie Travels | 41 |

## Stations (8 Cities)
Nairobi, Mombasa, Kisumu, Nakuru, Eldoret, Thika, Nyeri, Malindi

## Features
- ✅ User registration & login with input validation
- ✅ CAPTCHA verification on registration
- ✅ Phone: exactly 10 digits, no letters
- ✅ Name: max 20 characters, no numbers
- ✅ Real bus seat layout (2-aisle-2) with available/booked/selected colors
- ✅ M-Pesa Daraja STK Push payment (sandbox ready)
- ✅ Downloadable PDF receipt generation
- ✅ Admin dashboard: manage bookings, view passengers, statistics
- ✅ Exit confirmation dialog
- ✅ Background images per screen (b.jpg, u.jpg, s.jpg, i.jpg)
- ✅ Colored buttons throughout
- ✅ Phone-sized frame (420×750), resizable

## Prerequisites
1. **Java 17+** (JDK)
2. **Maven 3.8+**
3. **MySQL 8.0+**

## Setup

### 1. Database
```bash
mysql -u root -p < sql/schema.sql
```

### 2. Configure Database Connection
Edit `src/main/java/com/busbooking/db/DatabaseConnection.java`:
```java
private static final String USER = "root";
private static final String PASSWORD = "your_mysql_password";
```

### 3. Background Images
Place your images in `src/main/resources/images/`:
- `b.jpg` - Login screen background
- `u.jpg` - Registration screen background
- `s.jpg` - Admin screens background
- `i.jpg` - Booking screen background

### 4. M-Pesa (Optional)
For real M-Pesa STK Push, edit `src/main/java/com/busbooking/mpesa/MpesaService.java`:
- Get credentials from https://developer.safaricom.co.ke
- Replace `CONSUMER_KEY`, `CONSUMER_SECRET`
- For production, change `BASE_URL` to `https://api.safaricom.co.ke`

### 5. Build & Run
```bash
# Compile and run
mvn clean compile exec:java -Dexec.mainClass="com.busbooking.BusBookingApp"

# Or package as JAR
mvn clean package
java -jar target/kenya-bus-booking-1.0-SNAPSHOT.jar
```

## Admin Login
- **Username:** admin
- **Password:** admin123

## Currency
All prices are in **KES (Kenya Shillings)**.

## Project Structure
```
src/main/java/com/busbooking/
├── BusBookingApp.java          # Main entry point
├── db/
│   └── DatabaseConnection.java # MySQL connection
├── model/
│   ├── User.java
│   ├── Booking.java
│   └── Route.java
├── mpesa/
│   └── MpesaService.java       # Daraja STK Push
├── ui/
│   ├── MainFrame.java          # Main window with CardLayout
│   ├── LoginPanel.java         # User login
│   ├── RegisterPanel.java      # Registration + CAPTCHA
│   ├── BookingPanel.java       # Route search, seat selection, payment
│   ├── SeatSelectionPanel.java # Bus seat layout
│   ├── AdminLoginPanel.java    # Admin login
│   └── AdminPanel.java         # Admin dashboard
└── util/
    ├── CaptchaPanel.java       # CAPTCHA widget
    ├── InputValidator.java     # Name/phone/email validation
    └── ReceiptGenerator.java   # PDF receipt (iText)
```
