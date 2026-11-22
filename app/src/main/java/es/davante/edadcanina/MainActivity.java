package es.davante.edadcanina;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import es.davante.edadcanina.databinding.ActivityMainBinding;
import es.davante.edadcanina.modelo.EdadCaninaModelo;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final EdadCaninaModelo modelo = new EdadCaninaModelo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Entregamos el modelo al binding: el XML pinta resultado y error solo.
        binding.setModelo(modelo);

        // La Activity ya no pinta nada: el botón solo pide al modelo que calcule.
        binding.btnCalc.setOnClickListener(view -> modelo.calcular());
    }
}