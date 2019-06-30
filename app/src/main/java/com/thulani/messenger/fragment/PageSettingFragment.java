package com.thulani.messenger.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thulani.messenger.R;
import com.thulani.messenger.utils.Preferences;

public class PageSettingFragment extends Fragment {
    View root_view;
    String name;
    String number;
    TextView txtname1, txtname, txtnumber, txtusername;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.page_fragment_setting, container, false);

        txtname = root_view.findViewById(R.id.name);
        txtnumber = root_view.findViewById(R.id.txtnumber);
        txtname1 = root_view.findViewById(R.id.txtnam);
        txtusername = root_view.findViewById(R.id.txtusername);

        txtname1.setText(Preferences.getUserName(getActivity()));
        txtusername.setText(Preferences.getUserName(getActivity()));
        txtnumber.setText(Preferences.getNumber(getActivity()));

        return root_view;
    }

    public TextView getChildTextView(View v) {
        for (int index = 0; index < ((LinearLayout) v).getChildCount(); ++index) {
            View nextChild = ((LinearLayout) v).getChildAt(index);
            if (nextChild instanceof TextView) {
                return (TextView) nextChild;
            }
        }
        return null;
    }
}
