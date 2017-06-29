package ar.com.utn.restogo.adapter;

import android.content.Context;
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
import ar.com.utn.restogo.RestauranteFragment;
import ar.com.utn.restogo.modelo.FacadeMain;
import ar.com.utn.restogo.modelo.Restaurante;
import ar.com.utn.restogo.storage.DistanceLoader;
import ar.com.utn.restogo.storage.ImageLoader;

public class RestauranteAdapter extends RecyclerView.Adapter<RestauranteAdapter.ViewHolder> {
    private FragmentManager fragmentManager;
    private LayoutInflater layoutInflater;
    private ArrayList<String> keys = new ArrayList<String>();
    private ArrayList<Restaurante> restaurantes = new ArrayList<Restaurante>();

    public RestauranteAdapter(Context context, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_restaurante, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Restaurante unRestante = restaurantes.get(position);

        holder.nameView.setText(unRestante.getDescripcion());
        if (unRestante.getUrl() != null){
            holder.imagePanel.setVisibility(View.VISIBLE);
            ImageLoader.instance.loadImage(unRestante.getUrl(), new OnLoadImage(holder.imagePanel,holder.imageprogressBar, holder.imageView));
        }
        if (unRestante.getLocation() != null){
            DistanceLoader.instance.loadDistance(new OnNewDistance(unRestante.getLocation(), holder.distanceView));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestauranteFragment restauranteFragment = RestauranteFragment.newInstance(unRestante);
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, restauranteFragment, "RestauranteFragment")
                        .addToBackStack("RestauranteFragment")
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameView;
        private TextView distanceView;
        private ConstraintLayout imagePanel;
        private ProgressBar imageprogressBar;
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.nameTextView);
            distanceView = (TextView) itemView.findViewById(R.id.distanceTextView);
            imagePanel = (ConstraintLayout) itemView.findViewById(R.id.imagePanel);
            imageprogressBar = (ProgressBar) itemView.findViewById(R.id.progressImage);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    public void add(String key, Restaurante restaurante){
        if (!keys.contains(key)) {
            keys.add(key);
            Integer position = keys.indexOf(key);
            restaurantes.add(restaurante);
            notifyDataSetChanged();
        }
    }

    public void update(String key, Restaurante restaurante){
        if (keys.contains(key)) {
            Integer position = keys.indexOf(key);
            restaurantes.set(position, restaurante);
            notifyDataSetChanged();
        }
    }

    public void delete(String key){
        if(keys.contains(key)) {
            Integer position = keys.indexOf(key);
            Restaurante restaurante = restaurantes.get(position);
            restaurantes.remove(restaurante);
            keys.remove(key);
            notifyDataSetChanged();
        }
    }

    public class OnNewDistance {
        Location location;
        TextView distanceView;

        private OnNewDistance(Location location, TextView distanceView) {
            this.location = location;
            this.distanceView = distanceView;
        }

        public void onSuccessNewDistance(Location newLocation){
            distanceView.setVisibility(View.VISIBLE);
            float distance = location.distanceTo(newLocation);
            distanceView.setText("Dist: " + String.format("%.02f", (distance / 1000)) + " kms");
        }

        public void onFailedCalculate(){
            distanceView.setVisibility(View.GONE);
        }
    }
}