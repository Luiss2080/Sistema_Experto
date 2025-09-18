package vista.comun;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Utilidad para actualizar interfaces de usuario
 */
public class ActualizadorInterfaces {

    /**
     * Interfaz para objetos que pueden ser actualizados
     */
    public interface Actualizable {
        void actualizar();
    }

    /**
     * Crea un botón de actualizar estándar
     */
    public static JButton crearBotonActualizar(Actualizable objetivo) {
        JButton btnActualizar = new JButton("🔄 Actualizar");
        btnActualizar.setToolTipText("Actualizar la lista de elementos");

        btnActualizar.addActionListener(e -> {
            try {
                objetivo.actualizar();
                mostrarMensajeExito((Component) objetivo, "Lista actualizada correctamente");
            } catch (Exception ex) {
                mostrarMensajeError((Component) objetivo, "Error al actualizar: " + ex.getMessage());
            }
        });

        return btnActualizar;
    }

    /**
     * Crea un botón de actualizar simple (sin emoji para compatibilidad)
     */
    public static JButton crearBotonActualizarSimple(Actualizable objetivo) {
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setToolTipText("Actualizar la lista de elementos");

        btnActualizar.addActionListener(e -> {
            try {
                objetivo.actualizar();
                mostrarMensajeExito((Component) objetivo, "Lista actualizada correctamente");
            } catch (Exception ex) {
                mostrarMensajeError((Component) objetivo, "Error al actualizar: " + ex.getMessage());
            }
        });

        return btnActualizar;
    }

    /**
     * Muestra un mensaje de éxito
     */
    public static void mostrarMensajeExito(Component parent, String mensaje) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(parent),
                mensaje,
                "Actualización Exitosa",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
    }

    /**
     * Muestra un mensaje de error
     */
    public static void mostrarMensajeError(Component parent, String mensaje) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(parent),
                mensaje,
                "Error de Actualización",
                JOptionPane.ERROR_MESSAGE
            );
        });
    }

    /**
     * Actualiza un ComboBox manteniendo la selección si es posible
     */
    public static <T> void actualizarComboBox(JComboBox<T> combo, java.util.List<T> nuevosElementos) {
        T seleccionada = (T) combo.getSelectedItem();

        combo.removeAllItems();
        for (T elemento : nuevosElementos) {
            combo.addItem(elemento);
        }

        // Restaurar selección si aún existe
        if (seleccionada != null) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                T item = combo.getItemAt(i);
                if (item != null && item.equals(seleccionada)) {
                    combo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    /**
     * Actualiza una lista manteniendo la selección si es posible
     */
    public static <T> void actualizarLista(JList<T> lista, DefaultListModel<T> modelo, java.util.List<T> nuevosElementos) {
        T seleccionada = lista.getSelectedValue();

        modelo.clear();
        for (T elemento : nuevosElementos) {
            modelo.addElement(elemento);
        }

        // Restaurar selección si aún existe
        if (seleccionada != null) {
            for (int i = 0; i < modelo.size(); i++) {
                T item = modelo.getElementAt(i);
                if (item != null && item.equals(seleccionada)) {
                    lista.setSelectedIndex(i);
                    lista.ensureIndexIsVisible(i);
                    break;
                }
            }
        }
    }

    /**
     * Programa una actualización en el hilo de eventos de Swing
     */
    public static void programarActualizacion(Runnable actualizacion) {
        SwingUtilities.invokeLater(actualizacion);
    }

    /**
     * Programa una actualización con delay
     */
    public static void programarActualizacionConDelay(Runnable actualizacion, int delayMs) {
        Timer timer = new Timer(delayMs, e -> actualizacion.run());
        timer.setRepeats(false);
        timer.start();
    }
}