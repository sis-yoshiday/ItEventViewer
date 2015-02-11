package org.iteventviewer.app;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by yuki_yoshida on 15/02/11.
 */
public abstract class BaseEventDetailActivity extends ToolBarActivity {

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_event_detail, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_share:
        Intent shareIntent = createShareIntent();
        if (shareIntent != null) {
          startActivity(Intent.createChooser(shareIntent, getString(R.string.msg_choose_apps)));
          return true;
        }
    }
    return super.onOptionsItemSelected(item);
  }

  protected Intent createShareIntent() {
    return null;
  }
}
