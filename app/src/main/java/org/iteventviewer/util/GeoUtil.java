package org.iteventviewer.util;

import android.content.Intent;
import android.net.Uri;

/**
 * Created by yuki_yoshida on 15/02/14.
 */
public class GeoUtil {

  public static Intent intent(double lat, double lng, int zoom, String label) {

    return new Intent(Intent.ACTION_VIEW,
        Uri.parse(String.format("geo:%f,%f?z=%d&q=%f,%f(%s)", lat, lng, zoom, lat, lng, label)));
  }
}
