import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Date;
import javax.imageio.ImageIO;

public class Main extends JFrame {

    static final Color WHITE        = new Color(0xFFFFFF);
    static final Color BG_BASE      = new Color(0xF6FBF7);
    static final Color BG_SURFACE   = new Color(0xEDF7EF);
    static final Color BG_CARD      = new Color(0xFFFFFF);
    static final Color GREEN_500    = new Color(0x2E9E5B);
    static final Color GREEN_400    = new Color(0x3DBF70);
    static final Color GREEN_300    = new Color(0x6DD494);
    static final Color GREEN_100    = new Color(0xD4F0DE);
    static final Color GREEN_50     = new Color(0xEBF8EF);
    static final Color TEXT_DARK    = new Color(0x1A2E22);
    static final Color TEXT_MID     = new Color(0x4A7060);
    static final Color TEXT_LIGHT   = new Color(0x8BB5A0);
    static final Color BORDER       = new Color(0xD6EDE0);
    static final Color BORDER_FOCUS = new Color(0x2E9E5B);
    static final Color SUCCESS      = new Color(0x2E9E5B);
    static final Color WARNING      = new Color(0xE8922A);
    static final Color ROW_ALT      = new Color(0xF8FCF9);
    static final Color SHADOW_COLOR = new Color(0x2E9E5B, true);

