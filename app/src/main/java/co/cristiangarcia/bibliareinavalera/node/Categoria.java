package co.cristiangarcia.bibliareinavalera.node;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Categoria implements Parcelable {
    public static final Creator<Categoria> CREATOR = new Creator<Categoria>() {
        public Categoria createFromParcel(Parcel in) {
            return new Categoria(in);
        }

        public Categoria[] newArray(int size) {
            return new Categoria[size];
        }
    };
    protected int id;
    protected String nombre;

    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Categoria(Parcel in) {
        this.id = in.readInt();
        this.nombre = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.nombre);
    }

    public int getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }
}
