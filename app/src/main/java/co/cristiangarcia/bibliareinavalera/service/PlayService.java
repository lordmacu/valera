package co.cristiangarcia.bibliareinavalera.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import java.io.File;
import co.cristiangarcia.bibliareinavalera.audiodown.AudioActivity;
import co.cristiangarcia.bibliareinavalera.audiodown.AudiosMap;

public class PlayService extends Service {
    public static final String action_progress = "co.cristiangarcia.bibliareinavalera.PlayService.action_progress";
    private AudioServiceBinder binder = new AudioServiceBinder();
    private final Handler handler = new Handler();
    private MediaPlayer mPlayer = null;

    public class AudioServiceBinder extends Binder {
        private volatile int capitulo;
        private volatile String libro;
        private volatile int ncapitulo;
        private volatile int nlibro;

        public void setplay(String libro, int nlibro, int capitulo, int ncapitulo) {
            this.libro = libro;
            this.nlibro = nlibro;
            this.capitulo = capitulo;
            this.ncapitulo = ncapitulo;
        }

        public void play() {
            try {
                sendTitle();
                File file = AudiosMap.getFile(PlayService.this.getApplicationContext(), this.nlibro, this.capitulo);
                if (file.exists()) {
                    Intent intent = new Intent(PlayService.action_progress);
                    intent.putExtra("initPlay", true);
                    PlayService.this.sendBroadcast(intent);
                    PlayService.this.mPlayer = new MediaPlayer();
                    Runtime.getRuntime().exec("chmod 644 " + file.getAbsolutePath());
                    PlayService.this.mPlayer.setDataSource(PlayService.this.getApplicationContext(), Uri.fromFile(file));
                    PlayService.this.mPlayer.setLooping(false);
                    PlayService.this.mPlayer.prepare();
                    PlayService.this.mPlayer.setOnCompletionListener(new OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            PlayService.this.handler.postDelayed(new Runnable() {
                                public void run() {
                                    if (AudioServiceBinder.this.capitulo < AudioServiceBinder.this.ncapitulo) {
                                        AudioServiceBinder.this.capitulo = AudioServiceBinder.this.capitulo + 1;
                                        AudioServiceBinder.this.play();
                                        return;
                                    }
                                    Intent intent = new Intent(PlayService.action_progress);
                                    intent.putExtra("donePlay", true);
                                    PlayService.this.sendBroadcast(intent);
                                }
                            }, 1500);
                        }
                    });
                    resume();
                    return;
                }
                Intent myIntent = new Intent();
                myIntent.setClass(PlayService.this.getApplicationContext(), AudioActivity.class);
                myIntent.putExtra("lb", this.nlibro);
                myIntent.setFlags(268435456);
                PlayService.this.startActivity(myIntent);
            } catch (Exception e) {
            }
        }

        public void resume() {
            if (PlayService.this.mPlayer != null) {
                PlayService.this.mPlayer.start();
                startPlayProgressUpdater();
            }
        }

        public void pause() {
            if (PlayService.this.mPlayer != null) {
                PlayService.this.mPlayer.pause();
            }
        }

        public void stop() {
            if (PlayService.this.mPlayer != null) {
                PlayService.this.mPlayer.pause();
                PlayService.this.mPlayer = null;
            }
        }

        public void next() {
            if (this.capitulo < this.ncapitulo) {
                if (isPlaying()) {
                    stop();
                }
                this.capitulo++;
                play();
            }
        }

        public void prev() {
            if (this.capitulo == 1) {
                setCurrentPosition(0);
            } else if (this.capitulo > 1) {
                if (isPlaying()) {
                    stop();
                }
                this.capitulo--;
                play();
            }
        }

        public boolean isPlaying() {
            if (PlayService.this.mPlayer != null) {
                return PlayService.this.mPlayer.isPlaying();
            }
            return false;
        }

        public boolean isPause() {
            if (PlayService.this.mPlayer == null || PlayService.this.mPlayer.getCurrentPosition() <= 0) {
                return false;
            }
            return true;
        }

        public void setCurrentPosition(int progress) {
            if (PlayService.this.mPlayer != null) {
                PlayService.this.mPlayer.seekTo(progress);
                resume();
            }
        }

        public void startPlayProgressUpdater() {
            if (PlayService.this.mPlayer != null) {
                sendPlay();
                if (PlayService.this.mPlayer.isPlaying()) {
                    PlayService.this.handler.postDelayed(new Runnable() {
                        public void run() {
                            AudioServiceBinder.this.startPlayProgressUpdater();
                        }
                    }, 1000);
                }
            }
        }

        public boolean isOpen() {
            return PlayService.this.mPlayer != null;
        }

        public void sendTitle() {
            Intent intent = new Intent(PlayService.action_progress);
            intent.putExtra("title", this.libro + " " + this.capitulo);
            PlayService.this.sendBroadcast(intent);
        }

        public void sendPlay() {
            Intent intent = new Intent(PlayService.action_progress);
            intent.putExtra("play", PlayService.this.mPlayer.getCurrentPosition());
            intent.putExtra("duration", PlayService.this.mPlayer.getDuration());
            PlayService.this.sendBroadcast(intent);
        }
    }

    public IBinder onBind(Intent arg0) {
        return this.binder;
    }
}
