package co.cristiangarcia.bibliareinavalera.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;
import co.cristiangarcia.bibliareinavalera.BuildConfig;
import co.cristiangarcia.bibliareinavalera.R;
import co.cristiangarcia.bibliareinavalera.bd.DatabaseHelper;
import co.cristiangarcia.bibliareinavalera.node.MiBusqueda;

public class BusquedaActivity extends BaseSubActivity {
    private Button busq_m_bt1;
    private EditText busq_m_et1;
    private TextView busq_m_tv1;
    private String busqueda;
    private ProgressDialog dialog;
    private ArrayList<MiBusqueda> list = new ArrayList();
    private RecyclerView recyclerView;

    public class MyAdapter extends Adapter<MyAdapter.ViewHolder> {
        private ArrayList<MiBusqueda> list = new ArrayList();
        private OnItemClickListener listener;

        public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
            public TextView tv1 = ((TextView) this.itemView.findViewById(R.id.item_v_tv1));

            public ViewHolder(View v) {
                super(v);
            }
        }

        public MyAdapter(ArrayList<MiBusqueda> list, OnItemClickListener listener) {
            this.list = list;
            this.listener = listener;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vers, parent, false);
            final ViewHolder vh = new ViewHolder(v);
            v.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    MyAdapter.this.listener.onItemClick(v, vh.getAdapterPosition());
                }
            });
            return vh;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            MiBusqueda nodo = (MiBusqueda) this.list.get(position);
            String texto = nodo.getTexto() + "\n" + nodo.getNameLibro() + " " + nodo.getCapitulo() + ":" + nodo.getVersiculo();
            SpannableString sb = new SpannableString(texto);
            sb.setSpan(new StyleSpan(1), String.valueOf(nodo.getTexto()).length() + 1, texto.length(), 33);
            holder.tv1.setText(sb);
            holder.tv1.setTextSize(2, (float) Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(BusquedaActivity.this).getString("list_preference", String.valueOf(BusquedaActivity.this.getResources().getInteger(R.integer.vers_size_def)))));
        }

        public int getItemCount() {
            return this.list.size();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int i);
    }

    private class TareaLoad extends AsyncTask<Void, Void, Void> {
        private DatabaseHelper helper;

        private TareaLoad() {
            this.helper = DatabaseHelper.getLtHelper(BusquedaActivity.this.getApplicationContext());
        }

        protected void onPreExecute() {
            BusquedaActivity.this.dialog.show();
        }

        protected Void doInBackground(Void... params) {
            BusquedaActivity.this.list.clear();
            BusquedaActivity.this.list = this.helper.getVersiculosBusq(BusquedaActivity.this.busqueda, getBaseContext());
            return null;
        }

        protected void onPostExecute(Void bytes) {
            BusquedaActivity.this.dialog.dismiss();
            if (BusquedaActivity.this.list != null) {
                BusquedaActivity.this.busq_m_tv1.setText(String.valueOf(BusquedaActivity.this.list.size()) + (BusquedaActivity.this.list.size() == 1 ? " resultado" : " resultados"));
                BusquedaActivity.this.recyclerView.setAdapter(new MyAdapter(BusquedaActivity.this.list, new OnItemClickListener() {
                    public void onItemClick(View v, int position) {
                        MiBusqueda nodo = (MiBusqueda) BusquedaActivity.this.list.get(position);
                        Intent myIntent = new Intent(BusquedaActivity.this, LibroActivity.class);
                        myIntent.putExtra("libro", nodo.getLibro());
                        myIntent.putExtra("capitulo", nodo.getCapitulo());
                        myIntent.putExtra("versiculo", nodo.getVersiculo());
                        BusquedaActivity.this.startActivity(myIntent);
                    }
                }));
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.busq_main);
        getSupportActionBar().setTitle("B\u00fasqueda Avanzada");
        this.dialog = new ProgressDialog(this);
        this.dialog.setIndeterminate(true);
        this.dialog.setCancelable(true);
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.setMessage("Buscando...");
        this.recyclerView = (RecyclerView) findViewById(R.id.busq_m_rv1);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.busq_m_tv1 = (TextView) findViewById(R.id.busq_m_tv1);
        this.busq_m_tv1.setTextSize(2, (float) getResources().getInteger(R.integer.vers_size));
        this.busq_m_et1 = (EditText) findViewById(R.id.busq_m_et1);
        this.busq_m_bt1 = (Button) findViewById(R.id.busq_m_bt1);
        this.busq_m_bt1.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                BusquedaActivity.this.busqueda = BusquedaActivity.this.busq_m_et1.getText().toString().toLowerCase(Locale.getDefault()).replace("\u00e1", "a").replace("\u00e9", "e").replace("\u00ed", "i").replace("\u00f3", "o").replace("\u00fa", "u").replace(" ", BuildConfig.FLAVOR).replace(".", BuildConfig.FLAVOR).replace(",", BuildConfig.FLAVOR).replace(":", BuildConfig.FLAVOR).replace(";", BuildConfig.FLAVOR).replace("?", BuildConfig.FLAVOR).replace("\u00bf", BuildConfig.FLAVOR).replace("\u00a1", BuildConfig.FLAVOR).replace("!", BuildConfig.FLAVOR).replace("(", BuildConfig.FLAVOR).replace(")", BuildConfig.FLAVOR);
                if (!BusquedaActivity.this.busqueda.equals(BuildConfig.FLAVOR)) {
                    BusquedaActivity.this.IniciarBusqueda();
                }
            }
        });
    }

    public void IniciarBusqueda() {
        new TareaLoad().execute(new Void[0]);
    }
}