    static final Font FONT_DISPLAY = new Font("Georgia", Font.BOLD, 30);
    static final Font FONT_TITLE   = new Font("Georgia", Font.BOLD, 22);
    static final Font FONT_HEAD    = new Font("Georgia", Font.BOLD, 16);
    static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 14);
    static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 12);
    static final Font FONT_BTN     = new Font("Segoe UI", Font.BOLD, 13);
    static final Font FONT_LABEL   = new Font("Segoe UI", Font.BOLD, 11);
    static final Font FONT_MONO    = new Font("Consolas", Font.PLAIN, 13);

    static final String DB_URL  = "jdbc:mysql://localhost:3306/vet_clinic";
    static final String DB_USER = "root";
    static final String DB_PASS = "";

    // ── PAW IMAGE ──────────────────────────────────────────────────────────────

    static final String PAW_IMAGE_PATH = "C:\\Users\\Renz\\Desktop\\YourProject\\images\\paw.png";
    private static Image pawImage;

    static {
        try {
            pawImage = ImageIO.read(new File(PAW_IMAGE_PATH));
        } catch (IOException e) {
            pawImage = null;
        }
    }

    private String loggedInClientName = "";

    public Main() { loginScreen(); }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    // ── PAW DRAWING HELPERS ────────────────────────────────────────────────────

    private static void paintPawIcon(Graphics2D g2, int x, int y, int w, int h, Color fallbackColor) {
        if (pawImage != null) {
            g2.drawImage(pawImage, x, y, w, h, null);
        } else {
            // Simple fallback paw
            g2.setColor(fallbackColor);
            int cx = x + w / 2;
            int cy = y + h / 2;
            int size = Math.min(w, h);
            g2.fillOval(cx - size/4, cy - size/8, size/2, (int)(size*0.44));
            g2.fillOval(cx - (int)(size*0.36), cy - (int)(size*0.36), (int)(size*0.20), (int)(size*0.26));
            g2.fillOval(cx - (int)(size*0.13), cy - (int)(size*0.48), (int)(size*0.20), (int)(size*0.26));
            g2.fillOval(cx + (int)(size*0.13) - (int)(size*0.20), cy - (int)(size*0.48), (int)(size*0.20), (int)(size*0.26));
            g2.fillOval(cx + (int)(size*0.16), cy - (int)(size*0.36), (int)(size*0.20), (int)(size*0.26));
        }
    }

    // ── BACKGROUND PANEL ───────────────────────────────────────────────────────

    private JPanel lightBgPanel() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(0xF2FAF5),
                    getWidth(), getHeight(), new Color(0xE8F6EE));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                RadialGradientPaint spot = new RadialGradientPaint(
                    new Point2D.Float(getWidth() * 0.78f, getHeight() * 0.1f),
                    getWidth() * 0.38f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(61, 191, 112, 28), new Color(242, 250, 245, 0)});
                g2.setPaint(spot);
                g2.fillRect(0, 0, getWidth(), getHeight());
                RadialGradientPaint spot2 = new RadialGradientPaint(
                    new Point2D.Float(getWidth() * 0.12f, getHeight() * 0.88f),
                    getWidth() * 0.28f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(46, 158, 91, 20), new Color(242, 250, 245, 0)});
                g2.setPaint(spot2);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(46, 158, 91, 9));
                int sp = 30;
                for (int x = sp; x < getWidth(); x += sp)
                    for (int y = sp; y < getHeight(); y += sp)
                        g2.fillOval(x - 1, y - 1, 3, 3);
            }
        };
    }

    // ── FORM COMPONENTS ────────────────────────────────────────────────────────

    private JTextField styledField() {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setOpaque(false);
        f.setBackground(WHITE);
        f.setForeground(TEXT_DARK);
        f.setCaretColor(GREEN_500);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            new FocusBorder(10, BORDER, BORDER_FOCUS, 1),
            BorderFactory.createEmptyBorder(11, 15, 11, 15)));
        return f;
    }

    private JPasswordField styledPassword() {
        JPasswordField f = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setOpaque(false);
        f.setForeground(TEXT_DARK);
        f.setCaretColor(GREEN_500);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            new FocusBorder(10, BORDER, BORDER_FOCUS, 1),
            BorderFactory.createEmptyBorder(11, 15, 11, 15)));
        return f;
    }

    private JButton primaryBtn(String text) {
        JButton b = new JButton(text) {
            private boolean hov = false, press = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e)  { hov = true;   repaint(); }
                    public void mouseExited(MouseEvent e)   { hov = false;  repaint(); }
                    public void mousePressed(MouseEvent e)  { press = true;  repaint(); }
                    public void mouseReleased(MouseEvent e) { press = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!press && hov) {
                    g2.setColor(new Color(46, 158, 91, 40));
                    g2.fillRoundRect(-3, -3, getWidth() + 6, getHeight() + 6, 14, 14);
                }
                GradientPaint gp = new GradientPaint(
                    0, 0, press ? new Color(0x267A47) : (hov ? new Color(0x38B265) : GREEN_500),
                    0, getHeight(), press ? new Color(0x1E6038) : (hov ? new Color(0x2E9E5B) : new Color(0x267A47)));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setFont(getFont());
                g2.setColor(WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        b.setOpaque(false); b.setContentAreaFilled(false);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setFont(FONT_BTN); b.setForeground(WHITE);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton ghostBtn(String text) {
        JButton b = new JButton(text) {
            private boolean hov = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                    public void mouseExited(MouseEvent e)  { hov = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hov) { g2.setColor(GREEN_50); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10); }
                g2.setColor(hov ? GREEN_500 : TEXT_MID);
                g2.setStroke(new BasicStroke(1.3f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        b.setOpaque(false); b.setContentAreaFilled(false);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setFont(FONT_BTN); b.setForeground(TEXT_MID);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_LIGHT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void styleTable(JTable table) {
        table.setBackground(WHITE);
        table.setForeground(TEXT_DARK);
        table.setFont(FONT_BODY);
        table.setRowHeight(46);
        table.setGridColor(new Color(0xEDF5F0));
        table.setSelectionBackground(GREEN_50);
        table.setSelectionForeground(GREEN_500);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_SURFACE);
        header.setForeground(TEXT_LIGHT);
        header.setFont(FONT_LABEL);
        header.setPreferredSize(new Dimension(header.getWidth(), 48));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBackground(sel ? GREEN_50 : (row % 2 == 0 ? WHITE : ROW_ALT));
                setForeground(TEXT_DARK);
                setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
                setFont(FONT_BODY);
                if (v != null) {
                    String s = v.toString();
                    if (s.equals("PAID"))             { setForeground(SUCCESS);  setFont(FONT_BTN); }
                    if (s.equals("UNPAID"))           { setForeground(WARNING);  setFont(FONT_BTN); }
                    if (s.equals("Approved"))         { setForeground(GREEN_400); }
                    if (s.equals("Completed"))        { setForeground(TEXT_MID); }
                    if (s.equals("Pending Approval")) { setForeground(WARNING); }
                }
                return this;
            }
        });
    }

    private JScrollPane lightScroll(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBackground(WHITE);
        sp.getViewport().setBackground(WHITE);
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        sp.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = GREEN_300; trackColor = BG_SURFACE;
            }
            @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
            @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }
            private JButton zeroBtn() {
                JButton b = new JButton(); b.setPreferredSize(new Dimension(0, 0)); return b;
            }
        });
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        return sp;
    }

    private void styleSpinner(JSpinner sp) {
        sp.setBackground(WHITE); sp.setForeground(TEXT_DARK); sp.setFont(FONT_BODY);
        sp.setBorder(new FocusBorder(10, BORDER, BORDER_FOCUS, 1));
        JComponent ed = sp.getEditor();
        if (ed instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) ed).getTextField();
            tf.setBackground(WHITE); tf.setForeground(TEXT_DARK);
            tf.setCaretColor(GREEN_500); tf.setFont(FONT_BODY);
            tf.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 4));
        }
    }

    private void styleComboBox(JComboBox<String> cb) {
        cb.setBackground(WHITE); cb.setForeground(TEXT_DARK); cb.setFont(FONT_BODY);
        cb.setBorder(new FocusBorder(10, BORDER, BORDER_FOCUS, 1));
        cb.setUI(new BasicComboBoxUI() {
            @Override protected JButton createArrowButton() {
                JButton b = new JButton("v");
                b.setBackground(WHITE); b.setForeground(TEXT_LIGHT);
                b.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                b.setFont(FONT_SMALL);
                return b;
            }
        });
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object val,
                    int idx, boolean sel, boolean foc) {
                super.getListCellRendererComponent(list, val, idx, sel, foc);
                setBackground(sel ? GREEN_50 : WHITE);
                setForeground(sel ? GREEN_500 : TEXT_DARK);
                setFont(FONT_BODY);
                setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
                return this;
            }
        });
    }

    // ── PAW ICON PANEL FACTORY ─────────────────────────────────────────────────

    private JPanel makePawIconPanel(int size) {
        int padding = (int)(size * 0.15);
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN_500);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                paintPawIcon(g2, padding, padding, getWidth() - padding*2, getHeight() - padding*2, WHITE);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(size, size));
        return p;
    }

    // ── SIDEBAR ────────────────────────────────────────────────────────────────

    private JPanel sidebarPanel(String portalLabel) {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, WHITE, 0, getHeight(), new Color(0xF0FAF3));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setPreferredSize(new Dimension(270, Integer.MAX_VALUE));
        sidebar.setLayout(new BorderLayout());
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(BorderFactory.createEmptyBorder(36, 28, 24, 28));

        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoRow.setOpaque(false);
        logoRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        logoRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoRow.add(makePawIconPanel(38));

        JLabel brandLbl = new JLabel("  VetCare");
        brandLbl.setFont(new Font("Georgia", Font.BOLD, 20));
        brandLbl.setForeground(TEXT_DARK);
        logoRow.add(brandLbl);

        JLabel subLbl = new JLabel(portalLabel);
        subLbl.setFont(FONT_SMALL);
        subLbl.setForeground(TEXT_LIGHT);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        subLbl.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 0));

        JPanel sep = new JPanel();
        sep.setOpaque(false);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        top.add(logoRow);
        top.add(subLbl);
        top.add(Box.createVerticalStrut(24));
        top.add(sep);

        sidebar.add(top, BorderLayout.NORTH);
        return sidebar;
    }

    // ── LOGIN SCREEN ───────────────────────────────────────────────────────────

    public void loginScreen() {
        JDialog dialog = new JDialog((Frame) null, "VetCare", true);
        dialog.setUndecorated(true);
        dialog.setSize(940, 620);
        dialog.setLocationRelativeTo(null);

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        dialog.setContentPane(root);

        // Left panel
        JPanel left = lightBgPanel();
        left.setLayout(new BorderLayout());
        JPanel leftInner = new JPanel();
        leftInner.setOpaque(false);
        leftInner.setLayout(new BoxLayout(leftInner, BoxLayout.Y_AXIS));
        leftInner.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 48));

        JPanel pawRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pawRow.setOpaque(false);
        pawRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        pawRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        pawRow.add(makePawIconPanel(52));
        JLabel brandName = new JLabel("  VetCare");
        brandName.setFont(FONT_DISPLAY);
        brandName.setForeground(TEXT_DARK);
        pawRow.add(brandName);

        JLabel tagline = new JLabel("Veterinary Clinic Management System");
        tagline.setFont(FONT_SMALL);
        tagline.setForeground(TEXT_LIGHT);
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);
        tagline.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 0));

        JPanel divider = new JPanel();
        divider.setOpaque(false);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        JLabel[] bullets = {
            new JLabel("Easy online appointment booking"),
            new JLabel("Full medical history for your pet"),
            new JLabel("Live status and visit updates"),
            new JLabel("Clear, transparent billing")
        };

        leftInner.add(pawRow);
        leftInner.add(Box.createVerticalStrut(4));
        leftInner.add(tagline);
        leftInner.add(Box.createVerticalStrut(40));
        leftInner.add(divider);
        leftInner.add(Box.createVerticalStrut(36));

        for (JLabel b : bullets) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            JPanel dot = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(GREEN_400);
                    g2.fillOval(0, 5, 7, 7);
                    g2.dispose();
                }
            };
            dot.setOpaque(false);
            dot.setPreferredSize(new Dimension(16, 18));
            b.setFont(FONT_BODY);
            b.setForeground(TEXT_MID);
            row.add(dot); row.add(b);
            leftInner.add(row);
            leftInner.add(Box.createVerticalStrut(14));
        }
        left.add(leftInner, BorderLayout.CENTER);

        // Right panel
        JPanel right = new JPanel();
        right.setBackground(WHITE);
        right.setLayout(new BorderLayout());
        JPanel rightInner = new JPanel();
        rightInner.setOpaque(false);
        rightInner.setLayout(new BoxLayout(rightInner, BoxLayout.Y_AXIS));
        rightInner.setBorder(BorderFactory.createEmptyBorder(70, 60, 60, 60));

        JLabel signInTitle = new JLabel("Welcome back");
        signInTitle.setFont(FONT_TITLE);
        signInTitle.setForeground(TEXT_DARK);
        signInTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel signInSub = new JLabel("Sign in to your account to continue.");
        signInSub.setFont(FONT_SMALL);
        signInSub.setForeground(TEXT_LIGHT);
        signInSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField usernameField = styledField();
        JPasswordField passwordField = styledPassword();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginBtn    = primaryBtn("Sign In");
        JButton registerBtn = ghostBtn("Create an Account");
        JButton exitBtn     = ghostBtn("Exit");
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        exitBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        exitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightInner.add(signInTitle);
        rightInner.add(Box.createVerticalStrut(6));
        rightInner.add(signInSub);
        rightInner.add(Box.createVerticalStrut(44));
        rightInner.add(lbl("Username"));
        rightInner.add(Box.createVerticalStrut(8));
        rightInner.add(usernameField);
        rightInner.add(Box.createVerticalStrut(20));
        rightInner.add(lbl("Password"));
        rightInner.add(Box.createVerticalStrut(8));
        rightInner.add(passwordField);
        rightInner.add(Box.createVerticalStrut(36));
        rightInner.add(loginBtn);
        rightInner.add(Box.createVerticalStrut(12));
        rightInner.add(registerBtn);
        rightInner.add(Box.createVerticalStrut(8));
        rightInner.add(exitBtn);

        right.add(rightInner, BorderLayout.CENTER);
        root.add(left);
        root.add(right);

        final boolean[] res = {false, false};
        loginBtn.addActionListener(e    -> { res[0] = true; dialog.dispose(); });
        registerBtn.addActionListener(e -> { res[1] = true; dialog.dispose(); });
        exitBtn.addActionListener(e     -> System.exit(0));
        passwordField.addActionListener(e -> { res[0] = true; dialog.dispose(); });

        dialog.setVisible(true);

        if (res[0]) {
            String u = usernameField.getText();
            String p = new String(passwordField.getPassword());
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "SELECT role FROM users WHERE username = ? AND password = ?")) {
                stmt.setString(1, u); stmt.setString(2, p);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String role = rs.getString("role");
                    if (role.equals("CLIENT"))    { loggedInClientName = u; clientPortal(); }
                    else if (role.equals("VET"))  vetPortal();
                } else {
                    showMsg("Invalid Credentials", "Please check your username and password.", false);
                    loginScreen();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showMsg("Database Error", ex.getMessage(), false);
                System.exit(0);
            }
        } else if (res[1]) {
            registerClientScreen();
        }
    }

    // ── REGISTER SCREEN ────────────────────────────────────────────────────────

    public void registerClientScreen() {
        JDialog dialog = new JDialog((Frame) null, "Create Account", true);
        dialog.setUndecorated(true);
        dialog.setSize(940, 580);
        dialog.setLocationRelativeTo(null);

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        dialog.setContentPane(root);

        JPanel left = lightBgPanel();
        left.setLayout(new BorderLayout());
        JPanel leftInner = new JPanel();
        leftInner.setOpaque(false);
        leftInner.setLayout(new BoxLayout(leftInner, BoxLayout.Y_AXIS));
        leftInner.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 48));

        JPanel pawRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pawRow.setOpaque(false);
        pawRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        pawRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        pawRow.add(makePawIconPanel(52));
        JLabel brandName = new JLabel("  VetCare");
        brandName.setFont(FONT_DISPLAY);
        brandName.setForeground(TEXT_DARK);
        pawRow.add(brandName);

        JLabel tagline = new JLabel("Join thousands of pet owners.");
        tagline.setFont(FONT_SMALL);
        tagline.setForeground(TEXT_LIGHT);
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);
        tagline.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 0));

        JLabel body = new JLabel("<html><body style='width:230px;line-height:1.8'>" +
            "Create your account to book appointments, track your pet's medical history, " +
            "and stay connected with your clinic team.</body></html>");
        body.setFont(FONT_BODY);
        body.setForeground(TEXT_MID);
        body.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftInner.add(pawRow);
        leftInner.add(Box.createVerticalStrut(4));
        leftInner.add(tagline);
        leftInner.add(Box.createVerticalStrut(40));
        leftInner.add(body);
        left.add(leftInner, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setBackground(WHITE);
        right.setLayout(new BorderLayout());
        JPanel rightInner = new JPanel();
        rightInner.setOpaque(false);
        rightInner.setLayout(new BoxLayout(rightInner, BoxLayout.Y_AXIS));
        rightInner.setBorder(BorderFactory.createEmptyBorder(70, 60, 60, 60));

        JLabel t = new JLabel("Create Account");
        t.setFont(FONT_TITLE); t.setForeground(TEXT_DARK);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel s = new JLabel("Register as a new client.");
        s.setFont(FONT_SMALL); s.setForeground(TEXT_LIGHT);
        s.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField userF = styledField();
        JPasswordField passF = styledPassword();
        userF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        passF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        userF.setAlignmentX(Component.LEFT_ALIGNMENT);
        passF.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton okBtn   = primaryBtn("Create Account");
        JButton backBtn = ghostBtn("Back to Login");
        okBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        backBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        okBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightInner.add(t);
        rightInner.add(Box.createVerticalStrut(6));
        rightInner.add(s);
        rightInner.add(Box.createVerticalStrut(44));
        rightInner.add(lbl("Username"));
        rightInner.add(Box.createVerticalStrut(8));
        rightInner.add(userF);
        rightInner.add(Box.createVerticalStrut(20));
        rightInner.add(lbl("Password"));
        rightInner.add(Box.createVerticalStrut(8));
        rightInner.add(passF);
        rightInner.add(Box.createVerticalStrut(36));
        rightInner.add(okBtn);
        rightInner.add(Box.createVerticalStrut(12));
        rightInner.add(backBtn);

        right.add(rightInner, BorderLayout.CENTER);
        root.add(left); root.add(right);

        final boolean[] go = {false, false};
        okBtn.addActionListener(e   -> { go[0] = true; dialog.dispose(); });
        backBtn.addActionListener(e -> { go[1] = true; dialog.dispose(); });

        dialog.setVisible(true);

        if (go[0]) {
            String u = userF.getText();
            String p = new String(passF.getPassword());
            if (u.isEmpty() || p.isEmpty()) {
                showMsg("Empty Fields", "Username and password cannot be empty.", false);
                registerClientScreen(); return;
            }
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (username, password, role) VALUES (?, ?, 'CLIENT')")) {
                stmt.setString(1, u); stmt.setString(2, p); stmt.executeUpdate();
                showMsg("Account Created", "Registration complete. You can now sign in.", true);
                loginScreen();
            } catch (SQLException ex) {
                if (ex.getMessage().contains("Duplicate entry")) {
                    showMsg("Username Taken", "Please choose a different username.", false);
                    registerClientScreen();
                } else {
                    ex.printStackTrace();
                    showMsg("Database Error", ex.getMessage(), false);
                    loginScreen();
                }
            }
        } else {
            loginScreen();
        }
    }

    // ── MESSAGE DIALOG ─────────────────────────────────────────────────────────

    private void showMsg(String title, String message, boolean success) {
        JDialog d = new JDialog((Frame) null, title, true);
        d.setUndecorated(true);
        d.setSize(420, 200);
        d.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(WHITE);
        root.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(success ? GREEN_100 : new Color(0xFFE5C8), 1),
            BorderFactory.createEmptyBorder(28, 32, 24, 32)));
        d.setContentPane(root);

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topRow.setOpaque(false);
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(success ? GREEN_400 : WARNING);
                g2.fillOval(0, 2, 10, 10);
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(10, 14));
        JLabel ttl = new JLabel("  " + title);
        ttl.setFont(FONT_HEAD);
        ttl.setForeground(success ? GREEN_500 : WARNING);
        topRow.add(dot); topRow.add(ttl);

        JLabel msg = new JLabel("<html><body style='width:320px'>" + message + "</body></html>");
        msg.setFont(FONT_BODY);
        msg.setForeground(TEXT_MID);
        msg.setBorder(BorderFactory.createEmptyBorder(12, 14, 0, 0));

        JButton ok = primaryBtn("OK");
        ok.setPreferredSize(new Dimension(100, 42));
        ok.addActionListener(e -> d.dispose());
        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnP.setOpaque(false); btnP.add(ok);

        root.add(topRow, BorderLayout.NORTH);
        root.add(msg,    BorderLayout.CENTER);
        root.add(btnP,   BorderLayout.SOUTH);
        d.setVisible(true);
    }

    // ── CLIENT PORTAL ──────────────────────────────────────────────────────────

    public void clientPortal() {
        getContentPane().removeAll();
        setTitle("VetCare");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = lightBgPanel();
        root.setLayout(new BorderLayout());
        setContentPane(root);

        JPanel sidebar = sidebarPanel("Client Portal");
        JPanel sideTop = (JPanel) sidebar.getComponent(0);

        JPanel userChip = new JPanel();
        userChip.setOpaque(false);
        userChip.setLayout(new BoxLayout(userChip, BoxLayout.X_AXIS));
        userChip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        userChip.setAlignmentX(Component.LEFT_ALIGNMENT);
        userChip.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPanel chipBg = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN_50);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        chipBg.setOpaque(false);
        chipBg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JPanel statusDot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN_400);
                g2.fillOval(0, 3, 8, 8);
                g2.dispose();
            }
        };
        statusDot.setOpaque(false);
        statusDot.setPreferredSize(new Dimension(8, 14));
        JLabel userLabel = new JLabel(" " + loggedInClientName);
        userLabel.setFont(FONT_BTN); userLabel.setForeground(TEXT_DARK);
        chipBg.add(statusDot); chipBg.add(userLabel);
        userChip.add(chipBg);
        sideTop.add(userChip);

        JPanel sideBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sideBottom.setOpaque(false);
        sideBottom.setBorder(BorderFactory.createEmptyBorder(0, 16, 28, 16));
        JButton logoutBtn = ghostBtn("Sign Out");
        logoutBtn.setPreferredSize(new Dimension(210, 42));
        sideBottom.add(logoutBtn);
        sidebar.add(sideTop, BorderLayout.NORTH);
        sidebar.add(sideBottom, BorderLayout.SOUTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setOpaque(false);
        tabs.setBackground(new Color(0, 0, 0, 0));
        tabs.setFont(FONT_BTN);
        tabs.setUI(new LightTabUI());

        // Booking panel
        JPanel bookingPanel = new JPanel(new BorderLayout(0, 0));
        bookingPanel.setOpaque(false);
        bookingPanel.setBorder(BorderFactory.createEmptyBorder(36, 44, 36, 44));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(36, 44, 36, 44)));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = styledField();
        nameField.setText(loggedInClientName);
        JTextField petField = styledField();

        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "MM/dd/yyyy");
        dateSpinner.setEditor(dateEditor);
        styleSpinner(dateSpinner);

        SpinnerDateModel timeModel = new SpinnerDateModel();
        JSpinner timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "hh:mm a");
        timeSpinner.setEditor(timeEditor);
        styleSpinner(timeSpinner);

        JButton bookButton = primaryBtn("Book Appointment");
        bookButton.setPreferredSize(new Dimension(220, 50));

        JLabel formTitle = new JLabel("New Appointment");
        formTitle.setFont(FONT_TITLE); formTitle.setForeground(TEXT_DARK);
        JLabel formSub = new JLabel("Schedule a visit for your pet.");
        formSub.setFont(FONT_SMALL); formSub.setForeground(TEXT_LIGHT);

        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2; gc.weightx = 1;
        gc.insets = new Insets(0, 10, 4, 10);
        formCard.add(formTitle, gc);
        gc.gridy = 1;
        gc.insets = new Insets(0, 10, 24, 10);
        formCard.add(formSub, gc);
        gc.gridwidth = 1; gc.insets = new Insets(10, 10, 10, 10);

        addFormRow(formCard, gc, 2, "Client Name", nameField);
        addFormRow(formCard, gc, 3, "Pet Name",    petField);
        addFormRow(formCard, gc, 4, "Date",        dateSpinner);
        addFormRow(formCard, gc, 5, "Time",        timeSpinner);

        gc.gridx = 1; gc.gridy = 6; gc.weightx = 0;
        gc.anchor = GridBagConstraints.EAST;
        gc.insets = new Insets(24, 10, 0, 10);
        formCard.add(bookButton, gc);

        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(WHITE);
        outputArea.setForeground(GREEN_500);
        outputArea.setFont(FONT_MONO);
        outputArea.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JPanel logPanel = new JPanel(new BorderLayout(0, 10));
        logPanel.setOpaque(false);
        logPanel.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));
        JLabel logTitle = lbl("Activity Log");
        logPanel.add(logTitle, BorderLayout.NORTH);
        logPanel.add(lightScroll(outputArea), BorderLayout.CENTER);

        bookingPanel.add(formCard, BorderLayout.NORTH);
        bookingPanel.add(logPanel, BorderLayout.CENTER);

        // History panel
        JPanel historyPanel = new JPanel(new BorderLayout(0, 20));
        historyPanel.setOpaque(false);
        historyPanel.setBorder(BorderFactory.createEmptyBorder(36, 44, 36, 44));

        JPanel histHeader = new JPanel(new BorderLayout());
        histHeader.setOpaque(false);
        histHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel histTitle = new JLabel("Appointment History");
        histTitle.setFont(FONT_TITLE); histTitle.setForeground(TEXT_DARK);
        JLabel histSub = new JLabel("All past and upcoming appointments.");
        histSub.setFont(FONT_SMALL); histSub.setForeground(TEXT_LIGHT);

        JPanel histLeft = new JPanel();
        histLeft.setOpaque(false);
        histLeft.setLayout(new BoxLayout(histLeft, BoxLayout.Y_AXIS));
        histLeft.add(histTitle); histLeft.add(Box.createVerticalStrut(4)); histLeft.add(histSub);

        JButton refreshBtn = ghostBtn("Refresh");
        refreshBtn.setPreferredSize(new Dimension(110, 40));
        histHeader.add(histLeft, BorderLayout.WEST);
        histHeader.add(refreshBtn, BorderLayout.EAST);

        String[] historyCols = {"Pet", "Date", "Time", "Status", "Diagnosis", "Total Bill", "Payment"};
        DefaultTableModel historyModel = new DefaultTableModel(historyCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable historyTable = new JTable(historyModel);
        styleTable(historyTable);

        historyPanel.add(histHeader, BorderLayout.NORTH);
        historyPanel.add(lightScroll(historyTable), BorderLayout.CENTER);

        tabs.addTab("  Book  ", bookingPanel);
        tabs.addTab("  History  ", historyPanel);

        root.add(sidebar, BorderLayout.WEST);
        root.add(tabs, BorderLayout.CENTER);

        bookButton.addActionListener(e -> {
            String name = nameField.getText();
            String pet  = petField.getText();
            String date = dateEditor.getFormat().format((Date) dateSpinner.getValue());
            String time = timeEditor.getFormat().format((Date) timeSpinner.getValue());
            if (name.isEmpty() || pet.isEmpty()) {
                showMsg("Missing Fields", "Please fill in all fields.", false); return;
            }
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO appointments (client_name, pet_name, appt_date, appt_time) VALUES (?, ?, ?, ?)")) {
                stmt.setString(1, name); stmt.setString(2, pet);
                stmt.setString(3, date); stmt.setString(4, time);
                stmt.executeUpdate();
                outputArea.append("Booked  " + pet + "  —  " + date + " at " + time + "\n");
                petField.setText("");
            } catch (SQLException ex) {
                ex.printStackTrace();
                showMsg("Database Error", ex.getMessage(), false);
            }
        });

        refreshBtn.addActionListener(e -> loadClientHistory(historyModel, loggedInClientName));
        logoutBtn.addActionListener(e -> { dispose(); new Main(); });

        setVisible(true); revalidate(); repaint();
    }

    // ── VET PORTAL ─────────────────────────────────────────────────────────────

    public void vetPortal() {
        getContentPane().removeAll();
        setTitle("VetCare — Veterinarian");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = lightBgPanel();
        root.setLayout(new BorderLayout());
        setContentPane(root);

        JPanel sidebar = sidebarPanel("Veterinarian Portal");
        JPanel sideTop = (JPanel) sidebar.getComponent(0);

        JLabel infoLbl = new JLabel("Clinic Dashboard");
        infoLbl.setFont(FONT_BODY); infoLbl.setForeground(TEXT_MID);
        infoLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoLbl.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        sideTop.add(infoLbl);

        JPanel sideBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sideBottom.setOpaque(false);
        sideBottom.setBorder(BorderFactory.createEmptyBorder(0, 16, 28, 16));
        JButton logoutBtn = ghostBtn("Sign Out");
        logoutBtn.setPreferredSize(new Dimension(210, 42));
        sideBottom.add(logoutBtn);
        sidebar.add(sideTop, BorderLayout.NORTH);
        sidebar.add(sideBottom, BorderLayout.SOUTH);

        JPanel mainArea = new JPanel(new BorderLayout(0, 0));
        mainArea.setOpaque(false);
        mainArea.setBorder(BorderFactory.createEmptyBorder(36, 44, 0, 44));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JLabel tableTitle = new JLabel("All Appointments");
        tableTitle.setFont(FONT_TITLE); tableTitle.setForeground(TEXT_DARK);
        JLabel tableSub = new JLabel("Select a row, then choose an action below.");
        tableSub.setFont(FONT_SMALL); tableSub.setForeground(TEXT_LIGHT);

        JPanel tableTitleStack = new JPanel();
        tableTitleStack.setOpaque(false);
        tableTitleStack.setLayout(new BoxLayout(tableTitleStack, BoxLayout.Y_AXIS));
        tableTitleStack.add(tableTitle);
        tableTitleStack.add(Box.createVerticalStrut(4));
        tableTitleStack.add(tableSub);
        tableHeader.add(tableTitleStack, BorderLayout.WEST);

        String[] columns = {"ID", "Client", "Pet", "Date", "Time", "Status", "Diagnosis", "Bill", "Payment"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);

        mainArea.add(tableHeader, BorderLayout.NORTH);
        mainArea.add(lightScroll(table), BorderLayout.CENTER);

        JPanel actionBar = new JPanel();
        actionBar.setBackground(WHITE);
        actionBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            BorderFactory.createEmptyBorder(18, 44, 18, 44)));
        actionBar.setLayout(new BoxLayout(actionBar, BoxLayout.X_AXIS));

        JLabel actionLbl = lbl("Action");
        JComboBox<String> actionDropdown = new JComboBox<>(
            new String[]{"Approve Appointment", "Process Medical", "Mark as Paid"});
        styleComboBox(actionDropdown);
        actionDropdown.setMaximumSize(new Dimension(260, 50));
        actionDropdown.setPreferredSize(new Dimension(260, 50));

        JButton executeBtn = primaryBtn("Execute");
        JButton refreshBtn = ghostBtn("Refresh");
        executeBtn.setPreferredSize(new Dimension(150, 50));
        refreshBtn.setPreferredSize(new Dimension(130, 50));

        actionBar.add(actionLbl);
        actionBar.add(Box.createHorizontalStrut(14));
        actionBar.add(actionDropdown);
        actionBar.add(Box.createHorizontalStrut(14));
        actionBar.add(executeBtn);
        actionBar.add(Box.createHorizontalStrut(10));
        actionBar.add(refreshBtn);
        actionBar.add(Box.createHorizontalGlue());

        loadVetData(model);

        root.add(sidebar, BorderLayout.WEST);
        root.add(mainArea, BorderLayout.CENTER);
        root.add(actionBar, BorderLayout.SOUTH);

        executeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { showMsg("No Selection", "Please select a row first.", false); return; }
            int apptId = (int) table.getValueAt(row, 0);
            String sel = (String) actionDropdown.getSelectedItem();
            if ("Approve Appointment".equals(sel))  updateAppointmentStatus(apptId, "Approved");
            else if ("Process Medical".equals(sel)) processMedicalData(apptId);
            else if ("Mark as Paid".equals(sel))    markAppointmentPaid(apptId);
            loadVetData(model);
        });
        refreshBtn.addActionListener(e -> loadVetData(model));
        logoutBtn.addActionListener(e -> { dispose(); new Main(); });

        setVisible(true); revalidate(); repaint();
    }

    // ── DATA HELPERS ───────────────────────────────────────────────────────────

    private void addFormRow(JPanel panel, GridBagConstraints gc, int row, String labelText, JComponent field) {
        field.setPreferredSize(new Dimension(360, 50));
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; gc.anchor = GridBagConstraints.WEST;
        JLabel l = lbl(labelText);
        l.setPreferredSize(new Dimension(160, 50));
        panel.add(l, gc);
        gc.gridx = 1; gc.weightx = 1;
        panel.add(field, gc);
    }

    private void loadClientHistory(DefaultTableModel model, String clientName) {
        model.setRowCount(0);
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM appointments WHERE client_name = ?")) {
            stmt.setString(1, clientName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("pet_name"), rs.getString("appt_date"),
                    rs.getString("appt_time"), rs.getString("status"),
                    rs.getString("diagnosis"),
                    "P" + rs.getDouble("total_bill"),
                    rs.getBoolean("is_paid") ? "PAID" : "UNPAID"
                });
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadVetData(DefaultTableModel model) {
        model.setRowCount(0);
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM appointments")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),            rs.getString("client_name"),
                    rs.getString("pet_name"),   rs.getString("appt_date"),
                    rs.getString("appt_time"),  rs.getString("status"),
                    rs.getString("diagnosis"),  "P" + rs.getDouble("total_bill"),
                    rs.getBoolean("is_paid") ? "PAID" : "UNPAID"
                });
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void updateAppointmentStatus(int id, String status) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE appointments SET status = ? WHERE id = ?")) {
            stmt.setString(1, status); stmt.setInt(2, id); stmt.executeUpdate();
            showMsg("Updated", "Appointment marked as " + status + ".", true);
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void processMedicalData(int id) {
        String diagnosis = JOptionPane.showInputDialog(this, "Enter Diagnosis:");
        if (diagnosis == null) return;
        String[] labs = {"Blood Test", "X-Ray", "None"};
        String[] treatments = {"Vaccination", "Minor Surgery", "None"};
        String lab = (String) JOptionPane.showInputDialog(this, "Select Lab", "Lab Service",
            JOptionPane.PLAIN_MESSAGE, null, labs, labs[0]);
        if (lab == null) return;
        String treatment = (String) JOptionPane.showInputDialog(this, "Select Treatment", "Treatment",
            JOptionPane.PLAIN_MESSAGE, null, treatments, treatments[0]);
        if (treatment == null) return;
        double labFee   = lab.equals("Blood Test") ? 800 : (lab.equals("X-Ray") ? 1500 : 0);
        double treatFee = treatment.equals("Vaccination") ? 700 : (treatment.equals("Minor Surgery") ? 5000 : 0);
        double total    = 500 + labFee + treatFee;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE appointments SET diagnosis=?,lab_service=?,lab_fee=?,treatment_service=?,treatment_fee=?,total_bill=?,status='Completed' WHERE id=?")) {
            stmt.setString(1, diagnosis); stmt.setString(2, lab);
            stmt.setDouble(3, labFee);   stmt.setString(4, treatment);
            stmt.setDouble(5, treatFee); stmt.setDouble(6, total);
            stmt.setInt(7, id); stmt.executeUpdate();
            showMsg("Processed", "Medical data saved. Total: P" + total, true);
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void markAppointmentPaid(int id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE appointments SET is_paid = TRUE WHERE id = ?")) {
            stmt.setInt(1, id); stmt.executeUpdate();
            showMsg("Payment", "Payment recorded successfully.", true);
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    // ── INNER CLASSES ──────────────────────────────────────────────────────────

    static class FocusBorder extends AbstractBorder {
        private final int radius;
        private final Color normal, focus;
        private final int thickness;

        FocusBorder(int r, Color n, Color f, int t) {
            radius = r; normal = n; this.focus = f; thickness = t;
        }

        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean focused = c.isFocusOwner();
            g2.setColor(focused ? focus : normal);
            g2.setStroke(new BasicStroke(focused ? 1.6f : thickness));
            g2.drawRoundRect(x + 1, y + 1, w - 2, h - 2, radius, radius);
            g2.dispose();
        }

        @Override public Insets getBorderInsets(Component c) {
            return new Insets(radius / 4, radius / 4, radius / 4, radius / 4);
        }
    }

    static class LightTabUI extends BasicTabbedPaneUI {
        @Override protected void installDefaults() {
            super.installDefaults();
            tabPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        }
        @Override protected void paintTabBackground(Graphics g, int tp, int idx, int x, int y, int w, int h, boolean sel) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(sel ? new Color(255, 255, 255, 240) : new Color(240, 250, 244, 100));
            g2.fillRoundRect(x, y, w, h + 4, 10, 10);
            if (sel) {
                g2.setColor(new Color(0x2E9E5B));
                g2.setStroke(new BasicStroke(2.2f));
                g2.drawLine(x + 10, y + h, x + w - 10, y + h);
            }
            g2.dispose();
        }
        @Override protected void paintTabBorder(Graphics g, int tp, int idx, int x, int y, int w, int h, boolean sel) {}
        @Override protected void paintFocusIndicator(Graphics g, int tp, Rectangle[] r, int idx, Rectangle ir, Rectangle tr, boolean sel) {}
        @Override protected void paintContentBorder(Graphics g, int tp, int sel) {
            g.setColor(new Color(0xD6EDE0));
            g.drawLine(tabPane.getX(),
                tabPane.getY() + calculateTabAreaHeight(tp, runCount, maxTabHeight),
                tabPane.getX() + tabPane.getWidth(),
                tabPane.getY() + calculateTabAreaHeight(tp, runCount, maxTabHeight));
        }
        @Override protected int calculateTabHeight(int tp, int idx, int fh) { return 46; }
    }

    // ── MAIN ───────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> new Main());
    }
}