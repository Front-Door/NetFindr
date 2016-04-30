package nz.frontdoor.netfindr.services;

import android.database.Cursor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by drb on 30/04/16.
 */
public class SuccessfulConnection {

    private final int id;

    private final String wifiName;

    private final long latitude;

    private final long longitude;

    private final String securityType;

    private final Date timestamp;

    private final int passwordId;

    private Password password;

    public SuccessfulConnection(String wifiName, int passwordId, long latitude, long longitude, String securityType, Date timestamp) {
        this.id = 0;
        this.passwordId = passwordId;
        this.wifiName = wifiName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.securityType = securityType;
        this.timestamp = timestamp;
    }

    private SuccessfulConnection(int id, String wifiName, int passwordId, long latitude, long longitude, String securityType, Date timestamp) {
        this.id = id;
        this.passwordId = passwordId;
        this.wifiName = wifiName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.securityType = securityType;
        this.timestamp = timestamp;
    }

    public static SuccessfulConnection fromCursor(Cursor cursor) {

        DateFormat format = SimpleDateFormat.getDateTimeInstance();
        Date date;
        try {
            date = format.parse(cursor.getString(6));
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }

        return new SuccessfulConnection(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getInt(2),
                cursor.getLong(3),
                cursor.getLong(4),
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

    public long getLatitude() {
        return latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public String getSecurityType() {
        return securityType;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Password getPassword(Database db) {
        if (password != null) {
            return password;
        }

        password = db.getPasswordById(passwordId);
        return password;
    }
}
