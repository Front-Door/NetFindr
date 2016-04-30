package nz.frontdoor.netfindr;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class WifiService extends IntentService {
    private static final String TAG = "WIFISERVICE";
    public static Context context;

    public WifiService() {
        super("WifiService");

        Log.v(TAG, "Service Created");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Log.v(TAG, "Action Recived: " + action);
        }
    }
}
