package co.cristiangarcia.bibliareinavalera.audiodown;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;
import java.util.ArrayList;
import co.cristiangarcia.bibliareinavalera.R;
import co.cristiangarcia.bibliareinavalera.audiodown.GestorDescarga.NotifyState;
import co.cristiangarcia.bibliareinavalera.node.Libro;
import co.cristiangarcia.bibliareinavalera.sync.SyncHelper;
import co.cristiangarcia.bibliareinavalera.util.LibrosHelper;

public class DownService extends Service {
    public static final String action_sendIsFailure = "co.cristiangarcia.bibliareinavalera.audiodown.DownService.action_sendIsFailure";
    public static final String action_sendcola = "co.cristiangarcia.bibliareinavalera.audiodown.DownService.action_sendcola";
    public static final String action_sendlibros = "co.cristiangarcia.bibliareinavalera.audiodown.DownService.action_sendlibros";
    public static final String action_updateitemcola = "co.cristiangarcia.bibliareinavalera.audiodown.DownService.action_updateitemcola";
    public static final String action_verifyaudioslib = "co.cristiangarcia.bibliareinavalera.audiodown.DownService.action_verifyaudioslib";
    private static final int id_noty = 103;
    private DownServiceBinder binder = new DownServiceBinder();
    private GestorDescarga gdown = new GestorDescarga(this);
    private boolean isFailure = false;
    private ArrayList<AudLibro> listlibro = new ArrayList();
    private NotificationManager notificationManager;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$reyfasoft$reinavalera1960$audiodown$GestorDescarga$NotifyState = new int[NotifyState.values().length];

