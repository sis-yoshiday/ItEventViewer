package org.iteventviewer.util;

import android.support.annotation.Nullable;
import com.google.common.base.Joiner;
import lombok.Getter;

/**
 * Created by yuki_yoshida on 15/02/02.
 */
public enum Region {

  HOKKAIDO(1, "北海道", new String[] { "北海道" }),
  TOHOKU(2, "東北", new String[] { "青森", "岩手", "宮城", "秋田", "山形", "福島" }),
  KANTO(3, "関東", new String[] { "茨城", "栃木", "群馬", "埼玉", "千葉", "東京", "神奈川" }),
  HOKURIKU(4, "北陸", new String[] { "新潟", "富山", "石川", "福井" }),
  CYUBU(5, "中部", new String[] { "山梨", "長野", "岐阜", "静岡", "愛知" }),
  KINKI(6, "近畿", new String[] { "三重", "滋賀", "京都", "大阪", "兵庫", "奈良", "和歌山" }),
  CYUGOKU(7, "中国地方", new String[] { "鳥取", "島根", "岡山", "広島", "山口" }),
  SHIKOKU(8, "四国", new String[] { "徳島", "香川", "愛媛", "高知" }),
  KYUSYU(9, "九州", new String[] { "福岡", "佐賀", "長崎", "熊本", "大分", "宮崎", "鹿児島" }),
  OKINAWA(10, "沖縄", new String[] { "沖縄" });

  @Getter private int id;
  @Getter private String name;
  @Getter private String[] prefs;

  Region(int id, String name, String[] prefs) {
    this.id = id;
    this.name = name;
    this.prefs = prefs;
  }

  public String toString(String separator) {
    return Joiner.on(separator).join(prefs);
  }

  public static @Nullable Region byId(int id) {
    for (Region region : values()) {
      if (id == region.id) {
        return region;
      }
    }
    return null;
  }
}
