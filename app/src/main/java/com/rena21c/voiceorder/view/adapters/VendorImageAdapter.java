package com.rena21c.voiceorder.view.adapters;


import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.rena21c.voiceorder.R;

import java.util.ArrayList;


public class VendorImageAdapter extends PagerAdapter {

    private final LayoutInflater inflater;
    private ArrayList<String> imageUrl;

    public VendorImageAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override public Object instantiateItem(ViewGroup container, int position) {
        View itemView = inflater.inflate(R.layout.item_viewpager_vendor_image,container,false);
        ImageView ivItem = (ImageView) itemView.findViewById(R.id.ivItem);

        Glide.with(ivItem.getContext())
                .load(imageUrl.get(position))
                .apply(RequestOptions.centerCropTransform())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivItem);

        container.addView(itemView);
        return itemView;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override public int getCount() {
        return imageUrl.size();
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setVendorImages(ArrayList<String> imageUrl) {
        this.imageUrl = imageUrl;
        notifyDataSetChanged();
    }

    public void updateVendorImages() {
        notifyDataSetChanged();
    }

    public String getCurrentImageUrl(int currentPositon) {
        return imageUrl.get(currentPositon);
    }
}
