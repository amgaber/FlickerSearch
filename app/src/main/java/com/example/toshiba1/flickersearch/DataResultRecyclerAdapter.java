package com.example.toshiba1.flickersearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.toshiba1.flickersearch.Controller.RecyclerViewClickListener;
import com.example.toshiba1.flickersearch.models.FlickerData;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by toshiba1 on 12/19/2015.
 */
public class DataResultRecyclerAdapter extends RecyclerView.Adapter<DataResultHolder> {
    private static final String TAG =DataResultRecyclerAdapter.class.getSimpleName() ;

    private List <FlickerData> searchResultData;
    private Context context;
    protected RecyclerViewClickListener recyclerViewClickListener;

    public DataResultRecyclerAdapter(Context context,RecyclerViewClickListener recyclerViewClickListener,List<FlickerData> searchResultData) {
        this.context=context;
        this.recyclerViewClickListener=recyclerViewClickListener;
        this.searchResultData=searchResultData;
    }

    @Override
    public DataResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_data_item,parent,false);
        DataResultHolder holder = new DataResultHolder(view,recyclerViewClickListener,searchResultData);
        return holder;
    }

    @Override
    public void onBindViewHolder(DataResultHolder holder, int position) {
        holder.title.setText(searchResultData.get(position).getTitle());
        //Download image using picasso library
        Picasso.with(holder.itemView.getContext())
                .load(searchResultData.get(position).getURL())
                .error(android.R.drawable.ic_menu_gallery)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.image);

    }


    @Override
    public int getItemCount() {
        return (null != this.searchResultData ? this.searchResultData.size() : 0);
    }
}
