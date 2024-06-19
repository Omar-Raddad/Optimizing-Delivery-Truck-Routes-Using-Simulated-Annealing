

import ui.*;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainUi gui = new MainUi();
            gui.setVisible(true);
        });
    }
}
