package ru.shift.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnectWindow extends JDialog {
    private final JTextField hostTextField;
    private final JTextField portTextField;
    private final JButton connectButton;
    private final JButton cancelButton;
    private boolean isConnecting = false;

    public ConnectWindow(JFrame owner) {
        super(owner, "Параметры сервера", true);
        setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("Host:"), gbc);

        gbc.gridx = 1;
        hostTextField = new JTextField(15);
        hostTextField.setText("localhost");
        contentPanel.add(hostTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("Port:"), gbc);

        gbc.gridx = 1;
        portTextField = new JTextField(15);
        portTextField.setText("1234");
        contentPanel.add(portTextField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cancelButton = new JButton("Отмена");
        connectButton = new JButton("Подключиться");

        buttonPanel.add(connectButton);
        buttonPanel.add(cancelButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setMinimumSize(new Dimension(300, getHeight()));
        setResizable(false);
        setLocationRelativeTo(owner);
        connectButton.addActionListener(e -> validateAndConnect());
    }

    private void validateAndConnect() {
        if (isConnecting) {
            return;
        }

        String host = getHost();
        String port = getPort();

        if (host.isEmpty() || port.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Поля Host и Port не могут быть пустыми",
                    "Ошибка ввода",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidHost(host)) {
            JOptionPane.showMessageDialog(this,
                    "Некорректный формат host. Введите наименование localhost",
                    "Ошибка ввода",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int portNumber = Integer.parseInt(port);
            if (portNumber < 1024 || portNumber > 65535) {
                JOptionPane.showMessageDialog(this,
                        "Port должен быть в диапазоне от 1024 до 65535",
                        "Ошибка ввода",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            isConnecting = true;
            connectButton.setEnabled(false);

            for (ActionListener listener : connectButton.getActionListeners()) {
                listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "connect"));
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Port должен быть числом",
                    "Ошибка ввода",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidHost(String host) {
        return host.equals("localhost");
    }

    public String getHost() {
        return hostTextField.getText().trim();
    }

    public String getPort() {
        return portTextField.getText().trim();
    }

    public void setConnectActionListener(ActionListener listener) {
        connectButton.addActionListener(listener);
    }

    public void setCancelActionListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }
}
