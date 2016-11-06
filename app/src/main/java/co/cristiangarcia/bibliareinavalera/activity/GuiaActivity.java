package co.cristiangarcia.bibliareinavalera.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import co.cristiangarcia.bibliareinavalera.R;
import co.cristiangarcia.bibliareinavalera.bd.DatabaseHelper;
import co.cristiangarcia.bibliareinavalera.node.Categoria;
import co.cristiangarcia.bibliareinavalera.node.SecuenciaVersiculos;
import co.cristiangarcia.bibliareinavalera.util.Util;

public class GuiaActivity extends BaseSubActivity {
    private ArrayList<SecuenciaVersiculos> list = new ArrayList();
    private Categoria mv;
    private RecyclerView recyclerView;

    public class MyAdapter extends Adapter<MyAdapter.ViewHolder> {
        private ArrayList<SecuenciaVersiculos> list = new ArrayList();
        private OnItemClickListener listener;

        public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
            public View btn1 = this.itemView.findViewById(R.id.item_c_guia_bt1);
            public View btn2 = this.itemView.findViewById(R.id.item_c_guia_bt2);
            public TextView tv1 = ((TextView) this.itemView.findViewById(R.id.item_c_guia_tv1));

            public ViewHolder(View v) {
                super(v);
            }
        }

        public MyAdapter(ArrayList<SecuenciaVersiculos> list, OnItemClickListener listener) {
            this.list = list;
            this.listener = listener;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_guia, parent, false);
            final ViewHolder vh = new ViewHolder(v);
            v.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    MyAdapter.this.listener.onItemClick(v, vh.getAdapterPosition());
                }
            });
            return vh;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            final SecuenciaVersiculos nodo = (SecuenciaVersiculos) this.list.get(position);
            holder.tv1.setText(nodo.getEscritura(true));
            holder.tv1.setTextSize(2, (float) Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GuiaActivity.this).getString("list_preference", String.valueOf(GuiaActivity.this.getResources().getInteger(R.integer.vers_size)))));
            holder.btn1.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Util.share(GuiaActivity.this, nodo.getEscritura(), nodo.getIdLibro(), nodo.getCapitulo(), nodo.getVersiculoI(), nodo.getVersiculoF());
                }
            });
            holder.btn2.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Util.copiar(GuiaActivity.this, nodo.getTitulo(), nodo.getEscritura());
                }
            });
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
            this.helper = DatabaseHelper.getLtHelper(GuiaActivity.this.getApplicationContext());
        }

        protected Void doInBackground(Void... params) {
            GuiaActivity.this.list.clear();
            GuiaActivity.this.list = this.helper.getPasajesCategoria(GuiaActivity.this.mv.getId(),getBaseContext());
            return null;
        }

        protected void onPostExecute(Void bytes) {
            GuiaActivity.this.recyclerView.setAdapter(new MyAdapter(GuiaActivity.this.list, new OnItemClickListener() {
                public void onItemClick(View v, int position) {
                    SecuenciaVersiculos nodo = (SecuenciaVersiculos) GuiaActivity.this.list.get(position);
                    Intent myIntent = new Intent(GuiaActivity.this, LibroActivity.class);
                    myIntent.putExtra("libro", nodo.getIdLibro());
                    myIntent.putExtra("capitulo", nodo.getCapitulo());
                    myIntent.putExtra("versiculo", nodo.getVersiculoI());
                    GuiaActivity.this.startActivity(myIntent);
                }
            }));
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.guia_main);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.mv = (Categoria) extras.getParcelable("mv");
        }
        this.recyclerView = (RecyclerView) findViewById(R.id.list_recycle);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setTitle(this.mv.getNombre());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadMisVersiculos();
    }

    public void loadMisVersiculos() {
        new TareaLoad().execute(new Void[0]);
    }
}
