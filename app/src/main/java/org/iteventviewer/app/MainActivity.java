package org.iteventviewer.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import org.iteventviewer.app.drawer.NavigationDrawerFragment;
import org.iteventviewer.app.drawer.SelectMenuEvent;
import org.iteventviewer.util.MenuUtils;
import org.iteventviewer.model.DrawerMenu;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends ToolBarActivity {

  @InjectView(R.id.drawer_layout) DrawerLayout drawerLayout;

  private NavigationDrawerFragment drawerFragment;

  private CompositeSubscription subscription = new CompositeSubscription();

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

    subscription.add(drawerFragment.observable().subscribe(new Action1<SelectMenuEvent>() {
      @Override public void call(SelectMenuEvent selectMenuEvent) {

        int position = selectMenuEvent.getPosition();

        DrawerMenu drawerMenu = drawerFragment.getDrawerMenu(position);
        Fragment fragment = MenuUtils.createDrawerMenuFragment(drawerMenu);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

        setTitle(drawerMenu.getTitleResId());
      }
    }));
  }

  @Override protected void onDestroy() {
    subscription.clear();
    super.onDestroy();
  }

  @Override public void onBackPressed() {
    // ドロワーを開いてから終了させる
    if (drawerFragment.isDrawerOpen()) {
      drawerFragment.closeDrawer();
    } else {
      super.onBackPressed();
    }
  }
}
