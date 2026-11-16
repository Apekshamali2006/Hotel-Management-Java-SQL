package com.hotel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class PaymentFrame extends JFrame {
    private final JTextField bookingIdField = new JTextField();
    private final JTextField amountField = new JTextField();
    private final JComboBox<String> methodBox =
            new JComboBox<>(new String[]{"Cash", "UPI", "Card", "Net Banking"});

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Payment ID","Booking ID","Amount","Method","Date"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public PaymentFrame() {
        setTitle("Manage Payments");
        setSize(850, 520);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridLayout(3,2,8,8));
        form.setBorder(BorderFactory.createTitledBorder("Payment Details"));
        form.add(new JLabel("Booking ID:")); form.add(bookingIdField);
        form.add(new JLabel("Amount:")); form.add(amountField);
        form.add(new JLabel("Payment Method:")); form.add(methodBox);

        JButton pay = new JButton("Add Payment");
        JButton refresh = new JButton("Refresh");

        JPanel buttons = new JPanel();
        buttons.add(pay); buttons.add(refresh);

        JPanel north = new JPanel(new BorderLayout());
        north.add(form, BorderLayout.CENTER);
        north.add(buttons, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        pay.addActionListener(e -> addPayment());
        refresh.addActionListener(e -> loadPayments());

        loadPayments();
    }

    private void addPayment() {
        try {
            int bookingId = Integer.parseInt(bookingIdField.getText().trim());
            double amount = Double.parseDouble(amountField.getText().trim());

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0.");
                return;
            }

            String sql = "INSERT INTO payments(booking_id,amount,payment_method,payment_date) " +
                         "VALUES(?,?,?,?)";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, bookingId);
                ps.setDouble(2, amount);
                ps.setString(3, methodBox.getSelectedItem().toString());
                ps.setDate(4, Date.valueOf(LocalDate.now()));
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Payment added.");
            bookingIdField.setText("");
            amountField.setText("");
            loadPayments();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid booking ID and amount.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPayments() {
        model.setRowCount(0);
        String sql = "SELECT * FROM payments ORDER BY payment_id DESC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("payment_id"),
                        rs.getInt("booking_id"),
                        rs.getBigDecimal("amount"),
                        rs.getString("payment_method"),
                        rs.getDate("payment_date")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
