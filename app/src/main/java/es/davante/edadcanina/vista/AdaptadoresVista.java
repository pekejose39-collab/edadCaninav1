package es.davante.edadcanina.vista;

import android.content.Context;
import android.widget.EditText;

import androidx.databinding.BindingAdapter;

import es.davante.edadcanina.R;
import es.davante.edadcanina.modelo.EdadCaninaModelo;
import es.davante.edadcanina.modelo.Validacion;

/**
 * Atributos XML propios para la vista.
 *
 * Va en el paquete vista (y no en modelo) porque decide cómo se ve algo: elige
 * el texto traducido con getString, lo pone en el EditText y mueve el foco.
 * Todo eso es Android y presentación; el modelo solo dice qué Validacion hay.
 */
public final class AdaptadoresVista {

    private AdaptadoresVista() {
        // Solo métodos estáticos: no tiene sentido crear objetos de esta clase.
    }

    /**
     * Programa el atributo app:errorValidacion="@{modelo.validacion}".
     * DataBinding lo llama cada vez que el modelo avisa de BR.validacion.
     * Debe ser public static; el primer parámetro es la vista y el segundo,
     * el valor de la expresión.
     */
    @BindingAdapter("errorValidacion")
    public static void mostrarErrorValidacion(EditText campo, Validacion validacion) {
        String mensaje = mensajeDeError(campo.getContext(), validacion);
        campo.setError(mensaje);          // null quita el error
        if (mensaje != null) {
            campo.requestFocus();         // llevamos al usuario a donde está el problema
        }
    }

    // Traduce la Validacion a un texto para el usuario. Aquí sí puede haber
    // textos, porque dependen del idioma y salen de strings.xml.
    private static String mensajeDeError(Context contexto, Validacion validacion) {
        if (validacion == null) return null;  // al enlazar por primera vez puede llegar null
        switch (validacion) {
            case VACIO:
                return contexto.getString(R.string.error_vacio);
            case NO_NUMERO:
                return contexto.getString(R.string.error_no_numero);
            case FUERA_DE_RANGO:
                return contexto.getString(R.string.error_rango,
                        EdadCaninaModelo.EDAD_MINIMA, EdadCaninaModelo.EDAD_MAXIMA);
            default:
                return null;                  // OK o SIN_CALCULAR: sin error
        }
    }
}