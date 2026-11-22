package es.davante.edadcanina.modelo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.databinding.Observable;

import java.util.ArrayList;
import java.util.List;

import es.davante.edadcanina.BR;
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
    @Test
    public void calcular_avisaDeEdadHumanaYValidacion() {
        List<Integer> avisos = escucharAvisos();
        modelo.setEdadTexto("6");
        avisos.clear();               // solo nos interesan los avisos del cálculo

        modelo.calcular();

        assertTrue(avisos.contains(BR.edadHumana));
        assertTrue(avisos.contains(BR.validacion));
    }

    @Test
    public void mismoTexto_noAvisa() {
        modelo.setEdadTexto("6");
        List<Integer> avisos = escucharAvisos();

        modelo.setEdadTexto("6");     // el viaje de vuelta del enlace @={...}

        assertTrue("Si avisara habría un bucle con la vista", avisos.isEmpty());
    }

    // Registra un oyente como haría el binding y devuelve la lista de
    // propiedades que el modelo va anunciando.
    private List<Integer> escucharAvisos() {
        List<Integer> avisos = new ArrayList<>();
        modelo.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                avisos.add(propertyId);
            }
        });
        return avisos;
    }}