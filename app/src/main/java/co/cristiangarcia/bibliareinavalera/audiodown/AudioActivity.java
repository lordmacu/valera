package co.cristiangarcia.bibliareinavalera.audiodown;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import co.cristiangarcia.bibliareinavalera.R;
import co.cristiangarcia.bibliareinavalera.activity.BaseSubActivity;
import co.cristiangarcia.bibliareinavalera.audiodown.DownService.DownServiceBinder;

public class AudioActivity extends BaseSubActivity implements ServiceConnection {
    private co.cristiangarcia.bibliareinavalera.audiodown.AdaptadorListAudio adapter;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(co.cristiangarcia.bibliareinavalera.audiodown.DownService.action_sendlibros)) {
                ArrayList<AudLibro> listLibro=intent.getParcelableArrayListExtra("listlibro");
                AudioActivity.this.adapter.updateLibro(listLibro);
            } else if (intent.getAction().equals(co.cristiangarcia.bibliareinavalera.audiodown.DownService.action_sendcola)) {
                ArrayList<Audio> listcola=intent.getParcelableArrayListExtra("listcola");

                AudioActivity.this.adapter.updateCola(listcola);
            } else if (intent.getAction().equals(co.cristiangarcia.bibliareinavalera.audiodown.DownService.action_sendIsFailure)) {
                if (intent.getBooleanExtra("isFailure", false)) {
                    AudioActivity.this.capareanu.setVisibility(0);
                } else {
                    AudioActivity.this.capareanu.setVisibility(8);
                }
            } else if (intent.getAction().equals(co.cristiangarcia.bibliareinavalera.audiodown.DownService.action_verifyaudioslib)) {

                ArrayList<AudLibro> listlibro=intent.getParcelableArrayListExtra("listlibro");

                AudioActivity.this.adapter.updateLibro(listlibro);
                if (AudioActivity.this.libroNoti > 0) {
                    new co.cristiangarcia.bibliareinavalera.audiodown.DialogAudioDown(AudioActivity.this, ((co.cristiangarcia.bibliareinavalera.audiodown.AdaptadorListAudio) AudioActivity.this.lv.getAdapter()).getAudLibro(AudioActivity.this.libroNoti - 1)).show();
                    AudioActivity.this.libroNoti = 0;
                }
            } else if (intent.getAction().equals(co.cristiangarcia.bibliareinavalera.audiodown.DownService.action_updateitemcola)) {

                ArrayList<Audio> cola=intent.getParcelableArrayListExtra("cola");

                AudioActivity.this.adapter.updateItemCola(cola, intent.getLongExtra("count", -1), intent.getLongExtra("size", -1));
            }
        }
    };
    private Button btnreanu;
    private LinearLayout capareanu;
    private co.cristiangarcia.bibliareinavalera.audiodown.DownService downService = null;
    private volatile int libroNoti = 0;
    private ListView lv;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_main);
        setTitle("Descargar Audios");
        this.lv = (ListView) findViewById(R.id.au_m_lv1);
        this.adapter = new co.cristiangarcia.bibliareinavalera.audiodown.AdaptadorListAudio(this);
        this.lv.setAdapter(this.adapter);
        this.lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Object listItem = AudioActivity.this.lv.getItemAtPosition(position);
                if (listItem instanceof co.cristiangarcia.bibliareinavalera.audiodown.AudLibro) {
                    new co.cristiangarcia.bibliareinavalera.audiodown.DialogAudioDown(AudioActivity.this, (co.cristiangarcia.bibliareinavalera.audiodown.AudLibro) listItem).show();
                }
            }
        });
        this.capareanu = (LinearLayout) findViewById(R.id.au_m_ll1);
        this.btnreanu = (Button) findViewById(R.id.au_m_bt1);
        this.btnreanu.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                AudioActivity.this.reanudarDescarga();
            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.libroNoti = extras.getInt("lb");
        }
        startService(new Intent(this, co.cristiangarcia.bibliareinavalera.audiodown.DownService.class));
    }

    public void onResume() {
        super.onResume();
        IntentFilter filtro = new IntentFilter();
        filtro.addAction(co.cristiangarcia.bibliareinavalera.audiodown.DownService.action_sendlibros);
        filtro.addAction(co.cristiangarcia.bibliareinavalera.audiodown.DownService.action_sendcola);
        filtro.addAction(co.cristiangarcia.bibliareinavalera.audiodown.DownService.action_sendIsFailure);
        filtro.addAction(co.cristiangarcia.bibliareinavalera.audiodown.DownService.action_verifyaudioslib);
        filtro.addAction(co.cristiangarcia.bibliareinavalera.audiodown.DownService.action_updateitemcola);
        registerReceiver(this.broadcastReceiver, filtro);
        bindService(new Intent(this, co.cristiangarcia.bibliareinavalera.audiodown.DownService.class), this, 1);
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(this.broadcastReceiver);
        unbindService(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Limpiar").setShowAsAction(1);
        menu.add(0, 2, 0, "Cancelar Todo").setShowAsAction(1);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            limpiar();
            return true;
        } else if (item.getItemId() != 2) {
            return super.onOptionsItemSelected(item);
        } else {
            borrarTodo();
            return true;
        }
    }

    public void onServiceConnected(ComponentName name, IBinder service) {
        this.downService = ((DownServiceBinder) service).getService();
        this.downService.sendLibros();
        this.downService.sendCola();
        this.downService.sendIsFailure();
        this.downService.VerifyAudiosLibro();
    }

    public void onServiceDisconnected(ComponentName name) {
        this.downService = null;
    }

    public void addAudioCola(Audio item) {
        if (this.downService != null) {
            this.downService.addAudioCola(item);
        }
    }

    public void addListAudiosCola(Audio[] list) {
        if (this.downService != null) {
            this.downService.addListAudiosCola(list);
        }
    }

    public void delAudioCola(Audio item) {
        if (this.downService != null) {
            this.downService.delAudioCola(item);
        }
    }

    public void limpiar() {
        if (this.downService != null) {
            this.downService.limpiarCola();
        }
    }

    public void borrarTodo() {
        if (this.downService != null) {
            this.downService.borrarTodo();
        }
    }

    public void reanudarDescarga() {
        if (this.downService != null) {
            this.downService.reanudarDescarga();
        }
    }
}
