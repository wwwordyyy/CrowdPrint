package lizares.gabriel.retrofittest;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Parcival on 9/24/2017.
 */

public class AccountPreference {
    private String username;

    private SharedPreferences sharedPreferences;

    public AccountPreference(Context context) {
        sharedPreferences = context.getSharedPreferences("com.gab.CrowdPrint", Context.MODE_PRIVATE);
    }

    public String getUsername() {
        return sharedPreferences.getString("Username",null);
    }

    public void setUsername(String username) {
        this.username = username;
        sharedPreferences.edit().putString("Username", username).commit();
    }


}
