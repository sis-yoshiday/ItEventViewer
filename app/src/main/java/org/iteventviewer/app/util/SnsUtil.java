package org.iteventviewer.app.util;

/**
 * Created by yuki_yoshida on 15/02/02.
 */
public class SnsUtil {

  public static String twitterUrlById(String twitterId) {
    return "https://twitter.com/intent/user?user_id=" + twitterId;
  }
}
