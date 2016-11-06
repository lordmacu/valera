package co.cristiangarcia.bibliareinavalera.audiodown;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import co.cristiangarcia.bibliareinavalera.audiodown.GestorDescarga.DownloadState;

public class Audio extends Progresed implements Parcelable {
    public static final Creator<Audio> CREATOR = new Creator<Audio>() {
        public Audio createFromParcel(Parcel in) {
            return new Audio(in);
        }

        public Audio[] newArray(int size) {
            return new Audio[size];
        }
    };
    public static final int DESCARGADO = 1;
    public static final int NODESCARGADO = 0;
    private int cap;
    private String downname;
    private int isDownloaded;
    private DownloadState mDownloadState;
    private String name;
    private String prefijo;

    public Audio(AudLibro libro, int cap) {
        this.name = libro.getName() + " " + cap;
        this.downname = libro.getPrefijo() + "_" + Util.rellenaUnCero(cap) + ".mp3";
        this.prefijo = libro.getPrefijo();
        this.cap = cap;
        this.isDownloaded = 0;
        this.mDownloadState = DownloadState.NONE;
    }

    public Audio(Parcel in) {
        super(in);
        this.name = in.readString();
        this.downname = in.readString();
        this.prefijo = in.readString();
        this.cap = in.readInt();
        this.isDownloaded = in.readInt();
        this.mDownloadState = DownloadState.valueOf(in.readString());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.name);
        dest.writeString(this.downname);
        dest.writeString(this.prefijo);
        dest.writeInt(this.cap);
        dest.writeInt(this.isDownloaded);
        dest.writeString(this.mDownloadState.name());
    }

    public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            Audio other = (Audio) obj;
            if (!(getName() == null || other.getName() == null || !other.getName().equals(getName()))) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return this.name;
    }

    public String getDownName() {
        return this.downname;
    }

    public String getPrefijo() {
        return this.prefijo;
    }

    public int getCapitulo() {
        return this.cap;
    }

    public int getIsDownloaded() {
        return this.isDownloaded;
    }

    public void setIsDownloaded(int status) {
        this.isDownloaded = status;
    }

    public DownloadState getDownloadState() {
        return this.mDownloadState;
    }

    public void setDownloadState(DownloadState state) {
        this.mDownloadState = state;
    }
}
