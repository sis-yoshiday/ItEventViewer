package org.iteventviewer.service.atnd;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.iteventviewer.service.atnd.json.AtndEvent;
import org.iteventviewer.service.atnd.json.AtndSearchResult;
import org.iteventviewer.service.atnd.model.AtndIndexViewModel;
import org.iteventviewer.util.Region;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

/**
 * Created by yuki_yoshida on 15/02/14.
 */
public class AtndService {

  private AtndApi atndApi;

  private int count;

  private final BehaviorSubject<Observable<List<AtndIndexViewModel>>> nextQuerySubject = BehaviorSubject.create();

  public AtndService(AtndApi atndApi, int count) {
    this.atndApi = atndApi;
    this.count = count;
  }

  public Observable<Observable<List<AtndIndexViewModel>>> nextSearchStream() {
    return nextQuerySubject.asObservable();
  }

  public Observable<List<AtndIndexViewModel>> search(final Region region, final Set<String> categories, final int page) {

    // 検索クエリを生成
    Map<String, String> query = new AtndEventSearchQuery.Builder().addKeywordsOr(categories)
        .addYmds(30)
        .start(1 + (page - 1) * count)
        .count(count)
        .build();

    return atndApi.searchEvent(query)
        .flatMap(
            new Func1<AtndSearchResult, rx.Observable<AtndSearchResult.EventContainer<AtndEvent>>>() {

              @Override public Observable<AtndSearchResult.EventContainer<AtndEvent>> call(
                  AtndSearchResult searchResult) {
                if (searchResult.getResultsReturned() == count) {
                  nextQuerySubject.onNext(search(region, categories, page + 1));
                } else {
                  nextQuerySubject.onCompleted();
                }
                return Observable.from(searchResult.getEvents());
              }
            })
        .map(new Func1<AtndSearchResult.EventContainer<AtndEvent>, AtndIndexViewModel>() {
          @Override public AtndIndexViewModel call(
              AtndSearchResult.EventContainer<AtndEvent> eventContainer) {
            return new AtndIndexViewModel(eventContainer.getEvent());
          }
        })
        .filter(AtndIndexViewModel.filter(region))
        .toList();
  }
}
