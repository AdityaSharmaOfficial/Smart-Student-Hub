package com.smartstudent.ui.admin;

import com.smartstudent.config.DatabaseConfig;
import com.smartstudent.dao.*;
import com.smartstudent.model.User;
import com.smartstudent.util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

// ── AttendancePanel ───────────────────────────────────────────────────────────
class AttendancePanel extends JPanel {
    private final User currentUser;
    private JTable table;
    private DefaultTableModel model;

    AttendancePanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        buildUI();
    }

    private void buildUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setOpaque(false);
        top.add(UITheme.sectionLabel("Attendance Management"));

        JLabel subLbl = UITheme.formLabel("Subject ID:");
        JTextField subField  = UITheme.styledField();
        subField.setPreferredSize(new Dimension(80, 34));
        JLabel dateLbl = UITheme.formLabel("Date (YYYY-MM-DD):");
        JTextField dateField = UITheme.styledField();
        dateField.setPreferredSize(new Dimension(130, 34));
        dateField.setText(LocalDate.now().toString());
        JButton loadBtn = UITheme.accentButton("Load");
        JButton saveBtn = UITheme.primaryButton("Save Attendance");

        top.add(subLbl); top.add(subField);
        top.add(dateLbl); top.add(dateField);
        top.add(loadBtn); top.add(saveBtn);
        add(top, BorderLayout.NORTH);

        String[] cols = {"Student ID", "Admission No", "Name", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 3; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);

        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Present","Absent","Late","Excused"});
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(statusCombo));
        add(UITheme.scrollPane(table), BorderLayout.CENTER);

        loadBtn.addActionListener(e -> {
            try {
                int subId = Integer.parseInt(subField.getText().trim());
                LocalDate date = LocalDate.parse(dateField.getText().trim());
                List<Map<String, Object>> rows = new AttendanceDAO().getAttendanceByDate(subId, date);
                model.setRowCount(0);
                for (Map<String, Object> row : rows)
                    model.addRow(new Object[]{row.get("student_id"), row.get("admission_no"), row.get("full_name"), row.get("status")});
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        saveBtn.addActionListener(e -> {
            try {
                int subId = Integer.parseInt(subField.getText().trim());
                LocalDate date = LocalDate.parse(dateField.getText().trim());
                AttendanceDAO dao = new AttendanceDAO();
                for (int r = 0; r < model.getRowCount(); r++) {
                    int studentId = (int) model.getValueAt(r, 0);
                    String status = (String) model.getValueAt(r, 3);
                    dao.markAttendance(studentId, subId, date, status, 1);
                }
                JOptionPane.showMessageDialog(this, "Attendance saved successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }
}

// ── ExaminationPanel ──────────────────────────────────────────────────────────
class ExaminationPanel extends JPanel {
    private final User currentUser;
    private JTable table;
    private DefaultTableModel model;

    ExaminationPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        buildUI();
        loadExams();
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UITheme.sectionLabel("Examination Management"), BorderLayout.WEST);

        JButton addBtn = UITheme.primaryButton("＋ Add Exam");
        addBtn.addActionListener(e -> showAddExamDialog());
        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnP.setOpaque(false);
        btnP.add(addBtn);
        top.add(btnP, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        String[] cols = {"Exam ID","Exam Name","Type","Subject ID","Semester","Date","Max Marks","Pass Marks"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        add(UITheme.scrollPane(table), BorderLayout.CENTER);
    }

    private void loadExams() {
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM exams ORDER BY exam_date DESC")) {
            model.setRowCount(0);
            while (rs.next())
                model.addRow(new Object[]{rs.getInt("exam_id"), rs.getString("exam_name"),
                    rs.getString("exam_type"), rs.getInt("subject_id"), rs.getInt("semester"),
                    rs.getDate("exam_date"), rs.getInt("max_marks"), rs.getInt("pass_marks")});
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void showAddExamDialog() {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), "Add Exam", Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(420, 420);
        d.setLocationRelativeTo(this);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,4,6,4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameF    = UITheme.styledField();
        JTextField subIdF   = UITheme.styledField();
        JTextField semF     = UITheme.styledField();
        JTextField dateF    = UITheme.styledField(); dateF.setText(LocalDate.now().toString());
        JTextField maxF     = UITheme.styledField(); maxF.setText("100");
        JTextField passF    = UITheme.styledField(); passF.setText("40");
        JTextField yearF    = UITheme.styledField(); yearF.setText("2025-26");
        JComboBox<String> typeCb = UITheme.styledCombo(new String[]{"Internal","External","Practical","Viva"});

        Object[][] rows = {{"Exam Name*",nameF},{"Type",typeCb},{"Subject ID*",subIdF},
            {"Semester*",semF},{"Exam Date",dateF},{"Max Marks",maxF},{"Pass Marks",passF},{"Academic Year",yearF}};
        for (int i = 0; i < rows.length; i++) {
            gbc.gridx=0; gbc.gridy=i; gbc.weightx=0; form.add(UITheme.formLabel((String)rows[i][0]), gbc);
            gbc.gridx=1; gbc.weightx=1; form.add((Component)rows[i][1], gbc);
        }

        JButton save = UITheme.primaryButton("Save");
        JButton cancel = UITheme.dangerButton("Cancel");
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.setOpaque(false); btnRow.add(cancel); btnRow.add(save);
        gbc.gridx=0; gbc.gridy=rows.length; gbc.gridwidth=2; form.add(btnRow, gbc);
        cancel.addActionListener(e -> d.dispose());

        save.addActionListener(e -> {
            try {
                String sql = "INSERT INTO exams (exam_name,exam_type,subject_id,semester,exam_date,max_marks,pass_marks,academic_year) VALUES(?,?,?,?,?,?,?,?)";
                PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql);
                ps.setString(1, nameF.getText().trim());
                ps.setString(2, (String) typeCb.getSelectedItem());
                ps.setInt(3, Integer.parseInt(subIdF.getText().trim()));
                ps.setInt(4, Integer.parseInt(semF.getText().trim()));
                ps.setDate(5, java.sql.Date.valueOf(dateF.getText().trim()));
                ps.setInt(6, Integer.parseInt(maxF.getText().trim()));
                ps.setInt(7, Integer.parseInt(passF.getText().trim()));
                ps.setString(8, yearF.getText().trim());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(d, "Exam added!");
                d.dispose();
                loadExams();
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage()); }
        });

        d.setContentPane(form);
        d.setVisible(true);
    }
}

// ── ResultsPanel ──────────────────────────────────────────────────────────────
class ResultsPanel extends JPanel {
    private final User currentUser;
    private JTable table;
    private DefaultTableModel model;

    ResultsPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        buildUI();
    }

    private void buildUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);
        top.add(UITheme.sectionLabel("Results Management"));

        JTextField examIdF = UITheme.styledField(); examIdF.setPreferredSize(new Dimension(80,34));
        JButton loadBtn = UITheme.accentButton("Load Results");
        JButton addBtn  = UITheme.primaryButton("Enter Marks");
        top.add(UITheme.formLabel("Exam ID:")); top.add(examIdF);
        top.add(loadBtn); top.add(addBtn);
        add(top, BorderLayout.NORTH);

        String[] cols = {"Admission No","Student Name","Marks","Grade","Pass"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        add(UITheme.scrollPane(table), BorderLayout.CENTER);

        loadBtn.addActionListener(e -> {
            try {
                int examId = Integer.parseInt(examIdF.getText().trim());
                List<Map<String, Object>> rows = new ResultDAO().getResultsByExam(examId);
                model.setRowCount(0);
                for (Map<String, Object> row : rows)
                    model.addRow(new Object[]{row.get("admission_no"), row.get("full_name"),
                        row.get("marks"), row.get("grade"), ((boolean) row.get("is_pass")) ? "Pass" : "Fail"});
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        addBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter: Student ID, Exam ID, Marks (comma separated):");
            if (input == null || input.trim().isEmpty()) return;
            try {
                String[] parts = input.split(",");
                int studentId = Integer.parseInt(parts[0].trim());
                int examId    = Integer.parseInt(parts[1].trim());
                double marks  = Double.parseDouble(parts[2].trim());
                // Get max marks
                PreparedStatement ps = DatabaseConfig.getConnection()
                    .prepareStatement("SELECT max_marks, pass_marks FROM exams WHERE exam_id=?");
                ps.setInt(1, examId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int maxMarks  = rs.getInt("max_marks");
                    int passMarks = rs.getInt("pass_marks");
                    String grade  = ResultDAO.calculateGrade(marks, maxMarks);
                    boolean pass  = marks >= passMarks;
                    new ResultDAO().saveResult(studentId, examId, marks, grade, pass);
                    JOptionPane.showMessageDialog(this, "Result saved! Grade: " + grade);
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });
    }
}

// ── TimetablePanel ────────────────────────────────────────────────────────────
class TimetablePanel extends JPanel {
    private final User currentUser;
    private JTable table;
    private DefaultTableModel model;

    TimetablePanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        buildUI();
        loadTimetable();
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UITheme.sectionLabel("Timetable Management"), BorderLayout.WEST);
        JButton addBtn = UITheme.primaryButton("＋ Add Slot");
        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT)); btnP.setOpaque(false);
        btnP.add(addBtn); top.add(btnP, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        String[] cols = {"ID","Course","Sem","Subject","Teacher","Room","Day","Start","End"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        add(UITheme.scrollPane(table), BorderLayout.CENTER);

        addBtn.addActionListener(e -> showAddSlotDialog());
    }

    private void loadTimetable() {
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT tt.tt_id, c.course_name, tt.semester, sub.subject_name, t.full_name, " +
                 "COALESCE(cr.room_name,'—') AS room, tt.day_of_week, tt.start_time, tt.end_time " +
                 "FROM timetable tt " +
                 "JOIN courses c ON tt.course_id=c.course_id " +
                 "JOIN subjects sub ON tt.subject_id=sub.subject_id " +
                 "JOIN teachers t ON tt.teacher_id=t.teacher_id " +
                 "LEFT JOIN classrooms cr ON tt.room_id=cr.room_id " +
                 "ORDER BY tt.day_of_week, tt.start_time")) {
            model.setRowCount(0);
            while (rs.next())
                model.addRow(new Object[]{rs.getInt("tt_id"), rs.getString("course_name"),
                    rs.getInt("semester"), rs.getString("subject_name"), rs.getString("full_name"),
                    rs.getString("room"), rs.getString("day_of_week"),
                    rs.getTime("start_time"), rs.getTime("end_time")});
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void showAddSlotDialog() {
        JOptionPane.showMessageDialog(this, "Timetable slot addition form:\nEnter Course ID, Semester, Subject ID, Teacher ID, Room ID, Day, Start time, End time via database or extend this dialog.");
    }
}

// ── FeeManagementPanel ────────────────────────────────────────────────────────
class FeeManagementPanel extends JPanel {
    private final User currentUser;
    private final FeeDAO feeDAO = new FeeDAO();
    private JTable table;
    private DefaultTableModel model;

    FeeManagementPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        buildUI();
    }

    private void buildUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BODY);
        tabs.addTab("Pending Dues",     buildPendingPanel());
        tabs.addTab("Record Payment",   buildPaymentForm());
        tabs.addTab("Payment History",  buildHistoryPanel());
        add(UITheme.sectionLabel("Fee Management"), BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        loadPendingDues();
    }

    private JPanel pendingPanel;
    private DefaultTableModel pendingModel;

    private JPanel buildPendingPanel() {
        pendingPanel = new JPanel(new BorderLayout(0, 8));
        pendingPanel.setOpaque(false);
        String[] cols = {"Student ID","Admission No","Name","Course","Pending Amount (₹)"};
        pendingModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(pendingModel);
        UITheme.styleTable(t);
        pendingPanel.add(UITheme.scrollPane(t), BorderLayout.CENTER);
        JButton refresh = UITheme.successButton("↺ Refresh");
        refresh.addActionListener(e -> loadPendingDues());
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bp.setOpaque(false);
        bp.add(refresh); pendingPanel.add(bp, BorderLayout.SOUTH);
        return pendingPanel;
    }

    private void loadPendingDues() {
        SwingWorker<List<Map<String,Object>>, Void> w = new SwingWorker<>() {
            @Override protected List<Map<String,Object>> doInBackground() throws Exception {
                return feeDAO.getStudentsWithPendingFees();
            }
            @Override protected void done() {
                try {
                    pendingModel.setRowCount(0);
                    for (Map<String, Object> row : get())
                        pendingModel.addRow(new Object[]{row.get("student_id"), row.get("admission_no"),
                            row.get("full_name"), row.get("course_name"),
                            String.format("₹%.2f", (double) row.get("pending"))});
                } catch (Exception e) { JOptionPane.showMessageDialog(null, e.getMessage()); }
            }
        };
        w.execute();
    }

    private JPanel buildPaymentForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 32, 20, 32));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,4,8,4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField studentIdF = UITheme.styledField();
        JTextField feeIdF     = UITheme.styledField();
        JTextField amountF    = UITheme.styledField();
        JTextField fineF      = UITheme.styledField(); fineF.setText("0");
        JTextField remarkF    = UITheme.styledField();
        JTextField dueDateF   = UITheme.styledField();
        JComboBox<String> modeCb = UITheme.styledCombo(new String[]{"Cash","Online","DD","Cheque"});

        Object[][] rows = {{"Student ID*",studentIdF},{"Fee ID*",feeIdF},{"Amount (₹)*",amountF},
            {"Fine (₹)",fineF},{"Due Date",dueDateF},{"Payment Mode",modeCb},{"Remarks",remarkF}};
        for (int i = 0; i < rows.length; i++) {
            gbc.gridx=0; gbc.gridy=i; gbc.weightx=0; panel.add(UITheme.formLabel((String)rows[i][0]), gbc);
            gbc.gridx=1; gbc.weightx=1; panel.add((Component)rows[i][1], gbc);
        }

        JButton saveBtn = UITheme.primaryButton("Record Payment");
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bp.setOpaque(false); bp.add(saveBtn);
        gbc.gridx=0; gbc.gridy=rows.length; gbc.gridwidth=2; panel.add(bp, gbc);

        saveBtn.addActionListener(e -> {
            try {
                int studentId = Integer.parseInt(studentIdF.getText().trim());
                int feeId     = Integer.parseInt(feeIdF.getText().trim());
                double amount = Double.parseDouble(amountF.getText().trim());
                double fine   = Double.parseDouble(fineF.getText().trim());
                String mode   = (String) modeCb.getSelectedItem();
                String remarks = remarkF.getText().trim();
                String receipt = feeDAO.generateReceiptNo();
                LocalDate dueDate = dueDateF.getText().trim().isEmpty() ? null : LocalDate.parse(dueDateF.getText().trim());
                feeDAO.recordPayment(studentId, feeId, amount, fine, LocalDate.now(), dueDate, mode, receipt, remarks);
                JOptionPane.showMessageDialog(panel, "Payment recorded!\nReceipt No: " + receipt);
                studentIdF.setText(""); feeIdF.setText(""); amountF.setText(""); fineF.setText("0");
                loadPendingDues();
            } catch (Exception ex) { JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage()); }
        });
        return panel;
    }

    private JPanel buildHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);
        JTextField sidF = UITheme.styledField(); sidF.setPreferredSize(new Dimension(100, 34));
        JButton loadBtn = UITheme.accentButton("Load");
        top.add(UITheme.formLabel("Student ID:")); top.add(sidF); top.add(loadBtn);
        panel.add(top, BorderLayout.NORTH);

        String[] cols = {"Receipt No","Fee Type","Semester","Amount","Fine","Total","Date","Mode"};
        DefaultTableModel hModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(hModel); UITheme.styleTable(t);
        panel.add(UITheme.scrollPane(t), BorderLayout.CENTER);

        loadBtn.addActionListener(e -> {
            try {
                int sid = Integer.parseInt(sidF.getText().trim());
                List<Map<String, Object>> rows = feeDAO.getPaymentHistory(sid);
                hModel.setRowCount(0);
                for (Map<String, Object> row : rows)
                    hModel.addRow(new Object[]{row.get("receipt_no"), row.get("fee_type"), row.get("semester"),
                        String.format("₹%.2f",(double)row.get("amount_paid")),
                        String.format("₹%.2f",(double)row.get("fine_amount")),
                        String.format("₹%.2f",(double)row.get("total")),
                        row.get("payment_date"), row.get("payment_mode")});
            } catch (Exception ex) { JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage()); }
        });
        return panel;
    }
}

