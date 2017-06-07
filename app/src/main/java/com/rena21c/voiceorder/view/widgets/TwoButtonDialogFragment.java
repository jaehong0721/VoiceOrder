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


public class TwoButtonDialogFragment extends DialogFragment {

    public interface TwoButtonDialogClickListener {
        void onClickNegativeButton();
        void onClickPositiveButton();
    }

    private static final String MESSAGE = "message";
    private static final String NEGATIVE_BUTTON_LABEL = "leftButtonLabel";
    private static final String POSITIVE_BUTTON_LABEL = "rightButtonLabel";

    private String message;
    private String negativeButtonLabel;
    private String positiveButtonLabel;

    private TextView tvMessage;
    private Button btnNegative;
    private Button btnPositive;

    private TwoButtonDialogClickListener listener;

    public TwoButtonDialogFragment() {}

    public static TwoButtonDialogFragment newInstance(String message, String negativeButtonLabel, String positiveButtonLabel) {
        TwoButtonDialogFragment fragment = new TwoButtonDialogFragment();
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        args.putString(NEGATIVE_BUTTON_LABEL, negativeButtonLabel);
        args.putString(POSITIVE_BUTTON_LABEL, positiveButtonLabel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TwoButtonDialogClickListener) {
            listener = (TwoButtonDialogClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TwoButtonDialogClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString(MESSAGE);
            negativeButtonLabel = getArguments().getString(NEGATIVE_BUTTON_LABEL);
            positiveButtonLabel = getArguments().getString(POSITIVE_BUTTON_LABEL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.fragment_two_button_dialog, container, true);

        tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        btnNegative = (Button) view.findViewById(R.id.btnNegative);
        btnPositive = (Button) view.findViewById(R.id.btnPositive);
        return view;
    }

    @Override public void onStart() {
        super.onStart();
        tvMessage.setText(message);
        btnNegative.setText(negativeButtonLabel);
        btnPositive.setText(positiveButtonLabel);

        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onClickNegativeButton();
            }
        });

        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onClickPositiveButton();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
