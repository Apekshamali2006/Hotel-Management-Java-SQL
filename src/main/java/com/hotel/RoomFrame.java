package com.hotel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class RoomFrame extends JFrame {
    private final JTextField idField = new JTextField();
    private final JTextField numberField = new JTextField();
    private final JComboBox<String> typeBox =
            new JComboBox<>(new String[]{"Single", "Double", "Deluxe", "Suite"});
    private final JTextField priceField = new JTextField();
    private final JComboBox<String> statusBox =
            new JComboBox<>(new String[]{"Available", "Occupied", "Maintenance"});

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Room No.", "Type", "Price", "Status"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public RoomFrame() {
        setTitle("Manage Rooms");
        setSize(850, 550);
        setLocationRelativeTo(null);

        idField.setEditable(false);

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Room Details"));
        form.add(new JLabel("Room ID:")); form.add(idField);
        form.add(new JLabel("Room Number:")); form.add(numberField);
        form.add(new JLabel("Room Type:")); form.add(typeBox);
        form.add(new JLabel("Price / Day:")); form.add(priceField);
        form.add(new JLabel("Status:")); form.add(statusBox);

        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton clear = new JButton("Clear");

        JPanel buttons = new JPanel();
        buttons.add(add); buttons.add(update); buttons.add(delete); buttons.add(clear);

        JPanel north = new JPanel(new BorderLayout());
        north.add(form, BorderLayout.CENTER);
        north.add(buttons, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        add.addActionListener(e -> addRoom());
        update.addActionListener(e -> updateRoom());
        delete.addActionListener(e -> deleteRoom());
        clear.addActionListener(e -> clearForm());
        table.getSelectionModel().addListSelectionListener(e -> fillForm());

        loadRooms();
    }

    private void loadRooms() {
        model.setRowCount(0);
        String sql = "SELECT * FROM rooms ORDER BY room_id";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getString("room_type"),
                        rs.getBigDecimal("price_per_day"),
                        rs.getString("status")
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void addRoom() {
        String sql = "INSERT INTO rooms(room_number,room_type,price_per_day,status) VALUES(?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, numberField.getText().trim());
            ps.setString(2, typeBox.getSelectedItem().toString());
            ps.setBigDecimal(3, new java.math.BigDecimal(priceField.getText().trim()));
            ps.setString(4, statusBox.getSelectedItem().toString());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Room added.");
            clearForm(); loadRooms();
        } catch (Exception ex) { showError(ex); }
    }

    private void updateRoom() {
        if (idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a room first.");
            return;
        }
        String sql = "UPDATE rooms SET room_number=?,room_type=?,price_per_day=?,status=? WHERE room_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, numberField.getText().trim());
            ps.setString(2, typeBox.getSelectedItem().toString());
            ps.setBigDecimal(3, new java.math.BigDecimal(priceField.getText().trim()));
            ps.setString(4, statusBox.getSelectedItem().toString());
            ps.setInt(5, Integer.parseInt(idField.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Room updated.");
            clearForm(); loadRooms();
        } catch (Exception ex) { showError(ex); }
    }

    private void deleteRoom() {
        if (idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a room first.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this, "Delete selected room?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM rooms WHERE room_id=?")) {
            ps.setInt(1, Integer.parseInt(idField.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Room deleted.");
            clearForm(); loadRooms();
        } catch (SQLException ex) { showError(ex); }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        idField.setText(model.getValueAt(row, 0).toString());
        numberField.setText(model.getValueAt(row, 1).toString());
        typeBox.setSelectedItem(model.getValueAt(row, 2).toString());
        priceField.setText(model.getValueAt(row, 3).toString());
        statusBox.setSelectedItem(model.getValueAt(row, 4).toString());
    }

    private void clearForm() {
        idField.setText("");
        numberField.setText("");
        priceField.setText("");
        typeBox.setSelectedIndex(0);
        statusBox.setSelectedIndex(0);
        table.clearSelection();
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
