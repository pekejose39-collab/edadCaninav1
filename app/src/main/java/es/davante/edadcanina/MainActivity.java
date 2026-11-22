package es.davante.edadcanina;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import es.davante.edadcanina.controlador.EdadCaninaControlador;
import es.davante.edadcanina.databinding.ActivityMainBinding;
import es.davante.edadcanina.modelo.EdadCaninaModelo;
import es.davante.edadcanina.modelo.Validacion;

/**
 * La Activity solo ensambla y atiende el ciclo de vida: crea el modelo y el
 * controlador, se los entrega a la vista y guarda/restaura el estado al girar.
 * No busca vistas a mano, no escribe textos en ellas ni registra listeners:
 * de todo eso se encarga DataBinding desde el XML.
 */
public class MainActivity extends AppCompatActivity {

    // Claves del Bundle como constantes: un error tecleando una clave daría un
    // fallo silencioso (se guarda con un nombre y se busca con otro).
    private static final String CLAVE_EDAD_TEXTO = "edad_texto";
    private static final String CLAVE_CALCULADO = "calculado";

    private ActivityMainBinding binding;
    private EdadCaninaModelo modelo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Al girar, Android destruye esta Activity y crea otra: este modelo
        // nuevo nace vacío y hay que reconstruirlo con lo que se guardó.
        modelo = new EdadCaninaModelo();
        if (savedInstanceState != null) {
            restaurarEstado(savedInstanceState);
        }

        // Restauramos ANTES de entregar el modelo: así la vista se pinta ya con
        // el estado correcto y no hay un parpadeo con la pantalla vacía.
        binding.setModelo(modelo);
        binding.setControlador(new EdadCaninaControlador(modelo));
    }

    /**
     * Android lo llama justo antes de destruir la Activity (giro, modo oscuro,
     * cambio de idioma...). Guardamos lo mínimo para reconstruir el modelo:
     * el texto y si se había pulsado Calcular. La edad humana y la Validacion
     * no hace falta guardarlas, porque el cálculo siempre da lo mismo para la
     * misma entrada.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CLAVE_EDAD_TEXTO, modelo.getEdadTexto());
        outState.putBoolean(CLAVE_CALCULADO,
                modelo.getValidacion() != Validacion.SIN_CALCULAR);
    }

    private void restaurarEstado(Bundle estado) {
        modelo.setEdadTexto(estado.getString(CLAVE_EDAD_TEXTO, ""));
        // Repetir el cálculo recupera tanto el resultado (42) como el error
        // (por ejemplo, FUERA_DE_RANGO con 31).
        if (estado.getBoolean(CLAVE_CALCULADO, false)) {
            modelo.calcular();
        }
    }
}