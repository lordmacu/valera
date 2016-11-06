package co.cristiangarcia.bibliareinavalera.bd;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;

import co.cristiangarcia.bibliareinavalera.BuildConfig;
import co.cristiangarcia.bibliareinavalera.node.Categoria;
import co.cristiangarcia.bibliareinavalera.node.Favorito;
import co.cristiangarcia.bibliareinavalera.node.Libro;
import co.cristiangarcia.bibliareinavalera.node.MiBusqueda;
import co.cristiangarcia.bibliareinavalera.node.SecuenciaVersiculos;
import co.cristiangarcia.bibliareinavalera.node.UltimaLectura.Lectura;
import co.cristiangarcia.bibliareinavalera.node.Versiculo;
import co.cristiangarcia.bibliareinavalera.sync.SyncFavorito;
import co.cristiangarcia.bibliareinavalera.util.LibrosHelper;
import co.cristiangarcia.bibliareinavalera.util.Util;

public class DatabaseHelper {
    private static DatabaseHelper bHelper = null;
    private static BibliaOpenHelper biblia = null;
    private static Context ctx;
    private static DinamicOpenHelper dinamic = null;

    public static synchronized DatabaseHelper getLtHelper(Context ctx) {
        DatabaseHelper databaseHelper;
        synchronized (DatabaseHelper.class) {
            if (bHelper == null) {
                ctx = ctx;
                bHelper = new DatabaseHelper();
            }
            databaseHelper = bHelper;
        }
        return databaseHelper;
    }

   


    public ArrayList<Versiculo> getVersiculos(int libro, int capitulo,Context context) {
        Cursor c;
        int rowCount;
        int i;
        ArrayList<Integer> favs = new ArrayList();
        try {
            c = new DinamicOpenHelper(context).getReadableDatabase().rawQuery("SELECT versiculoi, versiculof FROM favorito WHERE state != '0' AND libro = '" + libro + "' AND capitulo = '" + capitulo + "'", null);
            if (c.moveToFirst()) {
                rowCount = c.getCount();
                for (i = 0; i < rowCount; i++) {
                    int versiculoi = c.getInt(0);
                    int versiculof = c.getInt(1);
                    for (int j = versiculoi; j <= versiculof; j++) {
                        if (!favs.contains(Integer.valueOf(j))) {
                            favs.add(Integer.valueOf(j));
                        }
                    }
                    c.moveToNext();
                }
            }
            c.close();
        } catch (Exception e) {
        }
        ArrayList<Versiculo> list = new ArrayList();
        try {
            c = new BibliaOpenHelper(context).getReadableDatabase().rawQuery("SELECT _id, versiculo, escritura FROM biblia WHERE libro = '" + libro + "' AND capitulo = '" + capitulo + "' ORDER BY versiculo", null);
            if (c.moveToFirst()) {
                rowCount = c.getCount();
                for (i = 0; i < rowCount; i++) {
                    int id = c.getInt(0);
                    int versiculo = c.getInt(1);
                    list.add(new Versiculo(id, versiculo, c.getString(2).trim(), favs.contains(Integer.valueOf(versiculo)) ? 1 : 0));
                    c.moveToNext();
                }
            }
            c.close();
        } catch (Exception e2) {
        }
        return list;
    }

    public ArrayList<MiBusqueda> getVersiculosBusq(String frase,Context context) {
        ArrayList<MiBusqueda> list = new ArrayList();
        Cursor c = new BibliaOpenHelper(context).getReadableDatabase().rawQuery("SELECT _id, capitulo, versiculo, escritura, libro FROM biblia b WHERE REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(escritura,'\u00e1','a'), '\u00e9','e'),'\u00ed','i'),'\u00f3','o'),'\u00fa','u'),'.',''),',',''),' ',''),':',''),';',''),'?',''),'\u00bf',''),'\u00a1',''),'!',''),'(',''),')','') LIKE '%" + frase + "%' ORDER BY libro, capitulo, versiculo", null);
        if (c.moveToFirst()) {
            int rowCount = c.getCount();
            for (int i = 0; i < rowCount; i++) {
                list.add(new MiBusqueda(c.getInt(0), c.getInt(2), c.getString(3).trim(), 0, LibrosHelper.getLibro(c.getInt(4)), c.getInt(1)));
                c.moveToNext();
            }
        }
        c.close();
        return list;
    }

