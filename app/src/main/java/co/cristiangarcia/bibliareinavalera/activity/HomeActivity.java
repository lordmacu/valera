package co.cristiangarcia.bibliareinavalera.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import co.cristiangarcia.bibliareinavalera.R;
import co.cristiangarcia.bibliareinavalera.fragment.FavoritosFragment;
import co.cristiangarcia.bibliareinavalera.fragment.GuiaFragment;
import co.cristiangarcia.bibliareinavalera.fragment.HomeFragment;
import co.cristiangarcia.bibliareinavalera.service.GetDataService;
import co.cristiangarcia.bibliareinavalera.util.Preference;
import co.cristiangarcia.bibliareinavalera.util.Util;
import cz.msebera.android.httpclient.protocol.HTTP;

public class HomeActivity extends BaseActivity {
    private int requestConfig = 12315;
    private TabLayout tab_main;
    private ViewPager viewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.home_main);
        this.viewPager = (ViewPager) findViewById(R.id.viewpager_main);
        this.viewPager.setOffscreenPageLimit(3);
        setupViewPager(this.viewPager);
        this.tab_main = (TabLayout) findViewById(R.id.tabs_main);
        this.tab_main.setupWithViewPager(this.viewPager);
         syncData();
    }



    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    protected void onPause() {
        super.onPause();

    }

    protected void onResume() {
        super.onResume();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_act_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_busqd /*2131624170*/:
                startActivity(new Intent(getApplicationContext(), BusquedaActivity.class));
                return true;
            case R.id.menu_conf /*2131624172*/:
                startActivityForResult(new Intent(getApplicationContext(), ConfigActivity.class), this.requestConfig);
                return true;
            case R.id.menu_share /*2131624173*/:
                Intent shareIntent = new Intent("android.intent.action.SEND");
                shareIntent.setType(HTTP.PLAIN_TEXT_TYPE);
                shareIntent.putExtra("android.intent.extra.TEXT", "Si eres usuario Android, descarga la Santa Biblia Reina Valera con Audio https://goo.gl/h3juDy");
                startActivity(Intent.createChooser(shareIntent, "Compartir a trav\u00e9s de"));
                return true;
            case R.id.menu_fallo /*2131624174*/:
                Intent intent = new Intent("android.intent.action.SENDTO", Uri.fromParts("mailto", "yo@cristiangarcia.co", null));
                intent.putExtra("android.intent.extra.SUBJECT", "Reporte Fallo o Sugerencia Santa Biblia Reina Valera (" + Util.getDeviceInformation(getApplicationContext()) + ")");
                startActivity(Intent.createChooser(intent, "Enviar Email"));
                return true;
           /* case R.id.menu_dona
                View layout = ((LayoutInflater) getSystemService("layout_inflater")).inflate(R.layout.donar_dialog_, null);
                final ProgressBar donar_dig_pb1 = (ProgressBar) layout.findViewById(R.id.donar_dig_pb1);
                WebView donar_dig_wv1 = (WebView) layout.findViewById(R.id.donar_dig_wv1);
                donar_dig_wv1.setWebViewClient(new WebViewClient() {
                    public void onPageFinished(WebView view, String url) {
                        donar_dig_pb1.setVisibility(8);
                    }

                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Intent intent = new Intent("android.intent.action.VIEW");
                        intent.setData(Uri.parse(url));
                        HomeActivity.this.startActivity(intent);
                        return true;
                    }
                });
                donar_dig_wv1.loadData("<html><head><title>Donate</title></head><body><div align='center'><form action='https://www.paypal.com/cgi-bin/webscr' method='post' target='_top'><input type='hidden' name='cmd' value='_s-xclick'><input type='hidden' name='hosted_button_id' value='ST9M6JNLF9EK2'><input type='image' src='https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif' border='0' name='submit' alt='PayPal - The safer, easier way to pay online!'><img alt='' border='0' src='https://www.paypalobjects.com/es_XC/i/scr/pixel.gif' width='1' height='1'></form></div></body></html>", "text/html", HTTP.UTF_8);
                new AlertDialog.Builder(this).setTitle("Donaciones").setView(layout).setCancelable(true).show();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == this.requestConfig && resultCode == -1) {
            recreate();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerMenuAdapter adapter = new ViewPagerMenuAdapter(getSupportFragmentManager());
        adapter.addFragment(HomeFragment.newInstance(), getString(R.string.inicio));
        adapter.addFragment(FavoritosFragment.newInstance(), getString(R.string.favoritos));
        adapter.addFragment(GuiaFragment.newInstance(), getString(R.string.guia));
        viewPager.setAdapter(adapter);
    }

    private void syncData() {
        if (System.currentTimeMillis() >= Preference.getLong(getApplicationContext(), "sync_time", 0) + 86400000) {
            Preference.putLong(getApplicationContext(), "sync_time", System.currentTimeMillis());
            int update = Preference.getInt(getApplicationContext(), "sync_update", 3);
            Preference.putInt(getApplicationContext(), "sync_update", update - 1);
            if (update - 1 <= 0) {
                startService(new Intent(this, GetDataService.class));
            }
        }
    }
}
