package ar.com.utn.restogo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import butterknife.OnClick;

public class InformacionPedidoActivity extends AppCompatActivity {

    private TextView txtInfoReserva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtInfoReserva = (TextView) findViewById(R.id.infoReserva);
        txtInfoReserva.setText(getInformacionPedido(getIntent().getStringExtra("message")));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private String getInformacionPedido(String data){
        return "";
    }

    @OnClick(R.id.btnAceptarPedido)
    void aceptarReservaUsuario(){

    }

    @OnClick(R.id.btnRechazarPedido)
    void rechazarReservaUsuario(){

    }
}
