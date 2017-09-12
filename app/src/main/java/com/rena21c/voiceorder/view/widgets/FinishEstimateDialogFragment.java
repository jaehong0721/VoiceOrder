package com.rena21c.voiceorder.view.widgets;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.rena21c.voiceorder.R;


public class FinishEstimateDialogFragment extends DialogFragment {

    public interface FinishRequestEstimateListener {
        void onFinish(String key);
    }

    private static final String KEY = "key";

    private String key;

    private Button btnOk;

    private FinishRequestEstimateListener listener;

    public FinishEstimateDialogFragment() {}

    public static FinishEstimateDialogFragment newInstance(String vendorKey) {
        FinishEstimateDialogFragment fragment = new FinishEstimateDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY, vendorKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FinishRequestEstimateListener) {
            listener = (FinishRequestEstimateListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FinishRequestEstimateListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            key = getArguments().getString(KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.fragment_finish_estimate, container, true);

        btnOk = (Button) view.findViewById(R.id.btnOk);
        return view;
    }

    @Override public void onStart() {
        super.onStart();
        btnOk.setText("업체등록 및 견적종료");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onFinish(key);
            }
        });
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }
}
