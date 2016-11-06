package co.cristiangarcia.bibliareinavalera.audiodown;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import co.cristiangarcia.bibliareinavalera.R;

public class AdaptadorDialogListAudio extends BaseAdapter {
    private AudioActivity context;
    private AudLibro libro;

    private static class AudioCapituloHolder {
        public ImageView iv1;
        public ProgressBar pb1;
        public TextView tv1;

        private AudioCapituloHolder() {
        }
    }

    public AdaptadorDialogListAudio(AudioActivity context, AudLibro libro) {
        this.context = context;
        this.libro = libro;
    }

    public int getCount() {
        return this.libro.getListAudios().length + 1;
    }

    public Object getItem(int position) {
        if (position == 0) {
            return this.libro;
        }
        return this.libro.getListAudios()[position - 1];
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View item, ViewGroup parent) {
        AudioCapituloHolder holder;
        if (item == null || !(item.getTag() instanceof AudioCapituloHolder)) {
            item = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.item_audio_down, null);
            holder = new AudioCapituloHolder();
            holder.tv1 = (TextView) item.findViewById(R.id.itm_aud_cap_tv1);
            holder.pb1 = (ProgressBar) item.findViewById(R.id.itm_aud_cap_pb1);
            holder.iv1 = (ImageView) item.findViewById(R.id.itm_aud_cap_iv1);
            item.setTag(holder);
        } else {
            holder = (AudioCapituloHolder) item.getTag();
        }
        if (position == 0) {
            AudLibro node = (AudLibro) getItem(position);
            holder.tv1.setText("Libro " + node.getName());
            if (node.getIsDownloaded() == 1) {
                holder.iv1.setVisibility(0);
            } else {
                holder.iv1.setVisibility(8);
            }
            holder.pb1.setVisibility(8);
        } else {
            Audio node2 = (Audio) getItem(position);
            holder.tv1.setText("Cap\u00edtulo " + node2.getName());
            int status = node2.getIsDownloaded();
            if (status == 1) {
                holder.pb1.setVisibility(8);
                holder.iv1.setVisibility(0);
            } else if (status == 0) {
                holder.pb1.setVisibility(8);
                holder.iv1.setVisibility(8);
            }
        }
        return item;
    }

    public void update(AudLibro libro) {
        if (libro != null) {
            this.libro = libro;
        }
        notifyDataSetChanged();
    }
}