        static {
            try {
                $SwitchMap$org$reyfasoft$reinavalera1960$audiodown$GestorDescarga$NotifyState[NotifyState.DONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$reyfasoft$reinavalera1960$audiodown$GestorDescarga$NotifyState[NotifyState.FAIL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$reyfasoft$reinavalera1960$audiodown$GestorDescarga$NotifyState[NotifyState.CANCEL.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$reyfasoft$reinavalera1960$audiodown$GestorDescarga$NotifyState[NotifyState.UPDATE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public class DownServiceBinder extends Binder {
        DownService getService() {
            return DownService.this;
        }
    }

    private class VerifyAudiosLibro extends AsyncTask<Void, Integer, Boolean> {
        private VerifyAudiosLibro() {
        }

        protected Boolean doInBackground(Void... arg0) {
            Util.iterateAudiosLibro(DownService.this.getApplicationContext(), DownService.this.listlibro);
            return null;
        }

        protected void onPostExecute(Boolean result) {
            Intent intent = new Intent(DownService.action_verifyaudioslib);
            intent.putParcelableArrayListExtra("listlibro", DownService.this.listlibro);
            DownService.this.sendBroadcast(intent);
        }
    }

    public void onCreate() {
        super.onCreate();
        this.notificationManager = (NotificationManager) getSystemService("notification");
        inicializarListaLibros();
    }

    public IBinder onBind(Intent arg0) {
        return this.binder;
    }

    private void inicializarListaLibros() {
        if (this.listlibro.size() == 0) {
            int i;
            Libro nodo;
            ArrayList<Libro> ant = LibrosHelper.getLibrosAT();
            ArrayList<Libro> nvo = LibrosHelper.getLibrosNT();
            for (i = 0; i < ant.size(); i++) {
                nodo = (Libro) ant.get(i);
                this.listlibro.add(new AudLibro(nodo.getName(), AudiosMap.getPrefijo(nodo.getId()), nodo.getNumCap()));
            }
            for (i = 0; i < nvo.size(); i++) {
                nodo = (Libro) nvo.get(i);
                this.listlibro.add(new AudLibro(nodo.getName(), AudiosMap.getPrefijo(nodo.getId()), nodo.getNumCap()));
            }
        }
    }

    public void updateItemCola(ArrayList<Audio> cola) {
        Intent intent = new Intent(action_updateitemcola);
        intent.putParcelableArrayListExtra("cola", cola);
        sendBroadcast(intent);
    }

    public void updateItemLibro(Audio item) {
        for (int i = 0; i < this.listlibro.size(); i++) {
            AudLibro nodo = (AudLibro) this.listlibro.get(i);
            if (nodo.getPrefijo().equals(item.getPrefijo())) {
                nodo.setDownloadedAudio(item.getCapitulo());
                break;
            }
        }
        sendLibros();
    }

    public void limpiarCola() {
        this.gdown.limpiarTodoCola();
        sendCola();
    }

    public void borrarTodo() {
        this.gdown.borrarTodoCola();
        sendCola();
    }

    public void notificar(Audio aud, NotifyState status, int count) {
        Builder notificationBuilder;
        switch (AnonymousClass1.$SwitchMap$org$reyfasoft$reinavalera1960$audiodown$GestorDescarga$NotifyState[status.ordinal()]) {
            case SyncHelper.ST_NORMAL /*1*/:
                notificationBuilder = new Builder(getApplicationContext());
                notificationBuilder.setContentTitle(aud.getName());
                notificationBuilder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), AudioActivity.class), 134217728));
                notificationBuilder.setContentText("Descarga completa");
                notificationBuilder.setSmallIcon(17301634);
                notificationBuilder.setProgress(0, 0, false);
                notificationBuilder.setOngoing(false);
                this.notificationManager.notify(id_noty, notificationBuilder.build());
                return;
            case SyncHelper.ST_NEW /*2*/:
                notificationBuilder = new Builder(getApplicationContext());
                notificationBuilder.setContentTitle(aud.getName());
                notificationBuilder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), AudioActivity.class), 134217728));
                notificationBuilder.setContentText("Descarga fallida");
                notificationBuilder.setSmallIcon(17301634);
                notificationBuilder.setProgress(0, 0, false);
                notificationBuilder.setOngoing(false);
                notificationBuilder.setSound(RingtoneManager.getDefaultUri(2));
                this.notificationManager.notify(id_noty, notificationBuilder.build());
                return;
            case SyncHelper.ST_MODIFY /*3*/:
                this.notificationManager.cancel(id_noty);
                return;
            case R.styleable.View_theme /*4*/:
                notificationBuilder = new Builder(getApplicationContext());
                notificationBuilder.setContentTitle(aud.getName());
                notificationBuilder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), AudioActivity.class), 134217728));
                notificationBuilder.setContentText(count + "%");
                notificationBuilder.setProgress(100, count, false);
                notificationBuilder.setSmallIcon(17301633);
                notificationBuilder.setOngoing(true);
                this.notificationManager.notify(id_noty, notificationBuilder.build());
                return;
            default:
                return;
        }
    }

    public void limpiarNotificacion() {
        this.notificationManager.cancel(id_noty);
    }

    public void sendLibros() {
        Intent intent = new Intent(action_sendlibros);
        intent.putParcelableArrayListExtra("listlibro", this.listlibro);
        sendBroadcast(intent);
    }

    public void sendCola() {
        Intent intent = new Intent(action_sendcola);
        intent.putParcelableArrayListExtra("listcola", this.gdown.getCola());
        sendBroadcast(intent);
    }

    public void VerifyAudiosLibro() {
        new VerifyAudiosLibro().execute(new Void[0]);
    }

    public void addAudioCola(Audio audio) {
        if (this.gdown.agregarAudioCola(audio)) {
            this.gdown.inicDescarga();
            sendCola();
        }
    }

    public void addListAudiosCola(Audio[] list) {
        for (Audio item : list) {
            if (item.getIsDownloaded() != 1) {
                this.gdown.agregarAudioCola(item);
            }
        }
        sendCola();
        this.gdown.inicDescarga();
    }

    public void delAudioCola(Audio item) {
        this.gdown.borrarAudioCola(item);
        sendCola();
        this.gdown.inicDescarga();
    }

    public void marcarFailure() {
        this.isFailure = true;
        sendIsFailure();
    }

    public void sendIsFailure() {
        Intent intent = new Intent(action_sendIsFailure);
        intent.putExtra("isFailure", this.isFailure);
        sendBroadcast(intent);
    }

    public void reanudarDescarga() {
        this.gdown.inicDescarga();
        this.isFailure = false;
        sendIsFailure();
    }
}
