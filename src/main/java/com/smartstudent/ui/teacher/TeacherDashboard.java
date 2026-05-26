package com.smartstudent.ui.teacher;

import java.sql.Date;
import com.smartstudent.dao.*;
import com.smartstudent.model.*;
import com.smartstudent.ui.common.LoginScreen;
import com.smartstudent.util.UITheme;
import com.smartstudent.config.DatabaseConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class TeacherDashboard extends JFrame {

    private final User    currentUser;
    private       Teacher teacher;
    private       JPanel  contentArea;
    private       JLabel  pageTitle;
    private       JButton activeBtn;

    public TeacherDashboard(User user) {
        this.currentUser = user;
        UITheme.applyGlobalLook();
        setTitle("Smart Student Hub — Teacher Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 680));
        loadTeacherProfile();
        buildUI();
        showHome();
        setVisible(true);
    }

    private void loadTeacherProfile() {
        try {
            teacher = new TeacherDAO().getByUserId(currentUser.getUserId());
        } catch (SQLException e) {
            teacher = null;
        }
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(UITheme.BG_MAIN);
        contentArea.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        root.add(contentArea, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setPreferredSize(new Dimension(0, 56));
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(0, 20, 0, 20)));

        pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(UITheme.FONT_HEADING);
        pageTitle.setForeground(UITheme.PRIMARY);
        bar.add(pageTitle, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        String name = teacher != null ? teacher.getFullName() : currentUser.getUsername();
        JLabel userLbl = new JLabel("👩‍🏫 " + name + "  [Teacher]");
        userLbl.setFont(UITheme.FONT_BODY);
        userLbl.setForeground(UITheme.TEXT_SECONDARY);

        JButton logout = new JButton("Logout");
        logout.setFont(UITheme.FONT_SMALL);
        logout.setForeground(UITheme.DANGER);
        logout.setBackground(new Color(255, 235, 235));
        logout.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        logout.setFocusPainted(false);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Logout?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) { dispose(); new LoginScreen(); }
        });
        right.add(userLbl); right.add(logout);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JScrollPane buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.BG_SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        JLabel logo = new JLabel("  SSH Teacher");
        logo.setFont(UITheme.FONT_SUBHEAD);
        logo.setForeground(UITheme.ACCENT);
        logo.setBorder(BorderFactory.createEmptyBorder(8, 20, 16, 0));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logo);

        addSection(sidebar, "OVERVIEW");
        addSidebarBtn(sidebar, "🏠", "Dashboard",     e -> showHome());
        addSidebarBtn(sidebar, "👤", "My Profile",    e -> showMyProfile());

        addSection(sidebar, "CLASSROOM");
        addSidebarBtn(sidebar, "📅", "Mark Attendance", e -> showAttendance());
        addSidebarBtn(sidebar, "📋", "Assignments",     e -> showAssignments());
        addSidebarBtn(sidebar, "📝", "Enter Marks",     e -> showMarksEntry());
        addSidebarBtn(sidebar, "🕐", "My Timetable",   e -> showTimetable());

        addSection(sidebar, "RECORDS");
        addSidebarBtn(sidebar, "🏆", "Student Results", e -> showResults());
        addSidebarBtn(sidebar, "📊", "Attendance Report", e -> showAttReport());

        addSection(sidebar, "COMMUNICATION");
        addSidebarBtn(sidebar, "📢", "Notices",         e -> showNotices());
        addSidebarBtn(sidebar, "🏖️", "Apply Leave",    e -> showLeave());

        sidebar.add(Box.createVerticalGlue());

        JScrollPane sp = new JScrollPane(sidebar);
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return sp;
    }

    private void addSection(JPanel parent, String title) {
        JLabel lbl = new JLabel("  " + title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(150, 170, 220));
        lbl.setBorder(BorderFactory.createEmptyBorder(16, 0, 4, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
    }

    private void addSidebarBtn(JPanel parent, String icon, String label, ActionListener action) {
        JButton btn = new JButton(icon + "  " + label);
        btn.setFont(UITheme.FONT_SIDEBAR);
        btn.setForeground(new Color(200, 215, 255));
        btn.setBackground(UITheme.BG_SIDEBAR);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(220, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != activeBtn) { btn.setBackground(UITheme.SIDEBAR_HOVER); btn.setForeground(Color.WHITE); }
            }
            public void mouseExited(MouseEvent e) {
                if (btn != activeBtn) { btn.setBackground(UITheme.BG_SIDEBAR); btn.setForeground(new Color(200,215,255)); }
            }
        });
        btn.addActionListener(e -> { setActive(btn, label); action.actionPerformed(e); });
        parent.add(btn);
    }

    private void setActive(JButton btn, String title) {
        if (activeBtn != null) { activeBtn.setBackground(UITheme.BG_SIDEBAR); activeBtn.setForeground(new Color(200,215,255)); }
        activeBtn = btn;
        btn.setBackground(UITheme.SIDEBAR_ACTIVE);
        btn.setForeground(Color.WHITE);
        pageTitle.setText(title);
    }

    private void setContent(JPanel panel) {
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    // ── Dashboard Home ───────────────────────────────────────────────────────
    private void showHome() {
        pageTitle.setText("Dashboard");
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        String name = teacher != null ? teacher.getFullName() : currentUser.getUsername();
        JLabel welcome = new JLabel("Welcome, " + name + " 👋");
        welcome.setFont(UITheme.FONT_TITLE);
        welcome.setForeground(UITheme.PRIMARY);
        panel.add(welcome, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setOpaque(false);
        cards.add(UITheme.statCard("Department",    teacher != null ? teacher.getDeptName() : "—",    UITheme.PRIMARY_LIGHT));
        cards.add(UITheme.statCard("Employee ID",   teacher != null ? teacher.getEmployeeId() : "—",  UITheme.SUCCESS));
        cards.add(UITheme.statCard("Qualification", teacher != null ? teacher.getQualification() : "—", UITheme.ACCENT));
        panel.add(cards, BorderLayout.CENTER);

        JPanel quickCard = UITheme.cardPanel();
        quickCard.setLayout(new BorderLayout(0, 8));
        quickCard.add(UITheme.sectionLabel("Quick Actions"), BorderLayout.NORTH);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        actions.setOpaque(false);
        String[][] qa = {{"📅 Mark Attendance","att"},{"📋 New Assignment","asgn"},
                         {"📝 Enter Marks","marks"},{"📢 View Notices","notices"}};
        for (String[] q : qa) {
            JButton b = UITheme.accentButton(q[0]);
            b.setPreferredSize(new Dimension(180, 40));
            actions.add(b);
        }
        quickCard.add(actions, BorderLayout.CENTER);
        panel.add(quickCard, BorderLayout.SOUTH);
        setContent(panel);
    }

    // ── My Profile ───────────────────────────────────────────────────────────
    private void showMyProfile() {
        JPanel panel = UITheme.cardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        if (teacher == null) { panel.add(new JLabel("Profile not found.")); setContent(panel); return; }

        panel.add(UITheme.sectionLabel("My Profile"));
        panel.add(Box.createVerticalStrut(16));

        String[][] fields = {
            {"Employee ID", teacher.getEmployeeId()}, {"Full Name", teacher.getFullName()},
            {"Date of Birth", teacher.getDob() != null ? teacher.getDob().toString() : "—"},
            {"Gender", teacher.getGender()}, {"Email", teacher.getEmail()},
            {"Phone", teacher.getPhone()}, {"Address", teacher.getAddress()},
            {"Department", teacher.getDeptName()}, {"Qualification", teacher.getQualification()},
            {"Joining Date", teacher.getJoiningDate() != null ? teacher.getJoiningDate().toString() : "—"}
        };
        for (String[] f : fields) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            JLabel key = new JLabel(f[0] + ":"); key.setFont(UITheme.FONT_SMALL); key.setForeground(UITheme.TEXT_SECONDARY);
            key.setPreferredSize(new Dimension(140, 24));
            JLabel val = new JLabel(f[1] != null ? f[1] : "—"); val.setFont(UITheme.FONT_BODY);
            row.add(key, BorderLayout.WEST); row.add(val, BorderLayout.CENTER);
            panel.add(row);
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(panel, BorderLayout.NORTH);
        setContent(wrapper);
    }

    // ── Mark Attendance ──────────────────────────────────────────────────────
    private void showAttendance() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setOpaque(false);
        top.add(UITheme.sectionLabel("Mark Attendance"));

        JTextField subIdF = UITheme.styledField(); subIdF.setPreferredSize(new Dimension(80, 34));
        JTextField dateF  = UITheme.styledField(); dateF.setPreferredSize(new Dimension(130, 34));
        dateF.setText(LocalDate.now().toString());
        JButton loadBtn = UITheme.accentButton("Load Students");
        JButton saveBtn = UITheme.primaryButton("Save");

        top.add(UITheme.formLabel("Subject ID:")); top.add(subIdF);
        top.add(UITheme.formLabel("Date:")); top.add(dateF);
        top.add(loadBtn); top.add(saveBtn);
        panel.add(top, BorderLayout.NORTH);

        String[] cols = {"Student ID", "Admission No", "Name", "Status"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 3; }
        };
        JTable tbl = new JTable(mdl);
        UITheme.styleTable(tbl);
        JComboBox<String> statusCb = new JComboBox<>(new String[]{"Present","Absent","Late","Excused"});
        tbl.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(statusCb));
        panel.add(UITheme.scrollPane(tbl), BorderLayout.CENTER);

        int markedBy = teacher != null ? teacher.getTeacherId() : 1;

        loadBtn.addActionListener(e -> {
            try {
                int subId   = Integer.parseInt(subIdF.getText().trim());
                LocalDate d = LocalDate.parse(dateF.getText().trim());
                List<Map<String,Object>> rows = new AttendanceDAO().getAttendanceByDate(subId, d);
                mdl.setRowCount(0);
                for (Map<String,Object> row : rows)
                    mdl.addRow(new Object[]{row.get("student_id"), row.get("admission_no"), row.get("full_name"), row.get("status")});
            } catch (Exception ex) { JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage()); }
        });

        saveBtn.addActionListener(e -> {
            try {
                int subId   = Integer.parseInt(subIdF.getText().trim());
                LocalDate d = LocalDate.parse(dateF.getText().trim());
                AttendanceDAO dao = new AttendanceDAO();
                for (int r = 0; r < mdl.getRowCount(); r++)
                    dao.markAttendance((int)mdl.getValueAt(r,0), subId, d, (String)mdl.getValueAt(r,3), markedBy);
                JOptionPane.showMessageDialog(panel, "Attendance saved!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage()); }
        });

        setContent(panel);
    }

    // ── Assignments ──────────────────────────────────────────────────────────
    private void showAssignments() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UITheme.sectionLabel("Assignments"), BorderLayout.WEST);
        JButton addBtn = UITheme.primaryButton("＋ New Assignment");
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bp.setOpaque(false); bp.add(addBtn);
        top.add(bp, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        String[] cols = {"ID","Title","Subject","Due Date","Max Marks","Submissions"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(mdl); UITheme.styleTable(tbl);
        panel.add(UITheme.scrollPane(tbl), BorderLayout.CENTER);

        if (teacher != null) {
            SwingWorker<List<Map<String,Object>>,Void> w = new SwingWorker<>() {
                @Override protected List<Map<String,Object>> doInBackground() throws Exception {
                    return new AssignmentDAO().getByTeacher(teacher.getTeacherId());
                }
                @Override protected void done() {
                    try {
                        for (Map<String,Object> row : get())
                            mdl.addRow(new Object[]{row.get("assignment_id"),row.get("title"),
                                row.get("subject_name"),row.get("due_date"),row.get("max_marks"),row.get("submissions")});
                    } catch (Exception e) { JOptionPane.showMessageDialog(null, e.getMessage()); }
                }
            };
            w.execute();
        }

        addBtn.addActionListener(e -> showAddAssignmentDialog(mdl));
        setContent(panel);
    }

    private void showAddAssignmentDialog(DefaultTableModel mdl) {
        JDialog d = new JDialog(this, "New Assignment", Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(440, 380);
        d.setLocationRelativeTo(this);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20,24,20,24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,4,6,4); gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField titleF   = UITheme.styledField();
        JTextField subIdF   = UITheme.styledField();
        JTextField dueDateF = UITheme.styledField(); dueDateF.setText(LocalDate.now().plusDays(7).toString());
        JTextField maxF     = UITheme.styledField(); maxF.setText("10");
        JTextArea  descF    = new JTextArea(3,20); descF.setFont(UITheme.FONT_BODY);
        descF.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        Object[][] rows = {{"Title*",titleF},{"Subject ID*",subIdF},{"Due Date",dueDateF},{"Max Marks",maxF}};
        for (int i = 0; i < rows.length; i++) {
            gbc.gridx=0; gbc.gridy=i; gbc.weightx=0; form.add(UITheme.formLabel((String)rows[i][0]),gbc);
            gbc.gridx=1; gbc.weightx=1; form.add((Component)rows[i][1],gbc);
        }
        gbc.gridx=0; gbc.gridy=rows.length; form.add(UITheme.formLabel("Description"),gbc);
        gbc.gridx=1; form.add(new JScrollPane(descF),gbc);

        JButton save = UITheme.primaryButton("Save");
        JButton cancel = UITheme.dangerButton("Cancel");
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bp.setOpaque(false); bp.add(cancel); bp.add(save);
        gbc.gridx=0; gbc.gridy=rows.length+1; gbc.gridwidth=2; form.add(bp,gbc);
        cancel.addActionListener(e -> d.dispose());

        save.addActionListener(e -> {
            try {
                new AssignmentDAO().addAssignment(titleF.getText().trim(), descF.getText().trim(),
                    Integer.parseInt(subIdF.getText().trim()), teacher.getTeacherId(),
                    LocalDate.parse(dueDateF.getText().trim()), Integer.parseInt(maxF.getText().trim()));
                JOptionPane.showMessageDialog(d, "Assignment posted!");
                d.dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage()); }
        });
        d.setContentPane(form);
        d.setVisible(true);
    }

    // ── Enter Marks ──────────────────────────────────────────────────────────
    private void showMarksEntry() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.add(UITheme.sectionLabel("Enter Student Marks"), BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);
        JTextField examIdF = UITheme.styledField(); examIdF.setPreferredSize(new Dimension(90, 34));
        JButton loadBtn = UITheme.accentButton("Load Students");
        top.add(UITheme.formLabel("Exam ID:")); top.add(examIdF); top.add(loadBtn);
        panel.add(top, BorderLayout.NORTH);

        String[] cols = {"Student ID","Admission No","Name","Marks"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 3; }
        };
        JTable tbl = new JTable(mdl); UITheme.styleTable(tbl);

        JButton saveBtn = UITheme.primaryButton("Save All Marks");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bottom.setOpaque(false);
        bottom.add(saveBtn);
        panel.add(UITheme.scrollPane(tbl), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> {
            try {
                String examIdStr = examIdF.getText().trim();
                if (examIdStr.isEmpty()) return;
                int examId = Integer.parseInt(examIdStr);
                PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(
                    "SELECT s.student_id, s.admission_no, s.full_name, " +
                    "COALESCE(r.marks,'') AS marks " +
                    "FROM students s " +
                    "LEFT JOIN results r ON r.student_id=s.student_id AND r.exam_id=? " +
                    "WHERE s.is_active=TRUE ORDER BY s.full_name");
                ps.setInt(1, examId);
                ResultSet rs = ps.executeQuery();
                mdl.setRowCount(0);
                while (rs.next())
                    mdl.addRow(new Object[]{rs.getInt("student_id"),rs.getString("admission_no"),
                        rs.getString("full_name"), rs.getString("marks")});
            } catch (Exception ex) { JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage()); }
        });

        saveBtn.addActionListener(e -> {
            try {
                String examIdStr = examIdF.getText().trim();
                if (examIdStr.isEmpty()) { JOptionPane.showMessageDialog(panel, "Enter exam ID first."); return; }
                int examId = Integer.parseInt(examIdStr);
                PreparedStatement psMeta = DatabaseConfig.getConnection().prepareStatement(
                    "SELECT max_marks, pass_marks FROM exams WHERE exam_id=?");
                psMeta.setInt(1, examId);
                ResultSet meta = psMeta.executeQuery();
                if (!meta.next()) { JOptionPane.showMessageDialog(panel, "Exam not found."); return; }
                int maxMarks = meta.getInt("max_marks"), passMarks = meta.getInt("pass_marks");
                ResultDAO dao = new ResultDAO();
                int saved = 0;
                for (int r = 0; r < mdl.getRowCount(); r++) {
                    Object marksObj = mdl.getValueAt(r, 3);
                    if (marksObj == null || marksObj.toString().isEmpty()) continue;
                    double marks = Double.parseDouble(marksObj.toString().trim());
                    int studentId = (int) mdl.getValueAt(r, 0);
                    String grade  = ResultDAO.calculateGrade(marks, maxMarks);
                    dao.saveResult(studentId, examId, marks, grade, marks >= passMarks);
                    saved++;
                }
                JOptionPane.showMessageDialog(panel, "Saved marks for " + saved + " students.");
            } catch (Exception ex) { JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage()); }
        });

        setContent(panel);
    }

    // ── My Timetable ─────────────────────────────────────────────────────────
    private void showTimetable() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.add(UITheme.sectionLabel("My Timetable"), BorderLayout.NORTH);

        String[] cols = {"Day","Start Time","End Time","Subject","Course","Semester","Room"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(mdl); UITheme.styleTable(tbl);
        panel.add(UITheme.scrollPane(tbl), BorderLayout.CENTER);

        if (teacher != null) {
            try {
                PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(
                    "SELECT tt.day_of_week, tt.start_time, tt.end_time, sub.subject_name, " +
                    "c.course_name, tt.semester, COALESCE(cr.room_name,'—') AS room " +
                    "FROM timetable tt " +
                    "JOIN subjects sub ON tt.subject_id=sub.subject_id " +
                    "JOIN courses c ON tt.course_id=c.course_id " +
                    "LEFT JOIN classrooms cr ON tt.room_id=cr.room_id " +
                    "WHERE tt.teacher_id=? ORDER BY FIELD(tt.day_of_week," +
                    "'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'), tt.start_time");
                ps.setInt(1, teacher.getTeacherId());
                ResultSet rs = ps.executeQuery();
                while (rs.next())
                    mdl.addRow(new Object[]{rs.getString("day_of_week"), rs.getTime("start_time"),
                        rs.getTime("end_time"), rs.getString("subject_name"),
                        rs.getString("course_name"), rs.getInt("semester"), rs.getString("room")});
            } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
        }
        setContent(panel);
    }

    // ── Student Results ───────────────────────────────────────────────────────
    private void showResults() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);
        top.add(UITheme.sectionLabel("Student Results"));
        JTextField examIdF = UITheme.styledField(); examIdF.setPreferredSize(new Dimension(90,34));
        JButton loadBtn = UITheme.accentButton("Load");
        top.add(UITheme.formLabel("Exam ID:")); top.add(examIdF); top.add(loadBtn);
        panel.add(top, BorderLayout.NORTH);

        String[] cols = {"Rank","Admission No","Name","Marks","Grade","Pass"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(mdl); UITheme.styleTable(tbl);
        panel.add(UITheme.scrollPane(tbl), BorderLayout.CENTER);

        loadBtn.addActionListener(e -> {
            try {
                int examId = Integer.parseInt(examIdF.getText().trim());
                List<Map<String,Object>> rows = new ResultDAO().getResultsByExam(examId);
                mdl.setRowCount(0);
                int rank = 1;
                for (Map<String,Object> row : rows)
                    mdl.addRow(new Object[]{rank++, row.get("admission_no"), row.get("full_name"),
                        row.get("marks"), row.get("grade"), ((boolean)row.get("is_pass"))?"✓ Pass":"✗ Fail"});
            } catch (Exception ex) { JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage()); }
        });
        setContent(panel);
    }

    // ── Attendance Report ─────────────────────────────────────────────────────
    private void showAttReport() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);
        top.add(UITheme.sectionLabel("Attendance Report"));
        JTextField subIdF  = UITheme.styledField(); subIdF.setPreferredSize(new Dimension(80, 34));
        JTextField monthF  = UITheme.styledField(); monthF.setPreferredSize(new Dimension(60, 34));
        monthF.setText(String.valueOf(LocalDate.now().getMonthValue()));
        JTextField yearF   = UITheme.styledField(); yearF.setPreferredSize(new Dimension(80, 34));
        yearF.setText(String.valueOf(LocalDate.now().getYear()));
        JButton loadBtn = UITheme.accentButton("Generate");
        top.add(UITheme.formLabel("Subject ID:")); top.add(subIdF);
        top.add(UITheme.formLabel("Month:")); top.add(monthF);
        top.add(UITheme.formLabel("Year:")); top.add(yearF);
        top.add(loadBtn);
        panel.add(top, BorderLayout.NORTH);

        String[] cols = {"Student ID","Admission No","Name","Total Days","Present","Attendance %","Warning"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(mdl); UITheme.styleTable(tbl);
        panel.add(UITheme.scrollPane(tbl), BorderLayout.CENTER);

        loadBtn.addActionListener(e -> {
            try {
                int subId = Integer.parseInt(subIdF.getText().trim());
                int month = Integer.parseInt(monthF.getText().trim());
                int year  = Integer.parseInt(yearF.getText().trim());
                List<Map<String,Object>> students = new AttendanceDAO().getLowAttendanceStudents(subId, 0);
                mdl.setRowCount(0);
                for (Map<String,Object> row : students) {
                    int total   = (int) row.get("total");
                    int present = (int) row.get("present");
                    double pct  = total > 0 ? present * 100.0 / total : 0;
                    mdl.addRow(new Object[]{row.get("student_id"), row.get("admission_no"),
                        row.get("full_name"), total, present,
                        String.format("%.1f%%", pct), pct < 75 ? "⚠ Low" : "OK"});
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage()); }
        });
        setContent(panel);
    }

    // ── Notices ───────────────────────────────────────────────────────────────
    private void showNotices() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.add(UITheme.sectionLabel("Notice Board"), BorderLayout.NORTH);

        String[] cols = {"Title","Target","Important","Posted By","Posted At"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(mdl); UITheme.styleTable(tbl);
        panel.add(UITheme.scrollPane(tbl), BorderLayout.CENTER);

        SwingWorker<List<Map<String,Object>>,Void> w = new SwingWorker<>() {
            @Override protected List<Map<String,Object>> doInBackground() throws Exception {
                return new NoticeDAO().getNoticesForRole("Teacher");
            }
            @Override protected void done() {
                try {
                    for (Map<String,Object> row : get())
                        mdl.addRow(new Object[]{row.get("title"), row.get("target"),
                            ((boolean)row.get("important")) ? "★ Yes" : "No",
                            row.get("posted_by"), row.get("posted_at")});
                } catch (Exception e) { JOptionPane.showMessageDialog(null, e.getMessage()); }
            }
        };
        w.execute();

        JButton postBtn = UITheme.primaryButton("＋ Post Notice");
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bp.setOpaque(false); bp.add(postBtn);
        panel.add(bp, BorderLayout.SOUTH);
        postBtn.addActionListener(e -> showPostNoticeDialog(mdl));
        setContent(panel);
    }

    private void showPostNoticeDialog(DefaultTableModel mdl) {
        JDialog d = new JDialog(this, "Post Notice", Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(440, 320);
        d.setLocationRelativeTo(this);
        JPanel form = new JPanel(new BorderLayout(0, 8));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JTextField titleF  = UITheme.styledField();
        JTextArea  contentF = new JTextArea(5, 30); contentF.setFont(UITheme.FONT_BODY);
        contentF.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        JPanel fields = new JPanel(new GridLayout(0,1,4,4)); fields.setOpaque(false);
        fields.add(UITheme.formLabel("Title *")); fields.add(titleF);
        fields.add(UITheme.formLabel("Content *")); fields.add(new JScrollPane(contentF));
        JButton save = UITheme.primaryButton("Post"); JButton cancel = UITheme.dangerButton("Cancel");
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bp.setOpaque(false); bp.add(cancel); bp.add(save);
        form.add(fields, BorderLayout.CENTER); form.add(bp, BorderLayout.SOUTH);
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            try {
                new NoticeDAO().addNotice(titleF.getText().trim(), contentF.getText().trim(),
                    currentUser.getUserId(), "Student", false, LocalDate.now().plusDays(30));
                JOptionPane.showMessageDialog(d, "Notice posted!");
                d.dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage()); }
        });
        d.setContentPane(form);
        d.setVisible(true);
    }

    // ── Apply Leave ───────────────────────────────────────────────────────────
    private void showLeave() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        JPanel card = UITheme.cardPanel();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(450, 340));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,4,8,4); gbc.fill = GridBagConstraints.HORIZONTAL;

        card.add(UITheme.sectionLabel("Apply for Leave"), gbc); gbc.gridy=1;

        JTextField fromF   = UITheme.styledField(); fromF.setText(LocalDate.now().toString());
        JTextField toF     = UITheme.styledField(); toF.setText(LocalDate.now().toString());
        JTextField reasonF = UITheme.styledField();
        JComboBox<String> typeCb = UITheme.styledCombo(new String[]{"Personal","Medical","Emergency","Other"});

        Object[][] rows = {{"From Date",fromF},{"To Date",toF},{"Leave Type",typeCb},{"Reason",reasonF}};
        for (int i = 0; i < rows.length; i++) {
            gbc.gridx=0; gbc.gridy=i+1; gbc.weightx=0; card.add(UITheme.formLabel((String)rows[i][0]),gbc);
            gbc.gridx=1; gbc.weightx=1; card.add((Component)rows[i][1],gbc);
        }
        JButton submitBtn = UITheme.primaryButton("Submit Application");
        gbc.gridx=0; gbc.gridy=rows.length+1; gbc.gridwidth=2; card.add(submitBtn,gbc);

        submitBtn.addActionListener(e -> {
            try {
                PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(
                    "INSERT INTO leaves (applicant_id, applicant_role, leave_type, from_date, to_date, reason) VALUES (?,?,?,?,?,?)");
                ps.setInt(1, currentUser.getUserId());
                ps.setString(2, "Teacher");
                ps.setString(3, (String)typeCb.getSelectedItem());
                ps.setDate(4, Date.valueOf(fromF.getText().trim()));
                ps.setDate(5, Date.valueOf(toF.getText().trim()));
                ps.setString(6, reasonF.getText().trim());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Leave application submitted!");
                fromF.setText(LocalDate.now().toString()); toF.setText(LocalDate.now().toString()); reasonF.setText("");
            } catch (Exception ex) { JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage()); }
        });
        panel.add(card);
        setContent(panel);
    }
}
