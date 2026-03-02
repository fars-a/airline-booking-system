/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airline;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class MyBookings extends JFrame {

    private JTable tblBookings;
    private JButton btnCancel;
    private int passengerId; // current passenger

    public MyBookings(int passengerId) {
        this.passengerId = passengerId;
        setTitle("My Bookings");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        loadBookings();
    }

    private void initComponents() {
        tblBookings = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblBookings);

        btnCancel = new JButton("Cancel Booking");
        btnCancel.addActionListener(e -> cancelSelectedBooking());

        // Layout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(scrollPane);
        panel.add(btnCancel);

        add(panel);
    }

    private void loadBookings() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            Connection con = DriverManager.getConnection(
                "jdbc:oracle:thin:@//localhost:1522/XEPDB1", "system", "oracle");

            String sql = "SELECT b.booking_id, f.flight_name, f.source, f.destination, " +
                         "b.class_type, b.seats, b.total_price, b.booking_date " +
                         "FROM booking b JOIN flight f ON b.flight_id = f.flight_id " +
                         "WHERE b.passenger_id = ? ORDER BY b.booking_date DESC";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, passengerId);

            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Booking ID");
            model.addColumn("Flight");
            model.addColumn("Source");
            model.addColumn("Destination");
            model.addColumn("Class");
            model.addColumn("Seats");
            model.addColumn("Total Price");
            model.addColumn("Booking Date");

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("flight_name"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getString("class_type"),
                    rs.getInt("seats"),
                    rs.getDouble("total_price"),
                    rs.getTimestamp("booking_date")
                });
            }

            tblBookings.setModel(model);

            rs.close();
            pst.close();
            con.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + ex.getMessage());
        }
    }

    private void cancelSelectedBooking() {
        int row = tblBookings.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.");
            return;
        }

        int bookingId = (int) tblBookings.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel booking ID " + bookingId + "?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            Class.forName("oracle.jdbc.OracleDriver");
            Connection con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@//localhost:1522/XEPDB1", "system", "oracle");

            String sql = "DELETE FROM booking WHERE booking_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, bookingId);

            int rows = pst.executeUpdate();
            pst.close();
            con.close();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Booking cancelled successfully!");
                loadBookings(); // refresh table
            } else {
                JOptionPane.showMessageDialog(this, "Booking cancellation failed!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cancelling booking: " + ex.getMessage());
        }
    }
}