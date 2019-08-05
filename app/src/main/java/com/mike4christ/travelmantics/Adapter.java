package com.mike4christ.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private Context context;
    private List<Model> dataList;
  

    public Adapter(Context context, List<Model> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ButterKnife.bind(this, view);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Model dataOption = dataList.get(i);

//        bind data to view

        if (dataOption.holidy_title != null) {
            holder.mHolidayTitle.setText(dataOption.holidy_title);
            holder.mAmount.setText(dataOption.amount);
            if(dataOption.description.length()>=20) {
                holder.mDiscriptn.setText(dataOption.description.substring(0, 20).concat("..."));
            }else{
                holder.mDiscriptn.setText(dataOption.description);
            }
            Log.d("Check Database: -----", dataOption.img_url);
            holder.setThumbnail(dataOption.getImg_url());
        }
        
        holder.setItemClickListener(pos -> {


            nextActivity(dataList.get(pos).getImg_url(),dataList.get(pos).holidy_title,dataList.get(pos).amount,dataList.get(pos).description, DetailActivity.class);
            Log.i("ProdID",dataList.get(pos).holidy_title);

        });
    }

    private void nextActivity(String img_url,String title,String amount,String description, Class detailActivityClass) {
        Intent i = new Intent(context, detailActivityClass);
        i.putExtra("place_title", title);
        i.putExtra("amount", amount);
        i.putExtra("img_url", img_url);
        i.putExtra("description", description);
        context.startActivity(i);
    }

    @Override
    public int getItemCount() {
        if (dataList != null) {
            return dataList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.thumbnail)
        ImageView mThumnail;
        @BindView(R.id.item_card)
        MaterialCardView item_card;
        @BindView(R.id.holiday_title)
        TextView mHolidayTitle;
        @BindView(R.id.amount)
        TextView mAmount;
        @BindView(R.id.decriptn)
        TextView mDiscriptn;

        ItemClickListener itemClickListener;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

      @Override
        public void onClick(View view) {
            this.itemClickListener.onItemClick(this.getLayoutPosition());
        }
        public void setThumbnail(String url) {
            ImageView imageView = this.mThumnail;
            if (imageView != null) {
                Glide.with(imageView.getContext()).load(url).apply(new RequestOptions().fitCenter().circleCrop()).into(this.mThumnail);
            }
        }
    }
}
