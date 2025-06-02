package ru.shift.view;

import lombok.Setter;
import ru.shift.UsernameValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class NameWindow extends JDialog {
    JButton cancelButton;
    private final JTextField nameTextField;
    @Setter
    private NameListener nameListener;

    public NameWindow(JFrame owner) {
        super(owner, "Выбор имени", true);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Введите ваше имя для чата:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(titleLabel, gbc);

        JLabel nameLabel = new JLabel("Имя:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        contentPanel.add(nameLabel, gbc);

        nameTextField = new JTextField(20);
        nameTextField.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 1;
        contentPanel.add(nameTextField, gbc);

        JLabel hintLabel = new JLabel("(3-20 символов, только A-Z, 0-9 и _)");
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        hintLabel.setForeground(Color.GRAY);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        contentPanel.add(hintLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        cancelButton = new JButton("Отмена");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 12));
        cancelButton.addActionListener(e -> dispose());

        JButton joinButton = new JButton("Войти");
        joinButton.setFont(new Font("Arial", Font.PLAIN, 12));
        joinButton.addActionListener(e -> handleJoin());

        buttonPanel.add(joinButton);
        buttonPanel.add(cancelButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);

        nameTextField.addActionListener(e -> handleJoin());
    }

    private void handleJoin() {
        String name = nameTextField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Имя не может быть пустым",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (UsernameValidator.isInvalid(name)) {
            JOptionPane.showMessageDialog(this,
                    "Некорректное имя. Используйте:\n" +
                            "- Латиницу (A-Z)\n" +
                            "- Цифры (0-9)\n" +
                            "- Подчёркивание (_)\n" +
                            "- Длину 3-20 символов",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nameListener != null) {
            nameListener.onName(name);
        }
    }

    public void setExitListener(ActionListener actionListener) {
       cancelButton.addActionListener(actionListener);
    }
}