// ── LibraryPanel ──────────────────────────────────────────────────────────────
class LibraryPanel extends JPanel {
    private final User currentUser;
    private final LibraryDAO libDAO = new LibraryDAO();
    private DefaultTableModel bookModel, issuedModel;

    LibraryPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        buildUI();
    }

    private void buildUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BODY);
        tabs.addTab("Book Inventory", buildBookInventory());
        tabs.addTab("Issued Books",   buildIssuedBooks());
        tabs.addTab("Issue / Return", buildIssueReturn());
        add(UITheme.sectionLabel("Library Management"), BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        loadBooks(""); loadIssuedBooks();
    }

    private JPanel buildBookInventory() {
        JPanel panel = new JPanel(new BorderLayout(0,8));
        panel.setOpaque(false);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);
        JTextField searchF = UITheme.styledField(); searchF.setPreferredSize(new Dimension(200,34));
        JButton searchBtn = UITheme.accentButton("🔍 Search");
        searchBtn.addActionListener(e -> loadBooks(searchF.getText().trim()));
        searchF.addActionListener(e -> loadBooks(searchF.getText().trim()));
        top.add(searchF); top.add(searchBtn);
        panel.add(top, BorderLayout.NORTH);

        String[] cols = {"ID","Title","Author","ISBN","Category","Publisher","Year","Total","Available"};
        bookModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(bookModel); UITheme.styleTable(t);
        panel.add(UITheme.scrollPane(t), BorderLayout.CENTER);
        return panel;
    }

    private void loadBooks(String keyword) {
        SwingWorker<List<Map<String,Object>>,Void> w = new SwingWorker<>() {
            @Override protected List<Map<String,Object>> doInBackground() throws Exception {
                return keyword.isEmpty() ? libDAO.getAllBooks() : libDAO.searchBooks(keyword);
            }
            @Override protected void done() {
                try {
                    bookModel.setRowCount(0);
                    for (Map<String,Object> row : get())
                        bookModel.addRow(new Object[]{row.get("book_id"),row.get("title"),row.get("author"),
                            row.get("isbn"),row.get("category"),row.get("publisher"),
                            row.get("pub_year"),row.get("total"),row.get("available")});
                } catch (Exception e) { JOptionPane.showMessageDialog(null,e.getMessage()); }
            }
        };
        w.execute();
    }

    private JPanel buildIssuedBooks() {
        JPanel panel = new JPanel(new BorderLayout(0,8));
        panel.setOpaque(false);
        String[] cols = {"Issue ID","Title","Author","Borrower","Type","Issue Date","Due Date","Return Date","Fine","Status"};
        issuedModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(issuedModel); UITheme.styleTable(t);
        panel.add(UITheme.scrollPane(t), BorderLayout.CENTER);
        JButton refresh = UITheme.successButton("↺ Refresh");
        refresh.addActionListener(e -> loadIssuedBooks());
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bp.setOpaque(false); bp.add(refresh);
        panel.add(bp, BorderLayout.SOUTH);
        return panel;
    }

    private void loadIssuedBooks() {
        SwingWorker<List<Map<String,Object>>,Void> w = new SwingWorker<>() {
            @Override protected List<Map<String,Object>> doInBackground() throws Exception { return libDAO.getIssuedBooks(); }
            @Override protected void done() {
                try {
                    issuedModel.setRowCount(0);
                    for (Map<String,Object> row : get())
                        issuedModel.addRow(new Object[]{row.get("issue_id"),row.get("title"),row.get("author"),
                            row.get("borrower"),row.get("type"),row.get("issue_date"),row.get("due_date"),
                            row.get("return_date"),String.format("₹%.2f",(double)row.get("fine")),row.get("status")});
                } catch (Exception e) { JOptionPane.showMessageDialog(null,e.getMessage()); }
            }
        };
        w.execute();
    }

    private JPanel buildIssueReturn() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 16, 0));
        panel.setOpaque(false);

        JPanel issueCard = UITheme.cardPanel();
        issueCard.setLayout(new BoxLayout(issueCard, BoxLayout.Y_AXIS));
        issueCard.add(UITheme.sectionLabel("Issue Book"));
        issueCard.add(Box.createVerticalStrut(12));
        JTextField bookIdF    = UITheme.styledField(); bookIdF.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));
        JTextField studentIdF = UITheme.styledField(); studentIdF.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));
        JTextField dueDateF   = UITheme.styledField(); dueDateF.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));
        dueDateF.setText(LocalDate.now().plusDays(14).toString());
        JButton issueBtn = UITheme.primaryButton("Issue Book");
        issueCard.add(UITheme.formLabel("Book ID:")); issueCard.add(bookIdF); issueCard.add(Box.createVerticalStrut(8));
        issueCard.add(UITheme.formLabel("Student ID:")); issueCard.add(studentIdF); issueCard.add(Box.createVerticalStrut(8));
        issueCard.add(UITheme.formLabel("Due Date:")); issueCard.add(dueDateF); issueCard.add(Box.createVerticalStrut(12));
        issueCard.add(issueBtn);
        issueBtn.addActionListener(e -> {
            try {
                boolean ok = libDAO.issueBook(Integer.parseInt(bookIdF.getText().trim()),
                    studentIdF.getText().trim().isEmpty() ? null : Integer.parseInt(studentIdF.getText().trim()),
                    null, LocalDate.parse(dueDateF.getText().trim()));
                JOptionPane.showMessageDialog(panel, ok ? "Book issued!" : "No copies available!");
                loadIssuedBooks(); loadBooks("");
            } catch (Exception ex) { JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage()); }
        });

        JPanel returnCard = UITheme.cardPanel();
        returnCard.setLayout(new BoxLayout(returnCard, BoxLayout.Y_AXIS));
        returnCard.add(UITheme.sectionLabel("Return Book"));
        returnCard.add(Box.createVerticalStrut(12));
        JTextField issueIdF = UITheme.styledField(); issueIdF.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));
        JButton returnBtn = UITheme.successButton("Return Book");
        returnCard.add(UITheme.formLabel("Issue ID:")); returnCard.add(issueIdF);
        returnCard.add(Box.createVerticalStrut(12)); returnCard.add(returnBtn);
        returnBtn.addActionListener(e -> {
            try {
                boolean ok = libDAO.returnBook(Integer.parseInt(issueIdF.getText().trim()));
                JOptionPane.showMessageDialog(panel, ok ? "Book returned!" : "Issue ID not found!");
                loadIssuedBooks(); loadBooks("");
            } catch (Exception ex) { JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage()); }
        });

        panel.add(issueCard); panel.add(returnCard);
        return panel;
    }
}

