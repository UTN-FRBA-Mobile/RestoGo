package ar.com.utn.restogo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ar.com.utn.restogo.R;
import ar.com.utn.restogo.modelo.Restaurante;

public class RestauranteAdapter extends RecyclerView.Adapter<RestauranteAdapter.RestauranteViewHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<Restaurante> listaRestaurante;

    public RestauranteAdapter(Context context, ArrayList<Restaurante> listaRestaurante) {
        layoutInflater = LayoutInflater.from(context);
        this.listaRestaurante = listaRestaurante;
    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
//    }
//
    @Override
    public RestauranteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_restaurante, parent, false);
        return new RestauranteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RestauranteViewHolder holder, int position) {
     //   holder.textView.setText(texto + " " + String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return listaRestaurante.size();
    }

    public class RestauranteViewHolder extends RecyclerView.ViewHolder {
//        TextView descripcion;
//
        public RestauranteViewHolder(View itemView) {
            super(itemView);
//            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }
}