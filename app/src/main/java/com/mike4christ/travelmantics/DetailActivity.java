package com.mike4christ.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.detail_layout)
    LinearLayout detailLayout;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.place)
    TextView place;
    @BindView(R.id.desc)
    TextView desc;
    @BindView(R.id.amount)
    TextView amount;
    @BindView(R.id.progress)
    AVLoadingIndicatorView progress;
    @BindView(R.id.data_layout)
    LinearLayout data_layout;

String placeStrg="",amountStrg="",descStrg="",img_url="";
NetworkConnection networkConnection=new NetworkConnection();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent=getIntent();
        placeStrg=intent.getStringExtra("place_title");
        amountStrg=intent.getStringExtra("amount");
        descStrg=intent.getStringExtra("description");
        img_url=intent.getStringExtra("img_url");

        progress.setVisibility(View.VISIBLE);
        data_layout.setVisibility(View.GONE);
        if(networkConnection.isNetworkConnected(this)){
            ImageView imageView = this.img;
            if (imageView != null) {
                Glide.with(imageView.getContext()).load(img_url).apply(new RequestOptions().fitCenter().circleCrop()).into(this.img);

                progress.setVisibility(View.VISIBLE);
                data_layout.setVisibility(View.GONE);
            }
        }else {
            showMessage("No Internet Connection");
        }








    }
    private void showMessage(String s) {
        Snackbar.make(detailLayout, s, Snackbar.LENGTH_SHORT).show();
    }
}
