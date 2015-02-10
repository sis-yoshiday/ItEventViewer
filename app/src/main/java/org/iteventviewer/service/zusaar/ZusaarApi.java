package org.iteventviewer.service.zusaar;

import java.util.Map;
import org.iteventviewer.service.compass.json.ConnpassSearchResult;
import org.iteventviewer.service.zusaar.json.ZusaarSearchResult;
import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by yuki_yoshida on 15/02/09.
 */
public interface ZusaarApi {

  public static final String ENDPOINT = "http://www.zusaar.com/api";

  @GET("/event/") Observable<ZusaarSearchResult> searchEvent(
      @QueryMap Map<String, String> queryMap);
}
