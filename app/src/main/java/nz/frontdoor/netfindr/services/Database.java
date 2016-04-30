package nz.frontdoor.netfindr.services;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by drb on 30/04/16.
 */
public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "netfindr";
    private static final int DATABASE_VERSION = 1;

    private static final String RESULTS_TABLE_NAME = "results";

    private static final String RESULTS_ID_NAME = "id";

    private static final String RESULTS_WIFI_NAME = "wifi_name";
    private static final String RESULTS_WIFI_TYPE = "TEXT";

    private static final String RESULTS_LAT_NAME = "lat";
    private static final String RESULTS_LAT_TYPE = "REAL";

    private static final String RESULTS_LONG_NAME = "long";
    private static final String RESULTS_LONG_TYPE = "REAL";

    private static final String RESULTS_SECURITY_NAME = "security";
    private static final String RESULTS_SECURITY_TYPE = "TEXT";

    private static final String RESULTS_TIMESTAMP_NAME = "timestamp";
    private static final String RESULTS_TIMESTAMP_TYPE = "INTEGER";

    private static final String RESULTS_TABLE_CREATE =
            "CREATE TABLE " + RESULTS_TABLE_NAME + " (" +
                    RESULTS_ID_NAME + " INTEGER PRIMARY KEY," +
                    RESULTS_WIFI_NAME + " " + RESULTS_WIFI_TYPE + ", " +
                    RESULTS_LAT_NAME + " " + RESULTS_LAT_TYPE + ", " +
                    RESULTS_LONG_NAME + " " + RESULTS_LONG_TYPE + ", " +
                    RESULTS_SECURITY_NAME + " " + RESULTS_SECURITY_TYPE + ", " +
                    RESULTS_TIMESTAMP_NAME + " " + RESULTS_TIMESTAMP_TYPE + " " +
                    ");";

    private static String PASSWORDS_TABLE_NAME = "passwords";

    private static String PASSWORD_ID_NAME = "id";

    private static String PASSWORD_RANK_NAME = "rank";
    private static String PASSWORD_RANK_TYPE = "INTEGER";

    private static String PASSWORD_PHRASE_NAME = "phrase";
    private static String PASSWORD_PHRASE_TYPE = "TEXT";

    private static final String PASSWORDS_TABLE_CREATE =
            "CREATE TABLE " + PASSWORDS_TABLE_NAME + " (" +
                    PASSWORD_ID_NAME + " INTEGER PRIMARY KEY," +
                    PASSWORD_RANK_NAME + " " + PASSWORD_RANK_TYPE + ", " +
                    PASSWORD_PHRASE_NAME + " " + PASSWORD_PHRASE_TYPE + " " +
                    ");";

    private static final String PASSWORD_QUERY_ALL =
            "SELECT * FROM " + PASSWORDS_TABLE_NAME + ";";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RESULTS_TABLE_CREATE);
        db.execSQL(PASSWORDS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RESULTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PASSWORDS_TABLE_NAME);
        onCreate(db);
    }

    public void addPassword(String password) {
        ContentValues values = new ContentValues();
        values.put(PASSWORD_PHRASE_NAME, password);
        values.put(PASSWORD_RANK_NAME, 1);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(PASSWORDS_TABLE_NAME, null, values);
    }

    public List<Password> getPasswords() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cusor = db.query(PASSWORDS_TABLE_NAME,
                new String [] {PASSWORD_ID_NAME, PASSWORD_PHRASE_NAME, PASSWORD_RANK_NAME},
                null, null, null, null, null, null
        );

        cusor.moveToFirst();
        List<Password> passwords = new ArrayList<>();
        do {
            passwords.add(Password.fromCursor(cusor));
        } while (cusor.moveToNext());

        cusor.close();

        return passwords;
    }

}
