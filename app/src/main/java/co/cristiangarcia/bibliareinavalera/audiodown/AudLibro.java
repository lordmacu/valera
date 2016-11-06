package co.cristiangarcia.bibliareinavalera.audiodown;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class AudLibro implements Parcelable {
    public static final Creator<AudLibro> CREATOR = new Creator<AudLibro>() {
        public AudLibro createFromParcel(Parcel in) {
            return new AudLibro(in);
        }

        public AudLibro[] newArray(int size) {
            return new AudLibro[size];
        }
    };
    public static final int DESCARGADO = 1;
    public static final int NODESCARGADO = 0;
    public static final int NOVERIFICADO = -1;
    private int isDownloaded;
    private Audio[] listaudios;
    private String name;
    private int numcap;
    private String prefijo;

    public AudLibro(String name, String prefijo, int numcap) {
        this.name = name;
        this.prefijo = prefijo;
        this.numcap = numcap;
        this.isDownloaded = NOVERIFICADO;
        this.listaudios = new Audio[this.numcap];
        createListAudios();
    }

    public AudLibro(Parcel in) {
        this.name = in.readString();
        this.prefijo = in.readString();
        this.numcap = in.readInt();
        this.isDownloaded = in.readInt();
        this.listaudios = (Audio[]) in.createTypedArray(Audio.CREATOR);
    }

    public int describeContents() {
        return NODESCARGADO;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.prefijo);
        dest.writeInt(this.numcap);
        dest.writeInt(this.isDownloaded);
        dest.writeTypedArray(this.listaudios, flags);
    }

    private void createListAudios() {
        for (int i = NODESCARGADO; i < this.numcap; i += DESCARGADO) {
            this.listaudios[i] = new Audio(this, i + DESCARGADO);
        }
    }

    private void verifyAudios() {
        int count = NODESCARGADO;
        int i = NODESCARGADO;
        while (i < this.listaudios.length && this.listaudios[i].getIsDownloaded() == DESCARGADO) {
            count += DESCARGADO;
            i += DESCARGADO;
        }
        if (count == this.numcap) {
            setIsDownloaded(DESCARGADO);
        } else {
            setIsDownloaded(NODESCARGADO);
        }
    }

    public Audio[] getListAudios() {
        return this.listaudios;
    }

    public String getName() {
        return this.name;
    }

    public String getPrefijo() {
        return this.prefijo;
    }

    public int getIsDownloaded() {
        return this.isDownloaded;
    }

    private void setIsDownloaded(int status) {
        this.isDownloaded = status;
    }

    public void setDownloadedAudio(int cap) {
        this.listaudios[cap + NOVERIFICADO].setIsDownloaded(DESCARGADO);
        verifyAudios();
    }

    public void setVerificado() {
        if (getIsDownloaded() == NOVERIFICADO) {
            setIsDownloaded(NODESCARGADO);
        }
    }
}
