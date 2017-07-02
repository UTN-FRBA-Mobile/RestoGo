package ar.com.utn.restogo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import ar.com.utn.restogo.adapter.OnLoadImage;
import ar.com.utn.restogo.modelo.Reserva;
import ar.com.utn.restogo.modelo.Restaurante;
import ar.com.utn.restogo.storage.ImageLoader;

public class ReservaFragment extends Fragment {
    public static final String RESERVA_KEY = "RESERVA_KEY";

    private FirebaseAuth auth;

    private Reserva reserva;

    private TextView fecha;
    private TextView hora;
    private TextView cantidad;
    private Button buttonAceptar;
    private Button buttonRechazar;

    public static ReservaFragment newInstance(Reserva reserva) {
        ReservaFragment f = new ReservaFragment();
        Bundle args = new Bundle();
        args.putSerializable(RESERVA_KEY, reserva);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View output = inflater.inflate(R.layout.fragment_reserva, container, false);
        this.reserva = (Reserva) getArguments().getSerializable(RESERVA_KEY);
        auth = FirebaseAuth.getInstance();
        return output;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fecha = (TextView) getView().findViewById(R.id.diaTextView);
        hora = (TextView) getView().findViewById(R.id.horaTextView);
        cantidad = (TextView) getView().findViewById(R.id.cantidadTextView);
        buttonRechazar = (Button) getView().findViewById(R.id.btnRechazarPedido);
        buttonAceptar = (Button) getView().findViewById(R.id.btnAceptarPedido);

        fecha.setText(reserva.getDia());
        hora.setText(reserva.getHora());
        cantidad.setText(reserva.getCantidadPersonas());

        if (reserva.getFueRespondida()){
            buttonAceptar.setVisibility(View.GONE);
            buttonRechazar.setVisibility(View.GONE);
        }

        buttonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.getReference("reservas/"+reserva.getKeyRestaurante()+"/"+reserva.getKeyReserva()+"/fueRespondida").setValue(true);
                database.getReference("reservas/"+reserva.getKeyRestaurante()+"/"+reserva.getKeyReserva()+"/fueRechazada").setValue(false);
                getActivity().getSupportFragmentManager().popBackStack("ReservaFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        buttonRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();

                database.getReference("reservas/"+reserva.getKeyRestaurante()+"/"+reserva.getKeyReserva()+"/fueRespondida").setValue(true);
                database.getReference("reservas/"+reserva.getKeyRestaurante()+"/"+reserva.getKeyReserva()+"/fueRechazada").setValue(true);
                getActivity().getSupportFragmentManager().popBackStack("ReservaFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
