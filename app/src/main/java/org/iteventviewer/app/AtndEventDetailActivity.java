package org.iteventviewer.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.iteventviewer.common.BindableViewHolder;
import org.iteventviewer.common.SimpleRecyclerAdapter;
import org.iteventviewer.service.atnd.AtndApi;
import org.iteventviewer.service.atnd.AtndEventSearchQuery;
import org.iteventviewer.service.atnd.AtndMemberSearchQuery;
import org.iteventviewer.service.atnd.json.AtndEvent;
import org.iteventviewer.service.atnd.json.AtndEventMember;
import org.iteventviewer.service.atnd.json.AtndSearchResult;
import org.iteventviewer.service.atnd.json.AtndUser;
import org.iteventviewer.service.atnd.model.AtndEventDetailViewModel;
import org.iteventviewer.util.SnsUtil;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * TODO デザイン
 * TODO twitterへのリンク
 * TODO 100名以上参加者がいる場合のページネーション
 */
public class AtndEventDetailActivity extends BaseEventDetailActivity {

  public static final String EXTRA_EVENT = "event";

  @Inject AtndApi atndApi;

  @InjectView(R.id.recyclerView) RecyclerView recyclerView;

  EventDetailAdapter adapter;

  private Observable<AtndSearchResult<AtndEventMember>> currentResultObservable;

  private CompositeSubscription subscription = new CompositeSubscription();

  private AtndEvent event;

  public static void launch(Context context, AtndEvent event) {

    Intent intent = new Intent(context, AtndEventDetailActivity.class);
    intent.putExtra(EXTRA_EVENT, event);
    context.startActivity(intent);
  }

  /* activity lifecycle */

