package com.busbooking.util;

import com.busbooking.model.Booking;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReceiptGenerator {

    public static String generateReceipt(Booking booking, String savePath) {
        try {
            File file = new File(savePath);
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf, PageSize.A5);
            doc.setMargins(20, 20, 20, 20);

            DeviceRgb headerColor = new DeviceRgb(0, 100, 0); // Kenya green
            DeviceRgb accentColor = new DeviceRgb(187, 0, 0); // Kenya red

            // Title
            doc.add(new Paragraph("🚌 KENYA BUS BOOKING")
                    .setFontSize(18).setBold()
                    .setFontColor(headerColor)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("BOOKING RECEIPT")
                    .setFontSize(14).setBold()
                    .setFontColor(accentColor)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15));

            // Divider
            doc.add(new Paragraph("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(headerColor));

            // Details table
            float[] colWidths = {40, 60};
            Table table = new Table(UnitValue.createPercentArray(colWidths)).useAllAvailableWidth();

            addRow(table, "Booking ID:", "BK-" + String.format("%05d", booking.getId()));
            addRow(table, "Passenger:", booking.getPassengerName());
            addRow(table, "Bus:", booking.getBusName());
            addRow(table, "Route:", booking.getOrigin() + " → " + booking.getDestination());
            addRow(table, "Seat No:", String.valueOf(booking.getSeatNumber()));
            addRow(table, "Travel Date:", booking.getTravelDate().toString());
            addRow(table, "Amount Paid:", "KES " + booking.getAmountPaid());
            addRow(table, "M-Pesa Code:", booking.getMpesaCode() != null ? booking.getMpesaCode() : "N/A");
            addRow(table, "Status:", booking.getStatus());
            addRow(table, "Printed:", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            doc.add(table);

            // Footer
            doc.add(new Paragraph("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(headerColor)
                    .setMarginTop(15));

            doc.add(new Paragraph("Thank you for travelling with us!")
                    .setFontSize(11).setItalic()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(headerColor));

            doc.add(new Paragraph("For support call: 0700-000-000")
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY));

            doc.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void addRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold().setFontSize(10))
                .setBorder(null).setPadding(3));
        table.addCell(new Cell().add(new Paragraph(value).setFontSize(10))
                .setBorder(null).setPadding(3));
    }
}
