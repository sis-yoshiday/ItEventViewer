package org.iteventviewer.service.doorkeeper;

import java.util.List;
import java.util.Map;
import org.iteventviewer.service.doorkeeper.json.DoorkeeperEvent;
import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by yuki_yoshida on 15/02/13.
 */
public interface DoorkeeperApi {

  public static final String ENDPOINT = "http://api.doorkeeper.jp";

  @GET("/events")
  public Observable<List<DoorkeeperEvent>> searchEvent(@QueryMap Map<String, String> queryMap);
}
