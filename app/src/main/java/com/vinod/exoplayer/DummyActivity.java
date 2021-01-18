package com.vinod.exoplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.mediarouter.app.MediaRouteButton;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;

public class DummyActivity extends AppCompatActivity {

    MediaRouteButton media_route_button;
    CastContext mCastContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);
        media_route_button = findViewById(R.id.media_route_button);

        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), media_route_button);
      //  mCastContext = CastContext.getSharedInstance(this);
        media_route_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DummyActivity.this, "Hi", Toast.LENGTH_SHORT).show();
            }
        });

    }






}