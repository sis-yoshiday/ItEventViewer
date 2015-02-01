package org.iteventviewer.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by yuki_yoshida on 15/01/20.
 */
public abstract class BindableViewHolder extends RecyclerView.ViewHolder {

  public BindableViewHolder(View itemView) {
    super(itemView);
  }

  abstract public void bind(int position);
}
