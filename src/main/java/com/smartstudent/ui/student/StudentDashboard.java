package com.smartstudent.ui.student;

import com.smartstudent.config.DatabaseConfig;
import com.smartstudent.dao.*;
import com.smartstudent.model.*;
import com.smartstudent.ui.common.LoginScreen;
import com.smartstudent.util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class StudentDashboard extends JFrame {

    private final User    currentUser;
    private       Student student;
    private       JPanel  contentArea;
    private       JLabel  pageTitle;
    private       JButton activeBtn;

    public StudentDashboard(User user) {
        this.currentUser = user;
        UITheme.applyGlobalLook();
        setTitle("Smart Student Hub — Student Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 650));
        loadStudentProfile();
        buildUI();
        showHome();
        setVisible(true);
    }

    private void loadStudentProfile() {
        try {
            student = new StudentDAO().getByUserId(currentUser.getUserId());
        } catch (SQLException e) {
            student = null;
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
        String name = student != null ? student.getFullName() : currentUser.getUsername();
        JLabel userLbl = new JLabel("👨‍🎓 " + name + "  [Student]");
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

        JLabel logo = new JLabel("  SSH Student");
        logo.setFont(UITheme.FONT_SUBHEAD);
        logo.setForeground(UITheme.ACCENT);
        logo.setBorder(BorderFactory.createEmptyBorder(8, 20, 16, 0));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logo);

        addSection(sidebar, "OVERVIEW");
        addSidebarBtn(sidebar, "🏠", "Dashboard",         e -> showHome());
        addSidebarBtn(sidebar, "👤", "My Profile",        e -> showProfile());

        addSection(sidebar, "ACADEMICS");
        addSidebarBtn(sidebar, "📅", "My Attendance",     e -> showAttendance());
        addSidebarBtn(sidebar, "📝", "My Results",        e -> showResults());
        addSidebarBtn(sidebar, "🕐", "Timetable",        e -> showTimetable());
        addSidebarBtn(sidebar, "📋", "Assignments",       e -> showAssignments());

        addSection(sidebar, "FINANCE");
        addSidebarBtn(sidebar, "💰", "Fee Details",       e -> showFees());

        addSection(sidebar, "SERVICES");
        addSidebarBtn(sidebar, "📖", "Library",           e -> showLibrary());
        addSidebarBtn(sidebar, "📢", "Notices",           e -> showNotices());
        addSidebarBtn(sidebar, "🏖️", "Apply Leave",      e -> showLeave());
        addSidebarBtn(sidebar, "🎉", "Events",            e -> showEvents());
        addSidebarBtn(sidebar, "🔔", "Notifications",    e -> showNotifications());

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

    // ── Home ─────────────────────────────────────────────────────────────────
    private void showHome() {
        pageTitle.setText("Dashboard");
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        String name = student != null ? student.getFullName() : currentUser.getUsername();
        JLabel welcome = new JLabel("Welcome, " + name + " 👋");
        welcome.setFont(UITheme.FONT_TITLE);
        welcome.setForeground(UITheme.PRIMARY);
        panel.add(welcome, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 4, 16, 0));
        cards.setOpaque(false);
        if (student != null) {
            cards.add(UITheme.statCard("Course",     student.getCourseName(),                       UITheme.PRIMARY_LIGHT));
            cards.add(UITheme.statCard("Semester",   "Semester " + student.getCurrentSemester(),     UITheme.SUCCESS));
            cards.add(UITheme.statCard("Admission",  student.getAdmissionNo(),                       UITheme.ACCENT));
            cards.add(UITheme.statCard("Status",     student.isActive() ? "Active" : "Inactive",     UITheme.WARNING));
        }
        panel.add(cards, BorderLayout.CENTER);

        // Notifications preview
        JPanel notifCard = UITheme.cardPanel();
        notifCard.setLayout(new BorderLayout(0, 8));
        notifCard.add(UITheme.sectionLabel("Recent Notifications"), BorderLayout.NORTH);
        JTextArea notifArea = new JTextArea(4, 40);
        notifArea.setFont(UITheme.FONT_BODY);
        notifArea.setEditable(false);
        notifArea.setBackground(UITheme.BG_CARD);
        notifArea.setBorder(null);

        if (student != null) {
            SwingWorker<Void, Void> w = new SwingWorker<>() {
                @Override protected Void doInBackground() throws Exception {
                    PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(
                        "SELECT message FROM notifications WHERE user_id=? AND is_read=FALSE ORDER BY created_at DESC LIMIT 5");
                    ps.setInt(1, currentUser.getUserId());
                    ResultSet rs = ps.executeQuery();
                    StringBuilder sb = new StringBuilder();
                    int i = 1;
                    while (rs.next()) sb.append(i++).append(". ").append(rs.getString("message")).append("\n\n");
                    if (sb.length() == 0) sb.append("No new notifications.");
                    notifArea.setText(sb.toString());
                    return null;
                }
            };
            w.execute();
        }
        notifCard.add(new JScrollPane(notifArea), BorderLayout.CENTER);
        panel.add(notifCard, BorderLayout.SOUTH);
        setContent(panel);
    }

    // ── My Profile ───────────────────────────────────────────────────────────
    private void showProfile() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JPanel card = UITheme.cardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        if (student == null) { card.add(new JLabel("Profile not available.")); panel.add(card, BorderLayout.NORTH); setContent(panel); return; }

        JLabel nameL = new JLabel(student.getFullName());
        nameL.setFont(UITheme.FONT_HEADING); nameL.setForeground(UITheme.PRIMARY);
        nameL.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel admL = new JLabel(student.getAdmissionNo());
        admL.setFont(UITheme.FONT_SMALL); admL.setForeground(UITheme.TEXT_SECONDARY);
        admL.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(8)); card.add(nameL);
        card.add(Box.createVerticalStrut(4)); card.add(admL);
        card.add(Box.createVerticalStrut(16));
        card.add(new JSeparator()); card.add(Box.createVerticalStrut(12));

        String[][] fields = {
            {"Course",       student.getCourseName()},
            {"Semester",     String.valueOf(student.getCurrentSemester())},
            {"Date of Birth",student.getDob() != null ? student.getDob().toString() : "—"},
            {"Gender",       student.getGender()},
            {"Email",        student.getEmail()},
            {"Phone",        student.getPhone()},
            {"Address",      student.getAddress()},
            {"Blood Group",  student.getBloodGroup()},
            {"Guardian",     student.getGuardianName()},
            {"Guardian Ph",  student.getGuardianPhone()},
            {"Admission Date",student.getAdmissionDate() != null ? student.getAdmissionDate().toString() : "—"}
        };
        for (String[] f : fields) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false); row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            JLabel key = new JLabel(f[0] + ":"); key.setFont(UITheme.FONT_SMALL); key.setForeground(UITheme.TEXT_SECONDARY); key.setPreferredSize(new Dimension(130,22));
            JLabel val = new JLabel(f[1] != null ? f[1] : "—"); val.setFont(UITheme.FONT_BODY);
            row.add(key, BorderLayout.WEST); row.add(val, BorderLayout.CENTER);
            card.add(row);
        }
        panel.add(card, BorderLayout.NORTH);
        setContent(panel);
    }

    // ── My Attendance ─────────────────────────────────────────────────────────
    private void showAttendance() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);
        top.add(UITheme.sectionLabel("My Attendance"));
        JTextField monthF = UITheme.styledField(); monthF.setPreferredSize(new Dimension(50, 34));
        monthF.setText(String.valueOf(LocalDate.now().getMonthValue()));
        JTextField yearF  = UITheme.styledField(); yearF.setPreferredSize(new Dimension(80, 34));
        yearF.setText(String.valueOf(LocalDate.now().getYear()));
        JButton loadBtn = UITheme.accentButton("Generate Report");
        top.add(UITheme.formLabel("Month:")); top.add(monthF);
        top.add(UITheme.formLabel("Year:")); top.add(yearF); top.add(loadBtn);
        panel.add(top, BorderLayout.NORTH);

        String[] cols = {"Subject Code","Subject Name","Total Classes","Present","Attendance %","Status"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(mdl); UITheme.styleTable(tbl);

        // colour rows with low attendance red
        tbl.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                Object warn = mdl.getValueAt(row, 5);
                if (!sel && warn != null && warn.toString().startsWith("⚠")) {
                    setBackground(new Color(255, 235, 235));
                    setForeground(UITheme.DANGER);
                } else if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : UITheme.TABLE_ALT);
                    setForeground(UITheme.TEXT_PRIMARY);
                }
                setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
                return this;
            }
        });

        panel.add(UITheme.scrollPane(tbl), BorderLayout.CENTER);

        loadBtn.addActionListener(e -> {
            if (student == null) return;
            try {
                int month = Integer.parseInt(monthF.getText().trim());
                int year  = Integer.parseInt(yearF.getText().trim());
                List<Map<String,Object>> rows = new AttendanceDAO().getMonthlySummary(student.getStudentId(), month, year);
                mdl.setRowCount(0);
                for (Map<String,Object> row : rows) {
                    boolean warn = (boolean) row.get("warning");
                    mdl.addRow(new Object[]{row.get("subject_code"), row.get("subject_name"),
                        row.get("total"), row.get("present"), row.get("percent"),
                        warn ? "⚠ Low Attendance" : "✓ OK"});
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage()); }
        });

        if (student != null) loadBtn.doClick();
        setContent(panel);
    }

    // ── My Results ────────────────────────────────────────────────────────────
    private void showResults() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.add(UITheme.sectionLabel("My Exam Results"), BorderLayout.NORTH);

        String[] cols = {"Exam Name","Type","Subject","Date","Marks","Max","Grade","Status"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(mdl); UITheme.styleTable(tbl);
        panel.add(UITheme.scrollPane(tbl), BorderLayout.CENTER);

        if (student != null) {
            SwingWorker<List<Map<String,Object>>,Void> w = new SwingWorker<>() {
                @Override protected List<Map<String,Object>> doInBackground() throws Exception {
                    return new ResultDAO().getResultsByStudent(student.getStudentId());
                }
                @Override protected void done() {
                    try {
                        for (Map<String,Object> row : get())
                            mdl.addRow(new Object[]{row.get("exam_name"), row.get("exam_type"),
                                row.get("subject_name"), row.get("exam_date"),
                                row.get("marks"), row.get("max_marks"), row.get("grade"),
                                ((boolean)row.get("is_pass")) ? "✓ Pass" : "✗ Fail"});
                    } catch (Exception e) { JOptionPane.showMessageDialog(null, e.getMessage()); }
                }
            };
            w.execute();
        }

        // GPA summary
        JPanel summaryCard = UITheme.cardPanel();
        summaryCard.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 8));
        JLabel cgpaLbl = UITheme.sectionLabel("Performance Summary");
        summaryCard.add(cgpaLbl);
        panel.add(summaryCard, BorderLayout.SOUTH);
        setContent(panel);
    }

    // ── Timetable ─────────────────────────────────────────────────────────────
    private void showTimetable() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.add(UITheme.sectionLabel("My Class Timetable"), BorderLayout.NORTH);

        String[] cols = {"Day","Start Time","End Time","Subject","Teacher","Room"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(mdl); UITheme.styleTable(tbl);
        panel.add(UITheme.scrollPane(tbl), BorderLayout.CENTER);

        if (student != null) {
            try {
                PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(
                    "SELECT tt.day_of_week, tt.start_time, tt.end_time, " +
                    "sub.subject_name, t.full_name, COALESCE(cr.room_name,'—') AS room " +
                    "FROM timetable tt " +
                    "JOIN subjects sub ON tt.subject_id=sub.subject_id " +
                    "JOIN teachers t ON tt.teacher_id=t.teacher_id " +
                    "LEFT JOIN classrooms cr ON tt.room_id=cr.room_id " +
                    "WHERE tt.course_id=? AND tt.semester=? " +
                    "ORDER BY FIELD(tt.day_of_week,'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'), tt.start_time");
                ps.setInt(1, student.getCourseId());
                ps.setInt(2, student.getCurrentSemester());
                ResultSet rs = ps.executeQuery();
                while (rs.next())
                    mdl.addRow(new Object[]{rs.getString("day_of_week"), rs.getTime("start_time"),
                        rs.getTime("end_time"), rs.getString("subject_name"),
                        rs.getString("full_name"), rs.getString("room")});
            } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
        }
        setContent(panel);
    }

    // ── Assignments ───────────────────────────────────────────────────────────
    private void showAssignments() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);
        top.add(UITheme.sectionLabel("My Assignments"));
        JTextField subIdF = UITheme.styledField(); subIdF.setPreferredSize(new Dimension(80, 34));
        JButton loadBtn = UITheme.accentButton("Load");
        top.add(UITheme.formLabel("Subject ID:")); top.add(subIdF); top.add(loadBtn);
        panel.add(top, BorderLayout.NORTH);

        String[] cols = {"ID","Title","Description","Due Date","Max Marks","Teacher"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(mdl); UITheme.styleTable(tbl);
        panel.add(UITheme.scrollPane(tbl), BorderLayout.CENTER);

        loadBtn.addActionListener(e -> {
            try {
                List<Map<String,Object>> rows = new AssignmentDAO().getBySubject(Integer.parseInt(subIdF.getText().trim()));
                mdl.setRowCount(0);
                for (Map<String,Object> row : rows)
                    mdl.addRow(new Object[]{row.get("assignment_id"), row.get("title"),
                        row.get("description"), row.get("due_date"), row.get("max_marks"), row.get("teacher_name")});
            } catch (Exception ex) { JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage()); }
        });
        setContent(panel);
    }

    // ── Fee Details ───────────────────────────────────────────────────────────
    private void showFees() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.add(UITheme.sectionLabel("My Fee Details"), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BODY);

        // Payment history
        String[] cols = {"Receipt No","Fee Type","Semester","Amount Paid","Fine","Total","Date","Mode"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(mdl); UITheme.styleTable(tbl);
        tabs.addTab("Payment History", UITheme.scrollPane(tbl));

        // Pending dues card
        JPanel dueCard = UITheme.cardPanel();
        dueCard.setLayout(new BoxLayout(dueCard, BoxLayout.Y_AXIS));
        JLabel dueLabel = UITheme.sectionLabel("Pending Amount");
        dueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel dueAmount = new JLabel("Calculating...");
        dueAmount.setFont(new Font("Segoe UI", Font.BOLD, 36));
        dueAmount.setForeground(UITheme.WARNING);
        dueAmount.setAlignmentX(Component.CENTER_ALIGNMENT);
        dueCard.add(Box.createVerticalGlue()); dueCard.add(dueLabel);
        dueCard.add(Box.createVerticalStrut(12)); dueCard.add(dueAmount);
        dueCard.add(Box.createVerticalGlue());
        tabs.addTab("Pending Dues", dueCard);

        panel.add(tabs, BorderLayout.CENTER);

        if (student != null) {
            SwingWorker<Void, Void> w = new SwingWorker<>() {
                @Override protected Void doInBackground() throws Exception {
                    List<Map<String,Object>> hist = new FeeDAO().getPaymentHistory(student.getStudentId());
                    double pending = new FeeDAO().getPendingDues(student.getStudentId(), student.getCourseId());
                    SwingUtilities.invokeLater(() -> {
                        for (Map<String,Object> row : hist)
                            mdl.addRow(new Object[]{row.get("receipt_no"), row.get("fee_type"),
                                row.get("semester"), String.format("₹%.2f",(double)row.get("amount_paid")),
                                String.format("₹%.2f",(double)row.get("fine_amount")),
                                String.format("₹%.2f",(double)row.get("total")),
                                row.get("payment_date"), row.get("payment_mode")});
                        dueAmount.setText(String.format("₹%.2f", pending));
                        dueAmount.setForeground(pending > 0 ? UITheme.DANGER : UITheme.SUCCESS);
                    });
                    return null;
                }
            };
            w.execute();
        }
        setContent(panel);
    }

    // ── Library ───────────────────────────────────────────────────────────────
    private void showLibrary() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);
        top.add(UITheme.sectionLabel("Library"));
        JTextField searchF = UITheme.styledField(); searchF.setPreferredSize(new Dimension(200, 34));
        JButton searchBtn = UITheme.accentButton("🔍 Search");
        top.add(searchF); top.add(searchBtn);
        panel.add(top, BorderLayout.NORTH);

        String[] cols = {"Title","Author","ISBN","Category","Available"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(mdl); UITheme.styleTable(tbl);
        panel.add(UITheme.scrollPane(tbl), BorderLayout.CENTER);

        Runnable loadBooks = () -> {
            try {
                List<Map<String,Object>> rows = new LibraryDAO().searchBooks(searchF.getText().trim());
                mdl.setRowCount(0);
                for (Map<String,Object> row : rows)
                    mdl.addRow(new Object[]{row.get("title"), row.get("author"), row.get("isbn"),
                        row.get("category"), row.get("available") + "/" + row.get("total")});
            } catch (Exception e) { JOptionPane.showMessageDialog(panel, "Error: " + e.getMessage()); }
        };
        searchBtn.addActionListener(e -> loadBooks.run());
        searchF.addActionListener(e -> loadBooks.run());
        loadBooks.run();
        setContent(panel);
    }

    // ── Notices ───────────────────────────────────────────────────────────────
    private void showNotices() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.add(UITheme.sectionLabel("Notice Board"), BorderLayout.NORTH);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> noticeList = new JList<>(listModel);
        noticeList.setFont(UITheme.FONT_BODY);
        noticeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        noticeList.setCellRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                if (!isSelected && value.toString().startsWith("★")) lbl.setForeground(UITheme.DANGER);
                return lbl;
            }
        });

        JTextArea contentArea2 = new JTextArea();
        contentArea2.setFont(UITheme.FONT_BODY);
        contentArea2.setEditable(false);
        contentArea2.setWrapStyleWord(true);
        contentArea2.setLineWrap(true);
        contentArea2.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(noticeList), new JScrollPane(contentArea2));
        split.setDividerLocation(300);
        split.setBorder(null);
        panel.add(split, BorderLayout.CENTER);

        List<Map<String,Object>>[] noticesHolder = new List[1];
        SwingWorker<List<Map<String,Object>>,Void> w = new SwingWorker<>() {
            @Override protected List<Map<String,Object>> doInBackground() throws Exception {
                return new NoticeDAO().getNoticesForRole("Student");
            }
            @Override protected void done() {
                try {
                    noticesHolder[0] = get();
                    for (Map<String,Object> row : noticesHolder[0]) {
                        String prefix = (boolean)row.get("important") ? "★ " : "  ";
                        listModel.addElement(prefix + row.get("title") + "\n" + row.get("posted_by") + " — " + row.get("posted_at"));
                    }
                } catch (Exception e) { JOptionPane.showMessageDialog(null, e.getMessage()); }
            }
        };
        w.execute();

        noticeList.addListSelectionListener(e -> {
            int idx = noticeList.getSelectedIndex();
            if (idx >= 0 && noticesHolder[0] != null && idx < noticesHolder[0].size()) {
                Map<String,Object> row = noticesHolder[0].get(idx);
                contentArea2.setText("Title: " + row.get("title") + "\n\n" + row.get("content") +
                    "\n\n— Posted by: " + row.get("posted_by") + " on " + row.get("posted_at"));
            }
        });
        setContent(panel);
    }

    // ── Leave ─────────────────────────────────────────────────────────────────
    private void showLeave() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        JPanel card = UITheme.cardPanel();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(440, 320));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,4,8,4); gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = UITheme.sectionLabel("Apply for Leave");
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; card.add(title, gbc); gbc.gridwidth=1;

        JTextField fromF   = UITheme.styledField(); fromF.setText(LocalDate.now().toString());
        JTextField toF     = UITheme.styledField(); toF.setText(LocalDate.now().toString());
        JTextField reasonF = UITheme.styledField();
        JComboBox<String> typeCb = UITheme.styledCombo(new String[]{"Personal","Medical","Emergency","Other"});

        Object[][] rows = {{"From Date",fromF},{"To Date",toF},{"Leave Type",typeCb},{"Reason",reasonF}};
        for (int i = 0; i < rows.length; i++) {
            gbc.gridx=0; gbc.gridy=i+1; gbc.weightx=0; card.add(UITheme.formLabel((String)rows[i][0]), gbc);
            gbc.gridx=1; gbc.weightx=1; card.add((Component)rows[i][1], gbc);
        }
        JButton submitBtn = UITheme.primaryButton("Submit Application");
        gbc.gridx=0; gbc.gridy=rows.length+1; gbc.gridwidth=2; card.add(submitBtn, gbc);

        submitBtn.addActionListener(e -> {
            try {
                PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(
                    "INSERT INTO leaves (applicant_id, applicant_role, leave_type, from_date, to_date, reason) VALUES (?,?,?,?,?,?)");
                ps.setInt(1, currentUser.getUserId());
                ps.setString(2, "Student");
                ps.setString(3, (String)typeCb.getSelectedItem());
                ps.setDate(4, java.sql.Date.valueOf(fromF.getText().trim()));
                ps.setDate(5, java.sql.Date.valueOf(toF.getText().trim()));
                ps.setString(6, reasonF.getText().trim());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Leave application submitted! Awaiting admin approval.");
                fromF.setText(LocalDate.now().toString()); toF.setText(LocalDate.now().toString()); reasonF.setText("");
            } catch (Exception ex) { JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage()); }
        });
        panel.add(card);
        setContent(panel);
    }

    // ── Events ────────────────────────────────────────────────────────────────
    private void showEvents() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.add(UITheme.sectionLabel("College Events"), BorderLayout.NORTH);

        String[] cols = {"Event Name","Type","Date","Venue","Description"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(mdl); UITheme.styleTable(tbl);
        panel.add(UITheme.scrollPane(tbl), BorderLayout.CENTER);

        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT event_name, event_type, event_date, venue, description FROM events ORDER BY event_date")) {
            while (rs.next())
                mdl.addRow(new Object[]{rs.getString("event_name"), rs.getString("event_type"),
                    rs.getDate("event_date"), rs.getString("venue"), rs.getString("description")});
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
        setContent(panel);
    }

    // ── Notifications ─────────────────────────────────────────────────────────
    private void showNotifications() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.add(UITheme.sectionLabel("My Notifications"), BorderLayout.NORTH);

        String[] cols = {"Message","Type","Date","Read"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(mdl); UITheme.styleTable(tbl);
        panel.add(UITheme.scrollPane(tbl), BorderLayout.CENTER);

        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(
                "SELECT message, notif_type, created_at, is_read FROM notifications WHERE user_id=? ORDER BY created_at DESC")) {
            ps.setInt(1, currentUser.getUserId());
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                mdl.addRow(new Object[]{rs.getString("message"), rs.getString("notif_type"),
                    rs.getTimestamp("created_at"), rs.getBoolean("is_read") ? "Read" : "Unread"});
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }

        JButton markReadBtn = UITheme.successButton("Mark All Read");
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bp.setOpaque(false); bp.add(markReadBtn);
        panel.add(bp, BorderLayout.SOUTH);
        markReadBtn.addActionListener(e -> {
            try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(
                    "UPDATE notifications SET is_read=TRUE WHERE user_id=?")) {
                ps.setInt(1, currentUser.getUserId());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(panel, "All notifications marked as read.");
            } catch (SQLException ex) { JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage()); }
        });
        setContent(panel);
    }
}
