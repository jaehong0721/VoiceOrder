package com.rena21c.voiceorder.view.widgets;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.rena21c.voiceorder.R;


public class OneButtonDialogFragment extends DialogFragment {

    public interface OneButtonDialogClickListener {
        void onClickOkButton();
    }

    private static final String MESSAGE = "message";
    private static final String OK_BUTTON_LABEL = "okButtonLabel";

    private String message;
    private String okButtonLabel;

    private TextView tvMessage;
    private Button btnOk;

    private OneButtonDialogClickListener listener;

    public OneButtonDialogFragment() {}

    public static OneButtonDialogFragment newInstance(String message, String okButtonLabel) {
        OneButtonDialogFragment fragment = new OneButtonDialogFragment();
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        args.putString(OK_BUTTON_LABEL, okButtonLabel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OneButtonDialogClickListener) {
            listener = (OneButtonDialogClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OneButtonDialogClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString(MESSAGE);
            okButtonLabel = getArguments().getString(OK_BUTTON_LABEL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.fragment_one_button_dialog, container, true);

        tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        btnOk = (Button) view.findViewById(R.id.btnOk);
        return view;
    }

    @Override public void onStart() {
        super.onStart();
        tvMessage.setText(message);
        btnOk.setText(okButtonLabel);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onClickOkButton();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
