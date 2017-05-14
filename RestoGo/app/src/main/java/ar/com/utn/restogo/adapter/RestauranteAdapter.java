package ar.com.utn.restogo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ar.com.utn.restogo.R;
import ar.com.utn.restogo.modelo.Restaurante;

public class RestauranteAdapter extends RecyclerView.Adapter<RestauranteAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private HashMap<String, Restaurante> hashRestaurantes = new HashMap<String, Restaurante>();
    private ArrayList<Restaurante> restaurantes = new ArrayList<Restaurante>();

    public RestauranteAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_restaurante, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(restaurantes.get(position).getDescripcion());
    }

    @Override
    public int getItemCount() {
        return restaurantes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }

    public void add(String key, Restaurante restaurante){
        hashRestaurantes.put(key, restaurante);
        restaurantes.add(restaurante);
        notifyDataSetChanged();
    }

    public void update(String key, Restaurante restaurante){
        delete(key);
        add(key, restaurante);
        notifyDataSetChanged();
    }

    public void delete(String key){
        Restaurante old = hashRestaurantes.get(key);
        restaurantes.remove(old);
        notifyDataSetChanged();
    }
}