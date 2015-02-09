package org.iteventviewer.util;

import java.util.ArrayList;
import java.util.List;
import org.iteventviewer.app.R;
import org.iteventviewer.model.Setting;

/**
 * Created by yuki_yoshida on 15/01/30.
 */
public class SettingsUtil {

  public static List<Setting> load() {
    List<Setting> settings = new ArrayList<>();
    settings.add(new Setting(Setting.TYPE_CHECKBOX, R.string.setting_checkbox));
    settings.add(new Setting(Setting.TYPE_NORMAL, R.string.setting_privacy_policy));
    settings.add(new Setting(Setting.TYPE_NORMAL, R.string.setting_software_licences));
    return settings;
  }
}
