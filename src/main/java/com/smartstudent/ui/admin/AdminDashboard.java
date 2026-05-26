package com.smartstudent.ui.admin;

import com.smartstudent.dao.StudentDAO;
import com.smartstudent.dao.TeacherDAO;
import com.smartstudent.dao.FeeDAO;
import com.smartstudent.model.User;
import com.smartstudent.ui.common.LoginScreen;
import com.smartstudent.util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class AdminDashboard extends JFrame {

    private final User        currentUser;
    private       JPanel      contentArea;
    private       JLabel      pageTitle;
    private       JButton     activeBtn;

    public AdminDashboard(User user) {
        this.currentUser = user;
        UITheme.applyGlobalLook();
        setTitle("Smart Student Hub — Admin Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 700));
        buildUI();
        showDashboardHome();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
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
            BorderFactory.createEmptyBorder(0, 20, 0, 20)
        ));

        pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(UITheme.FONT_HEADING);
        pageTitle.setForeground(UITheme.PRIMARY);
        bar.add(pageTitle, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        JLabel user = new JLabel("👤 " + currentUser.getUsername() + "  [Administrator]");
        user.setFont(UITheme.FONT_BODY);
        user.setForeground(UITheme.TEXT_SECONDARY);

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

        right.add(user);
        right.add(logout);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JScrollPane buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.BG_SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        JLabel logo = new JLabel("  SSH Admin");
        logo.setFont(UITheme.FONT_SUBHEAD);
        logo.setForeground(UITheme.ACCENT);
        logo.setBorder(BorderFactory.createEmptyBorder(8, 20, 16, 0));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logo);

        addSection(sidebar, "CORE");
        addSidebarBtn(sidebar, "🏠", "Dashboard",     e -> showDashboardHome());
        addSidebarBtn(sidebar, "👨‍🎓", "Students",      e -> showStudentsPanel());
        addSidebarBtn(sidebar, "👩‍🏫", "Teachers",      e -> showTeachersPanel());
        addSidebarBtn(sidebar, "📚", "Courses",       e -> showCoursesPanel());

        addSection(sidebar, "ACADEMICS");
        addSidebarBtn(sidebar, "📅", "Attendance",    e -> showAttendancePanel());
        addSidebarBtn(sidebar, "📝", "Examinations",  e -> showExamsPanel());
        addSidebarBtn(sidebar, "🏆", "Results",       e -> showResultsPanel());
        addSidebarBtn(sidebar, "🕐", "Timetable",     e -> showTimetablePanel());

        addSection(sidebar, "FINANCE");
        addSidebarBtn(sidebar, "💰", "Fees",          e -> showFeesPanel());

        addSection(sidebar, "SERVICES");
        addSidebarBtn(sidebar, "📖", "Library",       e -> showLibraryPanel());
        addSidebarBtn(sidebar, "📢", "Notices",       e -> showNoticesPanel());
        addSidebarBtn(sidebar, "🏖️", "Leave Mgmt",   e -> showLeavePanel());
        addSidebarBtn(sidebar, "📋", "Assignments",   e -> showAssignmentsPanel());
        addSidebarBtn(sidebar, "🎉", "Events",        e -> showEventsPanel());
        addSidebarBtn(sidebar, "🏠", "Hostel",        e -> showHostelPanel());

        addSection(sidebar, "SYSTEM");
        addSidebarBtn(sidebar, "🔔", "Notifications", e -> showNotificationsPanel());
        addSidebarBtn(sidebar, "💾", "Backup",        e -> showBackupPanel());

        sidebar.add(Box.createVerticalGlue());

        JScrollPane sp = new JScrollPane(sidebar);
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(16);
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
                if (btn != activeBtn) {
                    btn.setBackground(UITheme.SIDEBAR_HOVER);
                    btn.setForeground(Color.WHITE);
                }
            }
            public void mouseExited(MouseEvent e) {
                if (btn != activeBtn) {
                    btn.setBackground(UITheme.BG_SIDEBAR);
                    btn.setForeground(new Color(200, 215, 255));
                }
            }
        });
        btn.addActionListener(e -> {
            setActive(btn, label);
            action.actionPerformed(e);
        });
        parent.add(btn);
    }

    private void setActive(JButton btn, String title) {
        if (activeBtn != null) {
            activeBtn.setBackground(UITheme.BG_SIDEBAR);
            activeBtn.setForeground(new Color(200, 215, 255));
        }
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

    // ── Panel Builders ──────────────────────────────────────────────────────

    private void showDashboardHome() {
        pageTitle.setText("Dashboard");
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        JLabel welcome = new JLabel("Welcome, " + currentUser.getUsername() + " 👋");
        welcome.setFont(UITheme.FONT_TITLE);
        welcome.setForeground(UITheme.PRIMARY);
        panel.add(welcome, BorderLayout.NORTH);

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);

        SwingWorker<int[], Void> worker = new SwingWorker<>() {
            @Override protected int[] doInBackground() throws Exception {
                int students = new StudentDAO().getTotalActive();
                int teachers = new TeacherDAO().getTotalActive();
                double pending = new FeeDAO().getTotalPendingAllStudents();
                return new int[]{students, teachers, (int) pending};
            }
            @Override protected void done() {
                try {
                    int[] d = get();
                    statsRow.add(UITheme.statCard("Total Students",   String.valueOf(d[0]),      UITheme.PRIMARY_LIGHT));
                    statsRow.add(UITheme.statCard("Total Teachers",   String.valueOf(d[1]),      UITheme.SUCCESS));
                    statsRow.add(UITheme.statCard("Pending Fees (₹)", "₹" + d[2],               UITheme.WARNING));
                    statsRow.add(UITheme.statCard("Active Modules",   "20",                      UITheme.ACCENT));
                    statsRow.revalidate();
                } catch (Exception ignored) {
                    statsRow.add(UITheme.statCard("Total Students",  "—", UITheme.PRIMARY_LIGHT));
                    statsRow.add(UITheme.statCard("Total Teachers",  "—", UITheme.SUCCESS));
                    statsRow.add(UITheme.statCard("Pending Fees",    "—", UITheme.WARNING));
                    statsRow.add(UITheme.statCard("Active Modules", "20", UITheme.ACCENT));
                }
            }
        };
        worker.execute();

        panel.add(statsRow, BorderLayout.CENTER);

        JPanel recentCard = UITheme.cardPanel();
        recentCard.setLayout(new BorderLayout());
        JLabel recentTitle = UITheme.sectionLabel("Quick Actions");
        recentCard.add(recentTitle, BorderLayout.NORTH);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        actions.setOpaque(false);
        String[][] quickActions = {
            {"Add Student", "👨‍🎓"}, {"Add Teacher", "👩‍🏫"}, {"Record Fee", "💰"},
            {"Mark Attendance", "📅"}, {"Post Notice", "📢"}, {"Add Book", "📖"}
        };
        for (String[] qa : quickActions) {
            JButton b = UITheme.accentButton(qa[1] + " " + qa[0]);
            b.setPreferredSize(new Dimension(160, 40));
            actions.add(b);
        }
        recentCard.add(actions, BorderLayout.CENTER);
        panel.add(recentCard, BorderLayout.SOUTH);

        setContent(panel);
    }

    private void showStudentsPanel()    { setContent(new StudentManagementPanel(currentUser)); }
    private void showTeachersPanel()    { setContent(new TeacherManagementPanel(currentUser)); }
    private void showCoursesPanel()     { setContent(buildPlaceholder("Course & Subject Management", "Manage courses, subjects, credits, and semester mappings.")); }
    private void showAttendancePanel()  { setContent(new AttendancePanel(currentUser)); }
    private void showExamsPanel()       { setContent(new ExaminationPanel(currentUser)); }
    private void showResultsPanel()     { setContent(new ResultsPanel(currentUser)); }
    private void showTimetablePanel()   { setContent(new TimetablePanel(currentUser)); }
    private void showFeesPanel()        { setContent(new FeeManagementPanel(currentUser)); }
    private void showLibraryPanel()     { setContent(new LibraryPanel(currentUser)); }
    private void showNoticesPanel()     { setContent(new NoticesPanel(currentUser)); }
    private void showLeavePanel()       { setContent(new LeaveManagementPanel(currentUser)); }
    private void showAssignmentsPanel() { setContent(buildPlaceholder("Assignment Management", "Teachers upload assignments; students submit work. Track deadlines and grades.")); }
    private void showEventsPanel()      { setContent(buildPlaceholder("Event Management", "Manage college events, seminars, workshops, and student participation records.")); }
    private void showHostelPanel()      { setContent(buildPlaceholder("Hostel Management", "Manage room allocations, hostel fees, and student hostel records.")); }
    private void showNotificationsPanel(){ setContent(buildPlaceholder("Notification System", "Low attendance alerts, fee reminders, exam schedules, result notifications.")); }
    private void showBackupPanel()      { setContent(new BackupPanel(currentUser)); }

    private JPanel buildPlaceholder(String title, String desc) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        JPanel card = UITheme.cardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(500, 200));

        JLabel t = UITheme.sectionLabel("🚧 " + title);
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel d = new JLabel("<html><center>" + desc + "</center></html>");
        d.setFont(UITheme.FONT_BODY);
        d.setForeground(UITheme.TEXT_SECONDARY);
        d.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(t);
        card.add(Box.createVerticalStrut(12));
        card.add(d);
        card.add(Box.createVerticalGlue());
        panel.add(card);
        return panel;
    }
}
