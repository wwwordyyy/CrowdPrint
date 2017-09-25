package lizares.gabriel.retrofittest;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * Created by Parcival on 9/22/2017.
 */

public class CrowdPrintAuthenticator extends AbstractAccountAuthenticator {
    private Context mContext;
    public CrowdPrintAuthenticator(Context context) {
        super(context);
        this.mContext=context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                             String authTokenType, String[] strings, Bundle options) throws NetworkErrorException {
        final Intent intent=new Intent(mContext,LoginActivity.class);
        intent.putExtra("com.gab.CrowdPrint", accountType);
        intent.putExtra("CROWDPRINT_USER",authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle bundle) throws NetworkErrorException {
        AccountManager accountManager= AccountManager.get(mContext);
        String authToken=accountManager.peekAuthToken(account,authTokenType);
        if(!TextUtils.isEmpty(authToken)){
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }
        final Intent intent=new Intent(mContext,LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,response);
        intent.putExtra("com.gab.CrowdPrint", account.type);
        intent.putExtra("CROWDPRINT_USER",authTokenType);
        Bundle retBundle= new Bundle();
        retBundle.putParcelable(AccountManager.KEY_INTENT,intent);
        return retBundle;

    }

    @Override
    public String getAuthTokenLabel(String s) {
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        return null;
    }
}