// ── NoticesPanel ──────────────────────────────────────────────────────────────
class NoticesPanel extends JPanel {
    private final User currentUser;
    private final NoticeDAO noticeDAO = new NoticeDAO();
    private DefaultTableModel model;

    NoticesPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        buildUI();
        loadNotices();
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UITheme.sectionLabel("Notice & Announcement Board"), BorderLayout.WEST);
        JButton addBtn = UITheme.primaryButton("＋ Post Notice");
        addBtn.addActionListener(e -> showAddNoticeDialog());
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bp.setOpaque(false); bp.add(addBtn);
        top.add(bp, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        String[] cols = {"ID","Title","Target","Important","Posted By","Posted At","Expires"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(model); UITheme.styleTable(t);
        t.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && t.getSelectedRow() >= 0) {
                    int row = t.getSelectedRow();
                    JOptionPane.showMessageDialog(NoticesPanel.this,
                        "Title: " + model.getValueAt(row, 1) + "\n\nClick to view full content.");
                }
            }
        });
        add(UITheme.scrollPane(t), BorderLayout.CENTER);
    }

    private void loadNotices() {
        SwingWorker<List<Map<String,Object>>,Void> w = new SwingWorker<>() {
            @Override protected List<Map<String,Object>> doInBackground() throws Exception {
                return noticeDAO.getNoticesForRole("All");
            }
            @Override protected void done() {
                try {
                    model.setRowCount(0);
                    for (Map<String,Object> row : get())
                        model.addRow(new Object[]{row.get("notice_id"),row.get("title"),
                            row.get("target"),((boolean)row.get("important"))?"★ Yes":"No",
                            row.get("posted_by"),row.get("posted_at"),row.get("expires_at")});
                } catch (Exception e) { JOptionPane.showMessageDialog(null, e.getMessage()); }
            }
        };
        w.execute();
    }

    private void showAddNoticeDialog() {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), "Post Notice", Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(480, 400);
        d.setLocationRelativeTo(this);
        JPanel form = new JPanel(new BorderLayout(0,8));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20,24,20,24));

        JTextField titleF   = UITheme.styledField();
        JTextArea  contentF = new JTextArea(6, 30);
        contentF.setFont(UITheme.FONT_BODY);
        contentF.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        JTextField expF = UITheme.styledField(); expF.setText(LocalDate.now().plusDays(30).toString());
        JComboBox<String> targetCb = UITheme.styledCombo(new String[]{"All","Student","Teacher"});
        JCheckBox importantCb = new JCheckBox("Mark as Important");
        importantCb.setFont(UITheme.FONT_BODY);

        JPanel fields = new JPanel(new GridLayout(0,1,4,4));
        fields.setOpaque(false);
        fields.add(UITheme.formLabel("Title *")); fields.add(titleF);
        fields.add(UITheme.formLabel("Content *")); fields.add(new JScrollPane(contentF));
        fields.add(UITheme.formLabel("Target")); fields.add(targetCb);
        fields.add(UITheme.formLabel("Expires (YYYY-MM-DD)")); fields.add(expF);
        fields.add(importantCb);

        JButton save = UITheme.primaryButton("Post");
        JButton cancel = UITheme.dangerButton("Cancel");
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bp.setOpaque(false);
        bp.add(cancel); bp.add(save);

        form.add(fields, BorderLayout.CENTER);
        form.add(bp, BorderLayout.SOUTH);
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            try {
                LocalDate exp = expF.getText().trim().isEmpty() ? null : LocalDate.parse(expF.getText().trim());
                noticeDAO.addNotice(titleF.getText().trim(), contentF.getText().trim(),
                    currentUser.getUserId(), (String)targetCb.getSelectedItem(), importantCb.isSelected(), exp);
                JOptionPane.showMessageDialog(d, "Notice posted!");
                d.dispose(); loadNotices();
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage()); }
        });
        d.setContentPane(form);
        d.setVisible(true);
    }
}

