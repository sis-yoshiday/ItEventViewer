package org.iteventviewer.service.qiita;

import org.iteventviewer.service.qiita.json.Tag;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by yuki_yoshida on 15/02/02.
 */
public interface QiitaService {

  public static final String ENDPOINT = "https://qiita.com/api/v2";

  @GET("/tags")
  public Observable<Tag> tags(@Header("Authorization") String token, @Query("page") int page,
      @Query("per_page") int parPage);
}
