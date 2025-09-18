package vista.login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.entidades.Usuario;

public class RegistroUsuario extends JDialog {
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JPasswordField txtConfirmarContrasena;
    private JComboBox<Usuario.TipoUsuario> cmbTipoUsuario;
    private JButton btnRegistrar;
    private JButton btnCancelar;
    private LoginPrincipal parent;

    public RegistroUsuario(LoginPrincipal parent) {
        super(parent, "Registro de Usuario", true);
        this.parent = parent;
        initComponents();
    }

    private void initComponents() {
        setSize(350, 250);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panelPrincipal.add(new JLabel("Usuario:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtUsuario = new JTextField(15);
        panelPrincipal.add(txtUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panelPrincipal.add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtContrasena = new JPasswordField(15);
        panelPrincipal.add(txtContrasena, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        panelPrincipal.add(new JLabel("Confirmar:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtConfirmarContrasena = new JPasswordField(15);
        panelPrincipal.add(txtConfirmarContrasena, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        panelPrincipal.add(new JLabel("Tipo:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbTipoUsuario = new JComboBox<>(Usuario.TipoUsuario.values());
        cmbTipoUsuario.setSelectedItem(Usuario.TipoUsuario.CLIENTE);
        panelPrincipal.add(cmbTipoUsuario, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnRegistrar = new JButton("Registrar");
        btnCancelar = new JButton("Cancelar");

        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrar();
            }
        });

        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelPrincipal.add(panelBotones, gbc);

        add(panelPrincipal);
    }

    private void registrar() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());
        String confirmarContrasena = new String(txtConfirmarContrasena.getPassword());
        Usuario.TipoUsuario tipo = (Usuario.TipoUsuario) cmbTipoUsuario.getSelectedItem();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe completar todos los campos",
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden",
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario nuevoUsuario = new Usuario(usuario, contrasena, tipo);

        if (parent.getUsuarioDAO().crear(nuevoUsuario)) {
            JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente",
                                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar usuario",
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}