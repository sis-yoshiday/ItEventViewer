package org.iteventviewer.app.util;

import com.google.common.base.Joiner;
import lombok.Getter;

/**
 * Created by yuki_yoshida on 15/02/02.
 */
public enum Region {

  HOKKAIDO("北海道", new String[] { "北海道" }),
  TOHOKU("東北", new String[] { "青森県", "岩手県", "宮城県", "秋田県", "山形県", "福島県" }),
  KANTO("関東", new String[] { "茨城県", "栃木県", "群馬県", "埼玉県", "千葉県", "東京都", "神奈川県" }),
  HOKURIKU("北陸", new String[] { "新潟県", "富山県", "石川県", "福井県" }),
  CYUBU("中部", new String[] { "山梨県", "長野県", "岐阜県", "静岡県", "愛知県" }),
  KINKI("近畿", new String[] { "三重県", "滋賀県", "京都府", "大阪府", "兵庫県", "奈良県", "和歌山県" }),
  CYUGOKU("中国地方", new String[] { "鳥取県", "島根県", "岡山県", "広島県", "山口県" }),
  SHIKOKU("四国", new String[] { "徳島県", "香川県", "愛媛県", "高知県" }),
  KYUSYU("九州", new String[] { "福岡県", "佐賀県", "長崎県", "熊本県", "大分県", "宮崎県", "鹿児島県" }),
  OKINAWA("沖縄", new String[] { "沖縄県" });

  @Getter
  private String name;
  private String[] prefs;

  private Region(String name, String[] prefs) {
    this.name = name;
    this.prefs = prefs;
  }

  public String toQuery() {
    return Joiner.on(" ").join(prefs);
  }
}
