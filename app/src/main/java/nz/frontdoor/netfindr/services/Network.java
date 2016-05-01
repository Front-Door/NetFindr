package nz.frontdoor.netfindr.services;

import android.database.Cursor;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by drb on 30/04/16.
 */
public class Network {

    private final int id;

    private final String wifiName;

    private final double latitude;

    private final double longitude;

    private final String securityType;

    private final Date timestamp;

    private final int passwordId;

    private Password password;

    private Network(int id, String wifiName, int passwordId, double latitude, double longitude, String securityType, Date timestamp) {
        this.id = id;
        this.passwordId = passwordId;
        this.wifiName = wifiName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.securityType = securityType;
        this.timestamp = timestamp;
    }

    /**
     * Build a Network from a successful connection
     *
     * @param wifiName
     * @param successful
     * @param latitude
     * @param longitude
     * @param securityType
     * @param timestamp
     * @return
     */
    public static Network SuccessfulConnection(String wifiName, Password successful, double latitude, double longitude, String securityType, Date timestamp) {
        return new Network(
                0,
                wifiName,
                successful.getId(),
                latitude,
                longitude,
                securityType,
                timestamp
        );
    }

    /**
     * Build a network from an unsuccessful connection
     *
     * @param wifiName
     * @param latitude
     * @param longitude
     * @param securityType
     * @param timestamp
     * @return
     */
    public static Network UnsuccessfulConnection(String wifiName, double latitude, double longitude, String securityType, Date timestamp) {
        return new Network(
                0,
                wifiName,
                0,
                latitude,
                longitude,
                securityType,
                timestamp
        );
    }

    public static Network fromCursor(Cursor cursor) {

        DateFormat format = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.DEFAULT, SimpleDateFormat.DEFAULT);
        Date date;
        try {
            date = format.parse(cursor.getString(6));
        } catch (ParseException ex) {
            Log.e("db", "Error failed to parse string for some reason, setting it to now");
            date = new Date();
            //throw new RuntimeException(ex);
        }

        return new Network(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getInt(2),
                cursor.getDouble(3),
                cursor.getDouble(4),
                cursor.getString(5),
                date
        );
    }

    public int getId() {
        return id;
    }

    public String getWifiName() {
        return wifiName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getSecurityType() {
        return securityType;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getPasswordId() {
        return passwordId;
    }

    public Password getPassword(Database db) {
        if (password != null) {
            return password;
        }

        if (passwordId == 0) {
            return null;
        }
        password = db.getPasswordById(passwordId);

        return password;
    }
}
