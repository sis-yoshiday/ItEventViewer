package org.iteventviewer.service;

import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.iteventviewer.model.IndexViewModel;
import org.iteventviewer.service.atnd.AtndService;
import org.iteventviewer.service.compass.ConnpassService;
import org.iteventviewer.service.doorkeeper.DoorkeeperService;
import org.iteventviewer.service.zusaar.ZusaarService;
import org.iteventviewer.util.Region;
import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by yuki_yoshida on 15/02/20.
 */
public class EventSearcher {

  @Inject AtndService atndService;
  @Inject ConnpassService connpassService;
  @Inject ZusaarService zusaarService;
  @Inject DoorkeeperService doorkeeperService;

  BehaviorSubject<List<? extends IndexViewModel>> subject;

  public void invalidate() {
    subject = null;
  }

  public Observable<List<? extends IndexViewModel>> search(Region region, Set<String> categories) {

    if (subject == null) {
      subject = BehaviorSubject.create();

      atndService.search(region, categories).subscribe(subject);
      connpassService.search(region, categories).subscribe(subject);
      zusaarService.search(region, categories).subscribe(subject);
      doorkeeperService.search(region, categories).subscribe(subject);
    }
    return subject;
  }
}
