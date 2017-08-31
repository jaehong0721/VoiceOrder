package com.rena21c.voiceorder.view.actionbar;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rena21c.voiceorder.R;

public class CloseActivityActionBar implements ActionBarInterface{

    private Context context;
    private ActionBar actionBar;

    private ImageView ibClose;
    private TextView tvTitle;

    @Override public void setUp() {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_close);
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context,android.R.color.white)));

        View view = actionBar.getCustomView();
        ibClose = (ImageView)view.findViewById(R.id.ibClose);
        tvTitle = (TextView) view.findViewById(R.id.tvTitleInActionBar);
    }

    private CloseActivityActionBar(Context context, ActionBar actionBar) {
        this.context = context;
        this.actionBar = actionBar;
    }

    public static CloseActivityActionBar createWithActionBar(Context context, ActionBar supportActionBar) {
        CloseActivityActionBar instance = new CloseActivityActionBar(context, supportActionBar);
        instance.setUp();
        return instance;
    }

    public CloseActivityActionBar setBackButtonClickListener(View.OnClickListener backButtonClickListener) {
        ibClose.setOnClickListener(backButtonClickListener);
        return this;
    }

    public CloseActivityActionBar setTitle(String title) {
        tvTitle.setText(title);
        return this;
    }

}
