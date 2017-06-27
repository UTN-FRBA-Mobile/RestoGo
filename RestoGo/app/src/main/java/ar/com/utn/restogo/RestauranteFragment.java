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

import ar.com.utn.restogo.adapter.OnLoadImage;
import ar.com.utn.restogo.modelo.Restaurante;
import ar.com.utn.restogo.storage.ImageLoader;

public class RestauranteFragment extends Fragment {
    public static final String RESTAURANTE_KEY = "RESTAURANTE_KEY";
    private Restaurante restaurante;
    private TextView tipoComidaText;
    private TextView ubicacionText;
    private TextView horarioText;
    private Button btnReservar;
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
        return output;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tipoComidaText = (TextView) view.findViewById(R.id.typeFoodText);
        ubicacionText = (TextView) view.findViewById(R.id.locationText);
        horarioText = (TextView) view.findViewById(R.id.openTimeText);
        btnReservar = (Button) view.findViewById(R.id.btnReservar);
        imagePanel = (ConstraintLayout) view.findViewById(R.id.imagePanel);
        imageprogressBar = (ProgressBar) view.findViewById(R.id.progressImage);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        if (restaurante.getUrl() != null){
            imagePanel.setVisibility(View.VISIBLE);
            ImageLoader.instance.loadImage(restaurante.getUrl(), new OnLoadImage(imagePanel,imageprogressBar, imageView));
        }

        if (restaurante.getUbicacion() != null){
            ubicacionText.setText(restaurante.getUbicacion());
        }

        if (restaurante.getHorario() != null){
            horarioText.setText(restaurante.getHorario());
        }

        if (restaurante.getTipoComida() != null){
            tipoComidaText.setText(restaurante.getTipoComida());
        }
    }
}
