package com.bixolon.sample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.UnsupportedEncodingException;

public class ScrFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private RadioGroup radioGroupSCSlot;
    private RadioGroup radioGroupSCMode;

    public static ScrFragment newInstance() {
        ScrFragment fragment = new ScrFragment();
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
        View view = inflater.inflate(R.layout.fragment_scr, container, false);

        view.findViewById(R.id.buttonSCROpen).setOnClickListener(this);
        view.findViewById(R.id.buttonSCRPowerUp).setOnClickListener(this);
        view.findViewById(R.id.buttonSCRRead).setOnClickListener(this);
        view.findViewById(R.id.buttonSCRPowerDown).setOnClickListener(this);
        view.findViewById(R.id.buttonCheckHealthNInfo).setOnClickListener(this);
        view.findViewById(R.id.buttonSCRClose).setOnClickListener(this);

        radioGroupSCSlot = view.findViewById(R.id.radioGroupSlotType);
        radioGroupSCSlot.setOnCheckedChangeListener(this);

        radioGroupSCMode = view.findViewById(R.id.radioGroupSCMode);
        radioGroupSCMode.setOnCheckedChangeListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonSCROpen){
            if (MainActivity.getPrinterInstance().smartCardRWOpen()) {
                setSCSlot(radioGroupSCSlot.getCheckedRadioButtonId());
                setSCMode(radioGroupSCMode.getCheckedRadioButtonId());
            }
        }
        else if (view.getId() == R.id.buttonSCRClose){ MainActivity.getPrinterInstance().smartCardRWClose();}
        else if (view.getId() == R.id.buttonSCRPowerUp){MainActivity.getPrinterInstance().SCPowerUp(5000); }
        else if (view.getId() == R.id.buttonSCRPowerDown){MainActivity.getPrinterInstance().SCPowerDown(5000); }
        else if (view.getId() == R.id.buttonSCRRead){
            try {
                String[] data = new String[]{
                        new String(new byte[]{
                                0x00, (byte) 0xA4, 0x04, 0x00, 0x07, (byte) 0xD4, 0x10, 0x65,
                                0x09, (byte) 0x90, 0x00, 0x10}, "CP437")
                };
                int[] count = new int[1];

                if (MainActivity.getPrinterInstance().SCRead(data, count)) {
                    Toast.makeText(getContext(), hexToString(data[0].getBytes("CP437")), Toast.LENGTH_LONG).show();
                }
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else if (view.getId() == R.id.buttonCheckHealthNInfo){
            String checkHealth = MainActivity.getPrinterInstance().smartCardCheckHealth();
            if (checkHealth != null) {
                Toast.makeText(getContext(), checkHealth, Toast.LENGTH_LONG);
            }

            String info = MainActivity.getPrinterInstance().getSmartCardInfo();
            if (info != null) {
                Toast.makeText(getContext(), info, Toast.LENGTH_LONG);
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (radioGroup.getId() == R.id.radioGroupSlotType) {setSCSlot(i);}
        if (radioGroup.getId() == R.id.radioGroupSCMode) {setSCMode(i);}
    }

    public void setSCSlot(int checkedItem) {
        int slot = 0;
        if (checkedItem == R.id.radioIC){slot = 0x01 << (Integer.SIZE - 1);}
        else if (checkedItem == R.id.radioSAM1){slot = 0x01 << (Integer.SIZE - 2);}
        else if (checkedItem == R.id.radioSAM2){ slot = 0x01 << (Integer.SIZE - 3);}

        MainActivity.getPrinterInstance().setSCSlot(slot);
    }

    public void setSCMode(int checkedItem) {
        int mode = 1;
        if (checkedItem == R.id.radioISO){mode = 1;}
        else if (checkedItem == R.id.radioEMV){mode = 2;}

        // 1 : ISO
        // 2 : EMV
        MainActivity.getPrinterInstance().setSCMode(mode);
    }

    public String hexToString(byte[] data) {
        if (data == null) {
            return null;
        }

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            String hexString = Integer.toHexString(data[i] & 0xff);
            if (hexString.length() == 1) {
                buffer.append("0");
            }
            buffer.append(hexString);

            if (i < data.length - 1) {
                buffer.append(" ");
            }
        }
        return buffer.toString();
    }
}
