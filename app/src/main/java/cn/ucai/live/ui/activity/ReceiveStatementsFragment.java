package cn.ucai.live.ui.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.ucai.live.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReceiveStatementsFragment extends Fragment {


    public ReceiveStatementsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_receive_statements, container, false);
    }

}
