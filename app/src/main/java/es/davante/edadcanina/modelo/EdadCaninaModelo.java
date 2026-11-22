package es.davante.edadcanina.modelo;

/**
 * Modelo de EdadCanina: guarda los datos y aplica las reglas (validar y calcular).
 * No importa nada de android.*: así se puede probar con JUnit sin emulador y no
 * hay que tocarlo si cambia la pantalla o el idioma.
 */
public class EdadCaninaModelo {

    // Reglas de negocio. Son públicas porque la vista necesita los límites para
    // construir el mensaje de error de rango (el número, no el texto).
    public static final int FACTOR_CANINO = 7;
    public static final int EDAD_MINIMA = 1;
    public static final int EDAD_MAXIMA = 30; // ningún perro supera esta edad

    // Estado: lo que ha escrito el usuario, el resultado y cómo fue la validación.
    private String edadTexto = "";
    private int edadHumana = 0;
    private Validacion validacion = Validacion.SIN_CALCULAR;

    public String getEdadTexto() {
        return edadTexto;
    }

    public int getEdadHumana() {
        return edadHumana;
    }

    public Validacion getValidacion() {
        return validacion;
    }

    public void setEdadTexto(String nuevo) {
        if (nuevo == null) nuevo = "";   // nunca guardamos null: evita NPE en calcular()
        if (nuevo.equals(edadTexto)) return;
        edadTexto = nuevo;
        // Regla: si el texto cambia, el resultado anterior deja de valer.
        // Va aquí (y no en la Activity) porque es una regla, no una forma de pintar.
        invalidarResultado();
    }

    /**
     * Valida el texto y, si es correcto, calcula la edad humana.
     * Devuelve la Validacion para que quien llame decida qué mostrar.
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

        edadHumana = edad * FACTOR_CANINO;
        validacion = Validacion.OK;
        return validacion;
    }

    // Un error borra cualquier resultado anterior: no queremos mostrar una edad
    // vieja junto a un mensaje de error nuevo.
    private Validacion error(Validacion tipo) {
        edadHumana = 0;
        validacion = tipo;
        return validacion;
    }

    private void invalidarResultado() {
        edadHumana = 0;
        validacion = Validacion.SIN_CALCULAR;
    }
}