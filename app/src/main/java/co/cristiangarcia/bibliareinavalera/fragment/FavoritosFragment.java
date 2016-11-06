package co.cristiangarcia.bibliareinavalera.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import co.cristiangarcia.bibliareinavalera.BuildConfig;
import co.cristiangarcia.bibliareinavalera.R;
import co.cristiangarcia.bibliareinavalera.activity.LibroActivity;
import co.cristiangarcia.bibliareinavalera.bd.DatabaseHelper;
import co.cristiangarcia.bibliareinavalera.node.Favorito;
import co.cristiangarcia.bibliareinavalera.util.GenericViewHolder;
import co.cristiangarcia.bibliareinavalera.util.OnItemClickListener;
import co.cristiangarcia.bibliareinavalera.util.Util;

public class FavoritosFragment extends Fragment {
    public static final String action_favoritos = "co.cristiangarcia.bibliareinavalera.fragment.FavoritosFragment.action_up_favoritos";
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            FavoritosFragment.this.loadFavoritos();
        }
    };
    private ArrayList<Object> list = new ArrayList();
    private RecyclerView recyclerView;

    private class MyAdapter extends Adapter<GenericViewHolder> {
        private static final int View_Type_Fav = 1;
        private static final int View_Type_SFav = 0;
        private ArrayList<Object> list = new ArrayList();
        private OnItemClickListener listener;
        private OnItemClickListener longlistener;

        public class FavoritoViewHolder extends GenericViewHolder {
            public View bt1;
            public View bt2;
            public View bt3;
            public TextView escritura;
            public TextView nota;

            public FavoritoViewHolder(View itemView) {
                super(itemView);
                this.escritura = (TextView) itemView.findViewById(R.id.item_c_hf_tv1);
                this.escritura.setTextSize(2, (float) Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(FavoritosFragment.this.getContext()).getString("list_preference", String.valueOf(FavoritosFragment.this.getResources().getInteger(R.integer.vers_size)))));

                this.nota = (TextView) itemView.findViewById(R.id.item_c_hf_tv2);
                this.nota.setTextSize(2, (float) Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(FavoritosFragment.this.getContext()).getString("list_preference", String.valueOf(FavoritosFragment.this.getResources().getInteger(R.integer.vers_size)))));
                this.bt3 = itemView.findViewById(R.id.item_c_hf_bt3);
                this.bt2 = itemView.findViewById(R.id.item_c_hf_bt2);
                this.bt1 = itemView.findViewById(R.id.item_c_hf_bt1);
            }

            public void setDataOnView(int position) {
                final Favorito nodo = (Favorito) MyAdapter.this.list.get(position);
                this.escritura.setText(nodo.getEscritura(true));
                if (nodo.hasNota()) {
                    this.nota.setText(nodo.getNota());
                    this.nota.setVisibility(0);
                } else {
                    this.nota.setVisibility(8);
                }
                this.bt3.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Util.copiar(FavoritosFragment.this.getContext(), nodo.getTitulo(), nodo.getEscritura());
                    }
                });
                this.bt2.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Util.share(FavoritosFragment.this.getContext(), nodo.getEscritura(), nodo.getIdLibro(), nodo.getCapitulo(), nodo.getVersiculoI(), nodo.getVersiculoF());
                    }
                });
                this.bt1.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        FavoritosFragment.this.openMoreOptions(nodo, v);
                    }
                });
            }
        }

        public class SeparadorFavoritoViewHolder extends GenericViewHolder {
            public TextView name;

            public SeparadorFavoritoViewHolder(View itemView) {
                super(itemView);
                this.name = (TextView) itemView.findViewById(R.id.menu2_tv1);
            }

            public void setDataOnView(int position) {
                this.name.setText((String) MyAdapter.this.list.get(position));
            }
        }

        public MyAdapter(ArrayList<Object> list, OnItemClickListener listener, OnItemClickListener longlistener) {
            this.list = list;
            this.listener = listener;
            this.longlistener = longlistener;
        }

        public int getItemViewType(int position) {
            if (this.list.get(position) instanceof Favorito) {
                return View_Type_Fav;
            }
            return 0;
        }

        public GenericViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case View_Type_Fav /*1*/:
                    View vf = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_hom_fav, parent, false);
                    final GenericViewHolder fvh = new FavoritoViewHolder(vf);
                    vf.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            MyAdapter.this.listener.onItemClick(v, fvh.getAdapterPosition());
                        }
                    });
                    vf.setOnLongClickListener(new OnLongClickListener() {
                        public boolean onLongClick(View v) {
                            MyAdapter.this.longlistener.onItemClick(v, fvh.getAdapterPosition());
                            return true;
                        }
                    });
                    return fvh;
                default:
                    return new SeparadorFavoritoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu2, parent, false));
            }
        }

        public void onBindViewHolder(GenericViewHolder holder, int position) {
            holder.setDataOnView(position);
        }

        public int getItemCount() {
            return this.list.size();
        }
    }

    private class TareaLoad extends AsyncTask<Void, Void, Void> {
        private DatabaseHelper helper;

        private TareaLoad() {


            this.helper = DatabaseHelper.getLtHelper(FavoritosFragment.this.getActivity());



        }

        protected Void doInBackground(Void... params) {
            FavoritosFragment.this.list = this.helper.getFavoritos(getContext());
            return null;
        }

        protected void onPostExecute(Void bytes) {
            FavoritosFragment.this.recyclerView.setAdapter(new MyAdapter(FavoritosFragment.this.list, new OnItemClickListener() {
                public void onItemClick(View v, int position) {
                    Favorito nodo = (Favorito) FavoritosFragment.this.list.get(position);
                    Intent myIntent = new Intent(FavoritosFragment.this.getActivity(), LibroActivity.class);
                    myIntent.putExtra("libro", nodo.getIdLibro());
                    myIntent.putExtra("capitulo", nodo.getCapitulo());
                    myIntent.putExtra("versiculo", nodo.getVersiculoI());
                    FavoritosFragment.this.startActivity(myIntent);
                }
            }, new OnItemClickListener() {
                public void onItemClick(View v, int position) {
                    FavoritosFragment.this.openMoreOptions((Favorito) FavoritosFragment.this.list.get(position), v.findViewById(R.id.item_c_hf_bt3));
                }
            }));
        }
    }

    public static FavoritosFragment newInstance() {
        return new FavoritosFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View item = inflater.inflate(R.layout.list_main, container, false);
        this.recyclerView = (RecyclerView) item.findViewById(R.id.list_recycle);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadFavoritos();
        return item;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().registerReceiver(this.broadcastReceiver, new IntentFilter(action_favoritos));
    }

    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(this.broadcastReceiver);
    }

    public void loadFavoritos() {
        new TareaLoad().execute(new Void[0]);
    }

    public Favorito getFavorito(int position) {
        return (Favorito) this.list.get(position);
    }

    public void openMoreOptions(final Favorito nodo, View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v, 3);
        popup.getMenuInflater().inflate(R.menu.menu_fav_main, popup.getMenu());
        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_mod_fav /*2131624179*/:
                        View layout = ((LayoutInflater) FavoritosFragment.this.getActivity().getSystemService("layout_inflater")).inflate(R.layout.fav_dialog, null);
                        TextView fav_dig_tv1 = (TextView) layout.findViewById(R.id.fav_dig_tv1);
                        fav_dig_tv1.setText(nodo.getEscritura());
                        fav_dig_tv1.setVisibility(0);
                        final EditText fav_d_et1 = (EditText) layout.findViewById(R.id.fav_dig_et1);
                        fav_d_et1.setText(nodo.getNota());
                        ((Button) layout.findViewById(R.id.fav_dig_b1)).setOnClickListener(new OnClickListener() {
                            public void onClick(View arg0) {
                                fav_d_et1.setText(BuildConfig.FLAVOR);
                            }
                        });
                        new Builder(FavoritosFragment.this.getActivity()).setTitle("Modificar Nota").setView(layout).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseHelper.getLtHelper(FavoritosFragment.this.getActivity().getApplicationContext()).modiFavorito(nodo.getIdFavorito(), fav_d_et1.getText().toString(),getContext());
                                FavoritosFragment.this.loadFavoritos();
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                        return true;
                    case R.id.menu_quit_fav /*2131624180*/:
                        new Builder(FavoritosFragment.this.getActivity()).setMessage("Desea quitar " + nodo.getTitulo() + " de sus favoritos?").setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseHelper.getLtHelper(FavoritosFragment.this.getActivity()).delFavorito(nodo.getIdFavorito(),getContext());
                                FavoritosFragment.this.loadFavoritos();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }
}
