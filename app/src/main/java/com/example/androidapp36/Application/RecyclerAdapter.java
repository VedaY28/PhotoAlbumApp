package com.example.androidapp36.Application;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp36.Controller.CurrentPhotoController;
import com.example.androidapp36.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Uri> uriArrayList;
    private Context context;
    private Album curAlbum;
    private photos photo;

    public RecyclerAdapter(ArrayList<Uri> uriArrayList, Context context) {
        this.context=context;
        this.uriArrayList = uriArrayList;
    }
    public void setAlbum(Album album){
        curAlbum=album;
    }
    public void setPhoto(photos Photo){
        photo=Photo;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_single_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.imageView.setImageURI(uriArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return uriArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById((R.id.Photo));
            imageView.setOnClickListener(this);
        }
        public void onClick(View view){
            int position = getAdapterPosition();
            Uri clickedItem = uriArrayList.get(position);
            Intent intent = new Intent(itemView.getContext(), CurrentPhotoController.class);
            ArrayList<String> uris=new ArrayList<String>();
            for(Uri i:uriArrayList){
                uris.add(i.toString());
            }
            System.out.println(uriArrayList.get(0).getClass().getSimpleName());
            System.out.println("Recycle "+curAlbum);
            intent.putExtra("photo", photo);
            intent.putExtra("curAlbum", curAlbum);
            intent.putExtra("Position" , position);
            intent.putExtra("clicked_item", clickedItem.toString());
            intent.putExtra("Arraylist", uris);
            itemView.getContext().startActivity(intent);
            }
        }
}
