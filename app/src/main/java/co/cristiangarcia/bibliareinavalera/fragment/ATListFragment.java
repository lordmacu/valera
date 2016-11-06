package co.cristiangarcia.bibliareinavalera.fragment;

import android.content.Intent;
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
import co.cristiangarcia.bibliareinavalera.node.Libro;
import co.cristiangarcia.bibliareinavalera.util.LibrosHelper;
import co.cristiangarcia.bibliareinavalera.util.OnItemClickListener;

public class ATListFragment extends Fragment {
    private ArrayList<Libro> list = new ArrayList();
    private RecyclerView recyclerView;

    public static ATListFragment newInstance() {
        return new ATListFragment();
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
        this.list.clear();
        this.list = LibrosHelper.getLibrosAT();
        this.recyclerView.setAdapter(new AdaptadorListMenu(this.list, new OnItemClickListener() {
            public void onItemClick(View v, int position) {
                Libro nodo = (Libro) ATListFragment.this.list.get(position);
                Intent myIntent = new Intent(ATListFragment.this.getActivity(), LibroActivity.class);
                myIntent.setFlags(67108864);
                myIntent.putExtra("libro", nodo.getId());
                myIntent.putExtra("capitulo", 1);
                myIntent.putExtra("versiculo", 0);
                ATListFragment.this.startActivity(myIntent);
            }
        }));
    }
}
