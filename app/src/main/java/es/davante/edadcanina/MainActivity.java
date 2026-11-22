package es.davante.edadcanina;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import es.davante.edadcanina.databinding.ActivityMainBinding;
import es.davante.edadcanina.modelo.EdadCaninaModelo;
import es.davante.edadcanina.modelo.Validacion;

public class MainActivity extends AppCompatActivity {

    // Las constantes y las reglas ya no están aquí: se han ido al modelo.
    private ActivityMainBinding binding;
    private final EdadCaninaModelo modelo = new EdadCaninaModelo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.btnCalc.setOnClickListener(view -> calcularEdad());
    }

    // La Activity ya solo hace dos cosas: pasar el texto al modelo y pintar
    // lo que el modelo responde. Validar y calcular es cosa del modelo.
    private void calcularEdad() {
        modelo.setEdadTexto(binding.ageInput.getText().toString());
        Validacion resultado = modelo.calcular();

        switch (resultado) {
            case OK:
                binding.ageInput.setError(null);
                binding.resultText.setText(
                        getString(R.string.tu_edad_format, modelo.getEdadHumana()));
                break;
            case VACIO:
                mostrarError(getString(R.string.error_vacio));
                break;
            case NO_NUMERO:
                mostrarError(getString(R.string.error_no_numero));
                break;
            case FUERA_DE_RANGO:
                mostrarError(getString(R.string.error_rango,
                        EdadCaninaModelo.EDAD_MINIMA, EdadCaninaModelo.EDAD_MAXIMA));
                break;
            default:
                break;
        }
    }

    private void mostrarError(String mensaje) {
        binding.ageInput.setError(mensaje);
        binding.ageInput.requestFocus();
        binding.resultText.setText("");
    }
}