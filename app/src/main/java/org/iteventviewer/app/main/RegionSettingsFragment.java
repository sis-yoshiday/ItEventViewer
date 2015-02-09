package org.iteventviewer.app.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.collect.Lists;
import jp.yokomark.widget.compound.CompoundFrameLayout;
import jp.yokomark.widget.compound.CompoundViewGroup;
import jp.yokomark.widget.compound.OnCheckedChangeListener;
import org.iteventviewer.app.BaseFragment;
import org.iteventviewer.app.R;
import org.iteventviewer.util.PreferenceUtil;
import org.iteventviewer.util.Region;
import org.iteventviewer.common.BindableViewHolder;
import org.iteventviewer.common.SimpleRecyclerAdapter;

/**
 * 地域の登録
 *
 * Created by yuki_yoshida on 15/02/02.
 */
public class RegionSettingsFragment extends BaseFragment {

  @InjectView(R.id.recyclerView) RecyclerView recyclerView;

  RegionAdapter adapter;

  public static RegionSettingsFragment newInstance() {
    return new RegionSettingsFragment();
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_region_settings, container, false);
    ButterKnife.inject(this, view);

    recyclerView.setLayoutManager(
        new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

    adapter = new RegionAdapter(getActivity());
    adapter.setItems(Lists.newArrayList(Region.values()));
    recyclerView.setAdapter(adapter);

    return view;
  }

  @Override public void onDestroyView() {
    ButterKnife.reset(this);
    super.onDestroyView();
  }

  class RegionAdapter extends SimpleRecyclerAdapter<Region, BindableViewHolder> {

    public RegionAdapter(Context context) {
      super(context);
    }

    @Override protected View newView(ViewGroup viewGroup, int viewType) {
      return inflater.inflate(R.layout.item_region, viewGroup, false);
    }

    @Override protected BindableViewHolder newViewHolder(View view, int viewType) {
      return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(BindableViewHolder holder, int position) {
      holder.bind(position);
    }

    class ViewHolder extends BindableViewHolder implements OnCheckedChangeListener {

      @InjectView(R.id.container) CompoundFrameLayout container;
      @InjectView(R.id.region) TextView region;
      @InjectView(R.id.regionDetail) TextView regionDetail;

      public ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
      }

      @Override public void bind(int position) {

        final Region item = getItem(position);

        region.setText(item.getName());
        regionDetail.setText(item.toString(", "));

        container.setOnCheckedChangeListener(null);
        if (PreferenceUtil.getRegion(getActivity()) == item.getId()) {
          container.setChecked(true);
        } else {
          container.setChecked(false);
        }
        container.setOnCheckedChangeListener(this);
      }

      @Override public void onCheckedChanged(CompoundViewGroup view, boolean checked) {
        int oldId = PreferenceUtil.getRegion(getActivity());
        final int oldPosition = indexOf(oldId);
        if (checked) {
          PreferenceUtil.saveRegion(getActivity(), getItem(getPosition()).getId());
        } else {
          PreferenceUtil.saveRegion(getActivity(), 0);
        }
        new Handler().post(new Runnable() {
          @Override public void run() {
            if (oldPosition > -1) {
              notifyItemChanged(oldPosition);
            }
            notifyItemChanged(getPosition());
          }
        });
      }
    }

    public int indexOf(int regionId) {

      for (Region item : items) {
        if (regionId == item.getId()) {
          return items.indexOf(item);
        }
      }
      return -1;
    }
  }
}
