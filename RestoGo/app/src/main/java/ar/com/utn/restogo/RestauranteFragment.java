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
import ar.com.utn.restogo.modelo.Restaurante;
import ar.com.utn.restogo.storage.ImageLoader;

public class RestauranteFragment extends Fragment {
    public static final String RESTAURANTE_KEY = "RESTAURANTE_KEY";

    private FirebaseAuth auth;

    private Restaurante restaurante;
    private TextView tipoComidaText;
    private TextView ubicacionText;
    private TextView horarioText;
    private Button btnReservar;
    private Button btnLogin;
    private ConstraintLayout imagePanel;
    private ProgressBar imageprogressBar;
    private ImageView imageView;

    public static RestauranteFragment newInstance(Restaurante restaurante) {
        RestauranteFragment f = new RestauranteFragment();
        Bundle args = new Bundle();
        args.putSerializable(RESTAURANTE_KEY, restaurante);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View output = inflater.inflate(R.layout.fragment_restaurante, container, false);
        this.restaurante = (Restaurante) getArguments().getSerializable(RESTAURANTE_KEY);
        auth = FirebaseAuth.getInstance();
        return output;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tipoComidaText = (TextView) view.findViewById(R.id.typeFoodText);
        ubicacionText = (TextView) view.findViewById(R.id.locationText);
        horarioText = (TextView) view.findViewById(R.id.openTimeText);
        btnReservar = (Button) view.findViewById(R.id.btnReservar);
        btnLogin = (Button) view.findViewById(R.id.btnLoguearse);
        imagePanel = (ConstraintLayout) view.findViewById(R.id.imagePanel);
        imageprogressBar = (ProgressBar) view.findViewById(R.id.progressImage);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        if (restaurante.getUrl() != null){
            imagePanel.setVisibility(View.VISIBLE);
            ImageLoader.instance.loadImage(restaurante.getUrl(), new OnLoadImage(imagePanel,imageprogressBar, imageView));
        }

        if (restaurante.getDireccion() != null){
            ubicacionText.setText(restaurante.getDireccion());
        }

        if (restaurante.getHorario() != null){
            horarioText.setText(restaurante.getHorario());
        }

        if (restaurante.getComidas() != null){
            String stringTipos = "";
            for (String comida : restaurante.getComidas()) {
                stringTipos = stringTipos + " ," + comida;
            }
            tipoComidaText.setText(stringTipos);
        }

        btnReservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReservarFragment reservarFragmenent = ReservarFragment.newInstance(restaurante);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, reservarFragmenent, "ReservarFragment")
                        .addToBackStack("ReservarFragment")
                        .commit();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment loginFragmenent = new LoginFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, loginFragmenent, "LoginFragment")
                        .addToBackStack("LoginFragment")
                        .commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh(auth.getCurrentUser());
    }

    private void refresh(FirebaseUser user) {
        if (user != null) {
            btnReservar.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
        } else {
            btnReservar.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
        }
    }
}
