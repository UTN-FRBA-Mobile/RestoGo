package ar.com.utn.restogo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ar.com.utn.restogo.R;
import ar.com.utn.restogo.ReservaFragment;
import ar.com.utn.restogo.RestauranteFragment;
import ar.com.utn.restogo.modelo.Reserva;
import ar.com.utn.restogo.modelo.Restaurante;
import ar.com.utn.restogo.modelo.TipoComida;
import ar.com.utn.restogo.storage.DistanceLoader;
import ar.com.utn.restogo.storage.ImageLoader;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ViewHolder> {
    private FragmentManager fragmentManager;
    private LayoutInflater layoutInflater;
    private ArrayList<String> keys = new ArrayList<String>();
    private ArrayList<Reserva> reservas = new ArrayList<Reserva>();

    public ReservaAdapter(Context context, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_reserva, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Reserva unaReserva = reservas.get(position);

        holder.fechaText.setText(unaReserva.getDia());
        holder.horaText.setText(unaReserva.getHora());
        holder.cantidadText.setText(unaReserva.getCantidadPersonas());

        if (unaReserva.getFueRespondida()){
            if (unaReserva.getFueRechazada()){
                holder.panel.setBackgroundColor(Color.RED);
            } else {
                holder.panel.setBackgroundColor(Color.GREEN);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReservaFragment reservaFragment = ReservaFragment.newInstance(unaReserva);
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, reservaFragment, "ReservaFragment")
                        .addToBackStack("ReservaFragment")
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout panel;
        private TextView fechaText;
        private TextView horaText;
        private TextView cantidadText;


        public ViewHolder(View itemView) {
            super(itemView);
            panel = (ConstraintLayout) itemView.findViewById(R.id.panel);
            fechaText = (TextView) itemView.findViewById(R.id.diaTextView);
            horaText = (TextView) itemView.findViewById(R.id.horaTextView);
            cantidadText = (TextView) itemView.findViewById(R.id.cantidadTextView);
        }
    }

    public void add(String key, Reserva reserva){
        if (!keys.contains(key)) {
            keys.add(key);
            Integer position = keys.indexOf(key);
            reservas.add(reserva);
            notifyDataSetChanged();
        }
    }
}