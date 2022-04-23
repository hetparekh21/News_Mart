package com.example.newsmart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsmart.Data.NewsObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class NewsObjectAdapter extends RecyclerView.Adapter<NewsObjectAdapter.ViewHolder> {

    List<NewsObject> NewsObjectList;
    OnNewsListener mOnNewsListener;
    Context context ;
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title, description, publisher, publishDate;
        public ImageView thumbnail, share;
        public View item_view;
        public OnNewsListener onNewsListener;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView, OnNewsListener onNewsListener) {
            super(itemView);

            item_view = itemView.findViewById(R.id.list_item);
            thumbnail = itemView.findViewById(R.id.image);
            publishDate = itemView.findViewById(R.id.publishDate);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
            title = (TextView) itemView.findViewById(R.id.title);
            share = itemView.findViewById(R.id.share);
            this.onNewsListener = onNewsListener;

            itemView.setOnClickListener(this);

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onNewsListener.OnViewClick(getAdapterPosition());

                }
            });


        }

        public TextView getTitle() {
            return title;
        }

        public TextView getDescription() {
            return description;
        }

        public TextView getPublisher() {
            return publisher;
        }

        public TextView getPublishDate() {
            return publishDate;
        }

        public ImageView getThumbnail() {
            return thumbnail;
        }

        @Override
        public void onClick(View v) {

            onNewsListener.OnNewsClick(getAdapterPosition());

        }
    }

    @NonNull
    @Override
    // Create new views (invoked by the layout manager)
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview, parent, false);

        return new ViewHolder(view, mOnNewsListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position ) {

        NewsObject newsObject = NewsObjectList.get(position);

        // set title
        holder.getTitle().setText(newsObject.getTitle());

        // set description
        holder.getDescription().setText(newsObject.getDescription());

        // set publisher
        holder.getPublisher().setText(newsObject.getPublisher());

        // set publishDate
        holder.getPublishDate().setText(newsObject.getPublishDate());

        // set thumbnail
        if (newsObject.getThumbnail()) {

            /*
             todo : get the file using title and set it

             newsObject.getThumbnail(),0,newsObject.getThumbnail().length
                        Bitmap bitmap = BitmapFactory.decodeByteArray(newsObject.getThumbnail(),0,newsObject.getThumbnail().length) ;
                        Bitmap bitmap = newsObject.getThumbnail();

                        if(bitmap == null){

                            Log.e("TAG", "couldn't parse byte[] " );

                        }
                        Environment.DIRECTORY_DOWNLOADS
                    + File.separator + "Lol.jpg"

            */

            String[] gg = newsObject.getTitle().split(" ");

            File file = new File(context.getFilesDir(),gg[0] + ".jpg");

            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            holder.getThumbnail().setImageBitmap(bitmap);


        } else {

            holder.getThumbnail().setVisibility(View.GONE);

        }

    }

    public interface OnNewsListener {

        void OnNewsClick(int position);

        void OnViewClick(int position);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return NewsObjectList.size();
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param newsObjectList ArrayList<NewsObject> containing the data to populate views to be used
     *                       by RecyclerView.
     */
    public NewsObjectAdapter(List<NewsObject> newsObjectList, OnNewsListener onNewsListener , Context context) {

        this.context = context ;
        this.NewsObjectList = newsObjectList;
        mOnNewsListener = onNewsListener;

    }

}
