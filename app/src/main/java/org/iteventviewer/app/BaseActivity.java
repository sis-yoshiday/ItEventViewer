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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import rx.android.app.support.RxActionBarActivity;

/**
 * Created by yuki_yoshida on 15/02/04.
 */
public abstract class BaseActivity extends RxActionBarActivity {

  protected abstract int contentView();

  protected Intent upIntent() {
    return NavUtils.getParentActivityIntent(this);
  }

  protected BaseActivity self = BaseActivity.this;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(contentView());
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case android.R.id.home:
        Intent intent = upIntent();
        if (intent != null) {
          NavUtils.navigateUpTo(this, intent);
          return true;
        }
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}
