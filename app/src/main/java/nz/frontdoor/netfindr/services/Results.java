package nz.frontdoor.netfindr.services;

import android.database.Cursor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by drb on 30/04/16.
 */
public class Results {

    private final int id;

    private final String wifiName;

    private final long latitude;

    private final long longitude;

    private final String securityType;

    private final Date timestamp;

    public Results(int id, String wifiName, long latitude, long longatuide, String securityType, Date timestamp) {
        this.id = id;
        this.wifiName = wifiName;
        this.latitude = latitude;
        this.longitude = longatuide;
        this.securityType = securityType;
        this.timestamp = timestamp;
    }

    public static Results fromCusor(Cursor cusor) {

        DateFormat format = new SimpleDateFormat();
        Date date;
        try {
            date = format.parse(cusor.getString(5));
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }

        return new Results(
                cusor.getInt(0),
                cusor.getString(1),
                cusor.getLong(2),
                cusor.getLong(3),
                cusor.getString(4),
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
}
