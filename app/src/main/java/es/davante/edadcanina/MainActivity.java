package es.davante.edadcanina;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import es.davante.edadcanina.databinding.ActivityMainBinding;
import es.davante.edadcanina.modelo.EdadCaninaModelo;
import es.davante.edadcanina.modelo.Validacion;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final EdadCaninaModelo modelo = new EdadCaninaModelo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Entregamos el modelo al binding (setModelo porque la <variable> se llama
        // "modelo"). Desde aquí el XML lee el modelo y se repinta con sus avisos.
        binding.setModelo(modelo);

        // Ya no leemos el EditText: el texto llega solo al modelo por @={...}.
        binding.btnCalc.setOnClickListener(view -> mostrarError(modelo.calcular()));
    }

    // De momento el error se sigue pintando a mano (en el paso 5 pasará al XML).
    // El resultado ya no: lo pinta el XML cuando el modelo avisa.
    private void mostrarError(Validacion resultado) {
        String mensaje;
        switch (resultado) {
            case VACIO:
                mensaje = getString(R.string.error_vacio);
                break;
            case NO_NUMERO:
                mensaje = getString(R.string.error_no_numero);
                break;
            case FUERA_DE_RANGO:
                mensaje = getString(R.string.error_rango,
                        EdadCaninaModelo.EDAD_MINIMA, EdadCaninaModelo.EDAD_MAXIMA);
                break;
            default:
                mensaje = null; // OK o SIN_CALCULAR: no hay error
                break;
        }
        binding.ageInput.setError(mensaje);
        if (mensaje != null) binding.ageInput.requestFocus();
    }
}