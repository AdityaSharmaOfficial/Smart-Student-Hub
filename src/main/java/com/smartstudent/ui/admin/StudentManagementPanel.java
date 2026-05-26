package com.smartstudent.ui.admin;

import com.smartstudent.dao.StudentDAO;
import com.smartstudent.dao.UserDAO;
import com.smartstudent.model.Student;
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

public class StudentManagementPanel extends JPanel {

    private final User            currentUser;
    private final StudentDAO      studentDAO = new StudentDAO();
    private final UserDAO         userDAO    = new UserDAO();
    private       JTable          table;
    private       DefaultTableModel model;
    private       JTextField      searchField;
    private       List<Student>   studentList;

    private static final String[] COLUMNS = {
        "ID", "Admission No", "Name", "Course", "Semester", "Gender", "Phone", "Email", "Active"
    };

    public StudentManagementPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        buildUI();
        loadData();
    }

    private void buildUI() {
        // ── Top bar ─────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout(12, 0));
        topBar.setOpaque(false);

        JLabel title = UITheme.sectionLabel("Student Management");
        topBar.add(title, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        searchField = UITheme.styledField();
        searchField.setPreferredSize(new Dimension(220, 34));
        searchField.putClientProperty("hint", "Search students...");
        searchField.setToolTipText("Search by name, admission no, email, phone");

        JButton searchBtn = UITheme.accentButton("🔍 Search");
        JButton addBtn    = UITheme.primaryButton("＋ Add Student");
        JButton editBtn   = UITheme.primaryButton("✏️ Edit");
        JButton deleteBtn = UITheme.dangerButton("🗑 Delete");
        JButton refreshBtn= UITheme.successButton("↺ Refresh");

        searchBtn.addActionListener(e -> doSearch());
        searchField.addActionListener(e -> doSearch());
        addBtn.addActionListener(e -> showStudentForm(null));
        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e -> loadData());

        btnPanel.add(searchField);
        btnPanel.add(searchBtn);
        btnPanel.add(Box.createHorizontalStrut(8));
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);
        topBar.add(btnPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ── Table ────────────────────────────────────────────────────────────
        model = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(160);
        table.getColumnModel().getColumn(3).setPreferredWidth(180);
        table.getColumnModel().getColumn(8).setPreferredWidth(50);
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) viewStudentProfile();
            }
        });

        add(UITheme.scrollPane(table), BorderLayout.CENTER);

        // ── Footer ───────────────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setOpaque(false);
        JButton viewBtn = UITheme.accentButton("👁 View Profile");
        viewBtn.addActionListener(e -> viewStudentProfile());
        footer.add(viewBtn);
        JLabel tip = new JLabel("  Double-click a row to view profile");
        tip.setFont(UITheme.FONT_SMALL);
        tip.setForeground(UITheme.TEXT_SECONDARY);
        footer.add(tip);
        add(footer, BorderLayout.SOUTH);
    }

    private void loadData() {
        SwingWorker<List<Student>, Void> w = new SwingWorker<>() {
            @Override protected List<Student> doInBackground() throws Exception {
                return studentDAO.getAll();
            }
            @Override protected void done() {
                try {
                    studentList = get();
                    populateTable(studentList);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StudentManagementPanel.this,
                        "Error loading students: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }

    private void populateTable(List<Student> list) {
        model.setRowCount(0);
        for (Student s : list) {
            model.addRow(new Object[]{
                s.getStudentId(), s.getAdmissionNo(), s.getFullName(),
                s.getCourseName(), s.getCurrentSemester(), s.getGender(),
                s.getPhone(), s.getEmail(), s.isActive() ? "Yes" : "No"
            });
        }
    }

    private void doSearch() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) { loadData(); return; }
        SwingWorker<List<Student>, Void> w = new SwingWorker<>() {
            @Override protected List<Student> doInBackground() throws Exception {
                return studentDAO.search(kw);
            }
            @Override protected void done() {
                try { populateTable(get()); }
                catch (Exception e) { JOptionPane.showMessageDialog(null, e.getMessage()); }
            }
        };
        w.execute();
    }

    private Student getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a student."); return null; }
        int id = (int) model.getValueAt(row, 0);
        if (studentList == null) return null;
        return studentList.stream().filter(s -> s.getStudentId() == id).findFirst().orElse(null);
    }

    private void editSelected() {
        Student s = getSelected();
        if (s != null) showStudentForm(s);
    }

    private void deleteSelected() {
        Student s = getSelected();
        if (s == null) return;
        int c = JOptionPane.showConfirmDialog(this,
            "Deactivate student: " + s.getFullName() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            studentDAO.delete(s.getStudentId());
            loadData();
            JOptionPane.showMessageDialog(this, "Student deactivated.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewStudentProfile() {
        Student s = getSelected();
        if (s == null) return;
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Student Profile", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);
        dialog.add(buildProfileCard(s));
        dialog.setVisible(true);
    }

    private JPanel buildProfileCard(Student s) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JLabel nameLabel = new JLabel(s.getFullName());
        nameLabel.setFont(UITheme.FONT_HEADING);
        nameLabel.setForeground(UITheme.PRIMARY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel admLabel = new JLabel(s.getAdmissionNo());
        admLabel.setFont(UITheme.FONT_BODY);
        admLabel.setForeground(UITheme.TEXT_SECONDARY);
        admLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        panel.add(Box.createVerticalStrut(8));
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(admLabel);
        panel.add(Box.createVerticalStrut(16));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(12));

        String[][] fields = {
            {"Course",   s.getCourseName()},
            {"Semester", String.valueOf(s.getCurrentSemester())},
            {"DOB",      s.getDob() != null ? s.getDob().toString() : "—"},
            {"Gender",   s.getGender()},
            {"Email",    s.getEmail()},
            {"Phone",    s.getPhone()},
            {"Address",  s.getAddress()},
            {"Guardian", s.getGuardianName() + " (" + s.getGuardianRelation() + ")"},
            {"Guardian Ph", s.getGuardianPhone()},
            {"Blood Group", s.getBloodGroup()},
            {"Admission Date", s.getAdmissionDate() != null ? s.getAdmissionDate().toString() : "—"},
            {"Status",   s.isActive() ? "Active" : "Inactive"}
        };

        for (String[] row : fields) {
            JPanel row2 = new JPanel(new BorderLayout());
            row2.setOpaque(false);
            row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            JLabel key = new JLabel(row[0] + ":");
            key.setFont(UITheme.FONT_SMALL);
            key.setForeground(UITheme.TEXT_SECONDARY);
            key.setPreferredSize(new Dimension(110, 22));
            JLabel val = new JLabel(row[1] != null ? row[1] : "—");
            val.setFont(UITheme.FONT_BODY);
            val.setForeground(UITheme.TEXT_PRIMARY);
            row2.add(key, BorderLayout.WEST);
            row2.add(val, BorderLayout.CENTER);
            panel.add(row2);
        }
        return panel;
    }

    private void showStudentForm(Student existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
            isEdit ? "Edit Student" : "Add Student", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(580, 660);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField nameF     = UITheme.styledField();
        JTextField dobF      = UITheme.styledField();
        JTextField emailF    = UITheme.styledField();
        JTextField phoneF    = UITheme.styledField();
        JTextArea  addressF  = new JTextArea(3, 20);
        addressF.setFont(UITheme.FONT_BODY);
        addressF.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(6,8,6,8)));
        JTextField guardNameF = UITheme.styledField();
        JTextField guardPhF   = UITheme.styledField();
        JTextField guardRelF  = UITheme.styledField();
        JTextField bloodF     = UITheme.styledField();

        String[] genders   = {"Male", "Female", "Other"};
        String[] semesters = {"1","2","3","4","5","6","7","8"};
        JComboBox<String> genderCb   = UITheme.styledCombo(genders);
        JComboBox<String> semesterCb = UITheme.styledCombo(semesters);

        // Pre-fill if editing
        if (isEdit) {
            nameF.setText(existing.getFullName());
            dobF.setText(existing.getDob() != null ? existing.getDob().toString() : "");
            emailF.setText(existing.getEmail());
            phoneF.setText(existing.getPhone());
            addressF.setText(existing.getAddress());
            guardNameF.setText(existing.getGuardianName());
            guardPhF.setText(existing.getGuardianPhone());
            guardRelF.setText(existing.getGuardianRelation());
            bloodF.setText(existing.getBloodGroup());
            genderCb.setSelectedItem(existing.getGender());
            semesterCb.setSelectedItem(String.valueOf(existing.getCurrentSemester()));
        }

        int row = 0;
        Object[][] formFields = {
            {"Full Name *",    nameF},
            {"Date of Birth (YYYY-MM-DD)", dobF},
            {"Gender",         genderCb},
            {"Email",          emailF},
            {"Phone",          phoneF},
            {"Current Semester", semesterCb},
            {"Guardian Name",  guardNameF},
            {"Guardian Phone", guardPhF},
            {"Guardian Relation", guardRelF},
            {"Blood Group",    bloodF},
        };

        for (Object[] field : formFields) {
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
            form.add(UITheme.formLabel((String)field[0]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            form.add((Component) field[1], gbc);
            row++;
        }

        gbc.gridx = 0; gbc.gridy = row;
        form.add(UITheme.formLabel("Address"), gbc);
        gbc.gridx = 1;
        form.add(new JScrollPane(addressF), gbc);
        row++;

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.setOpaque(false);
        JButton save   = UITheme.primaryButton(isEdit ? "Update" : "Save");
        JButton cancel = UITheme.dangerButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());
        btnRow.add(cancel); btnRow.add(save);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        form.add(btnRow, gbc);

        save.addActionListener(e -> {
            if (nameF.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name is required.");
                return;
            }
            Student s = isEdit ? existing : new Student();
            s.setFullName(nameF.getText().trim());
            try {
                String dob = dobF.getText().trim();
                if (!dob.isEmpty()) s.setDob(LocalDate.parse(dob));
            } catch (DateTimeParseException ignored) {}
            s.setGender((String) genderCb.getSelectedItem());
            s.setEmail(emailF.getText().trim());
            s.setPhone(phoneF.getText().trim());
            s.setAddress(addressF.getText().trim());
            s.setCurrentSemester(Integer.parseInt((String) semesterCb.getSelectedItem()));
            s.setGuardianName(guardNameF.getText().trim());
            s.setGuardianPhone(guardPhF.getText().trim());
            s.setGuardianRelation(guardRelF.getText().trim());
            s.setBloodGroup(bloodF.getText().trim());
            s.setCourseId(1); // Default; extend with combo

            try {
                if (isEdit) {
                    studentDAO.update(s);
                    JOptionPane.showMessageDialog(dialog, "Student updated successfully.");
                } else {
                    String admNo = studentDAO.generateAdmissionNo();
                    s.setAdmissionNo(admNo);
                    s.setAdmissionDate(LocalDate.now());
                    // Create user account
                    String username = nameF.getText().trim().toLowerCase().replaceAll("\\s+", "") +
                                     (int)(Math.random()*900+100);
                    int uid = userDAO.createUser(username, "Student@123", 3);
                    s.setUserId(uid);
                    studentDAO.insert(s);
                    JOptionPane.showMessageDialog(dialog,
                        "Student added!\nAdmission No: " + admNo + "\nUsername: " + username +
                        "\nPassword: Student@123");
                }
                dialog.dispose();
                loadData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setContentPane(new JScrollPane(form));
        dialog.setVisible(true);
    }
}
