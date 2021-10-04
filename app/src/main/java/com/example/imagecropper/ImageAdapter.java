package com.example.imagecropper;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    Context context;
    List<ImageModel>bitmaps;

    public ImageAdapter(Context context, List<ImageModel> bitmaps) {
        this.context = context;
        this.bitmaps = bitmaps;
    }



    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout,parent,false);

        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.imageView.setImageBitmap(bitmaps.get(position).getBitmap());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ZoomInZoomOut dialog=new ZoomInZoomOut(context,bitmaps.get(position));
            dialog.setCancelable(false);
            dialog.show();

        }
    });
    }

    @Override
    public int getItemCount() {
        return bitmaps.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView imageView;
        public View itemView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView=itemView;
            imageView=itemView.findViewById(R.id.imageView);
        }
    }
}
