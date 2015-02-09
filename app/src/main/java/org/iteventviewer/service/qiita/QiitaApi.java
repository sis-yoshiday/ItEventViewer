package org.iteventviewer.service.qiita;

import java.util.List;
import org.iteventviewer.service.qiita.json.Tag;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by yuki_yoshida on 15/02/02.
 */
public interface QiitaApi {

  public static final String ENDPOINT = "https://qiita.com/api/v2";

  @GET("/users/{user_id}/following_tags")
  public Observable<List<Tag>> tags(@Header("Authorization") String token,
      @Path("user_id") String userId, @Query("page") int page, @Query("per_page") int parPage);
}
