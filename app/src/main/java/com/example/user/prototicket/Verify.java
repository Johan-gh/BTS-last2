package com.example.user.prototicket;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class Verify extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_frag1, container, false);

     //   TextView tv = (TextView) v.findViewById(R.id.tvFragFirst);
     //   tv.setText(getArguments().getString("msg"));

        return v;
    }

    public static Verify newInstance(String text) {

        Verify f = new Verify();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}
