package ar.com.utn.restogo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ar.com.utn.restogo.adapter.OnLoadImage;
import ar.com.utn.restogo.modelo.Reserva;
import ar.com.utn.restogo.modelo.Restaurante;
import ar.com.utn.restogo.storage.ImageLoader;

public class ReservaFragment extends Fragment {
    public static final String RESERVA_KEY = "RESERVA_KEY";

    private FirebaseAuth auth;

    private Reserva reserva;

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
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
