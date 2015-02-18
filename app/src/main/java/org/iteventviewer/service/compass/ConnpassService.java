package org.iteventviewer.service.compass;

import android.support.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.iteventviewer.service.compass.json.ConnpassEvent;
import org.iteventviewer.service.compass.json.ConnpassSearchResult;
import org.iteventviewer.service.compass.model.ConnpassIndexViewModel;
import org.iteventviewer.util.Region;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by yuki_yoshida on 15/02/19.
 */
public class ConnpassService {

  private ConnpassApi connpassApi;

  public ConnpassService(ConnpassApi connpassApi) {
    this.connpassApi = connpassApi;
  }

  public Observable<List<ConnpassIndexViewModel>> search(@Nullable final Region region,
      final Set<String> categories) {

    return searchRecursive(region, categories, 1).map(
        new Func1<ConnpassEvent, ConnpassIndexViewModel>() {
          @Override public ConnpassIndexViewModel call(ConnpassEvent event) {
            return new ConnpassIndexViewModel(event);
          }
        }).filter(ConnpassIndexViewModel.filter(region)).toList();
  }

  private Observable<ConnpassEvent> searchRecursive(@Nullable final Region region,
      final Set<String> categories, final int page) {

    final int count = ConnpassEventSearchQuery.MAX_COUNT;

    // 検索クエリを生成
    Map<String, String> query = new ConnpassEventSearchQuery.Builder().addKeywordsOr(categories)
        .addYmds(30)
        .start(page + count * (page - 1))
        .count(count)
        .build();

    return connpassApi.searchEvent(query)
        .flatMap(new Func1<ConnpassSearchResult, Observable<ConnpassEvent>>() {
          @Override public Observable<ConnpassEvent> call(ConnpassSearchResult result) {
            Observable<ConnpassEvent> next;
            if (result.getResultsReturned() == count) {
              next = searchRecursive(region, categories, page + 1);
            } else {
              next = Observable.empty();
            }
            return Observable.from(result.getEvents()).mergeWith(next);
          }
        });
  }
}
