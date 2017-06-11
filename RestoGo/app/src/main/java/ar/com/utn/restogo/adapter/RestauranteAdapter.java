package ar.com.utn.restogo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

import ar.com.utn.restogo.R;
import ar.com.utn.restogo.modelo.Restaurante;
import ar.com.utn.restogo.storage.ImageLoader;

public class RestauranteAdapter extends RecyclerView.Adapter<RestauranteAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private ArrayList<String> keys = new ArrayList<String>();
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
        Restaurante unRestante = restaurantes.get(position);

        holder.nameView.setText(unRestante.getDescripcion());
        if (unRestante.getUrl() != null){
            holder.imagePanel.setVisibility(View.VISIBLE);
            ImageLoader.instance.loadImage(unRestante.getUrl(), new OnLoadImage(holder.imagePanel,holder.imageprogressBar, holder.imageView));
        }
    }

    @Override
    public int getItemCount() {
        return restaurantes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameView;
        TextView distanceView;
        ConstraintLayout imagePanel;
        ProgressBar imageprogressBar;
        ImageView imageView;

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

    public class OnLoadImage {
        ConstraintLayout imagePanel;
        ProgressBar imageprogressBar;
        ImageView imageView;

        private OnLoadImage(ConstraintLayout imagePanel, ProgressBar imageprogressBar, ImageView imageView) {
            this.imagePanel = imagePanel;
            this.imageprogressBar = imageprogressBar;
            this.imageView = imageView;
        }

        public void onSuccesLoad(Bitmap bitmap){
            imageprogressBar.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(bitmap);
        }

        public void onFailedLoad(){
            imagePanel.setVisibility(View.GONE);
        }
    }
}