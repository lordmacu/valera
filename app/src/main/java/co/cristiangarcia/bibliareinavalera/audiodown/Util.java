package co.cristiangarcia.bibliareinavalera.audiodown;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import co.cristiangarcia.bibliareinavalera.BuildConfig;

public class Util {
    public static final String url = "http://audios.tusversiculos.com/";

    public static String rellenaUnCero(int i) {
        return i < 10 ? "0" + i : BuildConfig.FLAVOR + i;
    }

    public static File getDirAudios(Context ctx) {
        File dir;
        if ("mounted".equals(Environment.getExternalStorageState())) {
            dir = new File(Environment.getExternalStorageDirectory() + "/AudiosReinaValera");
        } else {
            dir = ctx.getCacheDir();
        }
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    public static File getDirAudiosCache(Context ctx) {
        File cache = new File(getDirAudios(ctx) + "/cache");
        if (!cache.exists()) {
            cache.mkdir();
        }
        return cache;
    }

    public static void iterateAudiosLibro(Context ctx, ArrayList<AudLibro> list) {
        File[] files = getDirAudios(ctx).listFiles();
        if (files != null) {
            int i;
            for (File file : files) {
                try {
                    if (file.isFile()) {
                        String name = file.getName();
                        int ind1 = name.indexOf("_");
                        int ind2 = name.indexOf(".");
                        if (ind1 >= 0 && ind2 >= 0) {
                            int lib = AudiosMap.getLibID(name.substring(0, ind1));
                            if (lib > 0) {
                                ((AudLibro) list.get(lib - 1)).setDownloadedAudio(Integer.valueOf(name.substring(ind1 + 1, ind2)).intValue());
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
            for (i = 0; i < list.size(); i++) {
                ((AudLibro) list.get(i)).setVerificado();
            }
        }
    }

    public static void moveFile(File in, File out) {
        try {
            FileChannel outputChannel = new FileOutputStream(out).getChannel();
            FileChannel inputChannel = new FileInputStream(in).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            if (inputChannel != null) {
                inputChannel.close();
            }
            if (outputChannel != null) {
                outputChannel.close();
            }
            in.delete();
        } catch (Exception e) {
        }
    }
}
