package com.tradingsupervisor.adapter;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.AsyncListDiffer;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tradingsupervisor.R;
import com.tradingsupervisor.database.entity.PhotoPreview;

import java.util.List;


public class PhotoPreviewAdapter extends RecyclerView.Adapter<PhotoPreviewAdapter.MyViewHolder> {

    private final AsyncListDiffer<PhotoPreview> differ = new AsyncListDiffer<>(this, diffCallback);

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_photo_cardview,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PhotoPreview preview = differ.getCurrentList().get(position);
        holder.image.setImageBitmap(preview.getBitmap());
        holder.date.setText(preview.getStrDate());
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public void submitList(List<PhotoPreview> list) {
        differ.submitList(list);
    }


    private static DiffUtil.ItemCallback<PhotoPreview> diffCallback = new DiffUtil.ItemCallback<PhotoPreview>() {

        @Override
        public boolean areItemsTheSame(PhotoPreview oldItem, PhotoPreview newItem) {
            return oldItem.getStrDate().equals(newItem.getStrDate());
        }

        @Override
        public boolean areContentsTheSame(PhotoPreview oldItem, PhotoPreview newItem) {
            return oldItem.equals(newItem);
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView date;

        public MyViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.photo_preview_id);
            date = itemView.findViewById(R.id.photo_date_id);
        }
    }
}
