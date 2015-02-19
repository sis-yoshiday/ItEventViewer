package org.iteventviewer.service.doorkeeper;

import android.support.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.iteventviewer.service.doorkeeper.json.DoorkeeperEventContainer;
import org.iteventviewer.service.doorkeeper.model.DoorkeeperIndexViewModel;
import org.iteventviewer.util.Region;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by yuki_yoshida on 15/02/20.
 */
public class DoorkeeperService {

  @Inject DoorkeeperApi api;

  public DoorkeeperService(DoorkeeperApi api) {
    this.api = api;
  }

  public Observable<List<DoorkeeperIndexViewModel>> search(@Nullable final Region region,
      Set<String> categories) {

    return searchRecuisive(region, categories, 1).map(
        new Func1<DoorkeeperEventContainer, DoorkeeperIndexViewModel>() {
          @Override public DoorkeeperIndexViewModel call(DoorkeeperEventContainer eventContainer) {
            return new DoorkeeperIndexViewModel(eventContainer.getEvent());
          }
        }).filter(DoorkeeperIndexViewModel.filter(region, categories)).toList();
  }

  private Observable<DoorkeeperEventContainer> searchRecuisive(@Nullable final Region region,
      final Set<String> categories, final int page) {

    // 検索クエリを生成
    Map<String, String> query =
        new DoorkeeperEventSearchQuery.Builder().locale(DoorkeeperEventSearchQuery.LOCALE_JA)
            .sort(DoorkeeperEventSearchQuery.SORT_STARTS_AT)
            .page(page)
            .build();

    return api.searchEvent(query)
        .flatMap(new Func1<List<DoorkeeperEventContainer>, Observable<DoorkeeperEventContainer>>() {
          @Override
          public Observable<DoorkeeperEventContainer> call(List<DoorkeeperEventContainer> result) {

            Observable<DoorkeeperEventContainer> next;
            if (result.isEmpty()) {
              next = Observable.empty();
            } else {
              next = searchRecuisive(region, categories, page + 1);
            }
            return Observable.from(result).mergeWith(next);
          }
        });
  }
}
