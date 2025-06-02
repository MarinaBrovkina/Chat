package ru.shift.view;

import javax.swing.*;
import java.awt.*;

public class ErrorWindow extends JDialog {
    private final JLabel errorLabel;

    public ErrorWindow(JFrame owner) {
        super(owner, "Ошибка (ಥ﹏ಥ)", true);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 150));
        setResizable(false);

        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(errorLabel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        pack();
        setLocationRelativeTo(owner);
    }

    public void setErrorMessage(String errorMessage) {
        errorLabel.setText("<html><div style='text-align: center;'>" +
                errorMessage + "</div></html>");
    }
}