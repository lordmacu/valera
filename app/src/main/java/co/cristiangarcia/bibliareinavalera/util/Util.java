package co.cristiangarcia.bibliareinavalera.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Parcelable;
import android.widget.Toast;
import cz.msebera.android.httpclient.protocol.HTTP;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import co.cristiangarcia.bibliareinavalera.BuildConfig;
import co.cristiangarcia.bibliareinavalera.R;
import co.cristiangarcia.bibliareinavalera.sync.SyncHelper;

public class Util {
    private static volatile Typeface BibleTypeface = null;

    public static String formatFecha(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        Date date = cal.getTime();
        return new String[]{"Domingo", "Lunes", "Martes", "Mi\u00e9rcoles", "Jueves", "Viernes", "S\u00e1bado"}[cal.get(7) - 1] + " " + new SimpleDateFormat("dd/MM/yyyy", new Locale("es_ES")).format(date);
    }

    public static String rellenaUnCero(int i) {
        return i < 10 ? "0" + i : BuildConfig.FLAVOR + i;
    }

    public static synchronized Typeface getTypeface(Context ctx) {
        Typeface typeface;
        synchronized (Util.class) {
            switch (Preference.getInt(ctx, "pref_ver_font", Integer.parseInt(String.valueOf(ctx.getResources().getInteger(R.integer.vers_font_def))))) {
                case SyncHelper.ST_DELETE /*0*/:
                    typeface = Typeface.DEFAULT;
                    break;
                case SyncHelper.ST_NORMAL /*1*/:
                    if (BibleTypeface == null) {
                        BibleTypeface = Typeface.createFromAsset(ctx.getAssets(), "DAYROM__.ttf");
                    }
                    typeface = BibleTypeface;
                    break;
                case SyncHelper.ST_NEW /*2*/:
                    typeface = Typeface.SERIF;
                    break;
                case SyncHelper.ST_MODIFY /*3*/:
                    typeface = Typeface.MONOSPACE;
                    break;
                default:
                    typeface = Typeface.DEFAULT;
                    break;
            }
        }
        return typeface;
    }

    public static void copiar(Context ctx, String titulo, String texto) {
        ((ClipboardManager) ctx.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText(null, texto));
        Toast.makeText(ctx, titulo + " Copiado.", 1).show();
    }

    public static void share(Context ctx, String body, int libro, int capitulo, int versiculoi, int versiculof) {
        Intent queryIntent = new Intent("android.intent.action.SEND");
        queryIntent.setType(HTTP.PLAIN_TEXT_TYPE);
        List<ResolveInfo> resInfo = ctx.getPackageManager().queryIntentActivities(queryIntent, 64);
        List<Intent> appIntentList = new ArrayList();
        for (int i = 0; i < resInfo.size(); i++) {
            ResolveInfo ri = (ResolveInfo) resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            Intent intentToAdd = new Intent();
            intentToAdd.setAction("android.intent.action.SEND");
            intentToAdd.setComponent(new ComponentName(packageName, ri.activityInfo.name));
            intentToAdd.setType(HTTP.PLAIN_TEXT_TYPE);
            intentToAdd.setPackage(packageName);
            if (packageName.contains("com.facebook.katana")) {
                intentToAdd.putExtra("android.intent.extra.TEXT", LibrosHelper.getUrlLibCaps(libro, capitulo, versiculoi, versiculof));
            } else {
                intentToAdd.putExtra("android.intent.extra.TEXT", body);
            }
            appIntentList.add(intentToAdd);
        }
        Intent dummyIntent = new Intent();
        dummyIntent.setType("text/plaind");
        Intent chooser = Intent.createChooser(dummyIntent, "share");
        chooser.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) appIntentList.toArray(new Parcelable[0]));
        chooser.putExtra("android.intent.extra.TITLE", "Compartir a trav\u00e9s de");
        ctx.startActivity(chooser);
    }

    public static String getDeviceInformation(Context ctx) {
        String appversion = BuildConfig.FLAVOR;
        try {
            appversion = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
        }
        return Build.MODEL + "," + VERSION.RELEASE + "," + appversion;
    }
}