    public String[] getSynctimePasjYcatg(Context context) {
        String[] synctime = new String[2];
        try {
            Cursor c = new DinamicOpenHelper(context).getReadableDatabase().rawQuery("SELECT pasaje, categoria FROM synctime", null);
            if (c.moveToFirst()) {
                synctime[0] = c.getString(0);
                synctime[1] = c.getString(1);
            }
            c.close();
        } catch (Exception e) {
        }
        return synctime;
    }

    public void setSynctimePasjYcatg(String[] synctimes, Context context) {
        new DinamicOpenHelper(context).getWritableDatabase().execSQL("UPDATE synctime SET pasaje = '" + synctimes[0] + "', categoria = '" + synctimes[1] + "'");
    }

    public void syncPasaje(String id, String catg, String lib, String cap, String veri, String verf, int state,Context context) {
        if (state == 0) {
            new DinamicOpenHelper(context).getWritableDatabase().execSQL("DELETE FROM pasaje WHERE id = '" + id + "'");
        } else if (new DinamicOpenHelper(context).getReadableDatabase().rawQuery("SELECT * FROM pasaje WHERE id = '" + id + "'", null).getCount() > 0) {
            new DinamicOpenHelper(context).getWritableDatabase().execSQL("UPDATE pasaje SET catg='" + catg + "', libro='" + lib + "', capitulo='" + cap + "', versiculoi='" + veri + "', versiculof='" + verf + "' WHERE id = '" + id + "'");
        } else {
            new DinamicOpenHelper(context).getWritableDatabase().execSQL("INSERT INTO pasaje (id, catg, libro, capitulo, versiculoi, versiculof) values ('" + id + "', '" + catg + "', '" + lib + "', '" + cap + "', '" + veri + "', '" + verf + "')");
        }
    }

    public SecuenciaVersiculos getPasaje(Context context) {

        SecuenciaVersiculos nodo = null;

        Cursor c = new DinamicOpenHelper(context).getReadableDatabase().rawQuery("SELECT libro, capitulo, versiculoi, versiculof FROM pasaje ORDER BY RANDOM(), RANDOM(), RANDOM()  LIMIT 1", null);
        if (c.moveToFirst()) {
            int libro = c.getInt(0);
            int capitulo = c.getInt(1);
            int versiculoi = c.getInt(2);
            Cursor c2 = new BibliaOpenHelper(context).getReadableDatabase().rawQuery("SELECT versiculo, escritura FROM biblia WHERE libro = '" + libro + "' AND capitulo = '" + capitulo + "' AND versiculo BETWEEN '" + versiculoi + "' AND '" + c.getInt(3) + "'", null);
            if (c2.moveToFirst()) {
                int rowCount = c2.getCount();
                int i = 0;
                SecuenciaVersiculos nodo2 = null;
                while (i < rowCount) {
                    try {
                        int versiculo = c2.getInt(0);
                        String escritura = c2.getString(1).trim();
                        Libro lb = LibrosHelper.getLibro(libro);
                        if (nodo2 == null) {
                            nodo = new SecuenciaVersiculos(lb.getId(), lb.getName(), capitulo, versiculo, escritura);
                        } else {
                            nodo2.putVersiculo(versiculo, escritura);
                            nodo = nodo2;
                        }
                    } catch (Exception e) {
                        return nodo2;
                    }
                    try {
                        c2.moveToNext();
                        i++;
                        nodo2 = nodo;
                    } catch (Exception e2) {
                        return nodo;
                    }
                }
                nodo = nodo2;
            }
            c2.close();
        }
        c.close();
        return nodo;
    }

