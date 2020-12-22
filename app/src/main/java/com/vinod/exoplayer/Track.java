package com.vinod.exoplayer;

import com.google.android.exoplayer2.source.TrackGroupArray;

public class Track {

    int groupIndex;
    int groupIndexWithinTrack;
    String languageID;
    String languageLabel;
    TrackGroupArray tracks;

    public  Track(int groupIndex, int groupIndexWithinTrack, String languageID, String languageLabel, TrackGroupArray trackGroupArray) {
        this.groupIndex = groupIndex;
        this.groupIndexWithinTrack = groupIndexWithinTrack;
        this.languageID = languageID;
        this.languageLabel = languageLabel;
        this.tracks = trackGroupArray;
    }

    public String getLanguageID() {
        return languageID;
    }

    public void setLanguageID(String languageID) {
        this.languageID = languageID;
    }

    public String getLanguageLabel() {
        return languageLabel;
    }

    public void setLanguageLabel(String languageLabel) {
        this.languageLabel = languageLabel;
    }

}
