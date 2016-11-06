package co.cristiangarcia.bibliareinavalera.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import co.cristiangarcia.bibliareinavalera.R;

public class ConfigFragment extends PreferenceFragment {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.config);
    }
}
