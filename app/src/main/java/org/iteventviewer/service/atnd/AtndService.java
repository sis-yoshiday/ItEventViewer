package org.iteventviewer.service.atnd;

import android.support.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.iteventviewer.service.atnd.json.AtndEvent;
import org.iteventviewer.service.atnd.json.AtndSearchResult;
import org.iteventviewer.service.atnd.model.AtndIndexViewModel;
import org.iteventviewer.util.Region;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by yuki_yoshida on 15/02/14.
 */
public class AtndService {

  private AtndApi api;

  public AtndService(AtndApi api) {
    this.api = api;
  }

  public Observable<List<AtndIndexViewModel>> search(@Nullable final Region region,
      final Set<String> categories) {

    return searchRecursive(region, categories, 1).map(
        new Func1<AtndSearchResult.EventContainer<AtndEvent>, AtndIndexViewModel>() {
          @Override public AtndIndexViewModel call(
              AtndSearchResult.EventContainer<AtndEvent> eventContainer) {
            return new AtndIndexViewModel(eventContainer.getEvent());
          }
        }).filter(AtndIndexViewModel.filter(region)).toList();
  }

  private Observable<AtndSearchResult.EventContainer<AtndEvent>> searchRecursive(
      @Nullable final Region region, final Set<String> categories, final int page) {

    final int count = AtndEventSearchQuery.MAX_COUNT;

    // 検索クエリを生成
    Map<String, String> query = new AtndEventSearchQuery.Builder().addKeywordsOr(categories)
        .addYmds(30)
        .start(1 + count * (page - 1))
        .count(count)
        .build();

    return api.searchEvent(query)
        .flatMap(
            new Func1<AtndSearchResult<AtndEvent>, Observable<AtndSearchResult.EventContainer<AtndEvent>>>() {
              @Override public Observable<AtndSearchResult.EventContainer<AtndEvent>> call(
                  AtndSearchResult<AtndEvent> result) {
                Observable<AtndSearchResult.EventContainer<AtndEvent>> next;
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
