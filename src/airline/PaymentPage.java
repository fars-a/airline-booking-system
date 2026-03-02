package airline;

import java.sql.*;
import javax.swing.*;

public class PaymentPage extends JFrame {
    private double totalPrice;
    private int bookingId; // ✅ add this

    // Updated constructor to accept bookingId
    public PaymentPage(double totalAmount, int bookingId) {
        this.totalPrice = totalAmount;
        this.bookingId = bookingId; // store bookingId
        initComponents();
    }

    private void initComponents() {
        JLabel lblAmount = new JLabel("Amount to Pay: " + totalPrice);
        JButton btnPay = new JButton("Pay Now");

        btnPay.addActionListener(e -> {
            String paymentMethod = "Card"; // could be dynamic, e.g., from radio buttons
            savePayment(bookingId, totalPrice, paymentMethod); // use real bookingId
            this.dispose();
        });

        setLayout(new java.awt.FlowLayout());
        add(lblAmount);
        add(btnPay);

        setTitle("Payment Page");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void savePayment(int bookingId, double amount, String method) {
    try {
        Class.forName("oracle.jdbc.OracleDriver");
        Connection con = DriverManager.getConnection(
            "jdbc:oracle:thin:@//localhost:1522/XEPDB1", "system", "oracle");

        // Insert PAYMENT_DATE as CURRENT_TIMESTAMP to avoid conversion issues
        String sql = "INSERT INTO payment (BOOKING_ID, AMOUNT, PAYMENT_METHOD, STATUS, PAYMENT_DATE) "
                   + "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, bookingId);          // BOOKING_ID
        pst.setDouble(2, amount);          // AMOUNT
        pst.setString(3, method);          // PAYMENT_METHOD
        pst.setString(4, "Paid");          // STATUS

        int rows = pst.executeUpdate();

        if (rows > 0) {
            JOptionPane.showMessageDialog(this, "Payment Successful!");
        }
         SelectFlight selectFlightPage = new SelectFlight();
        selectFlightPage.setVisible(true);
        pst.close();
        con.close();

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Payment Error: " + e.getMessage());
    }
}
}