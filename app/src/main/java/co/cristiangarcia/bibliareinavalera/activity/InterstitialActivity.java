package co.cristiangarcia.bibliareinavalera.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import java.io.File;
import org.json.JSONArray;
import org.json.JSONObject;
import co.cristiangarcia.bibliareinavalera.BuildConfig;
import co.cristiangarcia.bibliareinavalera.R;
import co.cristiangarcia.bibliareinavalera.util.Preference;

public class InterstitialActivity extends Activity {
    private Button action;
    private ImageView close;
    private ImageView image;
    private TextView tvmessage;
    private TextView tvtitulo;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_activity);
        this.tvtitulo = (TextView) findViewById(R.id.sh_act_tv1);
        this.tvmessage = (TextView) findViewById(R.id.sh_act_tv2);
        this.image = (ImageView) findViewById(R.id.sh_act_iv1);
        this.close = (ImageView) findViewById(R.id.sh_act_iv2);
        this.close.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                InterstitialActivity.this.finish();
            }
        });
        this.action = (Button) findViewById(R.id.sh_act_bt1);
        try {
            JSONObject json = new JSONObject(Preference.getString(getApplicationContext(), "sync_data", BuildConfig.FLAVOR)).getJSONObject("data");
            String title = json.getString("title");
            String message = json.getString("message");
            String urlImage = json.getString("icon");
            this.tvtitulo.setText(title);
            this.tvmessage.setText(message);
            loadimage(urlImage);
            JSONArray button = json.getJSONArray("button");
            for (int i = 0; i < button.length(); i++) {
                json = button.getJSONObject(i);
                String name = json.getString("name");
                String accion = json.getString("accion");
                this.action.setText(name);
                if (accion.equals("none")) {
                    this.action.setOnClickListener(new OnClickListener() {
                        public void onClick(View arg0) {
                            InterstitialActivity.this.finish();
                        }
                    });
                } else if (accion.equals("link")) {
                    final String link = json.getString("link");
                    this.action.setOnClickListener(new OnClickListener() {
                        public void onClick(View arg0) {
                            Intent intent = new Intent("android.intent.action.VIEW");
                            intent.setData(Uri.parse(link));
                            InterstitialActivity.this.startActivity(intent);
                            InterstitialActivity.this.finish();
                        }
                    });
                }
            }
        } catch (Exception e) {
        }
    }

    public void loadimage(String url) {
        new AsyncHttpClient().get(url, new FileAsyncHttpResponseHandler(this) {
            public void onFailure(int arg0, Header[] arg1, Throwable arg2, File arg3) {
            }

            public void onSuccess(int arg0, Header[] arg1, File arg2) {
                InterstitialActivity.this.image.setImageBitmap(BitmapFactory.decodeFile(arg2.getAbsolutePath()));
            }
        });
    }
}
