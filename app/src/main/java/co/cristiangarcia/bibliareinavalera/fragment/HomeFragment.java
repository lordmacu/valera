package co.cristiangarcia.bibliareinavalera.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import co.cristiangarcia.bibliareinavalera.R;
import co.cristiangarcia.bibliareinavalera.activity.LibroActivity;
import co.cristiangarcia.bibliareinavalera.bd.DatabaseHelper;
import co.cristiangarcia.bibliareinavalera.bd.DinamicOpenHelper;
import co.cristiangarcia.bibliareinavalera.node.SecuenciaVersiculos;
import co.cristiangarcia.bibliareinavalera.node.UltimaLectura;
import co.cristiangarcia.bibliareinavalera.node.UltimaLectura.Lectura;
import co.cristiangarcia.bibliareinavalera.sync.SyncHelper;
import co.cristiangarcia.bibliareinavalera.util.GenericViewHolder;
import co.cristiangarcia.bibliareinavalera.util.OnItemClickListener;
import co.cristiangarcia.bibliareinavalera.util.Preference;
import co.cristiangarcia.bibliareinavalera.util.Util;

public class HomeFragment extends Fragment {
    public static final String action_up_ultlec = "co.cristiangarcia.bibliareinavalera.fragment.HomeFragment.action_up_ultlec";
    MyAdapter adapter;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            HomeFragment.this.loadUltLec();
        }
    };
    private RecyclerView recyclerView;

    private class MyAdapter extends Adapter<GenericViewHolder> {
        private static final int View_Type_UltLec = 1;
        private static final int View_Type_VerDia = 0;
        ArrayList<Object> list = new ArrayList();
        private OnItemClickListener listener;

        public class UltLecHolder extends GenericViewHolder {
            public View bt1 = this.itemView.findViewById(R.id.item_c_ul_bt1);
            public View bt2 = this.itemView.findViewById(R.id.item_c_ul_bt2);
            public TextView tv1 = ((TextView) this.itemView.findViewById(R.id.item_c_ul_tbt1));
            public TextView tv2 = ((TextView) this.itemView.findViewById(R.id.item_c_ul_tbt2));

            public UltLecHolder(View v) {
                super(v);
            }

            public void setDataOnView(int position) {
                final UltimaLectura nodo = (UltimaLectura) MyAdapter.this.list.get(position);
                if (nodo == null || !nodo.hasVisto()) {
                    this.bt1.setVisibility(8);
                } else {
                    this.bt1.setVisibility(0);
                    this.tv1.setText(nodo.getVisto().getLectura());
                    this.bt1.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            Intent myIntent = new Intent(HomeFragment.this.getActivity(), LibroActivity.class);
                            myIntent.putExtra("libro", nodo.getVisto().getIdLibro());
                            myIntent.putExtra("capitulo", nodo.getVisto().getCapitulo());
                            myIntent.putExtra("versiculo", 0);
                            HomeFragment.this.startActivity(myIntent);
                        }
                    });
                    this.bt1.setOnLongClickListener(new OnLongClickListener() {
                        public boolean onLongClick(View v) {
                            Toast.makeText(HomeFragment.this.getActivity(), "\u00daltimo Visto", 0).show();
                            return true;
                        }
                    });
                }
                if (nodo == null || !nodo.hasMarcado()) {
                    this.bt2.setVisibility(8);
                    return;
                }
                this.bt2.setVisibility(0);
                this.tv2.setText(nodo.getMarcado().getLectura());
                this.bt2.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Intent myIntent = new Intent(HomeFragment.this.getActivity(), LibroActivity.class);
                        myIntent.putExtra("libro", nodo.getMarcado().getIdLibro());
                        myIntent.putExtra("capitulo", nodo.getMarcado().getCapitulo());
                        myIntent.putExtra("versiculo", 0);
                        HomeFragment.this.startActivity(myIntent);
                    }
                });
                this.bt2.setOnLongClickListener(new OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        Toast.makeText(HomeFragment.this.getActivity(), "\u00daltimo Recordado", 0).show();
                        return true;
                    }
                });
            }
        }

        public class VersDiaHolder extends GenericViewHolder {
            public View bt1;
            public View bt2;
            public TextView name = ((TextView) this.itemView.findViewById(R.id.item_c_vd_tv3));

            public VersDiaHolder(View v) {
                super(v);
                this.name.setTextSize(2, (float) Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(HomeFragment.this.getContext()).getString("list_preference", String.valueOf(HomeFragment.this.getResources().getInteger(R.integer.vers_size)))));
                this.bt2 = this.itemView.findViewById(R.id.item_c_vd_bt2);
                this.bt1 = this.itemView.findViewById(R.id.item_c_vd_bt1);
            }

            public void setDataOnView(int position) {
                final SecuenciaVersiculos nodo = (SecuenciaVersiculos) MyAdapter.this.list.get(position);
                this.name.setText(nodo.getEscritura(true));
                this.bt2.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Util.copiar(HomeFragment.this.getContext(), nodo.getTitulo(), nodo.getEscritura());
                    }
                });
                this.bt1.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Util.share(HomeFragment.this.getContext(), nodo.getEscritura(), nodo.getIdLibro(), nodo.getCapitulo(), nodo.getVersiculoI(), nodo.getVersiculoF());
                    }
                });
            }
        }

        public MyAdapter(OnItemClickListener listener) {
            this.listener = listener;
        }

        public void updateUltLec(UltimaLectura ultlec) {
            if (ultlec != null && ultlec.hasData()) {
                for (int i = 0; i < this.list.size(); i += View_Type_UltLec) {
                    if (this.list.get(i) instanceof UltimaLectura) {
                        this.list.remove(i);
                    }
                }
                this.list.add(0, ultlec);
                notifyDataSetChanged();
            }
        }

        public void updateVerDia(SecuenciaVersiculos verdia) {
            if (verdia != null) {
                for (int i = 0; i < this.list.size(); i += View_Type_UltLec) {
                    if (this.list.get(i) instanceof SecuenciaVersiculos) {
                        this.list.remove(i);
                    }
                }
                this.list.add(verdia);
                notifyDataSetChanged();
            }
        }

        public int getItemViewType(int position) {
            if (this.list.get(position) instanceof UltimaLectura) {
                return View_Type_UltLec;
            }
            return 0;
        }

        public GenericViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case SyncHelper.ST_DELETE /*0*/:
                    View vvd = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_ver_dia, parent, false);
                    final GenericViewHolder vdh = new VersDiaHolder(vvd);
                    vvd.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            MyAdapter.this.listener.onItemClick(v, vdh.getAdapterPosition());
                        }
                    });
                    return vdh;
                default:
                    return new UltLecHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_ult_lec, parent, false));
            }
        }

        public void onBindViewHolder(GenericViewHolder holder, int position) {
            holder.setDataOnView(position);
        }

        public int getItemCount() {
            return this.list.size();
        }
    }

    private class TareaLoadUltLec extends AsyncTask<Integer, Float, Integer> {
        private Lectura marcado;
        private Lectura visto;

        private TareaLoadUltLec() {
            this.visto = null;
            this.marcado = null;
        }

        protected Integer doInBackground(Integer... params) {
            int libro = Preference.getInt(HomeFragment.this.getActivity(), LibroActivity.SHARE_LAST_LIBRO, 0);
            int capitulo = Preference.getInt(HomeFragment.this.getActivity(), LibroActivity.SHARE_LAST_CAPITULO, 1);
            if (libro > 0) {
                this.visto = new Lectura(libro, capitulo);
            }
            this.marcado = DatabaseHelper.getLtHelper(HomeFragment.this.getActivity()).getLastMarcado(getContext());
            return null;
        }

        protected void onPostExecute(Integer bytes) {
            HomeFragment.this.adapter.updateUltLec(new UltimaLectura(this.visto, this.marcado));
        }
    }

    private class TareaLoadVerDia extends AsyncTask<Integer, Float, Integer> {
        private SecuenciaVersiculos nodo;

        private TareaLoadVerDia() {
            this.nodo = null;
        }

        protected Integer doInBackground(Integer... params) {


            new DinamicOpenHelper(getContext());
           SecuenciaVersiculos getPasaje = DatabaseHelper.getLtHelper(getContext()).getPasaje(getContext());




             this.nodo = getPasaje;
            return null;
        }

        protected void onPostExecute(Integer bytes) {
            if (this.nodo == null) {
                Toast.makeText(HomeFragment.this.getContext(), "verdia nada :(", 0).show();
            }
            HomeFragment.this.adapter.updateVerDia(this.nodo);
        }
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View item = inflater.inflate(R.layout.list_main, container, false);
        this.recyclerView = (RecyclerView) item.findViewById(R.id.list_recycle);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.adapter = new MyAdapter(new OnItemClickListener() {
            public void onItemClick(View v, int position) {
                SecuenciaVersiculos nodo = (SecuenciaVersiculos) HomeFragment.this.adapter.list.get(position);
                Intent myIntent = new Intent(HomeFragment.this.getActivity(), LibroActivity.class);
                myIntent.putExtra("libro", nodo.getIdLibro());
                myIntent.putExtra("capitulo", nodo.getCapitulo());
                myIntent.putExtra("versiculo", nodo.getVersiculoI());
                HomeFragment.this.startActivity(myIntent);
            }
        });
        this.recyclerView.setAdapter(this.adapter);
        loadUltLec();
        loadVersiculoDia();
        return item;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().registerReceiver(this.broadcastReceiver, new IntentFilter(action_up_ultlec));
    }

    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(this.broadcastReceiver);
    }

    public void loadUltLec() {
        new TareaLoadUltLec().execute(new Integer[0]);
    }

    public void loadVersiculoDia() {
        new TareaLoadVerDia().execute(new Integer[0]);
    }
}
