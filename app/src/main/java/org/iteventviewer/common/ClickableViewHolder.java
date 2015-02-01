package org.iteventviewer.common;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by yuki_yoshida on 15/02/01.
 */
abstract public class ClickableViewHolder extends BindableViewHolder
    implements View.OnClickListener {

  private OnItemClickListener onItemClickListener;

  public ClickableViewHolder(View itemView, @NonNull OnItemClickListener listener) {
    super(itemView);
    this.onItemClickListener = listener;
    itemView.setOnClickListener(this);
  }

  @Override public void onClick(View v) {
    onItemClickListener.onItemClick(v, getPosition());
  }
}
