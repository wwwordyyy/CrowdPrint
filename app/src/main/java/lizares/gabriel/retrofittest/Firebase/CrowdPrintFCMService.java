package lizares.gabriel.retrofittest.Firebase;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

public class CrowdPrintFCMService extends FirebaseMessagingService {

    public static String FCM_NOTIFICATION= "FCM_NOTIFICATION";
    public static String FCM_PRINTSTATUSUPDATE="PrintStatusUpdate";
    public CrowdPrintFCMService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("cpdebug", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("cpdebug", "Message data payload: " + remoteMessage.getData());
            Map messagePayload = remoteMessage.getData();

            if(messagePayload.get("action").equals(FCM_PRINTSTATUSUPDATE)){
                LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(getBaseContext());
                Intent intent = new Intent(FCM_NOTIFICATION);
                intent.putExtra("action", "PrintStatusUpdate");
                intent.putExtra("printJobName", messagePayload.get("printJobName").toString());
                intent.putExtra("status",messagePayload.get("status").toString());
                broadcaster.sendBroadcast(intent);

                Log.d("cpdebug",messagePayload.get("action").toString());
                Log.d("cpdebug",messagePayload.get("printJobName").toString());
                Log.d("cpdebug",messagePayload.get("jobKey").toString());
                Log.d("cpdebug",messagePayload.get("status").toString());
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("cpdebug", "Message Notification Body: " + remoteMessage.getNotification().getBody());

        }
        /*
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(getBaseContext());
        Intent intent = new Intent();
        intent.putExtra("Key", value);
        intent.putExtra("key", value);
        broadcaster.sendBroadcast(intent);
        */

    }


}
