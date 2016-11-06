package co.cristiangarcia.bibliareinavalera.sync;

import android.content.Context;
import co.cristiangarcia.bibliareinavalera.bd.DatabaseHelper;

public class SyncHelper {
    public static final int ST_DELETE = 0;
    public static final int ST_MODIFY = 3;
    public static final int ST_NEW = 2;
    public static final int ST_NORMAL = 1;
    private static SyncHelper helper = null;
    public static final String urlBackup = "http://backup.tusversiculos.com/";
    private Context ctx;
    private DatabaseHelper dhelper;

    private SyncHelper(Context ctx) {
        this.dhelper = DatabaseHelper.getLtHelper(ctx);
        this.ctx = ctx;
    }

    public static SyncHelper getHelper(Context ctx) {
        if (helper == null) {
            helper = new SyncHelper(ctx);
        }
        return helper;
    }

    public void syncPasaje() {
    }

    public void syncFavoritos() {
    }
}
