package com.hotel;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DashboardFrame extends JFrame {
    private final JLabel roomsLabel = new JLabel();
    private final JLabel guestsLabel = new JLabel();
    private final JLabel bookingsLabel = new JLabel();
    private final JLabel revenueLabel = new JLabel();

    public DashboardFrame(String username) {
        setTitle("Hotel Management System - Dashboard");
        setSize(850, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel("HOTEL MANAGEMENT SYSTEM", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        top.add(title, BorderLayout.CENTER);
        top.add(new JLabel("  Logged in: " + username + "  "), BorderLayout.EAST);

        JPanel cards = new JPanel(new GridLayout(1, 4, 12, 12));
        cards.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cards.add(card("Rooms", roomsLabel));
        cards.add(card("Guests", guestsLabel));
        cards.add(card("Bookings", bookingsLabel));
        cards.add(card("Payments", revenueLabel));

        JPanel buttons = new JPanel(new GridLayout(2, 3, 12, 12));
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 60, 25, 60));

        JButton roomBtn = new JButton("Manage Rooms");
        JButton guestBtn = new JButton("Manage Guests");
        JButton bookingBtn = new JButton("Manage Bookings");
        JButton paymentBtn = new JButton("Manage Payments");
        JButton refreshBtn = new JButton("Refresh Dashboard");
        JButton logoutBtn = new JButton("Logout");

        buttons.add(roomBtn);
        buttons.add(guestBtn);
        buttons.add(bookingBtn);
        buttons.add(paymentBtn);
        buttons.add(refreshBtn);
        buttons.add(logoutBtn);

        add(top, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        roomBtn.addActionListener(e -> new RoomFrame().setVisible(true));
        guestBtn.addActionListener(e -> new GuestFrame().setVisible(true));
        bookingBtn.addActionListener(e -> new BookingFrame().setVisible(true));
        paymentBtn.addActionListener(e -> new PaymentFrame().setVisible(true));
        refreshBtn.addActionListener(e -> loadCounts());
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        loadCounts();
    }

    private JPanel card(String title, JLabel value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        value.setHorizontalAlignment(SwingConstants.CENTER);
        value.setFont(new Font("Arial", Font.BOLD, 24));
        p.add(value, BorderLayout.CENTER);
        return p;
    }

    private void loadCounts() {
        try (Connection con = DBConnection.getConnection()) {
            roomsLabel.setText(String.valueOf(count(con, "rooms")));
            guestsLabel.setText(String.valueOf(count(con, "guests")));
            bookingsLabel.setText(String.valueOf(count(con, "bookings")));
            revenueLabel.setText(String.format("₹ %.2f", paymentTotal(con)));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load dashboard: " + ex.getMessage());
        }
    }

    private int count(Connection con, String table) throws SQLException {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private double paymentTotal(Connection con) throws SQLException {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COALESCE(SUM(amount),0) FROM payments")) {
            rs.next();
            return rs.getDouble(1);
        }
    }
}
