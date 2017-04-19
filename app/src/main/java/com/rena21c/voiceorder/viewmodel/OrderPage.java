package com.rena21c.voiceorder.viewmodel;


import android.view.LayoutInflater;
import android.view.View;

import com.rena21c.voiceorder.model.VoiceRecord;

import java.util.HashMap;

abstract public class OrderPage {

    /** 주문화면의 "자세기 보기 ->" 버튼을 클릭한 경우 이벤트를 수신 */
    public interface OnClickDetailsOrderPageListener {
        void onClickDetailsOrderPage(String timeStamp, HashMap<String, VoiceRecord> itemHashMap);
    }

    protected final String timeStamp;

    public OrderPage(String timeStamp) {
        if (timeStamp.length() != 14)
            throw new IllegalArgumentException("타임 스탬프 형식이 잘 못 되었습니다: " + timeStamp);
        this.timeStamp = timeStamp;
    }

    abstract public View getView(LayoutInflater layoutInflater, OnClickDetailsOrderPageListener onClickListener);

}
