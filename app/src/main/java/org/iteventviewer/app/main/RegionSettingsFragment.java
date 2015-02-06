package org.iteventviewer.app.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.gc.materialdesign.views.CheckBox;
import com.google.common.collect.Lists;
import org.iteventviewer.app.BaseFragment;
import org.iteventviewer.app.R;
import org.iteventviewer.app.util.Region;
import org.iteventviewer.common.BindableViewHolder;
import org.iteventviewer.common.SimpleRecyclerAdapter;

/**
 * 興味のある分野の登録
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

    private static final String KEY_SELECTED_REGION = "selected_region";

    private SharedPreferences sharedPreferences;

    public RegionAdapter(Context context) {
      super(context);
      sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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

    class ViewHolder extends BindableViewHolder {

      @InjectView(R.id.container) CardView cardView;
      @InjectView(R.id.region) TextView region;
      @InjectView(R.id.regionDetail) TextView regionDetail;
      @InjectView(R.id.checkbox) CheckBox checkBox;

      public ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
      }

      @Override public void bind(int position) {

        final Region item = getItem(position);

        region.setText(item.getName());
        regionDetail.setText(item.toString(", "));

        if (sharedPreferences.getInt(KEY_SELECTED_REGION, 0) > 0) {
          checkBox.setChecked(true);
        } else {
          checkBox.setChecked(false);
        }

        checkBox.setOncheckListener(new CheckBox.OnCheckListener() {
          @Override public void onCheck(boolean b) {
            sharedPreferences.edit().putInt(KEY_SELECTED_REGION, item.getId()).apply();
            notifyDataSetChanged();
          }
        });
      }
    }
  }
}
