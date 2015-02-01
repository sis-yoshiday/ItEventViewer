package org.iteventviewer.app.drawer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import org.iteventviewer.common.BindableViewHolder;
import org.iteventviewer.common.SimpleRecyclerAdapter;
import org.iteventviewer.model.DrawerMenu;
import java.util.List;
import lombok.Setter;
import org.iteventviewer.app.R;

/**
 * Created by yuki_yoshida on 15/01/24.
 */
class NavigationDrawerAdapter extends SimpleRecyclerAdapter<DrawerMenu, BindableViewHolder> {

  @Setter private NavigationDrawerCallbacks navigationDrawerCallbacks;

  private int selectedPosition = -1;

  public NavigationDrawerAdapter(Context context, List<DrawerMenu> items) {
    super(context, items);
  }

  @Override protected View newView(ViewGroup viewGroup, int viewType) {
    return inflater.inflate(R.layout.item_drawer, viewGroup, false);
  }

  @Override protected BindableViewHolder newViewHolder(View view, int viewType) {
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(BindableViewHolder viewHolder, int i) {
    viewHolder.bind(i);
  }

  public void selectPosition(int position) {
    if (position == selectedPosition) {
      return;
    }
    notifyItemChanged(selectedPosition);
    selectedPosition = position;
    notifyItemChanged(position);
  }

  class ViewHolder extends BindableViewHolder {

    @InjectView(R.id.icon) ImageView icon;
    @InjectView(R.id.title) TextView title;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.inject(this, itemView);
    }

    public void bind(final int position) {

      final DrawerMenu item = getItem(position);

      itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {

          if (navigationDrawerCallbacks != null) {
            navigationDrawerCallbacks.onItemSelected(position);
            selectPosition(position);
          }
        }
      });

      itemView.setActivated(position == selectedPosition);

      icon.setImageResource(item.getIconResId());
      title.setText(item.getTitleResId());
    }
  }
}
