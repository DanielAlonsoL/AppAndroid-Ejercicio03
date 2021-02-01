package com.danielalonso.ejercicio03.sound;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import com.danielalonso.ejercicio03.R;

public class ServiceBackgroundMusic extends Service {

    MediaPlayer player;

    public ServiceBackgroundMusic() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        player = MediaPlayer.create(this, R.raw.pokemon_song);
        player.setLooping(true);
        player.setVolume(100,100);

        registerReceiver(new ServicioMusicaBroadcast(), new IntentFilter(getString(R.string.pausa_musica)));
        registerReceiver(new ServicioMusicaBroadcast(), new IntentFilter(getString(R.string.reproduce_musica)));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!player.isPlaying()) {
            player.seekTo(0);
            player.start();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
    }

    public class ServicioMusicaBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case "PAUSA_MUSICA":
                    player.pause();
                    break;
                case "REPRODUCE_MUSICA":
                    if(!player.isPlaying())
                        player.start();
                    break;
                default:
                    break;
            }
        }
    }
}