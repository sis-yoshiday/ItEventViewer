package org.iteventviewer.app.drawer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;
import org.iteventviewer.app.R;
import org.iteventviewer.app.util.MenuUtils;
import org.iteventviewer.model.DrawerMenu;

public class NavigationDrawerFragment extends Fragment implements NavigationDrawerCallbacks {

  private static final String TAG = NavigationDrawerFragment.class.getSimpleName();

  private static final String PREFERENCES_FILE = "drawer_settings";

  private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

  private NavigationDrawerCallbacks callbacks;

  private ActionBarDrawerToggle drawerToggle;

  private DrawerLayout drawerLayout;

  private View fragmentContainerView;

  private boolean userLearnedDrawer;

  private boolean fromSavedInstanceState;

  private NavigationDrawerAdapter adapter;

  @InjectView(R.id.recyclerView) RecyclerView recyclerView;

  @Icicle int selectedPosition = 0;

  public NavigationDrawerFragment() {
  }

  /* drawer callbacks */

  @Override public void onItemSelected(int position) {

    selectItem(position);
  }

  /* fragment lifecycle */

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Icepick.saveInstanceState(this, outState);
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      callbacks = (NavigationDrawerCallbacks) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
    }
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Icepick.restoreInstanceState(this, savedInstanceState);

    Boolean.valueOf(readSharedSetting(getActivity(), PREF_USER_LEARNED_DRAWER, "false"));
    if (savedInstanceState != null) {
      fromSavedInstanceState = true;
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
    ButterKnife.inject(this, view);

    recyclerView.setLayoutManager(
        new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    recyclerView.setHasFixedSize(true);

    adapter = new NavigationDrawerAdapter(getActivity(), MenuUtils.createDrawerMenu());
    adapter.setNavigationDrawerCallbacks(this);
    recyclerView.setAdapter(adapter);

    return view;
  }

  @Override public void onDetach() {
    super.onDetach();
    callbacks = null;
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    drawerToggle.onConfigurationChanged(newConfig);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
  }

  public boolean isDrawerOpen() {
    return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
  }

  public void toggle() {
    if (isDrawerOpen()) {
      closeDrawer();
    } else {
      drawerLayout.openDrawer(fragmentContainerView);
    }
  }

  public void closeDrawer() {
    drawerLayout.closeDrawer(fragmentContainerView);
  }

  /**
   * Users of this fragment must call this method to set up the navigation drawer interactions.
   *
   * @param fragmentId The android:id of this fragment in its activity's layout.
   * @param drawerLayout The DrawerLayout containing this fragment's UI.
   */
  public void setUp(int fragmentId, DrawerLayout drawerLayout) {
    fragmentContainerView = getActivity().findViewById(fragmentId);

    this.drawerLayout = drawerLayout;
    drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

    drawerToggle =
        new ActionBarDrawerToggle(getActivity(), this.drawerLayout, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close) {
          @Override public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
            if (!isAdded()) {
              return;
            }

            getActivity().invalidateOptionsMenu();
          }

          @Override public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            if (!isAdded()) {
              return;
            }

            if (!userLearnedDrawer) {
              userLearnedDrawer = true;
              saveSharedSetting(getActivity(), PREF_USER_LEARNED_DRAWER, "true");
            }

            getActivity().invalidateOptionsMenu();
          }
        };

    if (!userLearnedDrawer && !fromSavedInstanceState) {
      drawerLayout.openDrawer(fragmentContainerView);
    }

    drawerLayout.post(new Runnable() {
      @Override public void run() {
        drawerToggle.syncState();
      }
    });
    drawerLayout.setDrawerListener(drawerToggle);

    selectItem(selectedPosition);
  }

  void selectItem(int position) {
    selectedPosition = position;
    if (drawerLayout != null) {
      drawerLayout.closeDrawer(fragmentContainerView);
    }
    if (callbacks != null) {
      callbacks.onItemSelected(position);
    }
    adapter.selectPosition(position);
  }

  public DrawerMenu getDrawerMenu(int position) {
    return adapter.getItem(position);
  }

  private static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
    SharedPreferences sp = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    sp.edit().putString(settingName, settingValue).apply();
  }

  private static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
    SharedPreferences sp = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    return sp.getString(settingName, defaultValue);
  }
}
