package com.immymemine.kevin.musicplayer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.immymemine.kevin.musicplayer.R;
import com.immymemine.kevin.musicplayer.adapter.RecyclerViewAdapter;

/**
 * Created by quf93 on 2017-10-20.
 */

public class AlbumFragment extends Fragment {
    private static AlbumFragment f = null;
    private AlbumFragment() {

    }
    public static AlbumFragment getInstance() {
        if(f == null) {
            f = new AlbumFragment();
        }
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new RecyclerViewAdapter(context, mListener));
        }

        return view;
    }

    InteractionListener mListener;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof InteractionListener) {
            mListener = (InteractionListener) context;
        } else {
            Log.e(context.toString(), " must implement InteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
