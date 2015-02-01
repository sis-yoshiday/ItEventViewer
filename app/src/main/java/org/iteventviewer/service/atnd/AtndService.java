package org.iteventviewer.service.atnd;

import java.util.Map;
import org.iteventviewer.service.atnd.json.Event;
import org.iteventviewer.service.atnd.json.EventMember;
import org.iteventviewer.service.atnd.json.SearchResult;
import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
public interface AtndService {

  public static final String ENDPOINT = "http://api.atnd.org";

  @GET("/events") Observable<SearchResult<Event>> searchEvent(
      @QueryMap Map<String, String> queryMap);

  @GET("/events/users") Observable<SearchResult<EventMember>> searchEventMember(
      @QueryMap Map<String, String> queryMap);
}
