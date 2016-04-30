package nz.frontdoor.netfindr.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private static final int DATABASE_VERSION = 3;

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

    private static final String RESULTS_PASSWORD_NAME = "password_id";
    private static final String RESULTS_PASSWORD_TYPE = "INTEGER";

    private static final String RESULTS_TABLE_CREATE =
            "CREATE TABLE " + RESULTS_TABLE_NAME + " (" +
                    RESULTS_ID_NAME + " INTEGER PRIMARY KEY," +
                    RESULTS_WIFI_NAME + " " + RESULTS_WIFI_TYPE + ", " +
                    RESULTS_LAT_NAME + " " + RESULTS_LAT_TYPE + ", " +
                    RESULTS_LONG_NAME + " " + RESULTS_LONG_TYPE + ", " +
                    RESULTS_SECURITY_NAME + " " + RESULTS_SECURITY_TYPE + ", " +
                    RESULTS_PASSWORD_NAME + " " + RESULTS_PASSWORD_TYPE + ", " +
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

    /**
     * Add a successful connection to the database
     * @param result
     */
    public void addNetwork(Network result) {
        DateFormat format = SimpleDateFormat.getDateTimeInstance();

        ContentValues values = new ContentValues();
        values.put(RESULTS_WIFI_NAME, result.getWifiName());
        values.put(RESULTS_LONG_NAME, result.getLongitude());
        values.put(RESULTS_LAT_NAME, result.getLatitude());
        values.put(RESULTS_SECURITY_NAME, result.getSecurityType());
        values.put(RESULTS_TIMESTAMP_NAME,  format.format(result.getTimestamp()));
        values.put(RESULTS_PASSWORD_NAME, result.getPasswordId());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(RESULTS_TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Add known password into the database
     * @param password
     */
    public void addPassword(Password password) {
        ContentValues values = new ContentValues();
        values.put(PASSWORD_PHRASE_NAME, password.getPhrase());
        values.put(PASSWORD_RANK_NAME, password.getRank());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(PASSWORDS_TABLE_NAME, null, values);
        db.close();
    }

    public List<Password> getPasswords() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(PASSWORDS_TABLE_NAME,
                new String [] {PASSWORD_ID_NAME, PASSWORD_PHRASE_NAME, PASSWORD_RANK_NAME},
                null, null, null, null, null, null
        );

        cursor.moveToFirst();
        List<Password> passwords = new ArrayList<>();

        // Check if there is no elements
        if (cursor.isAfterLast()) {
            return passwords;
        }

        do {
            passwords.add(Password.fromCursor(cursor));
        } while (cursor.moveToNext());

        cursor.close();
        db.close();

        return passwords;
    }

    public List<Network> getSuccessfulNetworks() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(RESULTS_TABLE_NAME,
                new String [] {
                        RESULTS_ID_NAME,
                        RESULTS_WIFI_NAME,
                        RESULTS_PASSWORD_NAME,
                        RESULTS_LAT_NAME,
                        RESULTS_LONG_NAME,
                        RESULTS_SECURITY_NAME,
                        RESULTS_TIMESTAMP_NAME
                },
                RESULTS_PASSWORD_NAME + "!=?", new String[] {"0"}, null, null, null, null
        );

        cursor.moveToFirst();


        List<Network> connections = new ArrayList<>();

        // Check if there is no elements
        do {
            connections.add(Network.fromCursor(cursor));
        } while (cursor.moveToNext());

        cursor.close();
        db.close();

        return connections;
    }

    public List<Network> getUnsuccessfulNetworks() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(RESULTS_TABLE_NAME,
                new String [] {
                        RESULTS_ID_NAME,
                        RESULTS_WIFI_NAME,
                        RESULTS_PASSWORD_NAME,
                        RESULTS_LAT_NAME,
                        RESULTS_LONG_NAME,
                        RESULTS_SECURITY_NAME,
                        RESULTS_TIMESTAMP_NAME
                },
                RESULTS_PASSWORD_NAME + "=?", new String[] {"0"}, null, null, null, null
        );

        cursor.moveToFirst();


        List<Network> connections = new ArrayList<>();

        // Check if there is no elements
        do {
            connections.add(Network.fromCursor(cursor));
        } while (cursor.moveToNext());

        cursor.close();
        db.close();

        return connections;
    }

    public int getNetworkCount() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + RESULTS_TABLE_NAME + ";", null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

    public int getPasswordCount() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + PASSWORDS_TABLE_NAME + ";", null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

    public Password getPasswordById(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(PASSWORDS_TABLE_NAME,
                new String [] {
                        PASSWORD_ID_NAME,
                        PASSWORD_PHRASE_NAME,
                        PASSWORD_RANK_NAME
                },
                PASSWORD_ID_NAME + "=?", new String[] { String.valueOf(id) }, null, null, null, null
        );
        cursor.moveToNext();

        Password password = Password.fromCursor(cursor);

        cursor.close();
        db.close();

        return password;
    }

    public boolean isKnownNetwork(String wifiName) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(RESULTS_TABLE_NAME,
                new String [] {
                        RESULTS_ID_NAME
                },
                RESULTS_WIFI_NAME + "=?", new String[] { wifiName }, null, null, null, null
        );
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }
}
