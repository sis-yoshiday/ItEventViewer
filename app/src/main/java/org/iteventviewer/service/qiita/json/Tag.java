package org.iteventviewer.service.qiita.json;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by yuki_yoshida on 15/02/02.
 */
@Getter @Setter
public class Tag {

  @SerializedName("follower_count") int followerCount;

  @SerializedName("icon_url") String iconUrl;

  String id;

  @SerializedName("item_count") int itemCount;
}
