package com.vinod.exoplayer;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private Context mContext;
    View  bottomSheetView;
    ListView textTracks;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<Track> tracks;
    String[] tracksStringArray;
    ItemClickListener mListener;

    public BottomSheetDialog(Context mContext,ArrayList<Track> list) {
        this.mContext = mContext;
        this.tracks = list;
        Log.d("VD",list.size()+" ooooooooooooooooooooooooo");
    }

    private String[] getTracksLabels(){
        tracksStringArray = new String[]{tracks.get(0).languageLabel,tracks.get(1).languageLabel};
        return tracksStringArray;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bottomSheetView = inflater.inflate(R.layout.sheet_tracks,container,false);
        textTracks = bottomSheetView.findViewById(R.id.tracksList);
        return bottomSheetView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        arrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1,getTracksLabels());
        textTracks.setAdapter(arrayAdapter);
        textTracks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    
                }else if (position == 1){

                }
            }
        });
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ItemClickListener){
           mListener = (ItemClickListener)context;
        }else {
            throw new RuntimeException("BottomSheet must implement the listener....!!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ItemClickListener{
        void onItemClick(Track track);
    }

}