  @Override protected Intent createShareIntent() {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_TEXT, event.getEventUrl());
    return shareIntent;
  }

  @Override protected String title() {
    return getString(R.string.title_event_detail);
  }

  @Override protected int contentView() {
    return R.layout.activity_atnd_event_detail;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.inject(this);
    ((MyApplication) getApplication()).inject(this);

    event = (AtndEvent) getIntent().getSerializableExtra(EXTRA_EVENT);

    LinearLayoutManager layoutManager =
        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);

    adapter = new EventDetailAdapter(this);
    adapter.addItem(AtndEventDetailViewModel.detail(event));
    recyclerView.setAdapter(adapter);

    // 取得する件数
    final int searchCount = event.hasLimit() ? event.getMemberFetchCount() : 0;

    final Map<String, String> query =
        new AtndMemberSearchQuery.Builder().addEventId(event.getEventId())
            .count(searchCount)
            .build();

    currentResultObservable = atndApi.searchEventMember(query);

    subscription.add(AppObservable.bindActivity(this, currentResultObservable)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<AtndSearchResult<AtndEventMember>>() {
          @Override public void call(AtndSearchResult<AtndEventMember> result) {

            if (AtndEventSearchQuery.MAX_COUNT == result.getResultsReturned()) {
              // TODO 追加取得しないといけない
              currentResultObservable = atndApi.searchEventMember(AtndEventSearchQuery.next(query));
            } else {
              currentResultObservable = null;
            }

            Observable<AtndUser> userObservable =
                Observable.from(result.getEvents().get(0).getEvent().getUsers())
                    .map(new Func1<AtndEventMember.UserContainer, AtndUser>() {
                      @Override public AtndUser call(AtndEventMember.UserContainer userContainer) {
                        return userContainer.getUser();
                      }
                    });

            // 参加者
            adapter.addItem(AtndEventDetailViewModel.header(
                new AtndEventDetailViewModel.Header(getString(R.string.status_accepted),
                    event.getAccepted())));
            adapter.addItems(Lists.newArrayList(userObservable.filter(AtndUser.FILTER_ACCEPTED)
                .toSortedList(AtndUser.NAME_COMPARATOR)
                .flatMap(new Func1<List<AtndUser>, Observable<AtndUser>>() {
                  @Override public Observable<AtndUser> call(List<AtndUser> atndUsers) {
                    return Observable.from(atndUsers);
                  }
                })
                .map(new Func1<AtndUser, AtndEventDetailViewModel>() {
                  @Override public AtndEventDetailViewModel call(AtndUser atndUser) {
                    return AtndEventDetailViewModel.user(atndUser);
                  }
                })
                .toBlocking()
                .toIterable()));

            // キャンセル待ち
            adapter.addItem(AtndEventDetailViewModel.header(
                new AtndEventDetailViewModel.Header(getString(R.string.status_waiting),
                    event.getWaiting())));
            adapter.addItems(Lists.newArrayList(userObservable.filter(AtndUser.FILTER_WAITING)
                    .toSortedList(AtndUser.NAME_COMPARATOR)
                    .flatMap(new Func1<List<AtndUser>, Observable<AtndUser>>() {
                      @Override public Observable<AtndUser> call(List<AtndUser> atndUsers) {
                        return Observable.from(atndUsers);
                      }
                    })
                    .map(new Func1<AtndUser, AtndEventDetailViewModel>() {
                      @Override public AtndEventDetailViewModel call(AtndUser atndUser) {
                        return AtndEventDetailViewModel.user(atndUser);
                      }
                    })
                    .toBlocking()
                    .toIterable()));
          }
        }, new Action1<Throwable>() {
          @Override public void call(Throwable throwable) {

          }
        }));
  }

  @Override protected void onDestroy() {
    subscription.unsubscribe();
    super.onDestroy();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  class EventDetailAdapter
      extends SimpleRecyclerAdapter<AtndEventDetailViewModel, BindableViewHolder> {

    public EventDetailAdapter(Context context) {
      super(context);
    }

    @Override public int getItemViewType(int position) {
      return getItem(position).getType();
    }

    @Override protected View newView(ViewGroup viewGroup, int viewType) {
      switch (viewType) {
        case AtndEventDetailViewModel.TYPE_HEADER:
          return inflater.inflate(R.layout.item_event_header, viewGroup, false);
        case AtndEventDetailViewModel.TYPE_DETAIL:
          return inflater.inflate(R.layout.item_event_detail, viewGroup, false);
        case AtndEventDetailViewModel.TYPE_MEMBER:
          return inflater.inflate(R.layout.item_event_member, viewGroup, false);
        default:
          throw new IllegalArgumentException("viewType");
      }
    }

    @Override protected BindableViewHolder newViewHolder(View view, int viewType) {
      switch (viewType) {
        case AtndEventDetailViewModel.TYPE_HEADER:
          return new HeaderViewHolder(view);
        case AtndEventDetailViewModel.TYPE_DETAIL:
          return new DetailViewHolder(view);
        case AtndEventDetailViewModel.TYPE_MEMBER:
          return new MemberViewHolder(view);
        default:
          throw new IllegalArgumentException("viewType");
      }
    }

    @Override public void onBindViewHolder(BindableViewHolder holder, int position) {
      holder.bind(position);
    }

    class HeaderViewHolder extends BindableViewHolder {

      @InjectView(R.id.title) TextView title;
      @InjectView(R.id.count) TextView count;

      public HeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
      }

      @Override public void bind(int position) {

        AtndEventDetailViewModel.Header header = getItem(position).getHeader();
        title.setText(header.getTitle());
        count.setText(String.valueOf(header.getCount()));
      }
    }

    class DetailViewHolder extends BindableViewHolder {

      @InjectView(R.id.title) TextView title;
      @InjectView(R.id.owner) TextView owner;

      @InjectView(R.id.catchText) TextView catchText;
      @InjectView(R.id.description) TextView description;
      @InjectView(R.id.eventUrl) TextView eventUrlText;
      @InjectView(R.id.refUrl) TextView refUrlText;

      @InjectView(R.id.date) TextView date;

      @InjectView(R.id.stateNotEmptyContainer) ViewGroup statusNotEmptyContainer;
      @InjectView(R.id.stateEmpty) TextView statusEmpty;
      @InjectView(R.id.accepted) TextView accepted;
      @InjectView(R.id.limit) TextView limit;
      @InjectView(R.id.waiting) TextView waiting;

      @InjectView(R.id.addressAndPlace) TextView addressAndPlace;

      public DetailViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
      }

      @Override public void bind(int position) {

        final AtndEvent item = getItem(position).getEvent();

        // what
        title.setText(item.getTitle());
        // FIXME 動作してない
        owner.setText(item.getOwnerSpannableString(new ClickableSpan() {
          @Override public void onClick(View widget) {

            startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(SnsUtil.twitterUrlById(item.getOwnerTwitterId()))));
          }
        }));
        catchText.setText(item.getCatchText());
        description.setText(Html.fromHtml(item.getDescription()));

        eventUrlText.setText(item.getEventUrlText(self));
        refUrlText.setText(item.getRefUrlText(self));

        // when
        // TODO カレンダーへのリンク
        date.setText(item.getEventDateString());

        // how
        if (item.hasLimit()) {
          statusNotEmptyContainer.setVisibility(View.VISIBLE);
          statusEmpty.setVisibility(View.GONE);
          limit.setText(String.valueOf(item.getLimit()));
          accepted.setText(String.valueOf(item.getAccepted()));
          accepted.setTextColor(item.getAcceptedColor(self));
          waiting.setText(String.valueOf(item.getWaiting()));
          waiting.setTextColor(item.getWaitingColor(self));
        } else {
          statusNotEmptyContainer.setVisibility(View.GONE);
          statusEmpty.setVisibility(View.VISIBLE);
        }

        // where
        if (item.hasLocation()) {
          // FIXME 動作してない
          addressAndPlace.setText(item.getAddressAndPlaceSpannableString(self));
        } else {
          addressAndPlace.setText(item.getAddressAndPlaceString());
        }
      }
    }

    class MemberViewHolder extends BindableViewHolder {

      @InjectView(R.id.name) TextView name;

      public MemberViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
      }

      @Override public void bind(int position) {

        final AtndUser item = getItem(position).getUser();

        name.setText(item.getNameString());
      }
    }
  }
}
