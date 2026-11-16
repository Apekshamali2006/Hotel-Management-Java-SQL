package com.hotel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public class BookingFrame extends JFrame {
    private final JComboBox<String> guestBox = new JComboBox<>();
    private final JComboBox<String> roomBox = new JComboBox<>();
    private final JTextField checkInField = new JTextField(LocalDate.now().toString());
    private final JTextField checkOutField = new JTextField(LocalDate.now().plusDays(1).toString());

    private final Map<String,Integer> guestIds = new LinkedHashMap<>();
    private final Map<String,Integer> roomIds = new LinkedHashMap<>();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Booking ID","Guest","Room","Check In","Check Out","Status"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public BookingFrame() {
        setTitle("Manage Bookings");
        setSize(950, 580);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridLayout(4,2,8,8));
        form.setBorder(BorderFactory.createTitledBorder("New Booking"));
        form.add(new JLabel("Guest:")); form.add(guestBox);
        form.add(new JLabel("Available Room:")); form.add(roomBox);
        form.add(new JLabel("Check-in (YYYY-MM-DD):")); form.add(checkInField);
        form.add(new JLabel("Check-out (YYYY-MM-DD):")); form.add(checkOutField);

        JButton book = new JButton("Book Room");
        JButton checkout = new JButton("Checkout Selected");
        JButton refresh = new JButton("Refresh");

        JPanel buttons = new JPanel();
        buttons.add(book); buttons.add(checkout); buttons.add(refresh);

        JPanel north = new JPanel(new BorderLayout());
        north.add(form, BorderLayout.CENTER);
        north.add(buttons, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        book.addActionListener(e -> bookRoom());
        checkout.addActionListener(e -> checkoutBooking());
        refresh.addActionListener(e -> refreshAll());

        refreshAll();
    }

    private void refreshAll() {
        loadGuests();
        loadAvailableRooms();
        loadBookings();
    }

    private void loadGuests() {
        guestIds.clear();
        guestBox.removeAllItems();
        String sql = "SELECT guest_id,name,phone FROM guests ORDER BY name";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String label = rs.getInt("guest_id") + " - " +
                        rs.getString("name") + " (" + rs.getString("phone") + ")";
                guestIds.put(label, rs.getInt("guest_id"));
                guestBox.addItem(label);
            }
        } catch (SQLException ex) { showError(ex); }
    }

    private void loadAvailableRooms() {
        roomIds.clear();
        roomBox.removeAllItems();
        String sql = "SELECT room_id,room_number,room_type,price_per_day " +
                     "FROM rooms WHERE status='Available' ORDER BY room_number";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String label = rs.getString("room_number") + " - " +
                        rs.getString("room_type") + " - ₹" +
                        rs.getBigDecimal("price_per_day") + "/day";
                roomIds.put(label, rs.getInt("room_id"));
                roomBox.addItem(label);
            }
        } catch (SQLException ex) { showError(ex); }
    }

    private void loadBookings() {
        model.setRowCount(0);
        String sql = "SELECT b.booking_id,g.name,r.room_number,b.check_in,b.check_out,b.status " +
                     "FROM bookings b JOIN guests g ON b.guest_id=g.guest_id " +
                     "JOIN rooms r ON b.room_id=r.room_id ORDER BY b.booking_id DESC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getString("name"),
                        rs.getString("room_number"),
                        rs.getDate("check_in"),
                        rs.getDate("check_out"),
                        rs.getString("status")
                });
            }
        } catch (SQLException ex) { showError(ex); }
    }

    private void bookRoom() {
        if (guestBox.getSelectedItem() == null || roomBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Add a guest and an available room first.");
            return;
        }

        try {
            LocalDate in = LocalDate.parse(checkInField.getText().trim());
            LocalDate out = LocalDate.parse(checkOutField.getText().trim());

            if (!out.isAfter(in)) {
                JOptionPane.showMessageDialog(this, "Check-out must be after check-in.");
                return;
            }

            int guestId = guestIds.get(guestBox.getSelectedItem().toString());
            int roomId = roomIds.get(roomBox.getSelectedItem().toString());

            try (Connection con = DBConnection.getConnection()) {
                con.setAutoCommit(false);
                try {
                    String bookingSql =
                            "INSERT INTO bookings(guest_id,room_id,check_in,check_out,status) " +
                            "VALUES(?,?,?,?, 'Booked')";
                    try (PreparedStatement ps = con.prepareStatement(bookingSql)) {
                        ps.setInt(1, guestId);
                        ps.setInt(2, roomId);
                        ps.setDate(3, Date.valueOf(in));
                        ps.setDate(4, Date.valueOf(out));
                        ps.executeUpdate();
                    }

                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE rooms SET status='Occupied' WHERE room_id=?")) {
                        ps.setInt(1, roomId);
                        ps.executeUpdate();
                    }

                    con.commit();
                    JOptionPane.showMessageDialog(this, "Room booked successfully.");
                } catch (SQLException ex) {
                    con.rollback();
                    throw ex;
                } finally {
                    con.setAutoCommit(true);
                }
            }

            refreshAll();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Use date format YYYY-MM-DD.");
        } catch (SQLException ex) { showError(ex); }
    }

    private void checkoutBooking() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a booking first.");
            return;
        }

        int bookingId = Integer.parseInt(model.getValueAt(row,0).toString());
        String status = model.getValueAt(row,5).toString();

        if (!status.equalsIgnoreCase("Booked")) {
            JOptionPane.showMessageDialog(this, "This booking is already closed.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(this,
                "Checkout selected booking?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int roomId;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT room_id FROM bookings WHERE booking_id=?")) {
                    ps.setInt(1, bookingId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Booking not found.");
                        roomId = rs.getInt("room_id");
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE bookings SET status='Completed' WHERE booking_id=?")) {
                    ps.setInt(1, bookingId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE rooms SET status='Available' WHERE room_id=?")) {
                    ps.setInt(1, roomId);
                    ps.executeUpdate();
                }

                con.commit();
                JOptionPane.showMessageDialog(this, "Checkout completed.");
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
            refreshAll();
        } catch (SQLException ex) { showError(ex); }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
