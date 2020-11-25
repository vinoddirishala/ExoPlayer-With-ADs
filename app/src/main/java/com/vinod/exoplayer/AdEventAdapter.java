package com.vinod.exoplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flipkart.madman.listener.AdEventListener;

import java.util.List;

public class AdEventAdapter extends RecyclerView.Adapter<AdEventAdapter.AdEventVewHolder> {

    private Context mContext;
    private List<AdEventListener.AdEvent> adEvents;

    public AdEventAdapter(Context mContext,List<AdEventListener.AdEvent> adEvents) {
        this.adEvents = adEvents;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AdEventVewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdEventVewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_ad_event,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdEventVewHolder holder, int position) {
        holder.adEventTV.setText(
                        "AD Id: "+adEvents.get(position).getAdElement().getId().toString()+"\n"+
                        "Duration : "+adEvents.get(position).getAdElement().getDuration()+"\n"+
                        "Title : "+adEvents.get(position).getAdElement().getTitle()+"\n"+
                        "\n\n"+adEvents.get(position).getType().toString());
    }


    @Override
    public int getItemCount() {
        return adEvents.size();
    }

    class AdEventVewHolder extends RecyclerView.ViewHolder{

        TextView adEventTV;

        public AdEventVewHolder(@NonNull View itemView) {
            super(itemView);
            adEventTV= itemView.findViewById(R.id.adEventTV);
        }
    }


}
