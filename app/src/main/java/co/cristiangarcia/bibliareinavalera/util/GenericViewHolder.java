package co.cristiangarcia.bibliareinavalera.util;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

public abstract class GenericViewHolder extends ViewHolder {
    public abstract void setDataOnView(int i);

    public GenericViewHolder(View itemView) {
        super(itemView);
    }
}
