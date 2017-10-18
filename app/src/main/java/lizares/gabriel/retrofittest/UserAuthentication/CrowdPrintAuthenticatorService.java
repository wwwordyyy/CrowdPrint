package lizares.gabriel.retrofittest.UserAuthentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CrowdPrintAuthenticatorService extends Service {
    private CrowdPrintAuthenticator mAuthenticator;
    @Override
    public void onCreate() {

        mAuthenticator = new CrowdPrintAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
