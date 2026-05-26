package com.smartstudent.ui.common;

import com.smartstudent.config.DatabaseConfig;
import com.smartstudent.dao.UserDAO;
import com.smartstudent.model.User;
import com.smartstudent.ui.admin.AdminDashboard;
import com.smartstudent.ui.student.StudentDashboard;
import com.smartstudent.ui.teacher.TeacherDashboard;
import com.smartstudent.util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class LoginScreen extends JFrame {

    private JTextField     userField;
    private JPasswordField passField;
    private JLabel         statusLabel;
    private JButton        loginBtn;

    public LoginScreen() {
        UITheme.applyGlobalLook();
        setTitle("Smart Student Hub — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildLeftPanel(), BorderLayout.WEST);
        root.add(buildRightPanel(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildLeftPanel() {
        JPanel left = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.PRIMARY, 0, getHeight(), UITheme.PRIMARY_LIGHT);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        left.setPreferredSize(new Dimension(360, 0));
        left.setLayout(new GridBagLayout());

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        JLabel icon = new JLabel("🎓");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Smart Student Hub");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Academic ERP System");
        sub.setFont(UITheme.FONT_BODY);
        sub.setForeground(new Color(255, 255, 255, 180));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(Box.createVerticalGlue());
        inner.add(icon);
        inner.add(Box.createVerticalStrut(16));
        inner.add(title);
        inner.add(Box.createVerticalStrut(8));
        inner.add(sub);
        inner.add(Box.createVerticalStrut(40));

        String[] features = {"✓ Student Management", "✓ Attendance Tracking", "✓ Fee Management",
                             "✓ Exam & Results", "✓ Library System", "✓ Role-Based Access"};
        for (String f : features) {
            JLabel fl = new JLabel(f);
            fl.setFont(UITheme.FONT_SMALL);
            fl.setForeground(new Color(200, 220, 255));
            fl.setAlignmentX(Component.LEFT_ALIGNMENT);
            inner.add(fl);
            inner.add(Box.createVerticalStrut(5));
        }
        inner.add(Box.createVerticalGlue());

        left.add(inner);
        return left;
    }

    private JPanel buildRightPanel() {
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(UITheme.BG_MAIN);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(40, 48, 40, 48)
        ));
        form.setPreferredSize(new Dimension(360, 400));

        JLabel heading = new JLabel("Welcome Back");
        heading.setFont(UITheme.FONT_HEADING);
        heading.setForeground(UITheme.PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub2 = new JLabel("Sign in to your account");
        sub2.setFont(UITheme.FONT_SMALL);
        sub2.setForeground(UITheme.TEXT_SECONDARY);
        sub2.setAlignmentX(Component.LEFT_ALIGNMENT);

        userField = UITheme.styledField();
        userField.setAlignmentX(Component.LEFT_ALIGNMENT);
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        passField = UITheme.styledPasswordField();
        passField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        loginBtn = UITheme.primaryButton("Sign In");
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.DANGER);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(heading);
        form.add(Box.createVerticalStrut(4));
        form.add(sub2);
        form.add(Box.createVerticalStrut(28));
        form.add(makeLabel("Username"));
        form.add(Box.createVerticalStrut(6));
        form.add(userField);
        form.add(Box.createVerticalStrut(16));
        form.add(makeLabel("Password"));
        form.add(Box.createVerticalStrut(6));
        form.add(passField);
        form.add(Box.createVerticalStrut(24));
        form.add(loginBtn);
        form.add(Box.createVerticalStrut(12));
        form.add(statusLabel);

        loginBtn.addActionListener(e -> doLogin());
        passField.addActionListener(e -> doLogin());

        JPanel hint = new JPanel();
        hint.setBackground(UITheme.BG_MAIN);
        hint.setLayout(new BoxLayout(hint, BoxLayout.Y_AXIS));
        JLabel h1 = new JLabel("Demo credentials:");
        h1.setFont(UITheme.FONT_SMALL); h1.setForeground(UITheme.TEXT_SECONDARY);
        JLabel h2 = new JLabel("Admin: admin / Admin@123");
        h2.setFont(UITheme.FONT_SMALL); h2.setForeground(UITheme.TEXT_SECONDARY);
        JLabel h3 = new JLabel("Teacher: meena896 / Teacher@123");
        h3.setFont(UITheme.FONT_SMALL); h3.setForeground(UITheme.TEXT_SECONDARY);
        JLabel h4 = new JLabel("Student: mohan189 / Student@123");
        h4.setFont(UITheme.FONT_SMALL); h4.setForeground(UITheme.TEXT_SECONDARY);
        hint.add(h1); hint.add(h2); hint.add(h3); hint.add(h4);

        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.add(form);
        wrapper.add(Box.createVerticalStrut(16));
        wrapper.add(hint);

        right.add(wrapper);
        return right;
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void doLogin() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password.");
            return;
        }

        loginBtn.setEnabled(false);
        loginBtn.setText("Signing in...");
        statusLabel.setText(" ");

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override protected User doInBackground() throws Exception {
                return new UserDAO().authenticate(username, password);
            }
            @Override protected void done() {
                loginBtn.setEnabled(true);
                loginBtn.setText("Sign In");
                try {
                    User user = get();
                    if (user == null) {
                        statusLabel.setText("Invalid username or password.");
                        passField.setText("");
                        return;
                    }
                    dispose();
                    openDashboard(user);
                } catch (Exception e) {
                    statusLabel.setText("Connection error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void openDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            switch (user.getRoleName()) {
                case "Administrator" -> new AdminDashboard(user);
                case "Teacher"      -> new TeacherDashboard(user);
                case "Student"      -> new StudentDashboard(user);
                default -> {
                    JOptionPane.showMessageDialog(null, "Unknown role: " + user.getRoleName());
                    new LoginScreen();
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}
