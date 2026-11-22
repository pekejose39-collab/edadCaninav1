package es.davante.edadcanina.controlador;

import es.davante.edadcanina.modelo.EdadCaninaModelo;

/**
 * Controlador: convierte cada acción del usuario en una orden al modelo.
 *
 * No tiene referencias a vistas ni pinta nada: después de la orden, el modelo
 * avisa y DataBinding repinta la pantalla. Que sea tan corto es buena señal:
 * las reglas están en el modelo y la presentación en la vista.
 */
public class EdadCaninaControlador {

    private final EdadCaninaModelo modelo;

    // Recibe el modelo desde fuera (lo crea la Activity): así en las pruebas
    // podemos darle un modelo nuestro y comprobar qué le ha pasado.
    public EdadCaninaControlador(EdadCaninaModelo modelo) {
        this.modelo = modelo;
    }

    /** Botón Calcular: android:onClick="@{() -> controlador.onCalcularClick()}" */
    public void onCalcularClick() {
        modelo.calcular();
    }

    /** Botón Limpiar: android:onClick="@{() -> controlador.onLimpiarClick()}" */
    public void onLimpiarClick() {
        modelo.limpiar();
    }
}