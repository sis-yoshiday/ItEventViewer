/*
 * Copyright 2015. Yuki YOSHIDA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.iteventviewer.app;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import lombok.Getter;

/**
 * Created by yuki_yoshida on 15/01/24.
 */
public abstract class ToolBarActivity extends BaseActivity {

  @Getter @InjectView(R.id.toolbar) Toolbar toolbar;

  protected String title() {
    return "";
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.inject(this);

    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    toolbar.setTitle(title());

    if (upIntent() != null) {
      toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
    } else {
      toolbar.setNavigationIcon(R.drawable.abc_ic_clear_mtrl_alpha);
    }
  }
}
