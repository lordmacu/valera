package co.cristiangarcia.bibliareinavalera.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import co.cristiangarcia.bibliareinavalera.util.Preference;


public class DinamicOpenHelper extends SQLiteOpenHelper {
    private static int DATABASE_VERSION = 5;
    private static String DB_NAME = "dinamicssd.db";
    private Context context;

    public DinamicOpenHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pasaje (id INTEGER PRIMARY KEY, catg INTEGER NOT NULL, libro INTEGER NOT NULL, capitulo INTEGER NOT NULL, versiculoi INTEGER NOT NULL, versiculof INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE categoria (id INTEGER PRIMARY KEY, name VARCHAR NOT NULL)");
        db.execSQL("CREATE TABLE favorito (id INTEGER PRIMARY KEY, libro INTEGER NOT NULL, capitulo INTEGER NOT NULL, versiculoi INTEGER NOT NULL, versiculof INTEGER NOT NULL, text VARCHAR NULL, date VARCHAR, state INTEGER NOT NULL, sync INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE synctime (pasaje VARCHAR, categoria VARCHAR, favorito VARCHAR)");
        db.execSQL("CREATE TABLE frecuentes (id INTEGER PRIMARY KEY, count INTEGER NOT NULL, date VARCHAR NOT NULL)");
        db.execSQL("CREATE TABLE marcador (id INTEGER PRIMARY KEY, libro INTEGER NOT NULL, capitulo INTEGER NOT NULL)");
        addDataDinamicInicial(db);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            addDataDinamicInicial(db);
        }
        if (oldVersion < 4) {
            db.execSQL("CREATE TABLE marcador (id INTEGER PRIMARY KEY, libro INTEGER NOT NULL, capitulo INTEGER NOT NULL)");
        }
        if (oldVersion < 5) {
            db.execSQL("DELETE FROM pasaje WHERE libro = '49' AND capitulo = '6' AND versiculoi = '37' AND versiculof = '37'");
        }
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }



    public synchronized void addDataDinamicInicial(SQLiteDatabase db) {
        db.execSQL("DELETE FROM pasaje;");
        db.execSQL("DELETE FROM categoria;");
        db.execSQL("DELETE FROM synctime;");
        db.execSQL("INSERT INTO categoria VALUES(1,'Salvaci\u00f3n')");
        db.execSQL("INSERT INTO categoria VALUES(2,'Oraci\u00f3n')");
        db.execSQL("INSERT INTO categoria VALUES(3,'Perd\u00f3n')");
        db.execSQL("INSERT INTO categoria VALUES(4,'Felicidad')");
        db.execSQL("INSERT INTO categoria VALUES(5,'Amor')");
        db.execSQL("INSERT INTO pasaje VALUES(7,1,43,3,16,16)");
        db.execSQL("INSERT INTO pasaje VALUES(8,1,45,3,23,23)");
        db.execSQL("INSERT INTO pasaje VALUES(9,1,45,6,23,23)");
        db.execSQL("INSERT INTO pasaje VALUES(10,1,43,14,6,6)");
        db.execSQL("INSERT INTO pasaje VALUES(31,1,43,1,12,12)");
        db.execSQL("INSERT INTO pasaje VALUES(32,1,43,3,3,3)");
        db.execSQL("INSERT INTO pasaje VALUES(33,1,58,9,22,22)");
        db.execSQL("INSERT INTO pasaje VALUES(34,1,40,18,3,3)");
        db.execSQL("INSERT INTO pasaje VALUES(35,1,66,3,20,20)");
        db.execSQL("INSERT INTO pasaje VALUES(36,1,62,1,9,9)");
        db.execSQL("INSERT INTO pasaje VALUES(37,1,45,10,9,10)");
        db.execSQL("INSERT INTO pasaje VALUES(38,1,49,2,8,9)");
        db.execSQL("INSERT INTO pasaje VALUES(39,1,56,3,5,5)");
        db.execSQL("INSERT INTO pasaje VALUES(40,1,44,16,31,31)");
        db.execSQL("INSERT INTO pasaje VALUES(41,1,43,3,36,36)");
        db.execSQL("INSERT INTO pasaje VALUES(42,1,43,10,28,28)");
        db.execSQL("INSERT INTO pasaje VALUES(43,1,47,5,17,17)");
        db.execSQL("INSERT INTO pasaje VALUES(54,2,40,7,7,7)");
        db.execSQL("INSERT INTO pasaje VALUES(55,2,43,15,7,7)");
        db.execSQL("INSERT INTO pasaje VALUES(56,2,24,33,3,3)");
        db.execSQL("INSERT INTO pasaje VALUES(57,2,24,29,13,13)");
        db.execSQL("INSERT INTO pasaje VALUES(58,2,23,65,24,24)");
        db.execSQL("INSERT INTO pasaje VALUES(59,2,40,18,19,20)");
        db.execSQL("INSERT INTO pasaje VALUES(60,2,62,5,14,15)");
        db.execSQL("INSERT INTO pasaje VALUES(61,2,52,5,17,17)");
        db.execSQL("INSERT INTO pasaje VALUES(62,2,19,66,18,19)");
        db.execSQL("INSERT INTO pasaje VALUES(63,2,42,22,31,32)");
        db.execSQL("INSERT INTO pasaje VALUES(64,2,58,7,25,25)");
        db.execSQL("INSERT INTO pasaje VALUES(65,2,54,2,8,8)");
        db.execSQL("INSERT INTO pasaje VALUES(66,2,45,8,26,27)");
        db.execSQL("INSERT INTO pasaje VALUES(67,2,41,11,24,24)");
        db.execSQL("INSERT INTO pasaje VALUES(68,2,62,3,22,22)");
        db.execSQL("INSERT INTO pasaje VALUES(69,2,40,26,39,39)");
        db.execSQL("INSERT INTO pasaje VALUES(70,2,40,6,6,7)");
        db.execSQL("INSERT INTO pasaje VALUES(71,2,58,4,16,16)");
        db.execSQL("INSERT INTO pasaje VALUES(72,2,49,6,18,18)");
        db.execSQL("INSERT INTO pasaje VALUES(73,2,19,55,17,17)");
        db.execSQL("INSERT INTO pasaje VALUES(74,2,19,102,17,17)");
        db.execSQL("INSERT INTO pasaje VALUES(75,2,19,116,1,2)");
        db.execSQL("INSERT INTO pasaje VALUES(86,3,40,6,15,15)");
        db.execSQL("INSERT INTO pasaje VALUES(87,3,42,23,34,34)");
        db.execSQL("INSERT INTO pasaje VALUES(88,3,43,8,11,11)");
        db.execSQL("INSERT INTO pasaje VALUES(89,3,23,43,25,25)");
        db.execSQL("INSERT INTO pasaje VALUES(90,3,40,5,7,7)");
        db.execSQL("INSERT INTO pasaje VALUES(91,3,58,8,12,12)");
        db.execSQL("INSERT INTO pasaje VALUES(92,3,40,18,21,22)");
        db.execSQL("INSERT INTO pasaje VALUES(93,3,20,3,3,4)");
        db.execSQL("INSERT INTO pasaje VALUES(94,3,19,18,25,25)");
        db.execSQL("INSERT INTO pasaje VALUES(95,3,19,86,5,5)");
        db.execSQL("INSERT INTO pasaje VALUES(96,3,40,18,35,35)");
        db.execSQL("INSERT INTO pasaje VALUES(97,3,41,11,25,25)");
        db.execSQL("INSERT INTO pasaje VALUES(98,3,42,6,37,37)");
        db.execSQL("INSERT INTO pasaje VALUES(100,3,49,4,32,32)");
        db.execSQL("INSERT INTO pasaje VALUES(101,3,51,3,13,13)");
        db.execSQL("INSERT INTO pasaje VALUES(102,3,58,12,15,15)");
        db.execSQL("INSERT INTO pasaje VALUES(103,4,19,5,11,11)");
        db.execSQL("INSERT INTO pasaje VALUES(104,4,19,16,11,11)");
        db.execSQL("INSERT INTO pasaje VALUES(105,4,19,30,5,5)");
        db.execSQL("INSERT INTO pasaje VALUES(106,4,19,126,5,5)");
        db.execSQL("INSERT INTO pasaje VALUES(107,4,19,128,1,2)");
        db.execSQL("INSERT INTO pasaje VALUES(108,4,23,35,10,10)");
        db.execSQL("INSERT INTO pasaje VALUES(109,4,43,15,11,11)");
        db.execSQL("INSERT INTO pasaje VALUES(110,4,45,14,17,17)");
        db.execSQL("INSERT INTO pasaje VALUES(111,5,46,13,13,13)");
        db.execSQL("INSERT INTO pasaje VALUES(114,5,40,22,37,40)");
        db.execSQL("INSERT INTO pasaje VALUES(115,5,60,4,8,8)");
        db.execSQL("INSERT INTO pasaje VALUES(116,5,62,3,16,18)");
        db.execSQL("INSERT INTO pasaje VALUES(117,5,62,4,7,8)");
        db.execSQL("INSERT INTO pasaje VALUES(118,5,48,5,14,14)");
        db.execSQL("INSERT INTO pasaje VALUES(119,5,43,14,15,15)");
        db.execSQL("INSERT INTO pasaje VALUES(120,5,40,25,40,40)");
        db.execSQL("INSERT INTO pasaje VALUES(121,5,40,7,12,12)");
        db.execSQL("INSERT INTO pasaje VALUES(122,5,51,3,13,14)");
        db.execSQL("INSERT INTO pasaje VALUES(123,5,46,16,14,14)");
        db.execSQL("INSERT INTO pasaje VALUES(124,5,43,15,13,13)");
        db.execSQL("INSERT INTO pasaje VALUES(125,5,62,4,19,19)");
        db.execSQL("INSERT INTO pasaje VALUES(126,5,45,8,38,39)");
        db.execSQL("INSERT INTO pasaje VALUES(127,5,43,13,34,34)");
        db.execSQL("INSERT INTO pasaje VALUES(128,5,43,14,21,24)");
        db.execSQL("INSERT INTO pasaje VALUES(129,5,45,12,9,9)");
        db.execSQL("INSERT INTO pasaje VALUES(130,5,62,4,11,11)");
        db.execSQL("INSERT INTO pasaje VALUES(131,5,62,4,21,21)");
        db.execSQL("INSERT INTO synctime VALUES('2015-08-06 10:39:20','2015-08-04 09:58:49','0')");
        checkMigracion(db);
    }

    public synchronized void checkMigracion(SQLiteDatabase db) {
        if (!Preference.getBoolean(this.context, "verificacion_antigua_bd", false)) {
            SQLiteDatabase antigua = Migracion.verificar(this.context);
            if (antigua != null) {
                boolean is_migrado = Migracion.migrar(db, antigua);
                antigua.close();
                if (is_migrado) {
                    Migracion.borrar(this.context);
                    Preference.getBoolean(this.context, "verificacion_antigua_bd", true);
                }
            } else {
                Preference.getBoolean(this.context, "verificacion_antigua_bd", true);
            }
        }
    }
}