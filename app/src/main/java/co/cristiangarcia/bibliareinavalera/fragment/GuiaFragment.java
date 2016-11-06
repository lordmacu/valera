package co.cristiangarcia.bibliareinavalera.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import co.cristiangarcia.bibliareinavalera.R;
import co.cristiangarcia.bibliareinavalera.activity.GuiaActivity;
import co.cristiangarcia.bibliareinavalera.bd.DatabaseHelper;
import co.cristiangarcia.bibliareinavalera.node.Categoria;
import co.cristiangarcia.bibliareinavalera.util.OnItemClickListener;

public class GuiaFragment extends Fragment {
    private ArrayList<Categoria> list = new ArrayList();
    private RecyclerView recyclerView;

    private class MyAdapter extends Adapter<MyAdapter.ViewHolder> {
        private ArrayList<Categoria> list = new ArrayList();
        private OnItemClickListener listener;

        public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
            public TextView name = ((TextView) this.itemView.findViewById(R.id.tvi_nomb));

            public ViewHolder(View v) {
                super(v);
            }
        }

        public MyAdapter(ArrayList<Categoria> list, OnItemClickListener listener) {
            this.list = list;
            this.listener = listener;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
            final ViewHolder vh = new ViewHolder(v);
            v.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    MyAdapter.this.listener.onItemClick(v, vh.getAdapterPosition());
                }
            });
            return vh;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.name.setText(((Categoria) this.list.get(position)).getNombre());
        }

        public int getItemCount() {
            return this.list.size();
        }
    }

    private class TareaLoad extends AsyncTask<Void, Void, Void> {
        private DatabaseHelper helper;

        private TareaLoad() {
            this.helper = DatabaseHelper.getLtHelper(GuiaFragment.this.getActivity());
        }

        protected Void doInBackground(Void... params) {
            GuiaFragment.this.list.clear();
            GuiaFragment.this.list = this.helper.getCategorias(getContext());
            return null;
        }

        protected void onPostExecute(Void bytes) {
            GuiaFragment.this.recyclerView.setAdapter(new MyAdapter(GuiaFragment.this.list, new OnItemClickListener() {
                public void onItemClick(View v, int position) {
                    Categoria nodo = (Categoria) GuiaFragment.this.list.get(position);
                    Intent myIntent = new Intent(GuiaFragment.this.getActivity(), GuiaActivity.class);
                    myIntent.setFlags(67108864);
                    myIntent.putExtra("mv", nodo);
                    GuiaFragment.this.startActivity(myIntent);
                }
            }));
        }
    }

    public static GuiaFragment newInstance() {
        return new GuiaFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View item = inflater.inflate(R.layout.list_main, container, false);
        this.recyclerView = (RecyclerView) item.findViewById(R.id.list_recycle);
        this.recyclerView.setHasFixedSize(true);
        LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(mLayoutManager);
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this.recyclerView.getContext(), ((LinearLayoutManager) mLayoutManager).getOrientation()));
        loadGuia();
        return item;
    }

    public void loadGuia() {
        new TareaLoad().execute(new Void[0]);
    }
}