// ── LeaveManagementPanel ──────────────────────────────────────────────────────
class LeaveManagementPanel extends JPanel {
    private final User currentUser;
    private DefaultTableModel model;

    LeaveManagementPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        buildUI();
        loadLeaves();
    }

    private void buildUI() {
        add(UITheme.sectionLabel("Leave Management"), BorderLayout.NORTH);
        String[] cols = {"Leave ID","Applicant","Role","Type","From","To","Reason","Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(model); UITheme.styleTable(t);
        add(UITheme.scrollPane(t), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);
        JButton approveBtn = UITheme.successButton("✓ Approve");
        JButton rejectBtn  = UITheme.dangerButton("✗ Reject");
        JButton refreshBtn = UITheme.accentButton("↺ Refresh");

        approveBtn.addActionListener(e -> updateLeaveStatus(t, "Approved"));
        rejectBtn.addActionListener(e  -> updateLeaveStatus(t, "Rejected"));
        refreshBtn.addActionListener(e -> loadLeaves());
        btnPanel.add(approveBtn); btnPanel.add(rejectBtn); btnPanel.add(refreshBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadLeaves() {
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT l.leave_id, l.applicant_id, l.applicant_role, l.leave_type, " +
                 "l.from_date, l.to_date, l.reason, l.status FROM leaves l ORDER BY l.applied_at DESC")) {
            model.setRowCount(0);
            while (rs.next())
                model.addRow(new Object[]{rs.getInt("leave_id"), rs.getInt("applicant_id"),
                    rs.getString("applicant_role"), rs.getString("leave_type"),
                    rs.getDate("from_date"), rs.getDate("to_date"),
                    rs.getString("reason"), rs.getString("status")});
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void updateLeaveStatus(JTable t, String status) {
        int row = t.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a leave application."); return; }
        int leaveId = (int) model.getValueAt(row, 0);
        try (PreparedStatement ps = DatabaseConfig.getConnection()
                .prepareStatement("UPDATE leaves SET status=?, reviewed_by=? WHERE leave_id=?")) {
            ps.setString(1, status);
            ps.setInt(2, currentUser.getUserId());
            ps.setInt(3, leaveId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Leave " + status.toLowerCase() + "!");
            loadLeaves();
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }
}

// ── BackupPanel ───────────────────────────────────────────────────────────────
class BackupPanel extends JPanel {
    private final User currentUser;
    private JTextArea logArea;

    BackupPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        buildUI();
    }

    private void buildUI() {
        add(UITheme.sectionLabel("Backup & Restore System"), BorderLayout.NORTH);

        JPanel centerCard = UITheme.cardPanel();
        centerCard.setLayout(new BoxLayout(centerCard, BoxLayout.Y_AXIS));

        JLabel info = new JLabel("<html>Use mysqldump to create database backups. Ensure MySQL binaries are in your system PATH.<br>" +
            "Default backup location: <b>./backups/</b></html>");
        info.setFont(UITheme.FONT_BODY);
        info.setForeground(UITheme.TEXT_SECONDARY);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);

        logArea = new JTextArea(10, 60);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setEditable(false);
        logArea.setBackground(new Color(0x1E1E2E));
        logArea.setForeground(new Color(0xA6E3A1));
        logArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JButton backupBtn  = UITheme.primaryButton("💾 Create Backup");
        JButton restoreBtn = UITheme.accentButton("📂 Restore Backup");
        backupBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        restoreBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        backupBtn.addActionListener(e -> doBackup());
        restoreBtn.addActionListener(e -> doRestore());

        centerCard.add(info);
        centerCard.add(Box.createVerticalStrut(16));
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(backupBtn); btnRow.add(restoreBtn);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerCard.add(btnRow);
        centerCard.add(Box.createVerticalStrut(16));
        centerCard.add(new JLabel("Backup Log:"));
        centerCard.add(Box.createVerticalStrut(4));
        centerCard.add(new JScrollPane(logArea));

        add(centerCard, BorderLayout.CENTER);
    }

    private void doBackup() {
        String dir = "backups";
        new File(dir).mkdirs();
        String filename = dir + "/backup_" + LocalDate.now() + "_" + System.currentTimeMillis() + ".sql";
        log("Starting backup to: " + filename);
        SwingWorker<Boolean, String> w = new SwingWorker<>() {
            @Override protected Boolean doInBackground() throws Exception {
                ProcessBuilder pb = new ProcessBuilder(
                    "mysqldump", "-u", "root", "-pyour_password_here", "smart_student_hub"
                );
                pb.redirectOutput(new File(filename));
                Process p = pb.start();
                return p.waitFor() == 0;
            }
            @Override protected void done() {
                try {
                    if (get()) {
                        log("✓ Backup successful: " + filename);
                        try (PreparedStatement ps = DatabaseConfig.getConnection()
                                .prepareStatement("INSERT INTO backup_log (backup_file, created_by) VALUES (?,?)")) {
                            ps.setString(1, filename);
                            ps.setInt(2, currentUser.getUserId());
                            ps.executeUpdate();
                        } catch (SQLException ignored) {}
                    } else {
                        log("✗ Backup failed. Check mysqldump is installed and credentials are correct.");
                    }
                } catch (Exception e) { log("✗ Error: " + e.getMessage()); }
            }
        };
        w.execute();
    }

    private void doRestore() {
        JFileChooser fc = new JFileChooser("backups");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filename = fc.getSelectedFile().getAbsolutePath();
            int confirm = JOptionPane.showConfirmDialog(this,
                "Restore from:\n" + filename + "\n\nThis will overwrite current data!", "Confirm Restore", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            log("Starting restore from: " + filename);
            SwingWorker<Boolean, Void> w = new SwingWorker<>() {
                @Override protected Boolean doInBackground() throws Exception {
                    ProcessBuilder pb = new ProcessBuilder(
                        "mysql", "-u", "root", "-pyour_password_here", "smart_student_hub"
                    );
                    pb.redirectInput(new File(filename));
                    return pb.start().waitFor() == 0;
                }
                @Override protected void done() {
                    try { log(get() ? "✓ Restore successful!" : "✗ Restore failed."); }
                    catch (Exception e) { log("✗ Error: " + e.getMessage()); }
                }
            };
            w.execute();
        }
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append("[" + java.time.LocalTime.now().withNano(0) + "] " + msg + "\n"));
    }
}
