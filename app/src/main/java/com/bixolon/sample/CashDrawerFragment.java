package com.bixolon.sample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class CashDrawerFragment extends Fragment {

    private TextView deviceMessagesTextView;

    private Button buttonCashDrawerOpen;
    private Button buttonDrawerOpen;
    private Button buttonCheckHealth;
    private Button buttonInfo;
    private Button buttonCashDrawerClose;

    public static CashDrawerFragment newInstance() {
        CashDrawerFragment fragment = new CashDrawerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cash_drawer, container, false);

        buttonCashDrawerOpen = view.findViewById(R.id.buttonCashDrawerOpen);
        buttonDrawerOpen = view.findViewById(R.id.buttonDrawerOpen);
        buttonCheckHealth = view.findViewById(R.id.buttonCheckHealth);
        buttonInfo = view.findViewById(R.id.buttonInfo);
        buttonCashDrawerClose = view.findViewById(R.id.buttonCashDrawerClose);

        deviceMessagesTextView = view.findViewById(R.id.textViewDeviceMessages);
        deviceMessagesTextView.setMovementMethod(new ScrollingMovementMethod());
        deviceMessagesTextView.setVerticalScrollBarEnabled(true);


        buttonCashDrawerOpen.setOnClickListener(v -> {MainActivity.getPrinterInstance().cashDrawerOpen();});
        buttonDrawerOpen.setOnClickListener(v -> {MainActivity.getPrinterInstance().drawerOpen();});
        buttonCheckHealth.setOnClickListener(v -> {
            String checkHealth = MainActivity.getPrinterInstance().cashDrawerCheckHealth();
            if (checkHealth != null) {
                Toast.makeText(getContext(), checkHealth, Toast.LENGTH_LONG).show();
            }
        });
        buttonInfo.setOnClickListener(v -> {
            String info = MainActivity.getPrinterInstance().getCashDrawerInfo();
            if (info != null) {
                Toast.makeText(getContext(), info, Toast.LENGTH_LONG).show();
            }
        });
        buttonCashDrawerClose.setOnClickListener(v -> {MainActivity.getPrinterInstance().cashDrawerClose();});

        return view;
    }

    public void setDeviceLog(String data) {
        mHandler.obtainMessage(0, 0, 0, data).sendToTarget();
    }

    public final Handler mHandler = new Handler(new Handler.Callback() {
        @SuppressWarnings("unchecked")
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    deviceMessagesTextView.append((String) msg.obj + "\n");

                    Layout layout = deviceMessagesTextView.getLayout();
                    if (layout != null) {
                        int y = layout.getLineTop(
                                deviceMessagesTextView.getLineCount()) - deviceMessagesTextView.getHeight();
                        if (y > 0) {
                            deviceMessagesTextView.scrollTo(0, y);
                            deviceMessagesTextView.invalidate();
                        }
                    }
                    break;
            }
            return false;
        }
    });
}
