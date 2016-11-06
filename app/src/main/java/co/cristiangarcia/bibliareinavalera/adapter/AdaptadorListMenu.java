package co.cristiangarcia.bibliareinavalera.adapter;

import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import co.cristiangarcia.bibliareinavalera.R;
import co.cristiangarcia.bibliareinavalera.node.Libro;
import co.cristiangarcia.bibliareinavalera.util.OnItemClickListener;

public class AdaptadorListMenu extends Adapter<AdaptadorListMenu.ViewHolder> {
    private ArrayList<Libro> list = new ArrayList();
    private OnItemClickListener listener;

    public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public TextView name = ((TextView) this.itemView.findViewById(R.id.tvi_nomb));

        public ViewHolder(View v) {
            super(v);
        }
    }

    public AdaptadorListMenu(ArrayList<Libro> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AdaptadorListMenu.this.listener.onItemClick(v, vh.getAdapterPosition());
            }
        });
        return vh;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(((Libro) this.list.get(position)).getName());
    }

    public int getItemCount() {
        return this.list.size();
    }
}
