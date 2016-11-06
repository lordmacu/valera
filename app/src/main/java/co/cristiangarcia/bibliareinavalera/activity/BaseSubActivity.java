package co.cristiangarcia.bibliareinavalera.activity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import co.cristiangarcia.bibliareinavalera.R;
import co.cristiangarcia.bibliareinavalera.util.ColorPickerDialogPreference;

public class BaseSubActivity extends AppCompatActivity {
    private Toolbar toolbar;

    public void onCreate(Bundle savedInstanceState, int contentView) {
        super.onCreate(savedInstanceState);
        setTheme(ColorPickerDialogPreference.getStyle(PreferenceManager.getDefaultSharedPreferences(this).getString("theme_color_preference", "1")));
        setContentView(contentView);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        this.toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BaseSubActivity.this.onBackPressed();
            }
        });
    }
}
