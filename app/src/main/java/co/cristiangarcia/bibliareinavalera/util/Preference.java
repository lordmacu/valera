package co.cristiangarcia.bibliareinavalera.util;

import android.content.Context;
import android.content.SharedPreferences.Editor;

public class Preference {
    public static String getString(Context c, String var, String val) {
        return c.getSharedPreferences("perfil", 0).getString(var, val);
    }

    public static void putString(Context c, String var, String val) {
        Editor editor = c.getSharedPreferences("perfil", 0).edit();
        editor.putString(var, val);
        editor.commit();
    }

    public static int getInt(Context c, String var, int val) {
        return c.getSharedPreferences("perfil", 0).getInt(var, val);
    }

    public static void putInt(Context c, String var, int val) {
        Editor editor = c.getSharedPreferences("perfil", 0).edit();
        editor.putInt(var, val);
        editor.commit();
    }

    public static long getLong(Context c, String var, long val) {
        return c.getSharedPreferences("perfil", 0).getLong(var, val);
    }

    public static void putLong(Context c, String var, long val) {
        Editor editor = c.getSharedPreferences("perfil", 0).edit();
        editor.putLong(var, val);
        editor.commit();
    }

    public static boolean getBoolean(Context c, String var, boolean val) {
        return c.getSharedPreferences("perfil", 0).getBoolean(var, val);
    }

    public static void putBoolean(Context c, String var, boolean val) {
        Editor editor = c.getSharedPreferences("perfil", 0).edit();
        editor.putBoolean(var, val);
        editor.commit();
    }
}
