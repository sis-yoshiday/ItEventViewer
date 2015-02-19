package org.iteventviewer.service.zusaar;

import android.support.annotation.Nullable;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.iteventviewer.service.zusaar.json.ZusaarEvent;
import org.iteventviewer.service.zusaar.json.ZusaarSearchResult;
import org.iteventviewer.service.zusaar.model.ZusaarIndexViewModel;
import org.iteventviewer.util.Region;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * Created by yuki_yoshida on 15/02/20.
 */
public class ZusaarService {

  @Inject ZusaarApi api;

  public ZusaarService(ZusaarApi api) {
    this.api = api;
  }

  /**
   * FIXME : NOTE : 現状キーワード検索は使い物にならないのでクエリに含めない
   */
  public Observable<List<ZusaarIndexViewModel>> search(@Nullable final Region region,
      Set<String> categories) {

    return searchRecursive(region, categories, 1)
        .map(new Func1<ZusaarEvent, ZusaarIndexViewModel>() {
          @Override public ZusaarIndexViewModel call(ZusaarEvent zusaarEvent) {
            return new ZusaarIndexViewModel(zusaarEvent);
          }
        })
        .filter(ZusaarIndexViewModel.filter(region))
        .toList();
  }

  private Observable<ZusaarEvent> searchRecursive(@Nullable final Region region,
      final Set<String> categories, final int page) {

    final int count = ZusaarEventSearchQuery.MAX_COUNT;

    // NOTE : Zusaarのkeyword, keyword_orはcase-sensitiveなので1文字目を大文字にしたやつとかを無理やり入れる
    final Set<String> newCategories = Sets.newHashSet(
        Observable.from(categories).flatMap(new Func1<String, Observable<String>>() {
          @Override public Observable<String> call(String s) {
            return Observable.from(
                Sets.newHashSet(s, CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, s),
                    CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, s)));
          }
        }).toList().toBlocking().first());

    // 検索クエリを生成
    Map<String, String> query = new ZusaarEventSearchQuery.Builder().addKeywordsOr(newCategories)
        .addYmds(30)
        .start(1 + count * (page - 1))
        .count(count)
        .build();

    return api.searchEvent(query)
        .flatMap(new Func1<ZusaarSearchResult, Observable<ZusaarEvent>>() {
          @Override public Observable<ZusaarEvent> call(ZusaarSearchResult searchResult) {
            Observable<ZusaarEvent> next;
            if (searchResult.getResultsReturned() == ZusaarEventSearchQuery.MAX_COUNT) {
              next = searchRecursive(region, categories, page + 1);
              Timber.d("zusaar query has next items");
            } else {
              next = Observable.empty();
            }
            return Observable.from(searchResult.getEvents()).mergeWith(next);
          }
        });
  }
}
