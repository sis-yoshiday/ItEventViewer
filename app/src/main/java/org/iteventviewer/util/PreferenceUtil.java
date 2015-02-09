package org.iteventviewer.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yuki_yoshida on 15/02/08.
 */
public class PreferenceUtil {

  private static final String KEY_SELECTED_REGION = "selected_region";
  private static final String KEY_SELECTED_CATEGORY = "category";

  public static int getRegion(Context context) {
    return preferences(context).getInt(KEY_SELECTED_REGION, 0);
  }

  public static void saveRegion(Context context, int region) {
    preferences(context).edit().putInt(KEY_SELECTED_REGION, region).apply();
  }

  public static Set<String> getCategories(Context context) {
    return preferences(context).getStringSet(KEY_SELECTED_CATEGORY, new HashSet<String>());
  }

  public static void saveCategories(Context context, Set<String> categories) {
    preferences(context).edit().putStringSet(KEY_SELECTED_CATEGORY, categories).apply();
  }

  private static SharedPreferences preferences(Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context);
  }
}
