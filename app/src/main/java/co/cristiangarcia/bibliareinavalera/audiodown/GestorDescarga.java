package co.cristiangarcia.bibliareinavalera.audiodown;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class GestorDescarga {
    private AsyncHttpClient client = new AsyncHttpClient();
    private ArrayList<Audio> cola = new ArrayList();
    private DownService ctx;
    private long time;

    public enum DownloadState {
        NONE,
        QUEUED,
        DOWNLOADING,
        COMPLETE,
        FAILURE
    }

    public enum NotifyState {
        UPDATE,
        DONE,
        CANCEL,
        FAIL
    }

    public GestorDescarga(DownService ctx) {
        this.ctx = ctx;
    }

    public void inicDescarga() {
        int i = 0;
        while (i < this.cola.size()) {
            if (((Audio) this.cola.get(i)).getDownloadState() != DownloadState.DOWNLOADING) {
                i++;
            } else {
                return;
            }
        }
        for (i = 0; i < this.cola.size(); i++) {
            Audio aud = (Audio) this.cola.get(i);
            if (aud.getDownloadState() == DownloadState.FAILURE || aud.getDownloadState() == DownloadState.QUEUED) {
                aud.setDownloadState(DownloadState.DOWNLOADING);
                descargar(aud);
                return;
            }
        }
    }

    private void descargar(final Audio nodo) {
        String mediaUrl = Util.url + nodo.getDownName();
        final File tempFile = new File(Util.getDirAudiosCache(this.ctx), nodo.getDownName());
        this.time = System.currentTimeMillis();
        this.client.get(this.ctx, mediaUrl, new FileAsyncHttpResponseHandler(tempFile) {
            public void onFailure(int arg0, Header[] arg1, Throwable arg2, File arg3) {
                tempFile.delete();
                nodo.setDownloadState(DownloadState.FAILURE);
                GestorDescarga.this.ctx.marcarFailure();
                GestorDescarga.this.ctx.notificar(nodo, NotifyState.FAIL, 0);
            }

            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                long current = System.currentTimeMillis();
                if (current - GestorDescarga.this.time >= 500) {
                    GestorDescarga.this.time = current;
                    nodo.setProgress(bytesWritten, totalSize);
                    GestorDescarga.this.ctx.notificar(nodo, NotifyState.UPDATE, (int) ((((double) bytesWritten) / ((double) totalSize)) * 100.0d));
                    GestorDescarga.this.ctx.updateItemCola(GestorDescarga.this.cola);
                }
            }

            public void onCancel() {
                super.onCancel();
                GestorDescarga.this.ctx.notificar(nodo, NotifyState.CANCEL, 0);
            }

            public void onSuccess(int arg0, Header[] arg1, File arg2) {
                Util.moveFile(tempFile, new File(Util.getDirAudios(GestorDescarga.this.ctx), tempFile.getName()));
                nodo.setDownloadState(DownloadState.COMPLETE);
                nodo.setCountWithSize();
                GestorDescarga.this.ctx.notificar(nodo, NotifyState.DONE, 0);
                GestorDescarga.this.ctx.updateItemCola(GestorDescarga.this.cola);
                GestorDescarga.this.ctx.updateItemLibro(nodo);
                GestorDescarga.this.inicDescarga();
            }
        });
    }

    private void cancelarDescargas() {
        this.client.cancelAllRequests(true);
    }

    public boolean agregarAudioCola(Audio nodo) {
        if (this.cola == null || this.cola.contains(nodo) || !this.cola.add(nodo)) {
            return false;
        }
        nodo.setDownloadState(DownloadState.QUEUED);
        return true;
    }

    public ArrayList<Audio> getCola() {
        return this.cola;
    }

    public void borrarAudioCola(Audio item) {
        for (int i = 0; i < this.cola.size(); i++) {
            Audio nodo = (Audio) this.cola.get(i);
            if (nodo.equals(item)) {
                if (nodo.getDownloadState() == DownloadState.DOWNLOADING) {
                    cancelarDescargas();
                }
                this.cola.remove(i);
                return;
            }
        }
    }

    public void limpiarTodoCola() {
        boolean noty = true;
        Iterator<Audio> iter = this.cola.iterator();
        while (iter.hasNext()) {
            Audio nodo = (Audio) iter.next();
            if (nodo.getDownloadState() == DownloadState.COMPLETE) {
                iter.remove();
            } else if (nodo.getDownloadState() == DownloadState.DOWNLOADING) {
                noty = false;
            }
        }
        if (noty) {
            this.ctx.limpiarNotificacion();
        }
    }

    public void borrarTodoCola() {
        cancelarDescargas();
        this.cola.clear();
        this.ctx.limpiarNotificacion();
    }
}
