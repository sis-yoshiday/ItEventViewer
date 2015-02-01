package org.iteventviewer.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import butterknife.ButterKnife;
import butterknife.InjectView;
import org.iteventviewer.app.util.MenuUtils;
import org.iteventviewer.app.drawer.NavigationDrawerCallbacks;
import org.iteventviewer.app.drawer.NavigationDrawerFragment;
import org.iteventviewer.model.DrawerMenu;

public class MainActivity extends ToolBarActivity implements NavigationDrawerCallbacks {

  @InjectView(R.id.drawer_layout) DrawerLayout drawerLayout;

  private NavigationDrawerFragment drawerFragment;

  /* drawer callbacks */

  @Override public void onItemSelected(int position) {

    DrawerMenu drawerMenu = drawerFragment.getDrawerMenu(position);
    Fragment fragment = MenuUtils.createDrawerMenuFragment(drawerMenu);
    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

    toolbar.setTitle(drawerMenu.getTitleResId());
  }

  /* activity lifecycle */

  @Override protected String title() {
    return getString(R.string.app_name);
  }

  @Override protected int contentView() {
    return R.layout.activity_main;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.inject(this);

    drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(
        R.id.navigation_drawer);

    drawerFragment.setUp(R.id.navigation_drawer, drawerLayout);
  }

  @Override public void onBackPressed() {
    // ドロワーを開いてから終了させる
    if (drawerFragment.isDrawerOpen()) {
      drawerFragment.closeDrawer();
    } else {
      super.onBackPressed();
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case android.R.id.home:
        drawerFragment.toggle();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
