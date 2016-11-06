package co.cristiangarcia.bibliareinavalera.audiodown;

import android.content.Context;
import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import co.cristiangarcia.bibliareinavalera.BuildConfig;

public class AudiosMap {
    private static HashMap<Integer, String> maplib = null;

    public static String getPrefijo(int lib) {
        return (String) getMapLib().get(Integer.valueOf(lib));
    }

    public static String getUrl(int lib, int cap) {
        return Util.url + ((String) getMapLib().get(Integer.valueOf(lib))) + (cap > 0 ? "_" + Util.rellenaUnCero(cap) : BuildConfig.FLAVOR);
    }

    public static int getLibID(String libro) {
        for (Entry<Integer, String> entry : getMapLib().entrySet()) {
            if (((String) entry.getValue()).equals(libro)) {
                return ((Integer) entry.getKey()).intValue();
            }
        }
        return -1;
    }

    public static File getFile(Context ctx, int lib, int cap) {
        return new File(Util.getDirAudios(ctx), ((String) getMapLib().get(Integer.valueOf(lib))) + "_" + Util.rellenaUnCero(cap) + ".mp3");
    }

    private static HashMap<Integer, String> getMapLib() {
        if (maplib == null) {
            maplib = new HashMap();
            maplib.put(Integer.valueOf(1), "genesis");
            maplib.put(Integer.valueOf(2), "exodus");
            maplib.put(Integer.valueOf(3), "lev");
            maplib.put(Integer.valueOf(4), "num");
            maplib.put(Integer.valueOf(5), "deu");
            maplib.put(Integer.valueOf(6), "joshua");
            maplib.put(Integer.valueOf(7), "judges");
            maplib.put(Integer.valueOf(8), "ruth");
            maplib.put(Integer.valueOf(9), "1sam");
            maplib.put(Integer.valueOf(10), "2sam");
            maplib.put(Integer.valueOf(11), "1kin");
            maplib.put(Integer.valueOf(12), "2kin");
            maplib.put(Integer.valueOf(13), "1chron");
            maplib.put(Integer.valueOf(14), "2chron");
            maplib.put(Integer.valueOf(15), "ezra");
            maplib.put(Integer.valueOf(16), "nehemiah");
            maplib.put(Integer.valueOf(17), "esther");
            maplib.put(Integer.valueOf(18), "job");
            maplib.put(Integer.valueOf(19), "psalm");
            maplib.put(Integer.valueOf(20), "proverbs");
            maplib.put(Integer.valueOf(21), "ecc");
            maplib.put(Integer.valueOf(22), "song");
            maplib.put(Integer.valueOf(23), "isaiah");
            maplib.put(Integer.valueOf(24), "jere");
            maplib.put(Integer.valueOf(25), "lam");
            maplib.put(Integer.valueOf(26), "ezek");
            maplib.put(Integer.valueOf(27), "dan");
            maplib.put(Integer.valueOf(28), "hosea");
            maplib.put(Integer.valueOf(29), "joel");
            maplib.put(Integer.valueOf(30), "amos");
            maplib.put(Integer.valueOf(31), "oba");
            maplib.put(Integer.valueOf(32), "jonah");
            maplib.put(Integer.valueOf(33), "micah");
            maplib.put(Integer.valueOf(34), "nahum");
            maplib.put(Integer.valueOf(35), "habak");
            maplib.put(Integer.valueOf(36), "zeph");
            maplib.put(Integer.valueOf(37), "haggai");
            maplib.put(Integer.valueOf(38), "zech");
            maplib.put(Integer.valueOf(39), "mal");
            maplib.put(Integer.valueOf(40), "matt");
            maplib.put(Integer.valueOf(41), "mark");
            maplib.put(Integer.valueOf(42), "luke");
            maplib.put(Integer.valueOf(43), "john");
            maplib.put(Integer.valueOf(44), "acts");
            maplib.put(Integer.valueOf(45), "romans");
            maplib.put(Integer.valueOf(46), "1cor");
            maplib.put(Integer.valueOf(47), "2cor");
            maplib.put(Integer.valueOf(48), "gal");
            maplib.put(Integer.valueOf(49), "eph");
            maplib.put(Integer.valueOf(50), "phil");
            maplib.put(Integer.valueOf(51), "col");
            maplib.put(Integer.valueOf(52), "1the");
            maplib.put(Integer.valueOf(53), "2the");
            maplib.put(Integer.valueOf(54), "1tim");
            maplib.put(Integer.valueOf(55), "2tim");
            maplib.put(Integer.valueOf(56), "tit");
            maplib.put(Integer.valueOf(57), "phi");
            maplib.put(Integer.valueOf(58), "heb");
            maplib.put(Integer.valueOf(59), "jam");
            maplib.put(Integer.valueOf(60), "1pet");
            maplib.put(Integer.valueOf(61), "2pet");
            maplib.put(Integer.valueOf(62), "1joh");
            maplib.put(Integer.valueOf(63), "2joh");
            maplib.put(Integer.valueOf(64), "3joh");
            maplib.put(Integer.valueOf(65), "jude");
            maplib.put(Integer.valueOf(66), "rev");
        }
        return maplib;
    }
}
