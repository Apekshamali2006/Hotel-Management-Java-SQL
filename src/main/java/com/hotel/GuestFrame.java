package com.hotel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class GuestFrame extends JFrame {
    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField addressField = new JTextField();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID","Name","Phone","Email","Address"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public GuestFrame() {
        setTitle("Manage Guests");
        setSize(900, 560);
        setLocationRelativeTo(null);
        idField.setEditable(false);

        JPanel form = new JPanel(new GridLayout(5,2,8,8));
        form.setBorder(BorderFactory.createTitledBorder("Guest Details"));
        form.add(new JLabel("Guest ID:")); form.add(idField);
        form.add(new JLabel("Name:")); form.add(nameField);
        form.add(new JLabel("Phone:")); form.add(phoneField);
        form.add(new JLabel("Email:")); form.add(emailField);
        form.add(new JLabel("Address:")); form.add(addressField);

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

        add.addActionListener(e -> addGuest());
        update.addActionListener(e -> updateGuest());
        delete.addActionListener(e -> deleteGuest());
        clear.addActionListener(e -> clearForm());
        table.getSelectionModel().addListSelectionListener(e -> fillForm());

        loadGuests();
    }

    private void loadGuests() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM guests ORDER BY guest_id")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("guest_id"), rs.getString("name"),
                        rs.getString("phone"), rs.getString("email"),
                        rs.getString("address")
                });
            }
        } catch (SQLException ex) { showError(ex); }
    }

    private void addGuest() {
        if (nameField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and phone are required.");
            return;
        }
        String sql = "INSERT INTO guests(name,phone,email,address) VALUES(?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nameField.getText().trim());
            ps.setString(2, phoneField.getText().trim());
            ps.setString(3, emailField.getText().trim());
            ps.setString(4, addressField.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Guest added.");
            clearForm(); loadGuests();
        } catch (SQLException ex) { showError(ex); }
    }

    private void updateGuest() {
        if (idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a guest first.");
            return;
        }
        String sql = "UPDATE guests SET name=?,phone=?,email=?,address=? WHERE guest_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nameField.getText().trim());
            ps.setString(2, phoneField.getText().trim());
            ps.setString(3, emailField.getText().trim());
            ps.setString(4, addressField.getText().trim());
            ps.setInt(5, Integer.parseInt(idField.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Guest updated.");
            clearForm(); loadGuests();
        } catch (SQLException ex) { showError(ex); }
    }

    private void deleteGuest() {
        if (idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a guest first.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this, "Delete selected guest?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM guests WHERE guest_id=?")) {
            ps.setInt(1, Integer.parseInt(idField.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Guest deleted.");
            clearForm(); loadGuests();
        } catch (SQLException ex) { showError(ex); }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        idField.setText(model.getValueAt(row,0).toString());
        nameField.setText(model.getValueAt(row,1).toString());
        phoneField.setText(model.getValueAt(row,2).toString());
        emailField.setText(model.getValueAt(row,3).toString());
        addressField.setText(model.getValueAt(row,4).toString());
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        table.clearSelection();
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
