package es.davante.edadcanina.modelo;

/**
 * Resultado de validar la entrada. El modelo devuelve uno de estos valores y no
 * un mensaje, porque los mensajes son textos para el usuario (dependen del idioma
 * y de getString, que es de Android) y eso es trabajo de la vista.
 */
public enum Validacion {
    /** Todavía no se ha calculado, o el texto cambió y el resultado anterior ya no vale. */
    SIN_CALCULAR,
    /** Entrada correcta: hay edad humana calculada. */
    OK,
    /** El campo está vacío (o solo tiene espacios). */
    VACIO,
    /** El texto no es un número entero. */
    NO_NUMERO,
    /** Es un número, pero no está entre EDAD_MINIMA y EDAD_MAXIMA. */
    FUERA_DE_RANGO
}