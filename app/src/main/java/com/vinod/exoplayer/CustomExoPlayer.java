package com.vinod.exoplayer;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomExoPlayer extends AppCompatActivity implements AdEventListener, AdLoadListener, AdErrorListener, AdsMediaSource.MediaSourceFactory, View.OnClickListener {

    SimpleExoPlayerView simpleExoPlayerView;
    SimpleExoPlayer simpleExoPlayer;
    ProgressBar progress_circular;
    private String sampleUrl = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4";

    MadmanAdLoader madmanAdLoader;
    DefaultDataSourceFactory defaultDataSourceFactory;

    Button playPauseAd;
    RecyclerView adCallBacks;
    List<AdEvent> adEventList;
    LinearLayoutManager linearLayoutManager;
    AdEventAdapter adEventAdapter;

    ImageView bckwrdIV,fwdIV;
    DefaultTimeBar defaultTimeBar;
    TextView exoPos,exoDuration;

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
        simpleExoPlayerView = findViewById(R.id.simpleExoPlayerView);
        progress_circular = findViewById(R.id.progress_circular);
        playPauseAd = findViewById(R.id.playPauseAd);
        adCallBacks = findViewById(R.id.adCallBacks);

        fwdIV = simpleExoPlayerView.findViewById(R.id.exo_ffwd);
        bckwrdIV = simpleExoPlayerView.findViewById(R.id.exo_rew);
        defaultTimeBar = findViewById(R.id.exo_progress);
        exoDuration = findViewById(R.id.exo_duration);
        exoPos = findViewById(R.id.exo_position);


        fwdIV.setOnClickListener(this);
        bckwrdIV.setOnClickListener(this);

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
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this,new DefaultRenderersFactory(this),trackSelector,loadControl);
        simpleExoPlayerView.setPlayer(simpleExoPlayer);
        madmanAdLoader.setPlayer(simpleExoPlayer);

        MediaSource contentMediaSourceWithAds = new AdsMediaSource(buildMediaSource(Uri.parse(sampleUrl)), this, madmanAdLoader,
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

        simpleExoPlayer.prepare(contentMediaSourceWithAds);
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


    private void initMadMan(){
        AdViewBinder adViewBinder = new AdViewBinder.Builder()
                .setSkipViewId(R.id.skip_view)
                .setClickThroughViewId(R.id.click_through)
                .build(R.layout.ad_overlay);
        MadmanAdLoader.Builder builder=  new MadmanAdLoader.Builder(
                this,
                new DefaultNetworkLayer.Builder().build(this))
                .setAdViewBinder(adViewBinder)
                .setAdEventListener(this)
                .setAdLoadListener(this)
                .setAdErrorListener(this);
       // madmanAdLoader = builder.buildForAdUri(Uri.parse("http://13.58.41.117:8082/vast_ads_server.xml"));
        madmanAdLoader = builder.buildForAdsResponse(readVampDataFromXML("ad_response.xml"));
        defaultDataSourceFactory = new DefaultDataSourceFactory(this,getString(R.string.app_name));
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
        if (adEvent.getType().name().equalsIgnoreCase("COMPLETED") || adEvent.getType().name().equalsIgnoreCase("SKIPPED")){
            showCustomControls();
        }else if (adEvent.getType().name().equalsIgnoreCase("LOADED") || adEvent.getType().name().equalsIgnoreCase("STARTED")){
            hideCustomControls();
        }
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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.exo_ffwd:

                break;
            case R.id.exo_rew:

                break;
        }
    }
}