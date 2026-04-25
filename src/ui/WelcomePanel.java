package ui;

import javax.swing.*;
import java.awt.*;

public class WelcomePanel extends JPanel {

    public WelcomePanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 252));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 220, 240)),
            BorderFactory.createEmptyBorder(50, 60, 50, 60)
        ));

        JLabel icon = new JLabel("👋");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel("Welcome, HR Administrator");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(25, 55, 109));
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Select an action from the left-hand panel to begin.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 110, 130));
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        card.add(icon);
        card.add(Box.createVerticalStrut(16));
        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(subtitle);

        add(card);
    }
}
