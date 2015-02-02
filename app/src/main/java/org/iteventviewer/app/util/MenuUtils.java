package org.iteventviewer.app.util;

import android.support.v4.app.Fragment;
import java.util.ArrayList;
import java.util.List;
import org.iteventviewer.app.R;
import org.iteventviewer.app.main.IndexFragment;
import org.iteventviewer.app.main.PersonalSettingsFragment;
import org.iteventviewer.app.main.SettingsFragment;
import org.iteventviewer.model.DrawerMenu;

/**
 * Created by yuki_yoshida on 15/01/20.
 */
public class MenuUtils {

  public static List<DrawerMenu> createDrawerMenu() {
    List<DrawerMenu> menuList = new ArrayList<>();

    menuList.add(new DrawerMenu(R.drawable.ic_face_black_24dp, R.string.menu_index));
    menuList.add(new DrawerMenu(R.drawable.ic_settings_black_24dp, R.string.menu_personal_settings));
    menuList.add(
        new DrawerMenu(R.drawable.ic_settings_black_24dp, R.string.menu_settings));

    return menuList;
  }

  public static Fragment createDrawerMenuFragment(DrawerMenu menu) {

    switch (menu.getTitleResId()) {
      case R.string.menu_index:
        return IndexFragment.newInstance();
      case R.string.menu_personal_settings:
        return PersonalSettingsFragment.newInstance();
      case R.string.menu_settings:
        return SettingsFragment.newInstance();
      default:
        throw new IllegalArgumentException("menu.titleResId");
    }
  }
}
