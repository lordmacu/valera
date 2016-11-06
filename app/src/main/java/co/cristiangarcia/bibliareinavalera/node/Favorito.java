package co.cristiangarcia.bibliareinavalera.node;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import co.cristiangarcia.bibliareinavalera.BuildConfig;

public class Favorito extends SecuenciaVersiculos implements Parcelable {
    public static final Creator<Favorito> CREATOR = new Creator<Favorito>() {
        public Favorito createFromParcel(Parcel in) {
            return new Favorito(in);
        }

        public Favorito[] newArray(int size) {
            return new Favorito[size];
        }
    };
    private int id;
    private String nota = BuildConfig.FLAVOR;

    public Favorito(int id, String nota, int id_libro, String nb_libro, int capitulo, int versiculo, String escritura) {
        super(id_libro, nb_libro, capitulo, versiculo, escritura);
        this.id = id;
        this.nota = nota;
    }

    public Favorito(Parcel in) {
        super(in);
        this.id = in.readInt();
        this.nota = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.id);
        dest.writeString(this.nota);
    }

    public int getIdFavorito() {
        return this.id;
    }

    public boolean hasNota() {
        return (this.nota == null || this.nota.equals(BuildConfig.FLAVOR)) ? false : true;
    }

    public String getNota() {
        return this.nota;
    }
}
