package org.iteventviewer.service.compass;

import java.util.Map;
import org.iteventviewer.service.compass.json.ConnpassSearchResult;
import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by yuki_yoshida on 15/02/09.
 */
public interface ConnpassApi {

  public static final String ENDPOINT = "http://connpass.com/api/v1";

  @GET("/event/") Observable<ConnpassSearchResult> searchEvent(
      @QueryMap Map<String, String> queryMap);
}
