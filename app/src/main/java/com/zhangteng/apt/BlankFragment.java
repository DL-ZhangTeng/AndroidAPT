package com.zhangteng.apt;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zhangteng.annotation.BindView;
import com.zhangteng.annotation.OnClick;
import com.zhangteng.library.BindViewTool;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment {

    @SuppressLint("NonConstantResourceId")
    @BindView(value = R.id.tv_hello1)
    TextView textView1;
    @SuppressLint("NonConstantResourceId")
    @BindView(value = R.id.tv_hello2)
    TextView textView2;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public BlankFragment() {

    }

    public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BindViewTool.bind(this);

        textView1.setText("Hello World");
        textView2.setText("Hello World");
    }

    @OnClick({R.id.tv_hello1, R.id.tv_hello2})
    public void onClick(View view) {
        if (view.getId() == R.id.tv_hello1) {
            Toast.makeText(requireContext(), "tv_hello1", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.tv_hello2) {
            Toast.makeText(requireContext(), "tv_hello2", Toast.LENGTH_SHORT).show();
        }
    }
}