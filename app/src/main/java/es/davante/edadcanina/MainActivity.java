package es.davante.edadcanina;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import es.davante.edadcanina.controlador.EdadCaninaControlador;
import es.davante.edadcanina.databinding.ActivityMainBinding;
import es.davante.edadcanina.modelo.EdadCaninaModelo;

/**
 * La Activity ya solo ensambla: crea el modelo y el controlador y se los entrega
 * a la vista. No busca vistas a mano, no escribe textos en ellas ni registra
 * listeners: de todo eso se encarga DataBinding desde el XML.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private EdadCaninaModelo modelo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        modelo = new EdadCaninaModelo();
        // El controlador recibe el mismo modelo que pinta la vista: así las
        // órdenes que da se ven en pantalla.
        EdadCaninaControlador controlador = new EdadCaninaControlador(modelo);

        // Un setter por cada <variable> del XML.
        binding.setModelo(modelo);
        binding.setControlador(controlador);
    }
}