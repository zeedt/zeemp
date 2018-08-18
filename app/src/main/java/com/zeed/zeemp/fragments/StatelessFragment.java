package com.zeed.zeemp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zeed.zeemp.models.Audio;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by teamapt on 15/08/2018.
 */

public class StatelessFragment  extends Fragment {

    public Audio currentlyPlayed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

}
