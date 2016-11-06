package co.cristiangarcia.bibliareinavalera.node;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import java.util.ArrayList;
import java.util.List;

import co.cristiangarcia.bibliareinavalera.BuildConfig;

public class SecuenciaVersiculos implements Parcelable {
    public static final Creator<SecuenciaVersiculos> CREATOR = new Creator<SecuenciaVersiculos>() {
        public SecuenciaVersiculos createFromParcel(Parcel in) {
            return new SecuenciaVersiculos(in);
        }

        public SecuenciaVersiculos[] newArray(int size) {
            return new SecuenciaVersiculos[size];
        }
    };
    protected int capitulo;
    protected List<String> escritura = new ArrayList();
    protected int id_libro;
    protected String nb_libro;
    protected int versiculof;
    protected int versiculoi;

    public SecuenciaVersiculos(int id_libro, String nb_libro, int capitulo, int versiculo, String escritura) {
        this.id_libro = id_libro;
        this.nb_libro = nb_libro;
        this.capitulo = capitulo;
        this.versiculoi = versiculo;
        this.versiculof = versiculo;
        this.escritura.add(escritura);
    }

    public SecuenciaVersiculos(Parcel in) {
        this.id_libro = in.readInt();
        this.nb_libro = in.readString();
        this.capitulo = in.readInt();
        this.versiculoi = in.readInt();
        this.versiculof = in.readInt();
        in.readStringList(this.escritura);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id_libro);
        dest.writeString(this.nb_libro);
        dest.writeInt(this.capitulo);
        dest.writeInt(this.versiculoi);
        dest.writeInt(this.versiculof);
        dest.writeStringList(this.escritura);
    }

    public void putVersiculo(int versiculo, String escritura) {
        this.versiculof = versiculo;
        this.escritura.add(escritura);
    }

    public String getTitulo() {
        String res = this.nb_libro + " " + this.capitulo + ":" + this.versiculoi;
        if (this.versiculoi != this.versiculof) {
            return res + "-" + this.versiculof;
        }
        return res;
    }

    public String getBody() {
        String res = BuildConfig.FLAVOR;
        for (String e : this.escritura) {
            res = res.equals(BuildConfig.FLAVOR) ? e : res + " " + e.trim();
        }
        return res;
    }

    public String getEscritura() {
        return getBody() + "\n" + getTitulo();
    }

    public SpannableString getEscritura(boolean tituloNegrita) {
        String body = getBody();
        SpannableString sb = new SpannableString(getBody() + "\n\n" + getTitulo());
        if (tituloNegrita) {
            sb.setSpan(new StyleSpan(1), body.length() + 1, sb.length(), 33);
        }
        return sb;
    }

    public int getIdLibro() {
        return this.id_libro;
    }

    public int getCapitulo() {
        return this.capitulo;
    }

    public int getVersiculoI() {
        return this.versiculoi;
    }

    public int getVersiculoF() {
        return this.versiculof;
    }
}