    public void checkPasajes(Context context) {
        try {
            Cursor c = new DinamicOpenHelper(context).getReadableDatabase().rawQuery("SELECT libro, capitulo, versiculoi, versiculof FROM pasaje", null);
            if (c.moveToFirst()) {
                do {
                    int libro = c.getInt(0);
                    int capitulo = c.getInt(1);
                    int versiculoi = c.getInt(2);
                    int versiculof = c.getInt(3);
                    Cursor c2 = new BibliaOpenHelper(context).getReadableDatabase().rawQuery("SELECT versiculo, escritura FROM biblia WHERE libro = '" + libro + "' AND capitulo = '" + capitulo + "' AND versiculo BETWEEN '" + versiculoi + "' AND '" + versiculof + "'", null);
                    if (!c2.moveToFirst()) {
                        System.out.println("Noooooooooooooooooooo vAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA " + libro + " : " + capitulo + "  " + versiculoi + " - " + versiculof);
                    }
                    c2.close();
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
        }
    }

    public void syncCategoria(String id, String name, int state,Context context) {
        if (state == 0) {
            new DinamicOpenHelper(context).getWritableDatabase().execSQL("DELETE FROM categoria WHERE id = '" + id + "'");
        } else if (new DinamicOpenHelper(context).getReadableDatabase().rawQuery("SELECT * FROM categoria WHERE id = '" + id + "'", null).getCount() > 0) {
            new DinamicOpenHelper(context).getWritableDatabase().execSQL("UPDATE categoria SET name='" + name + "' WHERE id = '" + id + "'");
        } else {
            new DinamicOpenHelper(context).getWritableDatabase().execSQL("INSERT INTO categoria (id, name) values ('" + id + "', '" + name + "')");
        }
    }

    public ArrayList<Categoria> getCategorias(Context context) {
        ArrayList<Categoria> list = new ArrayList();
        try {
            Cursor c = new DinamicOpenHelper(context).getReadableDatabase().rawQuery("SELECT id, name FROM categoria ORDER BY name ASC", null);
            if (c.moveToFirst()) {
                int rowCount = c.getCount();
                for (int i = 0; i < rowCount; i++) {
                    list.add(new Categoria(c.getInt(0), c.getString(1)));
                    c.moveToNext();
                }
            }
            c.close();
        } catch (Exception e) {
        }
        return list;
    }

    public ArrayList<SecuenciaVersiculos> getPasajesCategoria(int cat,Context context) {
        ArrayList<SecuenciaVersiculos> list = new ArrayList();
        try {
            Cursor c = new DinamicOpenHelper(context).getReadableDatabase().rawQuery("SELECT libro, capitulo, versiculoi, versiculof FROM pasaje WHERE catg = '" + cat + "'", null);
            if (c.moveToFirst()) {
                int rowCount = c.getCount();
                for (int i = 0; i < rowCount; i++) {
                    int libro = c.getInt(0);
                    int capitulo = c.getInt(1);
                    int versiculoi = c.getInt(2);
                    int versiculof = c.getInt(3);
                    Cursor c2 = new BibliaOpenHelper(context).getReadableDatabase().rawQuery("SELECT versiculo, escritura FROM biblia b WHERE libro = '" + libro + "' AND capitulo = '" + capitulo + "' AND versiculo BETWEEN '" + versiculoi + "' AND '" + versiculof + "'", null);
                    if (c2.moveToFirst()) {
                        int rowCount2 = c2.getCount();
                        for (int j = 0; j < rowCount2; j++) {
                            int versiculo = c2.getInt(0);
                            String escritura = c2.getString(1).trim();
                            Libro lb = LibrosHelper.getLibro(libro);
                            if (versiculoi == versiculof || versiculoi == versiculo) {
                                list.add(new SecuenciaVersiculos(lb.getId(), lb.getName(), capitulo, versiculo, escritura));
                            } else {
                                ((SecuenciaVersiculos) list.get(list.size() - 1)).putVersiculo(versiculo, escritura);
                            }
                            c2.moveToNext();
                        }
                    }
                    c2.close();
                    c.moveToNext();
                }
            }
            c.close();
        } catch (Exception e) {
        }
        return list;
    }

    public void addFavorito(int libro, int capitulo, int vers1, int vers2, String text, Context context) {
        new DinamicOpenHelper(context).getWritableDatabase().execSQL("INSERT INTO favorito (libro, capitulo, versiculoi, versiculof, text, date, state, sync) values ('" + libro + "', '" + capitulo + "', '" + vers1 + "', '" + vers2 + "', " + (!text.equals(BuildConfig.FLAVOR) ? "'" + text + "'" : "NULL") + ", '" + new Date().getTime() + "', '" + 2 + "', '0')");
    }

    public void delFavorito(int id,Context context) {
        Cursor c = new DinamicOpenHelper(context).getReadableDatabase().rawQuery("SELECT sync FROM favorito WHERE id='" + id + "'", null);
        if (!c.moveToFirst()) {
            return;
        }
        if (c.getInt(0) == 0) {
            new DinamicOpenHelper(context).getWritableDatabase().execSQL("DELETE FROM favorito WHERE id = '" + id + "'");
        } else {
            new DinamicOpenHelper(context).getWritableDatabase().execSQL("UPDATE favorito SET state = '0' WHERE id = '" + id + "'");
        }
    }

    public void modiFavorito(int id, String text , Context context) {
        Cursor c = new DinamicOpenHelper(context).getReadableDatabase().rawQuery("SELECT sync FROM favorito WHERE id ='" + id + "'", null);
        if (c.moveToFirst()) {
            int sync = c.getInt(0);
            text = !text.equals(BuildConfig.FLAVOR) ? "'" + text + "'" : "NULL";
            if (sync == 0) {
                new DinamicOpenHelper(context).getWritableDatabase().execSQL("UPDATE favorito SET text = " + text + " WHERE id = '" + id + "'");
            } else {
                new DinamicOpenHelper(context).getWritableDatabase().execSQL("UPDATE favorito SET state = '3', text = " + text + " WHERE id = '" + id + "'");
            }
        }
    }

    public ArrayList<Object> getFavoritos(Context context) {
        ArrayList<Object> list = new ArrayList();
        try {
            Cursor c = new DinamicOpenHelper(context).getReadableDatabase().rawQuery("SELECT id, libro, capitulo, versiculoi, versiculof, text, date FROM favorito WHERE state != '0' ORDER BY date DESC", null);
            if (c.moveToFirst()) {
                String date_ant = BuildConfig.FLAVOR;
                do {
                    int id = c.getInt(0);
                    int libro = c.getInt(1);
                    int capitulo = c.getInt(2);
                    int versiculoi = c.getInt(3);
                    int versiculof = c.getInt(4);
                    String nota = c.getString(5);
                    String date = Util.formatFecha(c.getLong(6));
                    if (date_ant.equals(BuildConfig.FLAVOR) || !date_ant.equals(date)) {
                        list.add(date);
                    }
                    Cursor c2 = new BibliaOpenHelper(context).getReadableDatabase().rawQuery("SELECT versiculo, escritura FROM biblia WHERE libro = '" + libro + "' AND capitulo = '" + capitulo + "' AND versiculo BETWEEN '" + versiculoi + "' AND '" + versiculof + "'", null);
                    if (c2.moveToFirst()) {
                        do {
                            int versiculo = c2.getInt(0);
                            String escritura = c2.getString(1).trim();
                            Libro lb = LibrosHelper.getLibro(libro);
                            if (versiculoi == versiculof || versiculoi == versiculo) {
                                list.add(new Favorito(id, nota, lb.getId(), lb.getName(), capitulo, versiculo, escritura));
                            } else {
                                ((Favorito) list.get(list.size() - 1)).putVersiculo(versiculo, escritura);
                            }
                        } while (c2.moveToNext());
                    }
                    c2.close();
                    date_ant = date;
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
        }
        return list;
    }

    public String getSynctimeFavorito(Context context) {
        String synctime = "0";
        try {
            Cursor c = new DinamicOpenHelper(context).getReadableDatabase().rawQuery("SELECT favorito FROM synctime", null);
            if (c.moveToFirst()) {
                synctime = c.getString(0);
            }
            c.close();
        } catch (Exception e) {
        }
        return synctime;
    }

    public void setSynctimeFavorito(String synctimes, Context context) {
        new DinamicOpenHelper(context).getWritableDatabase().execSQL("UPDATE synctime SET favorito = '" + synctimes + "'");
    }

    public SyncFavorito getFavoritoSync(Context context) {
        SyncFavorito sync = new SyncFavorito();
        sync.setSyntime(getSynctimeFavorito(context));
        Cursor c = new DinamicOpenHelper(context).getReadableDatabase().rawQuery("SELECT id, libro, capitulo, versiculoi, versiculof, text, date, state FROM favorito", null);
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                int libro = c.getInt(1);
                int capitulo = c.getInt(2);
                int versiculoi = c.getInt(3);
                int versiculof = c.getInt(4);
                String text = c.getString(5);
                long date = c.getLong(6);
                int state = c.getInt(7);
                if (state != 1) {
                    sync.addNodeFavorito(id, libro, capitulo, versiculoi, versiculof, text, date, state);
                }
            } while (c.moveToNext());
        }
        return sync;
    }

    public void setFavoritoSync(int id, int id_loc, int libro, int capitulo, int versiculoi, int versiculof, String text, long date, int state,Context context) {
        if (state == 0) {
            new DinamicOpenHelper(context).getWritableDatabase().execSQL("DELETE FROM favorito WHERE id = '" + id + "'");
            return;
        }
        text = !text.equals(BuildConfig.FLAVOR) ? "'" + text + "'" : "NULL";
        Cursor c = new DinamicOpenHelper(context).getReadableDatabase().rawQuery("SELECT * FROM favorito WHERE id = '" + id_loc + "' AND sync = '0'", null);
        if (c.getCount() > 0) {
            new DinamicOpenHelper(context).getWritableDatabase().execSQL("UPDATE favorito SET id = '" + id + "', libro = '" + libro + "', capitulo = '" + capitulo + "', versiculoi = '" + versiculoi + "', versiculof = '" + versiculof + "', text = " + text + ", date = '" + date + "', state = '" + 1 + "', sync = '1' WHERE id = '" + id_loc + "'");
        } else {
            c = new DinamicOpenHelper(context).getReadableDatabase().rawQuery("SELECT * FROM favorito WHERE id ='" + id + "'", null);
            if (c.getCount() > 0) {
                new DinamicOpenHelper(context).getWritableDatabase().execSQL("UPDATE favorito SET libro = '" + libro + "', capitulo = '" + capitulo + "', versiculoi = '" + versiculoi + "', versiculof = '" + versiculof + "', text = " + text + ", date = '" + date + "', state = '" + 1 + "', sync = '1' WHERE id = '" + id + "'");
            } else {
                new DinamicOpenHelper(context).getWritableDatabase().execSQL("INSERT INTO favorito (id, libro, capitulo, versiculoi, versiculof, text, date, state, sync) values ('" + id + "', '" + libro + "', '" + capitulo + "', '" + versiculoi + "', '" + versiculof + "', " + text + ", '" + date + "', '" + 1 + "', '1')");
            }
        }
        c.close();
    }

    public void addLibrosFrecuentes(int libro, Context context) {
        try {
            Cursor c = new DinamicOpenHelper(context).getReadableDatabase().rawQuery("SELECT count FROM frecuentes WHERE id = '" + libro + "'", null);
            if (c.moveToFirst()) {
                new DinamicOpenHelper(context).getWritableDatabase().execSQL("UPDATE frecuentes SET id = '" + libro + "', count = '" + (c.getInt(0) + 1) + "', date = '" + new Date().getTime() + "' WHERE id = '" + libro + "'");
            } else {
                new DinamicOpenHelper(context).getWritableDatabase().execSQL("INSERT INTO frecuentes (id, count, date) VALUES ('" + libro + "', '1', '" + new Date().getTime() + "')");
            }
            c.close();
        } catch (Exception e) {
        }
    }

    public ArrayList<Libro> getLibrosFrecuentes(Context context) {
        ArrayList<Libro> list = new ArrayList();
        try {
            Cursor c = new DinamicOpenHelper(context).getReadableDatabase().rawQuery("SELECT id FROM frecuentes ORDER BY count DESC, date DESC", null);
            if (c.moveToFirst()) {
                int rowCount = c.getCount();
                for (int i = 0; i < rowCount; i++) {
                    Libro libro = LibrosHelper.getLibro(c.getInt(0));
                    if (libro != null) {
                        list.add(libro);
                    }
                    c.moveToNext();
                }
            }
            c.close();
        } catch (Exception e) {
        }
        return list;
    }

    public int addDelMarcador(int libro, int capitulo,Context context) {
        int accion = 0;
        try {
            Cursor c = new DinamicOpenHelper(context).getReadableDatabase().rawQuery("SELECT id FROM marcador WHERE libro = '" + libro + "' AND capitulo = '" + capitulo + "'", null);
            if (c.moveToFirst()) {
                new DinamicOpenHelper(context).getWritableDatabase().execSQL("DELETE FROM marcador WHERE id = '" + c.getInt(0) + "'");
                accion = 2;
            } else {
                new DinamicOpenHelper(context).getWritableDatabase().execSQL("INSERT INTO marcador (libro, capitulo) VALUES ('" + libro + "', '" + capitulo + "')");
                accion = 1;
            }
            c.close();
        } catch (Exception e) {
        }
        return accion;
    }

    public Lectura getLastMarcado(Context context) {
        Lectura nodo = null;
        try {
            Cursor c = new DinamicOpenHelper(context).getReadableDatabase().rawQuery("SELECT libro, capitulo FROM marcador ORDER BY id DESC LIMIT 1", null);
            if (c.moveToFirst()) {
                nodo = new Lectura(c.getInt(0), c.getInt(1));
            }
            c.close();
        } catch (Exception e) {
        }
        return nodo;
    }
}
