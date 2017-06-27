package ar.com.utn.restogo.adapter;

import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class OnLoadImage {
    ConstraintLayout imagePanel;
    ProgressBar imageprogressBar;
    ImageView imageView;

    public OnLoadImage(ConstraintLayout imagePanel, ProgressBar imageprogressBar, ImageView imageView) {
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