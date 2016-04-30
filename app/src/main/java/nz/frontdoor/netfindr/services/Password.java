package nz.frontdoor.netfindr.services;

import android.database.Cursor;

/**
 * Created by drb on 30/04/16.
 */
public class Password {

    private final int id;

    private final String phrase;

    private final int rank;

    public Password(String phrase, int rank) {
        this.id = 0;
        this.phrase = phrase;
        this.rank = rank;
    }

    private Password(int id, String phrase, int rank) {
        this.id = id;
        this.phrase = phrase;
        this.rank = rank;
    }

    public static Password fromCursor(Cursor cursor) {
        return new Password(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getInt(2)
        );
    }

    public int getRank() {
        return rank;
    }

    public String getPhrase() {
        return phrase;
    }

    public int getId() {
        return id;
    }
}
