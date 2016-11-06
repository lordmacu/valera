package co.cristiangarcia.bibliareinavalera.audiodown;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import co.cristiangarcia.bibliareinavalera.R;

public class DialogAudioDown extends Dialog {
    private AdaptadorDialogListAudio adapter;
    private AudioActivity ctx;
    private AudLibro libro;
    private ListView lv;

    public DialogAudioDown(AudioActivity ctx, AudLibro libro) {
        super(ctx);
        this.ctx = ctx;
        this.libro = libro;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.dialog_audiodown);
        this.lv = (ListView) findViewById(R.id.diag_audcap_lv1);
        this.adapter = new AdaptadorDialogListAudio(this.ctx, this.libro);
        this.lv.setAdapter(this.adapter);
        this.lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    Toast.makeText(DialogAudioDown.this.ctx, "descargar libro " + DialogAudioDown.this.libro.getName(), 1).show();
                    DialogAudioDown.this.ctx.addListAudiosCola(DialogAudioDown.this.libro.getListAudios());
                } else {
                    Audio audio = (Audio) DialogAudioDown.this.lv.getItemAtPosition(position);
                    Toast.makeText(DialogAudioDown.this.ctx, "descargar " + audio.getName(), 1).show();
                    DialogAudioDown.this.ctx.addAudioCola(audio);
                }
                DialogAudioDown.this.dismiss();
            }
        });
    }

    public void updateList(AudLibro libro) {
        this.adapter.update(libro);
    }
}
