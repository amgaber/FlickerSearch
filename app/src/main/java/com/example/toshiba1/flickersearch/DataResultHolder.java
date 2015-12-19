package com.example.toshiba1.flickersearch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.toshiba1.flickersearch.Controller.RecyclerViewClickListener;
import com.example.toshiba1.flickersearch.models.FlickerData;

import java.util.List;

/**
 * Created by toshiba1 on 12/19/2015.
 */
public class DataResultHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public  ImageView image;
    public  TextView title;

    protected RecyclerViewClickListener recyclerViewClickListener;
    private List<FlickerData> searchResultData;

    public DataResultHolder(View itemView, RecyclerViewClickListener recyclerViewClickListener, List<FlickerData> searchResultData) {
        super(itemView);
        this.searchResultData=searchResultData;
        this.recyclerViewClickListener=recyclerViewClickListener;


        title = (TextView) itemView.findViewById(R.id.title);
        image=(ImageView) itemView.findViewById(R.id.photo);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int position = getLayoutPosition();
        ((RecyclerViewClickListener)recyclerViewClickListener).recyclerViewListClicked(searchResultData.get(position).getId());

    }
}
