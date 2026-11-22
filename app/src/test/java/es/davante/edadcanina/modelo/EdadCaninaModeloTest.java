package es.davante.edadcanina.modelo;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas del modelo. Se ejecutan en la JVM del ordenador, sin emulador,
 * precisamente porque el modelo no depende de Android.
 */
public class EdadCaninaModeloTest {

    private EdadCaninaModelo modelo;

    @Before
    public void setUp() {
        modelo = new EdadCaninaModelo();
    }

    @Test
    public void alEmpezar_sinCalcular() {
        assertEquals(Validacion.SIN_CALCULAR, modelo.getValidacion());
        assertEquals(0, modelo.getEdadHumana());
    }

    @Test
    public void seis_da42() {
        modelo.setEdadTexto("6");
        assertEquals(Validacion.OK, modelo.calcular());
        assertEquals(42, modelo.getEdadHumana());
    }

    @Test
    public void limites_1y30_sonValidos() {
        modelo.setEdadTexto("1");
        assertEquals(Validacion.OK, modelo.calcular());
        assertEquals(7, modelo.getEdadHumana());

        modelo.setEdadTexto("30");
        assertEquals(Validacion.OK, modelo.calcular());
        assertEquals(210, modelo.getEdadHumana());
    }

    @Test
    public void vacio_daVacio() {
        modelo.setEdadTexto("");
        assertEquals(Validacion.VACIO, modelo.calcular());
    }

    @Test
    public void soloEspacios_daVacio() {
        modelo.setEdadTexto("   ");
        assertEquals(Validacion.VACIO, modelo.calcular());
    }

    @Test
    public void texto_daNoNumero() {
        modelo.setEdadTexto("abc");
        assertEquals(Validacion.NO_NUMERO, modelo.calcular());
    }

    @Test
    public void cero_daFueraDeRango() {
        modelo.setEdadTexto("0");
        assertEquals(Validacion.FUERA_DE_RANGO, modelo.calcular());
    }

    @Test
    public void treintaYUno_daFueraDeRango() {
        modelo.setEdadTexto("31");
        assertEquals(Validacion.FUERA_DE_RANGO, modelo.calcular());
        assertEquals(0, modelo.getEdadHumana());
    }

    @Test
    public void cambiarTexto_invalidaElResultado() {
        modelo.setEdadTexto("6");
        modelo.calcular();
        modelo.setEdadTexto("7");
        assertEquals(Validacion.SIN_CALCULAR, modelo.getValidacion());
        assertEquals(0, modelo.getEdadHumana());
    }

    @Test
    public void null_seGuardaComoVacio() {
        modelo.setEdadTexto(null);
        assertEquals("", modelo.getEdadTexto());
        assertEquals(Validacion.VACIO, modelo.calcular());
    }
}