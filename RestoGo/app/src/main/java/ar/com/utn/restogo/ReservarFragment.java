package ar.com.utn.restogo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import ar.com.utn.restogo.modelo.Restaurante;

public class ReservarFragment extends Fragment {
    public static final String RESTAURANTE_KEY = "RESTAURANTE_KEY";

    private FirebaseAuth auth;
    private Restaurante restaurante;

    public static ReservarFragment newInstance(Restaurante restaurante) {
        ReservarFragment f = new ReservarFragment();
        Bundle args = new Bundle();
        args.putSerializable(RESTAURANTE_KEY, restaurante);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View output = inflater.inflate(R.layout.fragment_reservar, container, false);
        this.restaurante = (Restaurante) getArguments().getSerializable(RESTAURANTE_KEY);
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
