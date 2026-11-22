package es.davante.edadcanina.modelo;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.Objects;

import es.davante.edadcanina.BR;

/**
 * Modelo de EdadCanina: guarda los datos, aplica las reglas (validar y calcular)
 * y avisa cuando algo cambia.
 *
 * Solo depende de androidx.databinding (no de android.*): así se puede probar con
 * JUnit sin emulador y no hay que tocarlo si cambia la pantalla o el idioma.
 *
 * Hereda de BaseObservable para poder avisar a quien le escuche (el binding):
 * el modelo no sabe qué vistas hay, solo dice "esta propiedad ha cambiado" y
 * DataBinding repinta lo que dependa de ella.
 */
public class EdadCaninaModelo extends BaseObservable {

    // Reglas de negocio. Son públicas porque la vista necesita los límites para
    // construir el mensaje de error de rango (el número, no el texto).
    public static final int FACTOR_CANINO = 7;
    public static final int EDAD_MINIMA = 1;
    public static final int EDAD_MAXIMA = 30; // ningún perro supera esta edad

    // Estado: lo que ha escrito el usuario, el resultado y cómo fue la validación.
    private String edadTexto = "";
    private int edadHumana = 0;
    private Validacion validacion = Validacion.SIN_CALCULAR;

    // @Bindable en cada getter que lee la vista: genera BR.edadTexto, BR.edadHumana
    // y BR.validacion, que son los identificadores con los que avisamos.
    @Bindable
    public String getEdadTexto() {
        return edadTexto;
    }

    @Bindable
    public int getEdadHumana() {
        return edadHumana;
    }

    @Bindable
    public Validacion getValidacion() {
        return validacion;
    }

    /**
     * Lo llama la vista cada vez que el usuario escribe (enlace @={...}).
     */
    public void setEdadTexto(String nuevo) {
        if (nuevo == null) nuevo = "";   // nunca guardamos null: evita NPE en calcular()
        // Si no ha cambiado, no avisamos. Sin este if, el enlace bidireccional
        // entraría en bucle: aviso → la vista reescribe el texto → la vista llama
        // otra vez a este setter → aviso → ...
        if (nuevo.equals(edadTexto)) return;
        edadTexto = nuevo;
        notifyPropertyChanged(BR.edadTexto);
        // Regla: si el texto cambia, el resultado anterior deja de valer.
        invalidarResultado();
    }

    // Setters privados: solo calcular() (y las demás operaciones del modelo)
    // pueden cambiar el resultado. Nadie de fuera puede "inventarse" una edad.
    private void setEdadHumana(int nueva) {
        if (nueva == edadHumana) return;
        edadHumana = nueva;
        notifyPropertyChanged(BR.edadHumana);
    }

    private void setValidacion(Validacion nueva) {
        if (Objects.equals(nueva, validacion)) return;
        validacion = nueva;
        notifyPropertyChanged(BR.validacion);
    }

    /**
     * Valida el texto y, si es correcto, calcula la edad humana.
     * Devuelve la Validacion para que quien llame pueda decidir qué hacer,
     * aunque la vista ya se entera sola gracias a los avisos.
     */
    public Validacion calcular() {
        String texto = edadTexto.trim();

        if (texto.isEmpty()) {
            return error(Validacion.VACIO);
        }

        int edad;
        try {
            edad = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            // parseInt es quien sabe si es un número: capturar la excepción evita
            // que la app se cierre con entradas como "abc".
            return error(Validacion.NO_NUMERO);
        }

        if (edad < EDAD_MINIMA || edad > EDAD_MAXIMA) {
            // Limitar a 30 también evita el desbordamiento de int al multiplicar.
            return error(Validacion.FUERA_DE_RANGO);
        }

        // Primero la edad y después la validación: cuando la vista vea OK y haga
        // visible el resultado, la edad ya será la correcta.
        setEdadHumana(edad * FACTOR_CANINO);
        setValidacion(Validacion.OK);
        return validacion;
    }

    // Un error borra cualquier resultado anterior: no queremos mostrar una edad
    /**
     * Vuelve al estado inicial: sin texto, sin resultado y sin error.
     * Es una operación del modelo porque decide qué significa "empezar de cero".
     */
    public void limpiar() {
        setEdadTexto("");
        // Hace falta aunque setEdadTexto ya invalide: si el texto ya estaba vacío
        // (por ejemplo, tras el error VACIO) setEdadTexto no hace nada.
        invalidarResultado();
    }
    // vieja junto a un mensaje de error nuevo.
    private Validacion error(Validacion tipo) {
        setEdadHumana(0);
        setValidacion(tipo);
        return validacion;
    }

    private void invalidarResultado() {
        setEdadHumana(0);
        setValidacion(Validacion.SIN_CALCULAR);
    }
}