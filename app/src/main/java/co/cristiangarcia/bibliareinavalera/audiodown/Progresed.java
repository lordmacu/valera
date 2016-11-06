package co.cristiangarcia.bibliareinavalera.audiodown;

import android.os.Parcel;

public class Progresed {
    private long count;
    private long size;

    protected Progresed() {
        this.count = 0;
        this.size = 0;
    }

    protected Progresed(Parcel in) {
        this.count = in.readLong();
        this.size = in.readLong();
    }

    protected void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.count);
        dest.writeLong(this.size);
    }

    protected void setProgress(long count, long size) {
        this.count = count;
        this.size = size;
    }

    protected void setCountWithSize() {
        this.count = this.size;
    }

    protected int getProgress() {
        return (int) ((((double) this.count) / ((double) this.size)) * 100.0d);
    }

    protected String getPorcent() {
        return getProgress() + "%";
    }

    protected String getSProgress() {
        return String.format("%.2f", new Object[]{Double.valueOf((((double) this.count) / 1024.0d) / 1024.0d)}) + "MB/" + String.format("%.2f", new Object[]{Double.valueOf((((double) this.size) / 1024.0d) / 1024.0d)}) + "MB";
    }
}
