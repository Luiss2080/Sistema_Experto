import javax.swing.*;
import vista.login.LoginPrincipal;

public class SistemaExperto {
    public static void main(String[] args) {
        try {
            // Intentar usar el look and feel del sistema
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Si falla, usar el look and feel por defecto
            System.out.println("Usando Look and Feel por defecto");
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginPrincipal().setVisible(true);
            }
        });
    }
}