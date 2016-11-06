package co.cristiangarcia.bibliareinavalera.bd;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BibliaOpenHelper extends SQLiteOpenHelper {
    private static int DATABASE_VERSION = 1;
    private static final String DB_NAME = "biblia.db";
    private final Context myContext;

    public BibliaOpenHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        try {
            createDataBase();
        } catch (Exception e) {
            Log.v("esepcion",e.getMessage());
        }
    }

    private void createDataBase() throws IOException {
        String dbFile = this.myContext.getDatabasePath(DB_NAME).getPath();

        Log.v("database-->",dbFile);
        if (!checkDataBase(dbFile)) {
            getReadableDatabase();
            copyDataBase(dbFile);
        }
    }

    private boolean checkDataBase(String db_path) {
        SQLiteDatabase checkDB = null;
        if (new File(db_path).exists()) {
            try {
                checkDB = SQLiteDatabase.openDatabase(db_path, null, 1);
            } catch (SQLiteException e) {
            }
            if (checkDB != null) {
                checkDB.close();
            }
        }
        if (checkDB != null) {
            return true;
        }
        return false;
    }

    private void copyDataBase(String db_path) throws IOException {
        AssetManager am = this.myContext.getAssets();

        Log.v("assets",am.toString());
        OutputStream os = new FileOutputStream(db_path);
        byte[] buffer = new byte[1024];
        for (int i = 1; i <= 5; i++) {
            InputStream is = am.open("biblia.db.00" + i);
            Log.v("assets",am.toString()+" "+"biblia.db.00" + i);

            while (true) {
                int length = is.read(buffer);
                if (length <= 0) {
                    break;
                }
                os.write(buffer, 0, length);
            }
            is.close();
        }
        os.flush();
        os.close();
    }

    public void onCreate(SQLiteDatabase db) {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
