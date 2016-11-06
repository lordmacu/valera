package co.cristiangarcia.bibliareinavalera.node;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class MiBusqueda extends Versiculo implements Parcelable {
    public static final Creator<MiBusqueda> CREATOR = new Creator<MiBusqueda>() {
        public MiBusqueda createFromParcel(Parcel in) {
            return new MiBusqueda(in);
        }

        public MiBusqueda[] newArray(int size) {
            return new MiBusqueda[size];
        }
    };
    private int capitulo;
    private int id_libro;
    private String libro;

    public MiBusqueda(int id, int versiculo, String escritura, int is_fav, Libro libro, int capitulo) {
        super(id, versiculo, escritura, is_fav);
        this.capitulo = capitulo;
        this.id_libro = libro.getId();
        this.libro = libro.getName();
    }

    public MiBusqueda(Parcel in) {
        super(in.readInt(), in.readInt(), in.readString(), in.readInt());
        this.id_libro = in.readInt();
        this.libro = in.readString();
        this.capitulo = in.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.versiculo);
        dest.writeString(this.escritura);
        dest.writeInt(this.is_fav);
        dest.writeInt(this.id_libro);
        dest.writeString(this.libro);
        dest.writeInt(this.capitulo);
    }

    public String getTexto() {
        return this.escritura;
    }

    public int getLibro() {
        return this.id_libro;
    }

    public String getNameLibro() {
        return this.libro;
    }

    public int getCapitulo() {
        return this.capitulo;
    }

    public int getVersiculo() {
        return this.versiculo;
    }
}
