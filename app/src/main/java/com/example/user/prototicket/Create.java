package com.example.user.prototicket;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class Create extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_frag2, container, false);

        //   TextView tv = (TextView) v.findViewById(R.id.tvFragFirst);
        //   tv.setText(getArguments().getString("msg"));

        return v;
    }

    public static Create newInstance(String text) {

        Create f = new Create();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}

