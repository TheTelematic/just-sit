package es.uma.artjuan.just_sit;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class PedidoActivity extends AppCompatActivity {

    private NestedScrollView nsv;
    private static TextView cuenta;
    private static String contenido = "";
    private static double total = 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Pedir la cuenta", Snackbar.LENGTH_LONG)
                        .setAction("pedirCuenta",null ).show();
            }
        });

        CollapsingToolbarLayout toolbar2 = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbar2.setTitle("Total: ");
        nsv = (NestedScrollView) findViewById(R.id.scroll_contenido_cuenta);


        cuenta = (TextView) findViewById(R.id.contenido_cuenta);

        List<Plato> platos = Menu.getSingleton().getListaMenu();


        for(Plato p : platos){

            if(p.getCantidad() > 0){
                contenido += p.getCantidad() + " - (" + p.getPrecio() + "€/ud) " + p.getNombre() +   "\r\n";
                double cantidad_plato = p.getCantidad() * 1.00;
                double precio = Double.parseDouble(p.getPrecio());
                total += cantidad_plato * precio;
            }


        }
        cuenta.setText(contenido);
        toolbar2.setTitle("Total: " + total + "€");
    }


    private void pedirCuenta(){

    }
}
