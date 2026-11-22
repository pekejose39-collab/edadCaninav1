package es.davante.edadcanina.modelo;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Reto «Casos raros». Decisión: solo se aceptan cifras del 0 al 9.
 *  - Los ceros a la izquierda no cambian el número: "05" es un 5 válido.
 *  - Los signos no son cifras: "+5" y "-4" son NO_NUMERO.
 *  - Muchas cifras sí son un número, solo que enorme: FUERA_DE_RANGO.
 * El teclado numérico ya impide muchas de estas entradas, pero el modelo no
 * debe fiarse de la vista (se puede pegar texto, o cambiar el layout mañana).
 */
public class CasosRarosTest {

    private EdadCaninaModelo modelo;

    @Before
    public void setUp() {
        modelo = new EdadCaninaModelo();
    }

    @Test
    public void ceroALaIzquierda_esValido() {
        modelo.setEdadTexto("05");
        assertEquals(Validacion.OK, modelo.calcular());
        assertEquals(35, modelo.getEdadHumana());
    }

    @Test
    public void signoMas_noEsNumero() {
        // Integer.parseInt("+5") devuelve 5: sin la regla nueva esto daba OK.
        modelo.setEdadTexto("+5");
        assertEquals(Validacion.NO_NUMERO, modelo.calcular());
    }

    @Test
    public void signoMenos_noEsNumero() {
        // Antes daba FUERA_DE_RANGO, que es un mensaje confuso para "-4".
        modelo.setEdadTexto("-4");
        assertEquals(Validacion.NO_NUMERO, modelo.calcular());
    }

    @Test
    public void decimal_noEsNumero() {
        modelo.setEdadTexto("3.5");
        assertEquals(Validacion.NO_NUMERO, modelo.calcular());
    }

    @Test
    public void numeroEnorme_esFueraDeRango() {
        // No cabe en un int, pero son cifras: el problema es el rango.
        modelo.setEdadTexto("99999999999");
        assertEquals(Validacion.FUERA_DE_RANGO, modelo.calcular());
    }

    @Test
    public void espaciosAlrededor_seIgnoran() {
        modelo.setEdadTexto(" 6 ");
        assertEquals(Validacion.OK, modelo.calcular());
        assertEquals(42, modelo.getEdadHumana());
    }

    @Test
    public void cifrasDeOtroAlfabeto_noSonNumero() {
        // parseInt acepta cifras árabes-índicas ("٥" = 5); decidimos no hacerlo.
        modelo.setEdadTexto("٥");
        assertEquals(Validacion.NO_NUMERO, modelo.calcular());
    }
}