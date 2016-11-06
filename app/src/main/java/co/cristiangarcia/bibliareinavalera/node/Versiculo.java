package co.cristiangarcia.bibliareinavalera.node;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import co.cristiangarcia.bibliareinavalera.R;

public class Versiculo implements Parcelable {
    public static final Creator<Versiculo> CREATOR = new Creator<Versiculo>() {
        public Versiculo createFromParcel(Parcel in) {
            return new Versiculo(in);
        }

        public Versiculo[] newArray(int size) {
            return new Versiculo[size];
        }
    };
    public static final int FAVORITO = 1;
    public static final int NO_FAVORITO = 0;
    protected String escritura;
    protected int id;
    protected int is_fav;
    protected int versiculo;

    public Versiculo(int id, int versiculo, String escritura, int is_fav) {
        this.id = id;
        this.versiculo = versiculo;
        this.escritura = escritura;
        this.is_fav = is_fav;
    }

    public Versiculo(Parcel in) {
        this.id = in.readInt();
        this.versiculo = in.readInt();
        this.escritura = in.readString();
        this.is_fav = in.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.versiculo);
        dest.writeString(this.escritura);
        dest.writeInt(this.is_fav);
    }

    public int getId() {
        return this.id;
    }

    public int getVersiculo() {
        return this.versiculo;
    }

    public String getEscritura() {
        return this.escritura;
    }

    public SpannableString getEscrituraSpanneada(Context ctx, boolean underline, boolean higtlight) {
        SpannableString sb = getEscrituraSpanneada();
        if (underline) {
            sb.setSpan(new UnderlineSpan(), String.valueOf(getVersiculo()).length() + 2, sb.length(), 33);
        }
        if (higtlight) {
            int color = 0;
            TypedValue typedValue = new TypedValue();
            if (ctx.getTheme().resolveAttribute(R.attr.color_favorito, typedValue, true)) {
                color = typedValue.data;
            }
            sb.setSpan(new BackgroundColorSpan(color), 0, sb.length(), 33);
        }
        return sb;
    }

    public SpannableString getEscrituraSpanneada() {
        SpannableString sb = new SpannableString(getVersiculo() + ". " + getEscritura());
        sb.setSpan(new StyleSpan(FAVORITO), 0, String.valueOf(getVersiculo()).length() + FAVORITO, 33);
        return sb;
    }

    public void setIsFav(int is_fav) {
        this.is_fav = is_fav;
    }

    public boolean is_fav() {
        return this.is_fav == FAVORITO;
    }
}
