package com.vinod.exoplayer;

public class Track {

    String languageID;
    String languageLabel;

    public Track(String languageID, String languageLabel) {
        this.languageID = languageID;
        this.languageLabel = languageLabel;
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
