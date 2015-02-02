package org.iteventviewer.app.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.iteventviewer.app.BaseFragment;
import org.iteventviewer.app.R;

/**
 * 興味のある分野の登録
 * 地域の登録
 *
 * Created by yuki_yoshida on 15/02/02.
 */
public class PersonalSettingsFragment extends BaseFragment {

  public static PersonalSettingsFragment newInstance() {
    return new PersonalSettingsFragment();
  }

  @OnClick(R.id.buttonRegion) void selectRegion(View view) {

  }

  @OnClick(R.id.buttonCategory) void selectCategory(View view) {

  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_personal_settings, container, false);
    ButterKnife.inject(this, view);

    return view;
  }

  @Override public void onDestroyView() {
    ButterKnife.reset(this);
    super.onDestroyView();
  }
}
