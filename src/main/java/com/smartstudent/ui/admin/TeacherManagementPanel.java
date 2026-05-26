package com.smartstudent.ui.admin;

import com.smartstudent.dao.TeacherDAO;
import com.smartstudent.dao.UserDAO;
import com.smartstudent.model.Teacher;
import com.smartstudent.model.User;
import com.smartstudent.util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TeacherManagementPanel extends JPanel {

    private final User        currentUser;
    private final TeacherDAO  teacherDAO = new TeacherDAO();
    private final UserDAO     userDAO    = new UserDAO();
    private       JTable      table;
    private       DefaultTableModel model;
    private       JTextField  searchField;
    private       List<Teacher> teacherList;

    private static final String[] COLUMNS = {"ID","Emp ID","Name","Department","Qualification","Email","Phone","Active"};

    public TeacherManagementPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel topBar = new JPanel(new BorderLayout(12, 0));
        topBar.setOpaque(false);
        topBar.add(UITheme.sectionLabel("Teacher Management"), BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        searchField = UITheme.styledField();
        searchField.setPreferredSize(new Dimension(200, 34));
        JButton searchBtn  = UITheme.accentButton("🔍 Search");
        JButton addBtn     = UITheme.primaryButton("＋ Add Teacher");
        JButton editBtn    = UITheme.primaryButton("✏️ Edit");
        JButton deleteBtn  = UITheme.dangerButton("🗑 Delete");
        JButton refreshBtn = UITheme.successButton("↺ Refresh");

        searchBtn.addActionListener(e  -> doSearch());
        searchField.addActionListener(e -> doSearch());
        addBtn.addActionListener(e    -> showTeacherForm(null));
        editBtn.addActionListener(e   -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e -> loadData());

        btnPanel.add(searchField); btnPanel.add(searchBtn);
        btnPanel.add(Box.createHorizontalStrut(8));
        btnPanel.add(addBtn); btnPanel.add(editBtn); btnPanel.add(deleteBtn); btnPanel.add(refreshBtn);
        topBar.add(btnPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        add(UITheme.scrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        SwingWorker<List<Teacher>, Void> w = new SwingWorker<>() {
            @Override protected List<Teacher> doInBackground() throws Exception { return teacherDAO.getAll(); }
            @Override protected void done() {
                try {
                    teacherList = get();
                    model.setRowCount(0);
                    for (Teacher t : teacherList)
                        model.addRow(new Object[]{t.getTeacherId(), t.getEmployeeId(), t.getFullName(),
                            t.getDeptName(), t.getQualification(), t.getEmail(), t.getPhone(),
                            t.isActive() ? "Yes" : "No"});
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                }
            }
        };
        w.execute();
    }

    private void doSearch() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) { loadData(); return; }
        SwingWorker<List<Teacher>, Void> w = new SwingWorker<>() {
            @Override protected List<Teacher> doInBackground() throws Exception { return teacherDAO.search(kw); }
            @Override protected void done() {
                try {
                    teacherList = get();
                    model.setRowCount(0);
                    for (Teacher t : teacherList)
                        model.addRow(new Object[]{t.getTeacherId(), t.getEmployeeId(), t.getFullName(),
                            t.getDeptName(), t.getQualification(), t.getEmail(), t.getPhone(),
                            t.isActive() ? "Yes" : "No"});
                } catch (Exception e) { JOptionPane.showMessageDialog(null, e.getMessage()); }
            }
        };
        w.execute();
    }

    private Teacher getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a teacher."); return null; }
        int id = (int) model.getValueAt(row, 0);
        if (teacherList == null) return null;
        return teacherList.stream().filter(t -> t.getTeacherId() == id).findFirst().orElse(null);
    }

    private void editSelected() {
        Teacher t = getSelected();
        if (t != null) showTeacherForm(t);
    }

    private void deleteSelected() {
        Teacher t = getSelected();
        if (t == null) return;
        int c = JOptionPane.showConfirmDialog(this, "Deactivate: " + t.getFullName() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        try { teacherDAO.delete(t.getTeacherId()); loadData(); }
        catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void showTeacherForm(Teacher existing) {
        boolean isEdit = existing != null;
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this),
            isEdit ? "Edit Teacher" : "Add Teacher", Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(540, 580);
        d.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameF  = UITheme.styledField();
        JTextField dobF   = UITheme.styledField();
        JTextField emailF = UITheme.styledField();
        JTextField phoneF = UITheme.styledField();
        JTextField qualF  = UITheme.styledField();
        JTextField addrF  = UITheme.styledField();
        JComboBox<String> genderCb = UITheme.styledCombo(new String[]{"Male","Female","Other"});
        JComboBox<String> deptCb   = UITheme.styledCombo(new String[]{"1-Computer Science","2-Electronics","3-Mechanical","4-Civil","5-Business"});

        if (isEdit) {
            nameF.setText(existing.getFullName());
            dobF.setText(existing.getDob() != null ? existing.getDob().toString() : "");
            emailF.setText(existing.getEmail());
            phoneF.setText(existing.getPhone());
            qualF.setText(existing.getQualification());
            addrF.setText(existing.getAddress());
            if (existing.getGender() != null) genderCb.setSelectedItem(existing.getGender());
        }

        Object[][] rows = {
            {"Full Name *", nameF}, {"Date of Birth", dobF}, {"Gender", genderCb},
            {"Email", emailF}, {"Phone", phoneF}, {"Qualification", qualF},
            {"Department", deptCb}, {"Address", addrF}
        };
        for (int i = 0; i < rows.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            form.add(UITheme.formLabel((String)rows[i][0]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            form.add((Component) rows[i][1], gbc);
        }

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.setOpaque(false);
        JButton save   = UITheme.primaryButton(isEdit ? "Update" : "Save");
        JButton cancel = UITheme.dangerButton("Cancel");
        cancel.addActionListener(e -> d.dispose());
        btnRow.add(cancel); btnRow.add(save);
        gbc.gridx = 0; gbc.gridy = rows.length; gbc.gridwidth = 2;
        form.add(btnRow, gbc);

        save.addActionListener(e -> {
            if (nameF.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(d, "Name required."); return; }
            Teacher t = isEdit ? existing : new Teacher();
            t.setFullName(nameF.getText().trim());
            try { if (!dobF.getText().trim().isEmpty()) t.setDob(LocalDate.parse(dobF.getText().trim())); }
            catch (DateTimeParseException ignored) {}
            t.setGender((String) genderCb.getSelectedItem());
            t.setEmail(emailF.getText().trim());
            t.setPhone(phoneF.getText().trim());
            t.setQualification(qualF.getText().trim());
            t.setAddress(addrF.getText().trim());
            String deptStr = (String) deptCb.getSelectedItem();
            t.setDeptId(Integer.parseInt(deptStr.split("-")[0].trim()));
            t.setJoiningDate(LocalDate.now());

            try {
                if (isEdit) {
                    teacherDAO.update(t);
                    JOptionPane.showMessageDialog(d, "Teacher updated.");
                } else {
                    String empId = teacherDAO.generateEmployeeId();
                    t.setEmployeeId(empId);
                    String username = nameF.getText().trim().toLowerCase().replaceAll("\\s+","_") +
                                     (int)(Math.random()*900+100);
                    int uid = userDAO.createUser(username, "Teacher@123", 2);
                    t.setUserId(uid);
                    teacherDAO.insert(t);
                    JOptionPane.showMessageDialog(d, "Teacher added!\nEmployee ID: " + empId +
                        "\nUsername: " + username + "\nPassword: Teacher@123");
                }
                d.dispose();
                loadData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        d.setContentPane(new JScrollPane(form));
        d.setVisible(true);
    }
}
