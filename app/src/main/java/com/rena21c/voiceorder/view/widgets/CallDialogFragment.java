package com.rena21c.voiceorder.view.widgets;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.network.ApiService;

import java.util.HashMap;

import me.grantland.widget.AutofitTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class CallDialogFragment extends DialogFragment {

    public interface CallDialogClickListener {
        void onClickCall(String phoneNumber);
        void onClickVoiceOrder();
    }

    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String NAME = "name";

    private String phoneNumber;
    private String name;

    private AutofitTextView aftvVendorName;
    private TextView tvBusinessContent;
    private TextView tvAddress;
    private ImageView ivCall;
    private Button btnMoveVoiceOrder;

    private CallDialogClickListener listener;

    public CallDialogFragment() {}

    public static CallDialogFragment newInstance(String phoneNumber, String name) {
        CallDialogFragment fragment = new CallDialogFragment();
        Bundle args = new Bundle();
        args.putString(PHONE_NUMBER, phoneNumber);
        args.putString(NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            phoneNumber = getArguments().getString(PHONE_NUMBER);
            name = getArguments().getString(NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.fragment_call_dialog, container, true);

        aftvVendorName = (AutofitTextView) view.findViewById(R.id.tvVendorName);
        tvBusinessContent = (TextView) view.findViewById(R.id.tvBusinessContent);
        tvAddress = (TextView) view.findViewById(R.id.tvAddress);
        ivCall = (ImageView) view.findViewById(R.id.ivCall);
        btnMoveVoiceOrder = (Button) view.findViewById(R.id.btnMoveVoiceOrder);

        return view;
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        requestVendorInfo();
        super.onActivityCreated(savedInstanceState);
    }

    @Override public void onStart() {
        super.onStart();
        aftvVendorName.setText(name);
        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onClickCall(phoneNumber);
            }
        });
        btnMoveVoiceOrder.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {listener.onClickVoiceOrder();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void setCallDialogClickListener(CallDialogClickListener listener) {
        this.listener = listener;
    }

    private void requestVendorInfo() {
        Retrofit retrofit = App.getApplication(getContext().getApplicationContext()).getRetrofit();
        ApiService apiService = retrofit.create(ApiService.class);
        apiService
                .getVendorInfo(phoneNumber)
                .enqueue(new Callback<HashMap<String, String>>() {
                    @Override public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                        if(response.body() == null) return;

                        if(response.body().get("address") != null) {
                            tvAddress.setVisibility(View.VISIBLE);
                            tvAddress.setText(transformToSimpleAddress(response.body().get("address")));
                        }

                        if(response.body().get("items") != null) {
                            tvBusinessContent.setVisibility(View.VISIBLE);
                            tvBusinessContent.setText(response.body().get("items"));
                        }
                    }

                    @Override public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                        FirebaseCrash.logcat(Log.WARN, "Retrofit", "납품업체 정보 얻기 통신 실패");
                    }
                });
    }

    private String transformToSimpleAddress(String fullAddress) {
        String[] array = fullAddress.split(" ");

        String state = array[0];
        String locality = array[1];

        state = state.substring(0,2);

        return state + " " + locality;
    }
}
