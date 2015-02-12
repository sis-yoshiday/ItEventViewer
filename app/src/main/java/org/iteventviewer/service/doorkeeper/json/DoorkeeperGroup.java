package org.iteventviewer.service.doorkeeper.json;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.Getter;

/**
 * Created by yuki_yoshida on 15/02/13.
 */
@Getter
public class DoorkeeperGroup implements Serializable {

  private static final long serialVersionUID = -991974078779629632L;

  /**
   * グループID
   */
  private long id;

  /**
   * グループ名
   */
  private String name;

  /**
   * 国コード
   */
  @SerializedName("country_code") private String countryCode;

  /**
   * ロゴ画像URL
   */
  private String logo;

  /**
   * 概要（HTML）
   */
  private String description;

  /**
   * URL
   */
  @SerializedName("public_url") private String publicUrl;
}
