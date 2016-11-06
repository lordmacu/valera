package co.cristiangarcia.bibliareinavalera.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.DialogPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import co.cristiangarcia.bibliareinavalera.R;
import co.cristiangarcia.bibliareinavalera.sync.SyncHelper;

public class ColorPickerDialogPreference extends DialogPreference {
    private OnPreferenceChangeListener changeListener;
    private Editor editor = this.prefs.edit();
    private CharSequence[] entryValues;
    private LayoutInflater mInflater;
    private SharedPreferences prefs;

    private class DialogPreferenceAdapter extends BaseAdapter {

        private class Holder {
            private ImageView check;
            private ImageView image;

            private Holder() {
                this.image = null;
                this.check = null;
            }
        }

        private DialogPreferenceAdapter() {
        }

        public int getCount() {
            return ColorPickerDialogPreference.this.entryValues.length;
        }

        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View item, ViewGroup parent) {
            Holder holder;
            if (item == null || !(item.getTag() instanceof Holder)) {
                item = ColorPickerDialogPreference.this.mInflater.inflate(R.layout.item_color_picker_pref, parent, false);
                holder = new Holder();
                item.setTag(holder);
            } else {
                holder = (Holder) item.getTag();
            }
            holder.image = (ImageView) item.findViewById(R.id.item_cpp_iv1);
            holder.image.setBackgroundResource(ColorPickerDialogPreference.this.getColor(ColorPickerDialogPreference.this.entryValues[position].toString()));
            holder.check = (ImageView) item.findViewById(R.id.item_cpp_iv2);
            if (ColorPickerDialogPreference.this.prefs.getString(ColorPickerDialogPreference.this.getKey(), "1").equals(String.valueOf(position + 1))) {
                holder.check.setVisibility(0);
            } else {
                holder.check.setVisibility(8);
            }
            return item;
        }
    }

    public ColorPickerDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_color_picker);
        setPositiveButtonText(null);
        this.mInflater = LayoutInflater.from(context);
        this.entryValues = context.getResources().getStringArray(R.array.valuescolortema);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    protected void onBindDialogView(View view) {
        GridView grid = (GridView) view.findViewById(R.id.dialog_cp_gv1);
        grid.setAdapter(new DialogPreferenceAdapter());
        grid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ColorPickerDialogPreference.this.editor.putString(ColorPickerDialogPreference.this.getKey(), String.valueOf(position + 1));
                ColorPickerDialogPreference.this.editor.commit();
                ColorPickerDialogPreference.this.getDialog().dismiss();
                ColorPickerDialogPreference.this.getOnPreferenceChangeListener().onPreferenceChange(ColorPickerDialogPreference.this, Integer.valueOf(position + 1));
            }
        });
        super.onBindDialogView(view);
    }

    private int getColor(String color) {
        switch (Integer.valueOf(color).intValue()) {
            case SyncHelper.ST_NEW /*2*/:
                return R.color.Orange_ColorPrimary;
            case SyncHelper.ST_MODIFY /*3*/:
                return R.color.DeepOrange_ColorPrimary;
            case R.styleable.View_theme /*4*/:
                return R.color.Grey_ColorPrimary;
            case R.styleable.Toolbar_contentInsetStart /*5*/:
                return R.color.BlueGrey_ColorPrimary;
            case R.styleable.Toolbar_contentInsetEnd /*6*/:
                return R.color.Green_ColorPrimary;
            case R.styleable.Toolbar_contentInsetLeft /*7*/:
                return R.color.Amber_ColorPrimary;
            case R.styleable.Toolbar_contentInsetRight /*8*/:
                return R.color.Indigo_ColorPrimary;
            case R.styleable.Toolbar_contentInsetStartWithNavigation /*9*/:
                return R.color.Blue_ColorPrimary;
            case R.styleable.Toolbar_contentInsetEndWithActions /*10*/:
                return R.color.Cyan_ColorPrimary;
            case R.styleable.Toolbar_popupTheme /*11*/:
                return R.color.Teal_ColorPrimary;
            case R.styleable.Toolbar_titleTextAppearance /*12*/:
                return R.color.Red_ColorPrimary;
            case R.styleable.Toolbar_subtitleTextAppearance /*13*/:
                return R.color.Pink_ColorPrimary;
            case R.styleable.Toolbar_titleMargin /*14*/:
                return R.color.Purple_ColorPrimary;
            case R.styleable.Toolbar_titleMarginStart /*15*/:
                return R.color.DeepPurple_ColorPrimary;
            default:
                return R.color.Brown_ColorPrimary;
        }
    }

    public static int getStyle(String value) {
        switch (Integer.valueOf(value).intValue()) {
            case SyncHelper.ST_NEW /*2*/:
                return R.style.AppThemeLighOrange;
            case SyncHelper.ST_MODIFY /*3*/:
                return R.style.AppThemeLighDeepOrange;
            case R.styleable.View_theme /*4*/:
                return R.style.AppThemeLighGrey;
            case R.styleable.Toolbar_contentInsetStart /*5*/:
                return R.style.AppThemeLighBlueGrey;
            case R.styleable.Toolbar_contentInsetEnd /*6*/:
                return R.style.AppThemeLighGreen;
            case R.styleable.Toolbar_contentInsetLeft /*7*/:
                return R.style.AppThemeLighAmber;
            case R.styleable.Toolbar_contentInsetRight /*8*/:
                return R.style.AppThemeLighIndigo;
            case R.styleable.Toolbar_contentInsetStartWithNavigation /*9*/:
                return R.style.AppThemeLighBlue;
            case R.styleable.Toolbar_contentInsetEndWithActions /*10*/:
                return R.style.AppThemeLighCyan;
            case R.styleable.Toolbar_popupTheme /*11*/:
                return R.style.AppThemeLighTeal;
            case R.styleable.Toolbar_titleTextAppearance /*12*/:
                return R.style.AppThemeLighRed;
            case R.styleable.Toolbar_subtitleTextAppearance /*13*/:
                return R.style.AppThemeLighPink;
            case R.styleable.Toolbar_titleMargin /*14*/:
                return R.style.AppThemeLighPurple;
            case R.styleable.Toolbar_titleMarginStart /*15*/:
                return R.style.AppThemeLighDeepPurple;
            default:
                return R.style.AppThemeLighBrown;
        }
    }
}
