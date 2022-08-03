package com.example.dynamicaudioplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOError;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button playBtn, pauseBtn;
    MediaPlayer mediaPlayer;
    String audioUrl;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playBtn = findViewById(R.id.idBtnPlay);
        pauseBtn = findViewById(R.id.idBtnPause);
        audioUrl = "";
        mediaPlayer = new MediaPlayer();

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance("https://dynamic-audio-player-71b41-default-rtdb.europe-west1.firebasedatabase.app/");

        databaseReference = firebaseDatabase.getReference();
        databaseReferenceChild = databaseReference.child("url");

        databaseReferenceChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                audioUrl = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,"Fail to get audio url.",Toast.LENGTH_SHORT).show();
            }
        });



        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    playAudio(audioUrl);

                }catch (IOError ex) {
                    Toast.makeText(MainActivity.this,"An error occurred while playing sound" + ex, Toast.LENGTH_SHORT).show();
                }

            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();

                    Toast.makeText(MainActivity.this,"Audio has been paused.",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"Auido has not played.",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void playAudio(String audioUrl){
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setAudioAttributes(
                new AudioAttributes
                        .Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());
        try {

            mediaPlayer.setDataSource(audioUrl);

            mediaPlayer.prepare();
            mediaPlayer.start();

        }catch (IOException e) {
            Toast.makeText(MainActivity.this,"Error found is" + e, Toast.LENGTH_SHORT).show();
        }
    }
}