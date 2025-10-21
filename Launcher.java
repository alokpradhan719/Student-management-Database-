import javax.swing.*;
import java.awt.*;

public class Launcher extends JFrame {
    private JButton registerButton;
    private JButton viewDataButton;
    private JButton exitButton;

    public Launcher() {
        super("Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 220);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                Color top = new Color(249, 250, 251);
                Color bottom = new Color(232, 234, 237);
                g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bottom));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setOpaque(true);

        JLabel title = new JLabel("Student Registration", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        panel.add(title, BorderLayout.NORTH);

        JLabel msg = new JLabel("Welcome! Click Register to continue.", SwingConstants.CENTER);
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        msg.setForeground(new Color(97, 97, 97));
        panel.add(msg, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        registerButton = pillButton("Register", new Color(33, 150, 243));
        viewDataButton = pillButton("View Data", new Color(98, 0, 238));
        exitButton = pillButton("Exit", new Color(244, 67, 54));
        buttons.add(registerButton);
        buttons.add(viewDataButton);
        buttons.add(exitButton);
        panel.add(buttons, BorderLayout.SOUTH);

        setContentPane(panel);

        registerButton.addActionListener(e -> {
            StudentRegistrationForm form = new StudentRegistrationForm();
            form.setVisible(true);
            dispose();
        });
        viewDataButton.addActionListener(e -> {
            StudentRegistrationForm form = new StudentRegistrationForm();
            form.showAllStudents();
        });
        exitButton.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo i : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(i.getName())) { UIManager.setLookAndFeel(i.getClassName()); break; }
            }
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new Launcher().setVisible(true));
    }

    private JButton pillButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 1, true),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        b.setOpaque(true);
        return b;
    }
}