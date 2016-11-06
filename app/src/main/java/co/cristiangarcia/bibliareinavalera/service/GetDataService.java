package co.cristiangarcia.bibliareinavalera.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import cz.msebera.android.httpclient.Header;
import java.util.Iterator;
import org.json.JSONObject;
import co.cristiangarcia.bibliareinavalera.BuildConfig;
import co.cristiangarcia.bibliareinavalera.activity.InterstitialActivity;
import co.cristiangarcia.bibliareinavalera.util.Preference;
import co.cristiangarcia.bibliareinavalera.util.SyncRestClient;
import co.cristiangarcia.bibliareinavalera.util.SyncRestClient.MiJsonHttpResponseHandler;

public class GetDataService extends Service {
    Handler h = new Handler();

    public int onStartCommand(Intent intent, int flags, int startId) {
        syncData();
        return 2;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void syncData() {
        String conf = getConfig();
        if (!conf.equals(BuildConfig.FLAVOR)) {
            SyncRestClient.get("sync" + conf, new MiJsonHttpResponseHandler(null) {
                public void onSuccess(int statusCode, Header[] headers, JSONObject robj) {
                    if (robj != null) {
                        GetDataService.this.procesarData(robj);
                    }
                }
            });
        }
        stopSelf();
    }

    private void showInterstitial() {
        Intent i = new Intent(getApplicationContext(), InterstitialActivity.class);
        i.addFlags(268435456);
        startActivity(i);
    }

    private String getConfig() {
        String sync_id = Preference.getString(getApplicationContext(), "sync_id", BuildConfig.FLAVOR);
        if (!sync_id.equals(BuildConfig.FLAVOR)) {
            return "?id=" + sync_id;
        }
        Account[] accounts = AccountManager.get(getApplicationContext()).getAccountsByType("com.google");
        if (accounts.length <= 0) {
            return BuildConfig.FLAVOR;
        }
        return "?mail=" + accounts[0].name;
    }

    private void procesarData(JSONObject robj) {
        try {
            Iterator<?> rkeys = robj.keys();
            while (rkeys.hasNext()) {
                String rkey = (String) rkeys.next();
                if (robj.get(rkey) instanceof JSONObject) {
                    if (rkey.equals("conf")) {
                        JSONObject oconf = (JSONObject) robj.get(rkey);
                        Iterator<?> iconf = oconf.keys();
                        while (iconf.hasNext()) {
                            String key2 = (String) iconf.next();
                            if (key2.equals("id")) {
                                Preference.putString(getApplicationContext(), "sync_id", oconf.getString("id"));
                            } else if (key2.equals("update")) {
                                Preference.putInt(getApplicationContext(), "sync_update", oconf.getInt("update"));
                            }
                        }
                    } else if (rkey.equals("data")) {
                        JSONObject odata = (JSONObject) robj.get(rkey);
                        Iterator<?> idata = odata.keys();
                        while (idata.hasNext()) {
                            if (((String) idata.next()).equals("data")) {
                                String data = odata.getString("data");
                                Preference.putString(getApplicationContext(), "sync_data", data);
                                if (new JSONObject(data).getString("tipo").equals("interstitial")) {
                                    showInterstitial();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}
