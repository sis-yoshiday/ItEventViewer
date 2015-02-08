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

package org.iteventviewer.app.drawer;

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
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;
import java.util.List;
import jp.yokomark.widget.compound.CompoundFrameLayout;
import org.iteventviewer.app.R;
import org.iteventviewer.app.util.MenuUtils;
import org.iteventviewer.common.SimpleRecyclerAdapter;
import org.iteventviewer.model.DrawerMenu;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.Subscriptions;

public class NavigationDrawerFragment extends Fragment {

  private static final String TAG = NavigationDrawerFragment.class.getSimpleName();

  private static final String PREFERENCES_FILE = "drawer_settings";

  private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

  private ActionBarDrawerToggle drawerToggle;

  private DrawerLayout drawerLayout;

  private View fragmentContainerView;

  private boolean userLearnedDrawer;

  private boolean fromSavedInstanceState;

  private NavigationDrawerAdapter adapter;

  @InjectView(R.id.recyclerView) RecyclerView recyclerView;

  @Icicle int selectedPosition = 0;

  private final BehaviorSubject<SelectMenuEvent> subject = BehaviorSubject.create();

  private Subscription subscription = Subscriptions.empty();

  public NavigationDrawerFragment() {
  }

  public Observable<SelectMenuEvent> observable() {
    return subject.asObservable();
  }

/* fragment lifecycle */

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Icepick.saveInstanceState(this, outState);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Icepick.restoreInstanceState(this, savedInstanceState);

    userLearnedDrawer =
        Boolean.valueOf(readSharedSetting(getActivity(), PREF_USER_LEARNED_DRAWER, "false"));
    if (savedInstanceState != null) {
      fromSavedInstanceState = true;
    }

    setHasOptionsMenu(true);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
    ButterKnife.inject(this, view);

    recyclerView.setLayoutManager(
        new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    recyclerView.setHasFixedSize(true);

    adapter = new NavigationDrawerAdapter(getActivity(), MenuUtils.createDrawerMenu());
    recyclerView.setAdapter(adapter);

    subscription = AppObservable.bindFragment(this, observable())
        .subscribe(new Action1<SelectMenuEvent>() {
          @Override public void call(SelectMenuEvent selectMenuEvent) {
            closeDrawer();
          }
        });

    return view;
  }

  @Override public void onDestroyView() {
    subscription.unsubscribe();
    ButterKnife.reset(this);
    super.onDestroyView();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    drawerToggle.onConfigurationChanged(newConfig);
  }

  public boolean isDrawerOpen() {
    return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
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

    subject.onNext(new SelectMenuEvent(null, selectedPosition, false));
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

  class NavigationDrawerAdapter
      extends SimpleRecyclerAdapter<DrawerMenu, NavigationDrawerAdapter.ViewHolder> {

    public NavigationDrawerAdapter(Context context, List<DrawerMenu> items) {
      super(context, items);
    }

    @Override protected View newView(ViewGroup viewGroup, int viewType) {
      return inflater.inflate(R.layout.item_drawer, viewGroup, false);
    }

    @Override protected ViewHolder newViewHolder(View view, int viewType) {
      return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(final ViewHolder vh, final int position) {

      final DrawerMenu item = getItem(position);

      vh.itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {

          boolean changed = selectedPosition != position;

          if (changed) {
            notifyItemChanged(selectedPosition);
            selectedPosition = position;
          }
          notifyItemChanged(position);

          subject.onNext(new SelectMenuEvent(v, position, !changed));
        }
      });

      vh.container.setChecked(selectedPosition == position);
      vh.icon.setImageResource(item.getIconResId());
      vh.title.setText(item.getTitleResId());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

      @InjectView(R.id.container) CompoundFrameLayout container;
      @InjectView(R.id.icon) ImageView icon;
      @InjectView(R.id.title) TextView title;

      public ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
      }
    }
  }
}
