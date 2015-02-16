package org.iteventviewer.app.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import org.iteventviewer.app.BaseFragment;
import org.iteventviewer.app.LicenseActivity;
import org.iteventviewer.app.R;
import org.iteventviewer.app.TestActivity;
import org.iteventviewer.common.BindableViewHolder;
import org.iteventviewer.common.OnItemClickListener;
import org.iteventviewer.common.SimpleRecyclerAdapter;
import org.iteventviewer.model.Setting;
import org.iteventviewer.util.SettingsUtil;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by yuki_yoshida on 15/01/24.
 */
public class SettingsFragment extends BaseFragment {

  @InjectView(R.id.recyclerView) RecyclerView recyclerView;

  private SettingsAdapter adapter;

  public static SettingsFragment newInstance() {
    SettingsFragment fragment = new SettingsFragment();
    return fragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_settings, container, false);
    ButterKnife.inject(this, view);

    recyclerView.setLayoutManager(
        new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

    adapter = new SettingsAdapter(getActivity());
    recyclerView.setAdapter(adapter);

    Observable<Setting> settingStream = SettingsUtil.stream();
    settingStream.subscribe(new Action1<Setting>() {
      @Override public void call(Setting setting) {
        adapter.addItem(setting);
      }
    });

    adapter.setOnItemClickListener(new OnItemClickListener() {
      @Override public void onItemClick(View itemView, int position) {
        Setting item = adapter.getItem(position);

        switch (item.getTitleResId()) {
          case R.string.setting_checkbox:
            adapter.toggleCheck(position);
            break;
          case R.string.setting_privacy_policy:
            // TODO
            Toast.makeText(getActivity(), item.getTitleResId(), Toast.LENGTH_SHORT).show();
            break;
          case R.string.setting_do_review:
            toPlayStore();
            break;
          case R.string.setting_software_licences:
            LicenseActivity.launch(getActivity());
            break;
          case R.string.setting_test:
            TestActivity.launch(getActivity());
            break;
        }
      }
    });

    return view;
  }

  @Override public void onDestroyView() {
    ButterKnife.reset(this);
    super.onDestroyView();
  }

  private void toPlayStore() {

    String appPackageName = getActivity().getPackageName();
    try {
      view("market://details?id=" + appPackageName);
    } catch (android.content.ActivityNotFoundException anfe) {
      view("http://play.google.com/store/apps/details?id=" + appPackageName);
    }
  }

  private void view(String url) {
    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
  }

  class SettingsAdapter extends SimpleRecyclerAdapter<Setting, BindableViewHolder> {

    public SettingsAdapter(Context context) {
      super(context);
    }

    public void toggleCheck(int position) {
      getItem(position).toggleCheck();
      notifyItemChanged(position);
    }

    @Override public int getItemViewType(int position) {
      return getItem(position).getType();
    }

    @Override public BindableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      switch (viewType) {
        case Setting.TYPE_NORMAL:
          return new NormalViewHolder(inflater.inflate(R.layout.item_setting, parent, false));
        case Setting.TYPE_CHECKBOX:
          return new CheckboxViewHolder(
              inflater.inflate(R.layout.item_setting_checkbox, parent, false));
        default:
          throw new IllegalArgumentException("viewType");
      }
    }

    @Override public void onBindViewHolder(BindableViewHolder holder, int position) {
      holder.bind(position);
    }

    abstract class ClickableViewHolder extends BindableViewHolder implements View.OnClickListener {

      public ClickableViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
      }

      @Override public void onClick(View v) {
        if (onItemClickListener != null) {
          onItemClickListener.onItemClick(v, getPosition());
        }
      }
    }

    class NormalViewHolder extends ClickableViewHolder {

      @InjectView(R.id.title) TextView title;

      public NormalViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
      }

      @Override public void bind(int position) {
        Setting item = getItem(position);
        title.setText(item.getTitleResId());
      }
    }

    class CheckboxViewHolder extends ClickableViewHolder {

      @InjectView(R.id.title) TextView title;
      @InjectView(R.id.checkbox) SwitchCompat checkbox;

      public CheckboxViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
      }

      @Override public void bind(int position) {

        final Setting item = getItem(position);

        title.setText(item.getTitleResId());

        checkbox.setChecked(item.isChecked());
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Toast.makeText(context, "check : " + String.valueOf(isChecked), Toast.LENGTH_SHORT)
                .show();
          }
        });
      }
    }
  }
}
