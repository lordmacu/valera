package co.cristiangarcia.bibliareinavalera.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import co.cristiangarcia.bibliareinavalera.R;
import co.cristiangarcia.bibliareinavalera.activity.LibroActivity;
import co.cristiangarcia.bibliareinavalera.adapter.AdaptadorListMenu;
import co.cristiangarcia.bibliareinavalera.bd.DatabaseHelper;
import co.cristiangarcia.bibliareinavalera.node.Libro;
import co.cristiangarcia.bibliareinavalera.util.OnItemClickListener;

public class FrecListFragment extends Fragment {
    public static final String action_frecuente = "co.cristiangarcia.bibliareinavalera.fragment.FrecListFragment.action_up_frecuente";
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            FrecListFragment.this.loadLibros();
        }
    };
    private ArrayList<Libro> list = new ArrayList();
    private RecyclerView recyclerView;

    private class TareaLoad extends AsyncTask<Void, Void, Void> {
        private DatabaseHelper helper;

        private TareaLoad() {
            this.helper = DatabaseHelper.getLtHelper(FrecListFragment.this.getActivity());
        }

        protected Void doInBackground(Void... params) {
            FrecListFragment.this.list.clear();
            FrecListFragment.this.list = this.helper.getLibrosFrecuentes(getContext());
            return null;
        }

        protected void onPostExecute(Void bytes) {
            FrecListFragment.this.recyclerView.setAdapter(new AdaptadorListMenu(FrecListFragment.this.list, new OnItemClickListener() {
                public void onItemClick(View v, int position) {
                    Libro nodo = (Libro) FrecListFragment.this.list.get(position);
                    Intent myIntent = new Intent(FrecListFragment.this.getActivity(), LibroActivity.class);
                    myIntent.setFlags(67108864);
                    myIntent.putExtra("libro", nodo.getId());
                    myIntent.putExtra("capitulo", 1);
                    myIntent.putExtra("versiculo", 0);
                    FrecListFragment.this.startActivity(myIntent);
                }
            }));
        }
    }

    public static FrecListFragment newInstance() {
        return new FrecListFragment();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().registerReceiver(this.broadcastReceiver, new IntentFilter(action_frecuente));
    }

    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(this.broadcastReceiver);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View item = inflater.inflate(R.layout.list_main, container, false);
        this.recyclerView = (RecyclerView) item.findViewById(R.id.list_recycle);
        this.recyclerView.setHasFixedSize(true);
        LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(mLayoutManager);
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this.recyclerView.getContext(), ((LinearLayoutManager) mLayoutManager).getOrientation()));
        loadLibros();
        return item;
    }

    public void loadLibros() {
        new TareaLoad().execute(new Void[0]);
    }
}
