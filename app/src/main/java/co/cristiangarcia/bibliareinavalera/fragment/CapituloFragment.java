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
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import co.cristiangarcia.bibliareinavalera.BuildConfig;
import co.cristiangarcia.bibliareinavalera.R;
import co.cristiangarcia.bibliareinavalera.bd.DatabaseHelper;
import co.cristiangarcia.bibliareinavalera.node.Versiculo;
import co.cristiangarcia.bibliareinavalera.util.LibrosHelper;
import co.cristiangarcia.bibliareinavalera.util.Util;

public class CapituloFragment extends Fragment {
    public static final String action_update_versiculos = "co.cristiangarcia.bibliareinavalera.CapituloFragment.action_update_versiculos";
    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private int capitulo;
    private GestureDetectorCompat gestureDetector;
    private int libro;
    private ArrayList<Versiculo> list = new ArrayList();
    private CustomRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private BroadcastReceiver mReceiver;
    private RecyclerView mRecyclerView;
    private ProgressBar pb1 = null;
    private int versiculo;

    private class ActionModeCallback implements Callback {
        private ActionModeCallback() {
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_share /*2131624173*/:
                    Util.share(CapituloFragment.this.getContext(), CapituloFragment.this.mAdapter.getFormatVersiculo(), CapituloFragment.this.libro, CapituloFragment.this.capitulo, CapituloFragment.this.mAdapter.getSelectedVersiculoInc(), CapituloFragment.this.mAdapter.getSelectedVersiculoFin());
                    CapituloFragment.this.actionMode.finish();
                    return true;
                case R.id.menu_fav /*2131624181*/:
                    View layout = ((LayoutInflater) CapituloFragment.this.getActivity().getSystemService("layout_inflater")).inflate(R.layout.fav_dialog, null);
                    TextView fav_dig_tv1 = (TextView) layout.findViewById(R.id.fav_dig_tv1);
                    fav_dig_tv1.setText(CapituloFragment.this.mAdapter.getFormatVersiculo());
                    fav_dig_tv1.setVisibility(0);
                    final EditText fav_d_et1 = (EditText) layout.findViewById(R.id.fav_dig_et1);
                    Button fav_dig_b1 = (Button) layout.findViewById(R.id.fav_dig_b1);
                    final int vers1 = CapituloFragment.this.mAdapter.getSelectedVersiculoInc();
                    final int vers2 = CapituloFragment.this.mAdapter.getSelectedVersiculoFin();
                    fav_dig_b1.setOnClickListener(new OnClickListener() {
                        public void onClick(View arg0) {
                            fav_d_et1.setText(BuildConfig.FLAVOR);
                        }
                    });
                    new Builder(CapituloFragment.this.getActivity()).setTitle("Agregar Favorito").setView(layout).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseHelper.getLtHelper(CapituloFragment.this.getActivity()).addFavorito(CapituloFragment.this.libro, CapituloFragment.this.capitulo, vers1, vers2, fav_d_et1.getText().toString().trim(),getContext());
                            CapituloFragment.this.loadVersiculos();
                            CapituloFragment.this.getActivity().sendBroadcast(new Intent(FavoritosFragment.action_favoritos));
                        }
                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                    CapituloFragment.this.actionMode.finish();
                    return true;
                case R.id.menu_copi /*2131624182*/:
                    Util.copiar(CapituloFragment.this.getContext(), LibrosHelper.getTitleLibCaps(CapituloFragment.this.libro, CapituloFragment.this.capitulo, CapituloFragment.this.mAdapter.getSelectedVersiculoInc(), CapituloFragment.this.mAdapter.getSelectedVersiculoFin()), CapituloFragment.this.mAdapter.getFormatVersiculo());
                    CapituloFragment.this.actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_selec_vers, menu);
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
            CapituloFragment.this.actionMode = null;
            CapituloFragment.this.mAdapter.clearSelections();
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    }

    private class CustomRecyclerAdapter extends Adapter<RecyclerViewHolder> {
        private List<Versiculo> mData;
        private SparseBooleanArray selectedItems;

        private CustomRecyclerAdapter() {
            this.mData = Collections.emptyList();
            this.selectedItems = new SparseBooleanArray();
        }

        public void updateList(List<Versiculo> data) {
            this.mData = data;
            notifyDataSetChanged();
        }

        public int getItemCount() {
            return this.mData.size();
        }

        public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            return new RecyclerViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_vers, viewGroup, false));
        }

        public void onBindViewHolder(RecyclerViewHolder viewHolder, int position) {
            viewHolder.escritura.setTextSize(2, (float) Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(CapituloFragment.this.getContext()).getString("list_preference", String.valueOf(CapituloFragment.this.getContext().getResources().getInteger(R.integer.vers_size)))));
            viewHolder.escritura.setTypeface(Util.getTypeface(CapituloFragment.this.getContext()));
            if (this.selectedItems.get(position, false)) {
                viewHolder.escritura.setText(((Versiculo) this.mData.get(position)).getEscrituraSpanneada(CapituloFragment.this.getContext(), true, ((Versiculo) this.mData.get(position)).is_fav()));
            } else {
                viewHolder.escritura.setText(((Versiculo) this.mData.get(position)).getEscrituraSpanneada(CapituloFragment.this.getContext(), false, ((Versiculo) this.mData.get(position)).is_fav()));
            }
        }

        public int toggleSelection(int pos) {
            int s;
            int c;
            int i;
            if (!this.selectedItems.get(pos, false)) {
                if (this.selectedItems.size() == 0) {
                    this.selectedItems.put(pos, true);
                    notifyItemChanged(pos);
                }
                if (pos > this.selectedItems.keyAt(0)) {
                    s = this.selectedItems.keyAt(0) + 1;
                    c = 0;
                    for (i = s; i <= pos; i++) {
                        this.selectedItems.put(i, true);
                        c++;
                    }
                    notifyItemRangeChanged(s, c);
                } else if (pos < this.selectedItems.keyAt(0)) {
                    s = this.selectedItems.keyAt(0) - 1;
                    c = 0;
                    for (i = this.selectedItems.keyAt(0) - 1; i >= pos; i--) {
                        this.selectedItems.put(i, true);
                        c++;
                    }
                    notifyItemRangeChanged(s, c);
                }
            } else if (this.selectedItems.size() == 1) {
                this.selectedItems.delete(pos);
                notifyItemChanged(pos);
            } else {
                s = pos + 1;
                c = 0;
                for (i = s; i <= this.selectedItems.keyAt(this.selectedItems.size() - 1); i++) {
                    this.selectedItems.delete(i);
                    c++;
                }
                notifyItemRangeChanged(s, c);
            }
            return getSelectedItemCount();
        }

        public void clearSelections() {
            this.selectedItems.clear();
            notifyDataSetChanged();
        }

        public int getSelectedItemCount() {
            return this.selectedItems.size();
        }

        public int getSelectedVersiculoInc() {
            if (this.selectedItems.size() > 0) {
                return this.selectedItems.keyAt(0) + 1;
            }
            return 0;
        }

        public int getSelectedVersiculoFin() {
            if (this.selectedItems.size() > 0) {
                return this.selectedItems.keyAt(this.selectedItems.size() - 1) + 1;
            }
            return 0;
        }

        private String getFormatVersiculo() {
            int veri = getSelectedVersiculoInc();
            int verf = getSelectedVersiculoFin();
            if (veri == 0) {
                return BuildConfig.FLAVOR;
            }
            String titleLibCaps = LibrosHelper.getTitleLibCaps(CapituloFragment.this.libro, CapituloFragment.this.capitulo, veri, verf);
            String vers = BuildConfig.FLAVOR;
            for (int i = veri - 1; i < verf; i++) {
                Versiculo vs = (Versiculo) this.mData.get(i);
                vers = vers.equals(BuildConfig.FLAVOR) ? vs.getEscritura() : vers + " " + vs.getEscritura();
            }
            return vers + "\n" + titleLibCaps;
        }
    }

    private class GetVersiculos extends AsyncTask<Void, Void, Void> {
        private GetVersiculos() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            CapituloFragment.this.pb1.setVisibility(0);
        }

        protected Void doInBackground(Void... arg0) {
            CapituloFragment.this.list.clear();
            CapituloFragment.this.list = DatabaseHelper.getLtHelper(CapituloFragment.this.getContext()).getVersiculos(CapituloFragment.this.libro, CapituloFragment.this.capitulo,getContext());
            return null;
        }

        protected void onPostExecute(Void result) {
            if (!(CapituloFragment.this.mAdapter == null || CapituloFragment.this.list.isEmpty())) {
                CapituloFragment.this.mAdapter.updateList(CapituloFragment.this.list);
                if (CapituloFragment.this.versiculo != 0) {
                    CapituloFragment.this.mRecyclerView.scrollToPosition(CapituloFragment.this.versiculo - 1);
                }
            }
            CapituloFragment.this.pb1.setVisibility(8);
        }
    }

    private class RecyclerViewHolder extends ViewHolder {
        public TextView escritura;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            this.escritura = (TextView) itemView.findViewById(R.id.item_v_tv1);
        }
    }

    private class RecyclerViewOnGestureListener extends SimpleOnGestureListener {
        private RecyclerViewOnGestureListener() {
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (CapituloFragment.this.actionMode == null) {
                CapituloFragment.this.actionMode = ((AppCompatActivity) CapituloFragment.this.getActivity()).startSupportActionMode(CapituloFragment.this.actionModeCallback);
            }
            CapituloFragment.this.myToggleSelection(CapituloFragment.this.mRecyclerView.getChildAdapterPosition(CapituloFragment.this.mRecyclerView.findChildViewUnder(e.getX(), e.getY())));
            return super.onSingleTapConfirmed(e);
        }

        public void onLongPress(MotionEvent e) {
            onSingleTapConfirmed(e);
            super.onLongPress(e);
        }
    }

    public static CapituloFragment newInstance(int libro, int capitulo, int versiculo) {
        CapituloFragment f = new CapituloFragment();
        Bundle args = new Bundle();
        args.putInt("libro", libro);
        args.putInt("capitulo", capitulo);
        args.putInt("versiculo", versiculo);
        f.setArguments(args);
        return f;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.libro = getArguments().getInt("libro");
        this.capitulo = getArguments().getInt("capitulo");
        this.versiculo = getArguments().getInt("versiculo");
        this.gestureDetector = new GestureDetectorCompat(getContext(), new RecyclerViewOnGestureListener());
    }

    public void onResume() {
        super.onResume();
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (CapituloFragment.this.mAdapter != null) {
                    CapituloFragment.this.mAdapter.notifyDataSetChanged();
                }
            }
        };
        getActivity().registerReceiver(this.mReceiver, new IntentFilter(action_update_versiculos));
    }

    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.mReceiver);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_capitulo, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        this.pb1 = (ProgressBar) view.findViewById(R.id.frag_publi_pb1);
        this.mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        this.mRecyclerView.setHasFixedSize(true);
        this.mLayoutManager = new LinearLayoutManager(getContext());
        this.mRecyclerView.setLayoutManager(this.mLayoutManager);
        this.mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.mAdapter = new CustomRecyclerAdapter();
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.mRecyclerView.addOnItemTouchListener(new OnItemTouchListener() {
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                CapituloFragment.this.gestureDetector.onTouchEvent(e);
                return false;
            }

            public void onRequestDisallowInterceptTouchEvent(boolean b) {
            }

            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }
        });
        loadVersiculos();
    }

    public void loadVersiculos() {
        new GetVersiculos().execute(new Void[0]);
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (!isVisibleToUser && this.actionMode != null) {
            this.actionMode.finish();
        }
    }

    private void myToggleSelection(int idx) {
        if (this.mAdapter != null) {
            int items = this.mAdapter.toggleSelection(idx);
            if (this.actionMode == null) {
                return;
            }
            if (items > 0) {
                this.actionMode.setTitle(LibrosHelper.getTitleLibCaps(this.libro, this.capitulo, this.mAdapter.getSelectedVersiculoInc(), this.mAdapter.getSelectedVersiculoFin()));
            } else {
                this.actionMode.finish();
            }
        }
    }

    public void refreshAdapter() {
        getCapitulo();
        if (this.mAdapter != null) {
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public void getCapitulo() {
        Log.e("error", "capitulo " + this.capitulo);
    }
}
