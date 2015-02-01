package org.iteventviewer.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import lombok.Getter;

/**
 * Created by yuki_yoshida on 15/01/24.
 */
public abstract class ToolBarActivity extends ActionBarActivity {

  @Getter @InjectView(R.id.toolbar) Toolbar toolbar;

  protected abstract int contentView();

  protected String title() {
    return "";
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(contentView());
    ButterKnife.inject(this);

    toolbar.setTitle(title());
    setSupportActionBar(toolbar);
    toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
  }
}
