package com.tradingsupervisor.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tradingsupervisor.R;
import com.tradingsupervisor.data.entity.Photo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PhotosPreviewAdapter extends RecyclerView.Adapter<PhotosPreviewAdapter.MyViewHolder> {
    private final AsyncListDiffer<Photo> differ = new AsyncListDiffer<>(this, diffCallback);
    private final File filesDir;
    private final SimpleDateFormat dateFormat;
    private final OnListItemClickListener itemClickListener;

    public PhotosPreviewAdapter(File filesDir, OnListItemClickListener itemClickListener) {
        this.filesDir = filesDir;
        this.itemClickListener = itemClickListener;
        this.dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_photo_preview, parent,false);
        return new PhotosPreviewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Photo photo = differ.getCurrentList().get(position);
        File file = new File(filesDir, photo.getFilename()); //==new File(getFilesDir(), filename);

        Glide.with(holder.image.getContext())
                .load(file)
                .apply(new RequestOptions().placeholder(new ColorDrawable(Color.GRAY)))
                .into(holder.image);
        //holder.image.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onPhotoItemClick(photo);
            }
        });
        /*
        holder.removeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onRemoveItemClick(photo);
            }
        });*/
        holder.date.setText(dateFormat.format(photo.getCreationTime()));
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public void submitList(List<Photo> list) {
        differ.submitList(list);
    }


    private static DiffUtil.ItemCallback<Photo> diffCallback = new DiffUtil.ItemCallback<Photo>() {
        @Override
        public boolean areItemsTheSame(@NonNull Photo p1, @NonNull Photo p2) {
            return p1.getId().longValue() == p2.getId().longValue(); //compare by ID
        }

        @Override
        public boolean areContentsTheSame(@NonNull Photo p1, @NonNull Photo p2) {
            return (p1.getFilename().equals(p2.getFilename()) &&
                    (p1.getCreationTime().getTime() == p2.getCreationTime().getTime()));
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        //public ImageView removeIcon;
        public TextView date;

        public MyViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.photo_preview_id);
            //removeIcon = itemView.findViewById(R.id.photo_remove_icon_id);
            date = itemView.findViewById(R.id.photo_date_id);
        }
    }

    public interface OnListItemClickListener {
        void onPhotoItemClick(Photo photo);
        //void onRemoveItemClick(Photo photo);
    }

    /*
    public static class MyItemDecoration extends RecyclerView.ItemDecoration {
        private int itemOffset;

        public MyItemDecoration(int offset) {
            this.itemOffset = offset;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            outRect.set(itemOffset, itemOffset, itemOffset, itemOffset);
            super.getItemOffsets(outRect, view, parent, state);
        }
    }*/
}
