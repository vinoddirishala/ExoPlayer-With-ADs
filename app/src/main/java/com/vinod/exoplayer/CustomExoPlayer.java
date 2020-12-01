package com.vinod.exoplayer;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.flipkart.madman.exo.extension.MadmanAdLoader;
import com.flipkart.madman.listener.AdErrorListener;
import com.flipkart.madman.listener.AdEventListener;
import com.flipkart.madman.listener.AdLoadListener;
import com.flipkart.madman.manager.AdManager;
import com.flipkart.madman.okhttp.extension.DefaultNetworkLayer;
import com.flipkart.madman.renderer.binder.AdViewBinder;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomExoPlayer extends AppCompatActivity implements AdEventListener, AdLoadListener, AdErrorListener, AdsMediaSource.MediaSourceFactory {

    SimpleExoPlayerView simpleExoPlayerView;
    SimpleExoPlayer simpleExoPlayer;
    ProgressBar progress_circular;
   // private String sampleUrl = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4";
    private String sampleUrl = "https://bitmovin-a.akamaihd.net/content/sintel/hls/playlist.m3u8";
 //   private String sampleUrl = "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8";

    MadmanAdLoader madmanAdLoader;
    DefaultDataSourceFactory defaultDataSourceFactory;
    DefaultTrackSelector trackSelector;
    DefaultTrackSelector.Parameters parameters;
    DefaultTrackSelector.ParametersBuilder builder;

    Button playPauseAd;
    RecyclerView adCallBacks;
    List<AdEvent> adEventList;
    LinearLayoutManager linearLayoutManager;
    AdEventAdapter adEventAdapter;

    ImageView bckwrdIV,fwdIV;
    DefaultTimeBar defaultTimeBar;
    TextView exoPos,exoDuration;

    ArrayList<String> supportedSubTitleLanguages;
    ArrayList<String> supportedAudioLanguages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_exo_player);
        initializeViews();
        initMadMan();
        initExoPlayer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.reset:
                if (simpleExoPlayerView != null && simpleExoPlayer != null){
                    simpleExoPlayer.release();
                    simpleExoPlayer = null;
                    madmanAdLoader.release();
                }
                initializeViews();
                initMadMan();
                initExoPlayer();
                break;
        }
        return true;
    }

    private void initializeViews(){
        supportedSubTitleLanguages = new ArrayList<>();
        supportedAudioLanguages = new ArrayList<>();

        simpleExoPlayerView = findViewById(R.id.simpleExoPlayerView);
        progress_circular = findViewById(R.id.progress_circular);
        playPauseAd = findViewById(R.id.playPauseAd);
        adCallBacks = findViewById(R.id.adCallBacks);

        fwdIV = simpleExoPlayerView.findViewById(R.id.exo_ffwd);
        bckwrdIV = simpleExoPlayerView.findViewById(R.id.exo_rew);
        defaultTimeBar = findViewById(R.id.exo_progress);
        exoDuration = findViewById(R.id.exo_duration);
        exoPos = findViewById(R.id.exo_position);

        linearLayoutManager = new LinearLayoutManager(this);
        adCallBacks.setLayoutManager(linearLayoutManager);
        adEventList = new ArrayList<>();
        adEventAdapter = new AdEventAdapter(this,adEventList);
        linearLayoutManager.setReverseLayout(true);
        adCallBacks.addItemDecoration(new DividerItemDecoration(this,linearLayoutManager.getOrientation()));
        adCallBacks.setAdapter(adEventAdapter);
    }

    private void initExoPlayer(){
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        LoadControl loadControl = new DefaultLoadControl();

        TrackSelection.Factory factory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(factory);
        parameters = trackSelector.getParameters();
        builder = parameters.buildUpon();



        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this,new DefaultRenderersFactory(this),trackSelector,loadControl);
        simpleExoPlayerView.setPlayer(simpleExoPlayer);
        madmanAdLoader.setPlayer(simpleExoPlayer);


        simpleExoPlayer.prepare(getContentMediaSourceWithAds(""));
        simpleExoPlayer.setPlayWhenReady(true);


        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == ExoPlayer.STATE_IDLE){
                    progress_circular.setVisibility(View.VISIBLE);
                }else if (playbackState == ExoPlayer.STATE_BUFFERING){
                    progress_circular.setVisibility(View.VISIBLE);
                }else if (playbackState == ExoPlayer.STATE_READY){
                    progress_circular.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }

    private MediaSource getContentMediaSourceWithAds(String subTitleUrl){
        return new AdsMediaSource(subTitleMediaSource(subTitleUrl), this, madmanAdLoader,
                new AdsLoader.AdViewProvider() {
                    @Override
                    public ViewGroup getAdViewGroup() {
                        return simpleExoPlayerView.getOverlayFrameLayout();
                    }

                    @Override
                    public View[] getAdOverlayViews() {
                        return new View[0];
                    }
                });
    }

    private void initMadMan(){
        AdViewBinder adViewBinder = new AdViewBinder.Builder()
                .setSkipViewId(R.id.skipButton)
                .setClickThroughViewId(R.id.learnMore)
                .setAdCountDownViewId(R.id.countDownTimer)
                .build(R.layout.ad_overlay);


        MadmanAdLoader.Builder builder=  new MadmanAdLoader.Builder(
                this,
                new DefaultNetworkLayer.Builder().build(this))
                .setAdViewBinder(adViewBinder)
                .setAdEventListener(this)
                .setAdLoadListener(this)
                .setAdErrorListener(this);
      //  madmanAdLoader = builder.buildForAdUri(Uri.parse("http://13.58.41.117:8082/ad_response.xml"));
        madmanAdLoader = builder.buildForAdsResponse(readVampDataFromXML("pre_roll_vast.xml"));
        defaultDataSourceFactory = new DefaultDataSourceFactory(this,getString(R.string.app_name));
    }

    private MediaSource subTitleMediaSource(String subTitleUri){
        if (subTitleUri == null || subTitleUri.equalsIgnoreCase("")){
            return buildMediaSource(Uri.parse(sampleUrl));
        }else {
            Format subTitleFormat = Format.createTextSampleFormat(
                    "1",
                    MimeTypes.APPLICATION_SUBRIP,
                    Format.NO_VALUE,
                    "en");
            MediaSource subTitleMediaSource = new SingleSampleMediaSource(Uri.parse(subTitleUri),defaultDataSourceFactory,subTitleFormat,C.TIME_UNSET);
            return new MergingMediaSource(buildMediaSource(Uri.parse(sampleUrl)),subTitleMediaSource);
        }
    }

    public String readVampDataFromXML(String inFile) {
        String tContents = "";
        try {
            InputStream stream = getAssets().open(inFile);
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            tContents = new String(buffer);
        } catch (IOException e) {
            // Handle exceptions here
        }
        return tContents;
    }

    private void hideCustomControls(){
        fwdIV.setVisibility(View.GONE);
        bckwrdIV.setVisibility(View.GONE);
        defaultTimeBar.setVisibility(View.GONE);
        exoPos.setVisibility(View.GONE);
        exoDuration.setVisibility(View.GONE);
        fwdIV.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        bckwrdIV.setLayoutParams(new LinearLayout.LayoutParams(0,0));
    }

    private void showCustomControls(){
        fwdIV.setVisibility(View.VISIBLE);
        bckwrdIV.setVisibility(View.VISIBLE);
        defaultTimeBar.setVisibility(View.VISIBLE);
        exoPos.setVisibility(View.VISIBLE);
        exoDuration.setVisibility(View.VISIBLE);
        fwdIV.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.thirtyFive),getResources().getDimensionPixelSize(R.dimen.thirtyFive)));
        bckwrdIV.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.thirtyFive),getResources().getDimensionPixelSize(R.dimen.thirtyFive)));
        fwdIV.requestLayout();
        bckwrdIV.requestLayout();
    }

    @Override
    public void onAdEvent(AdEvent adEvent) {
        adEventList.add(adEvent);
        adEventAdapter.notifyItemInserted(adEventList.size()+1);
        adEventAdapter.notifyDataSetChanged();
        adCallBacks.scrollToPosition(adEventList.size()-1);
        // Manage ad pods and standalone ads playbacks here
      //  showCustomControls();

      /*  if(adEvent.getAdElement().getAdPod().getTotalAds() <= 1){
            if (adEvent.getType().name().equalsIgnoreCase("COMPLETED") || adEvent.getType().name().equalsIgnoreCase("SKIPPED")){
                showCustomControls();
            }else if (adEvent.getType().name().equalsIgnoreCase("LOADED") || adEvent.getType().name().equalsIgnoreCase("STARTED")){
                hideCustomControls();
            }
        }else if (adEvent.getAdElement().getAdPod().getTotalAds() > 1){
            if (adEvent.getType().name().equalsIgnoreCase("CONTENT_RESUME_REQUESTED") || adEvent.getType().name().equalsIgnoreCase("SKIPPED")){
                showCustomControls();
            }else if (adEvent.getType().name().equalsIgnoreCase("LOADED") || adEvent.getType().name().equalsIgnoreCase("STARTED")){
                hideCustomControls();
            }
        }*/

    }

    @Override
    public void onAdManagerLoadFailed(AdErrorListener.AdError adError) {

    }

    @Override
    public void onAdManagerLoaded(AdManager adManager) {
        hideCustomControls();
    }

    @Override
    public void onAdError(AdError adError) {

    }

    @Override
    public MediaSource createMediaSource(Uri uri) {
        return buildMediaSource(uri);
    }

    private MediaSource buildMediaSource(Uri uri){
        @C.ContentType int type = Util.inferContentType(uri);
        switch (type){
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri);
        }
        return null;
    }

    @Override
    public int[] getSupportedTypes() {
        return new int[]{
                C.TYPE_DASH,
                C.TYPE_HLS,
                C.TYPE_OTHER
        };
    }

    public void pauseAd(View view) {
        simpleExoPlayer.setPlayWhenReady(!simpleExoPlayer.getPlayWhenReady());
        playPauseAd.setText(simpleExoPlayer.getPlayWhenReady() ? "Pause AD":"Play AD");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initExoPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (simpleExoPlayerView != null && simpleExoPlayer != null){
            simpleExoPlayer.release();
            simpleExoPlayer = null;
            madmanAdLoader.setPlayer(null);
        }
    }

    @Override
    protected void onDestroy() {
        if (simpleExoPlayerView != null && simpleExoPlayer != null){
            simpleExoPlayer.release();
            simpleExoPlayer = null;
            madmanAdLoader.release();
        }
        super.onDestroy();

    }

    public void changeAudio(View view) {

    }

    public void changeSubTitle(View view) {
        changeSubTitleAlert();
    }

    private void resetSubTitles(){
        int windowIndex = simpleExoPlayer.getCurrentWindowIndex();
    }

    private void changeSubTitleAlert(){
        String[] subtitleLanguages = {"Off","English","Telugu","Hindi"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setItems(subtitleLanguages, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    simpleExoPlayer.seekTo(simpleExoPlayer.getContentPosition());
                    simpleExoPlayer.prepare(getContentMediaSourceWithAds(""));
                }else if (which == 1){
                    simpleExoPlayer.seekTo(simpleExoPlayer.getContentPosition());
                    simpleExoPlayer.prepare(getContentMediaSourceWithAds("http://www.storiesinflight.com/js_videosub/jellies.srt"));
                }else if (which == 2){
                    simpleExoPlayer.seekTo(simpleExoPlayer.getContentPosition());
                    simpleExoPlayer.prepare(getContentMediaSourceWithAds("http://www.storiesinflight.com/js_videosub/jellies.srt"));
                }else if (which == 3){

                }
                dialog.dismiss();
            }

        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void fetchSubTitles(View view) {
        // Subtitle's purpose
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        TrackGroupArray groupArray = mappedTrackInfo.getTrackGroups(1);
        TrackSelectionArray currentTrackGroups = simpleExoPlayer.getCurrentTrackSelections();
        TrackSelection currentTrackSelection = currentTrackGroups.get(1);
        Log.d("Hi",currentTrackSelection.getSelectedFormat().label+" let's look");
        Log.d("Hi",groupArray.get(0).getFormat(0).label+" is the first language...!");
        for (int groupIndex = 0; groupIndex < groupArray.length; groupIndex++) {

            TrackGroup group = groupArray.get(groupIndex);

            Log.d("Hi",groupArray.length+"/"+group.length);


            for (int trackIndex = 0; trackIndex < group.length; trackIndex++) {
                Format trackFormat = group.getFormat(trackIndex);

                supportedSubTitleLanguages.add(trackFormat.label == null ? "None":trackFormat.label);


                Log.d("subtracks",group.length+"----"+trackFormat.label+" is the language label--"+groupIndex+"--"+trackIndex);


                if(currentTrackSelection!=null && currentTrackSelection.getSelectedFormat()==trackFormat){
                    //THIS ONE IS SELECTED
                    Log.d("track:vinod","Selected track is "+trackFormat.toString()+"-/"+ trackFormat.label);

                }else {
                    Log.d("track:vinod","This is not selected track");
                }
            }
        }
        Log.d("4th Language is:;-",supportedSubTitleLanguages.get(3));


        // setting selected sub title
        for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
            int trackType = mappedTrackInfo.getRendererType(rendererIndex);
            Log.d("trackType",trackType+"'");
            if (trackType == C.TRACK_TYPE_TEXT) {
                Log.d("trackType","TRACK_TYPE_TEXT");
                builder.clearSelectionOverrides(rendererIndex).setRendererDisabled(rendererIndex, false);
               int groupIndex = 0;
                int[] tracks = {0};
                DefaultTrackSelector.SelectionOverride override = new DefaultTrackSelector.SelectionOverride(groupIndex, tracks);
                builder.setSelectionOverride(rendererIndex, mappedTrackInfo.getTrackGroups(rendererIndex), override);
                Log.d("trackType","Override");
            }
        }
        trackSelector.setParameters(builder);
    }
}