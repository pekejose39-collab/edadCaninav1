package es.davante.edadcanina.controlador;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import es.davante.edadcanina.modelo.EdadCaninaModelo;
import es.davante.edadcanina.modelo.Validacion;

/**
 * Pruebas del controlador. Simulamos los clics llamando a sus métodos y
 * comprobamos el modelo: no hace falta ninguna vista, porque el controlador
 * no las conoce.
 */
public class EdadCaninaControladorTest {

    private EdadCaninaModelo modelo;
    private EdadCaninaControlador controlador;

    @Before
    public void setUp() {
        modelo = new EdadCaninaModelo();
        controlador = new EdadCaninaControlador(modelo);
    }

    @Test
    public void clicCalcular_calculaEnElModelo() {
        modelo.setEdadTexto("6");          // lo que haría el @={...} al escribir
        controlador.onCalcularClick();
        assertEquals(Validacion.OK, modelo.getValidacion());
        assertEquals(42, modelo.getEdadHumana());
    }

    @Test
    public void clicLimpiar_trasCalcular_borraTodo() {
        modelo.setEdadTexto("6");
        controlador.onCalcularClick();

        controlador.onLimpiarClick();

        assertEquals("", modelo.getEdadTexto());
        assertEquals(0, modelo.getEdadHumana());
        assertEquals(Validacion.SIN_CALCULAR, modelo.getValidacion());
    }

    @Test
    public void clicLimpiar_trasErrorVacio_quitaElError() {
        controlador.onCalcularClick();     // campo vacío → VACIO
        assertEquals(Validacion.VACIO, modelo.getValidacion());

        controlador.onLimpiarClick();      // el texto ya era "", pero el error debe irse

        assertEquals(Validacion.SIN_CALCULAR, modelo.getValidacion());
    }
}