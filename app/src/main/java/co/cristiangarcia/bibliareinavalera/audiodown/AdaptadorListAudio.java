package co.cristiangarcia.bibliareinavalera.audiodown;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import co.cristiangarcia.bibliareinavalera.R;
import co.cristiangarcia.bibliareinavalera.audiodown.GestorDescarga.DownloadState;

public class AdaptadorListAudio extends BaseAdapter {
    private AudioActivity context;
    private ArrayList<Audio> listcola = new ArrayList();
    private ArrayList<AudLibro> listlibro = new ArrayList();

    private static class AudioColaHolder {
        public Button btn1;
        public ProgressBar pbar;
        public TextView tvname;
        public TextView tvporc;
        public TextView tvprog;

        private AudioColaHolder() {
        }
    }

    private static class AudioLibroHolder {
        public ImageView iv1;
        public ProgressBar pb1;
        public TextView tv1;

        private AudioLibroHolder() {
        }
    }

    public AdaptadorListAudio(AudioActivity context) {
        this.context = context;
    }

    public int getCount() {
        return this.listcola.size() + this.listlibro.size();
    }

    public Object getItem(int position) {
        if (position < this.listcola.size()) {
            return this.listcola.get(position);
        }
        return this.listlibro.get(position - this.listcola.size());
    }

    public AudLibro getAudLibro(int position) {
        return (AudLibro) this.listlibro.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View item, ViewGroup parent) {
        if (position < this.listcola.size()) {
            AudioColaHolder holder;
            final Audio node = (Audio) getItem(position);
            if (item == null || !(item.getTag() instanceof AudioColaHolder)) {
                item = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.item_audio_cola, null);
                holder = new AudioColaHolder();
                holder.tvname = (TextView) item.findViewById(R.id.itm_aud_col_tv1);
                holder.pbar = (ProgressBar) item.findViewById(R.id.itm_aud_col_pbar1);
                holder.pbar.setMax(100);
                holder.tvporc = (TextView) item.findViewById(R.id.itm_aud_col_tv2);
                holder.tvprog = (TextView) item.findViewById(R.id.itm_aud_col_tv3);
                holder.btn1 = (Button) item.findViewById(R.id.itm_aud_col_btn1);
                item.setTag(holder);
            } else {
                holder = (AudioColaHolder) item.getTag();
            }
            holder.tvname.setText(node.getName());
            holder.tvporc.setText(node.getPorcent());
            holder.tvprog.setText(node.getSProgress());
            if (node.getDownloadState() == DownloadState.NONE || node.getDownloadState() == DownloadState.QUEUED) {
                holder.pbar.setIndeterminate(true);
            } else {
                holder.pbar.setIndeterminate(false);
                holder.pbar.setProgress(node.getProgress());
            }
            holder.btn1.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    AdaptadorListAudio.this.context.delAudioCola(node);
                }
            });
            return item;
        }
        AudioLibroHolder holder2;
        AudLibro node2 = (AudLibro) getItem(position);
        if (item == null || !(item.getTag() instanceof AudioLibroHolder)) {
            item = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.item_audio_libro, null);
            holder2 = new AudioLibroHolder();
            holder2.tv1 = (TextView) item.findViewById(R.id.itm_aud_lib_tv1);
            holder2.pb1 = (ProgressBar) item.findViewById(R.id.itm_aud_lib_pb1);
            holder2.iv1 = (ImageView) item.findViewById(R.id.itm_aud_lib_iv1);
            item.setTag(holder2);
        } else {
            holder2 = (AudioLibroHolder) item.getTag();
        }
        holder2.tv1.setText(node2.getName());
        int status = node2.getIsDownloaded();
        if (status == 1) {
            holder2.pb1.setVisibility(8);
            holder2.iv1.setVisibility(0);
        } else if (status == 0) {
            holder2.pb1.setVisibility(8);
            holder2.iv1.setVisibility(8);
        } else if (status == -1) {
            holder2.pb1.setVisibility(0);
            holder2.iv1.setVisibility(8);
        }
        return item;
    }

    public void updateLibro(ArrayList<AudLibro> listlibro) {
        if (listlibro != null) {
            this.listlibro = listlibro;
            notifyDataSetChanged();
        }
    }

    public void updateCola(ArrayList<Audio> listcola) {
        if (listcola != null) {
            this.listcola = listcola;
            notifyDataSetChanged();
        }
    }

    public void updateItemCola(ArrayList<Audio> cola, long count, long size) {
        if (this.listcola != null) {
            this.listcola = cola;
        }
        notifyDataSetChanged();
    }
}
