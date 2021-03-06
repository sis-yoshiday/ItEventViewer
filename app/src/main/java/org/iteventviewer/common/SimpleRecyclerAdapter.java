package org.iteventviewer.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import rx.Observable;

/**
 * Created by yuki_yoshida on 15/01/20.
 */
public abstract class SimpleRecyclerAdapter<T, VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> {

  protected Context context;

  protected LayoutInflater inflater;

  protected List<T> items;

  @Setter protected OnItemClickListener onItemClickListener;

  public SimpleRecyclerAdapter(Context context) {
    this(context, new ArrayList<T>());
  }

  public SimpleRecyclerAdapter(Context context, List<T> items) {
    this.context = context;
    inflater = LayoutInflater.from(context);
    this.items = items;
  }

  @Override public int getItemCount() {
    return items.size();
  }

  /* for item edit */

  public boolean isEmpty() {
    return items == null || items.isEmpty();
  }

  public T getItem(int position) {
    return items.get(position);
  }

  public void setItems(List<T> items) {
    this.items = items;
    notifyDataSetChanged();
  }

  public void addItem(T item) {
    items.add(item);
    notifyItemInserted(items.size() - 1);
  }

  public void addItem(T item, int insertPosition) {
    items.add(insertPosition, item);
    notifyItemInserted(insertPosition);
  }

  public void addItems(List<T> addItems) {
    int start = items.size() - 1;
    items.addAll(addItems);
    notifyItemRangeInserted(start, addItems.size());
  }

  public void addItems(List<T> addItems, int insertPosition) {
    items.addAll(insertPosition, addItems);
    notifyItemRangeInserted(insertPosition, addItems.size());
  }

  public void removeItem(T item) {
    int index = items.indexOf(item);
    items.remove(item);
    notifyItemRemoved(index);
  }

  public void removeItem(int position) {
    items.remove(position);
    notifyItemRemoved(position);
  }

  public void updateItem(T item, int position) {

    items.set(position, item);
    notifyItemChanged(position);
  }

  public Observable<T> items() {
    return Observable.from(items);
  }
}
