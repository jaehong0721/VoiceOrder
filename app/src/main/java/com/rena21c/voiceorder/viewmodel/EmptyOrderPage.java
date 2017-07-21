package com.rena21c.voiceorder.viewmodel;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.services.RecordedFilePlayer;
import com.rena21c.voiceorder.util.FileNameUtil;

public class EmptyOrderPage extends OrderPage {

    private final String fileName;
    private final boolean stored;

    public EmptyOrderPage(String timeStamp, String fileName, boolean stored) {
        super(timeStamp);
        this.fileName = fileName;
        this.stored = stored;
    }

    @Override public View getView(LayoutInflater layoutInflater, OnClickDetailsOrderPageListener onClickListener) {
        final View view = layoutInflater.inflate(R.layout.before_accept_order_view, null, false);

        if(! (view.getContext() instanceof RecordedFilePlayer.PlayRecordedFileListener))
            throw new RuntimeException("VoiceOrder activity must implement PlayRecordedFileListener");

        TextView tvTimeStamp = (TextView) view.findViewById(R.id.tvTimeStamp);
        String displayTime = FileNameUtil.getDisplayTimeFromfileName(timeStamp);
        tvTimeStamp.setText(displayTime);

        if(!stored) return view;

        ImageView ivPlay = (ImageView) view.findViewById(R.id.ivPlay);
        ImageView ivStop = (ImageView) view.findViewById(R.id.ivStop);

        ivPlay.setVisibility(View.VISIBLE);
        ivStop.setVisibility(View.VISIBLE);

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ((RecordedFilePlayer.PlayRecordedFileListener) view.getContext()).onPlayRecordedFile(fileName);
            }
        });

        ivStop.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ((RecordedFilePlayer.PlayRecordedFileListener) view.getContext()).onStopRecordedFile();
            }
        });
        return view;
    }

}
