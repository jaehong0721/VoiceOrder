package com.rena21c.voiceorder.viewholder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rena21c.voiceorder.R;

public class ItemOfListDividerTextViewHolder extends RecyclerView.ViewHolder {

    private TextView tvLabel;

    public ItemOfListDividerTextViewHolder(View itemView) {
        super(itemView);

        tvLabel = (TextView) itemView.findViewById(R.id.tvLabel);
    }

    public void bind(String label) {
        tvLabel.setText(label);
    }
}

