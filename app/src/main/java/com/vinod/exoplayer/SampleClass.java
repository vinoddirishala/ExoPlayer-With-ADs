package com.vinod.exoplayer;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.util.Assertions;

public class SampleClass {


  /*  public void setAudioTrack(int track) {
        System.out.println("setAudioTrack: " + track);
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = Assertions.checkNotNull(trackSelector.getCurrentMappedTrackInfo());
        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
        DefaultTrackSelector.ParametersBuilder builder = parameters.buildUpon();

        for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
            int trackType = mappedTrackInfo.getRendererType(rendererIndex);
            if (trackType == C.TRACK_TYPE_AUDIO) {
                builder.clearSelectionOverrides(rendererIndex).setRendererDisabled(rendererIndex, false);
                int groupIndex = track - 1;
                int[] tracks = {0};
                DefaultTrackSelector.SelectionOverride override = new DefaultTrackSelector.SelectionOverride(groupIndex, tracks);
                builder.setSelectionOverride(rendererIndex, mappedTrackInfo.getTrackGroups(rendererIndex), override);
            }
        }
        trackSelector.setParameters(builder);
        curentAudioTrack = track;
    }*/

}
