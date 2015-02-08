package org.iteventviewer.service.qiita.json;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import rx.functions.Func2;

/**
 * Created by yuki_yoshida on 15/02/02.
 */
@Getter @Setter
public class Tag {

  @SerializedName("follower_count") int followerCount;

  @SerializedName("icon_url") String iconUrl;

  String id;

  @SerializedName("item_count") int itemCount;

  public static final Func2<Tag, Tag, Integer> COMPARATOR_HOT = new Func2<Tag, Tag, Integer>() {
    @Override public Integer call(Tag tag, Tag tag2) {
      return compare(tag.itemCount, tag2.itemCount);
    }
  };

  private static int compare(int lhs, int rhs) {
    return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
  }
}
