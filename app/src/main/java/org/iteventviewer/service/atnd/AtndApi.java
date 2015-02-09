package org.iteventviewer.service.atnd;

import java.util.Map;
import org.iteventviewer.service.atnd.json.AtndEvent;
import org.iteventviewer.service.atnd.json.AtndEventMember;
import org.iteventviewer.service.atnd.json.AtndSearchResult;
import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
public interface AtndApi {

  public static final String ENDPOINT = "http://api.atnd.org";

  @GET("/events") Observable<AtndSearchResult<AtndEvent>> searchEvent(
      @QueryMap Map<String, String> queryMap);

  @GET("/events/users") Observable<AtndSearchResult<AtndEventMember>> searchEventMember(
      @QueryMap Map<String, String> queryMap);
}
