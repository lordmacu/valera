package co.cristiangarcia.bibliareinavalera.bd;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import co.cristiangarcia.bibliareinavalera.BuildConfig;

public class Migracion {

    private static class NodoFavorito {
        int capitulo;
        String date;
        int id;
        int libro;
        String text;
        int versiculof;
        int versiculoi;

        public NodoFavorito(int id, String text, String date, int libro, int capitulo, int versiculo) {
            this.id = id;
            this.text = text;
            this.date = date;
            this.libro = libro;
            this.capitulo = capitulo;
            this.versiculoi = versiculo;
            this.versiculof = versiculo;
        }

        public void setVersiculof(int versiculof) {
            this.versiculof = versiculof;
        }

        public String getQuery() {
            String text = (this.text == null || this.text.equals(BuildConfig.FLAVOR)) ? "NULL" : "'" + this.text + "'";
            return "INSERT INTO favorito (id, libro, capitulo, versiculoi, versiculof, text, date, state, sync) VALUES ('" + this.id + "', '" + this.libro + "', '" + this.capitulo + "', '" + this.versiculoi + "', '" + this.versiculof + "', " + text + ", '" + this.date + "', '" + 2 + "', 0)";
        }
    }

    public static SQLiteDatabase verificar(Context ctx) {
        SQLiteDatabase database = null;
        File dbFile = ctx.getDatabasePath("database.db");
        if (dbFile.exists()) {
            try {
                database = SQLiteDatabase.openDatabase(dbFile.getPath(), null, 1);
            } catch (SQLiteException e) {
            }
        } else {
            dbFile = ctx.getDatabasePath("database.db.back");
            if (dbFile.exists()) {
                try {
                    database = SQLiteDatabase.openDatabase(dbFile.getPath(), null, 1);
                } catch (SQLiteException e2) {
                }
            }
        }
        return database;
    }

    public static boolean migrar(SQLiteDatabase nueva, SQLiteDatabase antigua) {
        if (frecuentes(nueva, antigua) && favoritos(nueva, antigua)) {
            return true;
        }
        return false;
    }

    public static void borrar(Context ctx) {
        File dbFile1 = ctx.getDatabasePath("database.db");
        if (dbFile1.exists()) {
            dbFile1.delete();
        }
        File dbFile2 = ctx.getDatabasePath("database.db.back");
        if (dbFile2.exists()) {
            dbFile2.delete();
        }
    }

    private static boolean frecuentes(SQLiteDatabase nueva, SQLiteDatabase antigua) {
        try {
            Cursor c = antigua.rawQuery("SELECT _id, count, date FROM frecuentes", null);
            if (c.moveToFirst()) {
                do {
                    int id = c.getInt(0);
                    int count = c.getInt(1);
                    String date = c.getString(2);
                    Cursor c_check = nueva.rawQuery("SELECT id FROM frecuentes WHERE id = '" + id + "'", null);
                    if (!c_check.moveToFirst()) {
                        nueva.execSQL("INSERT INTO frecuentes (id, count, date) VALUES ('" + id + "', '" + count + "', '" + date + "')");
                    }
                    c_check.close();
                } while (c.moveToNext());
            }
            c.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean favoritos(SQLiteDatabase nueva, SQLiteDatabase antigua) {
        try {
            ArrayList<NodoFavorito> list = new ArrayList();
            Cursor c = antigua.rawQuery("SELECT f._id, f.text, f.date, b.libro, b.capitulo, b.versiculo FROM favorito f INNER JOIN fav_ver fv ON f._id = fv.fav INNER JOIN biblia b ON fv.vers = b._id", null);
            if (c.moveToFirst()) {
                int last_id = 0;
                do {
                    int id = c.getInt(0);
                    String text = c.getString(1);
                    String date = c.getString(2);
                    int libro = c.getInt(3);
                    int capitulo = c.getInt(4);
                    int versiculo = c.getInt(5);
                    if (last_id == 0 || last_id != id) {
                        list.add(new NodoFavorito(id, text, date, libro, capitulo, versiculo));
                    } else {
                        ((NodoFavorito) list.get(list.size() - 1)).setVersiculof(versiculo);
                    }
                    last_id = id;
                } while (c.moveToNext());
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    nueva.execSQL(((NodoFavorito) it.next()).getQuery());
                }
            }
            c.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
