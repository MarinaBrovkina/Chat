package ru.shift;

import lombok.extern.slf4j.Slf4j;
import ru.shift.controller.ClientController;
import ru.shift.exeptions.MessageException;

import javax.swing.*;

@Slf4j
public class MainClient {
      public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new ClientController();
            } catch (MessageException e) {
                log.error("Message processing error: {}", e.getMessage(), e);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Failed to start application",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
