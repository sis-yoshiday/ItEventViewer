package org.iteventviewer.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.gc.materialdesign.views.ButtonFlat;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.iteventviewer.app.util.SnsUtil;
import org.iteventviewer.common.BindableViewHolder;
import org.iteventviewer.common.SimpleRecyclerAdapter;
import org.iteventviewer.service.atnd.AtndService;
import org.iteventviewer.service.atnd.EventDetailViewModel;
import org.iteventviewer.service.atnd.EventSearchQuery;
import org.iteventviewer.service.atnd.MemberSearchQuery;
import org.iteventviewer.service.atnd.json.Event;
import org.iteventviewer.service.atnd.json.EventMember;
import org.iteventviewer.service.atnd.json.SearchResult;
import org.iteventviewer.service.atnd.json.User;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.events.OnClickEvent;
import rx.android.observables.AndroidObservable;
import rx.android.observables.ViewObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

/**
 * TODO デザイン
 * TODO HTMLスニペットをどう表示させるか
 * TODO twitterへのリンク
 * TODO 100名以上参加者がいる場合のページネーション
 */
public class EventDetailActivity extends ToolBarActivity {

  public static final String EXTRA_EVENT = "event";

  private static final String TAG = EventDetailActivity.class.getSimpleName();

  @Inject AtndService atndService;

  @InjectView(R.id.recyclerView) RecyclerView recyclerView;

  EventDetailAdapter adapter;

  private Observable<SearchResult<EventMember>> currentResultObservable;

  private Subscription subscription = Subscriptions.empty();

  private Event event;

  public static void launch(Context context, Event event) {

    Intent intent = new Intent(context, EventDetailActivity.class);
    intent.putExtra(EXTRA_EVENT, event);
    context.startActivity(intent);
  }

  /* activity lifecycle */

  @Override protected String title() {
    return getString(R.string.app_name);
  }

  @Override protected int contentView() {
    return R.layout.activity_event_detail;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.inject(this);
    ((MyApplication) getApplication()).inject(this);

    event = (Event) getIntent().getSerializableExtra(EXTRA_EVENT);

    LinearLayoutManager layoutManager =
        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);

    adapter = new EventDetailAdapter(this);
    adapter.addItem(EventDetailViewModel.header(getString(R.string.event_detail)));
    adapter.addItem(EventDetailViewModel.detail(event));
    adapter.addItem(EventDetailViewModel.header(getString(R.string.event_user)));
    recyclerView.setAdapter(adapter);

    // 取得する件数
    final int searchCount = event.getMemberFetchCount();

    final Map<String, String> query =
        new MemberSearchQuery.Builder().addEventId(event.getEventId()).setCount(searchCount).build();

    currentResultObservable = atndService.searchEventMember(query);

    subscription = AndroidObservable.bindActivity(this, currentResultObservable)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<SearchResult<EventMember>>() {
          @Override public void call(SearchResult<EventMember> result) {

            if (EventSearchQuery.MAX_COUNT == result.getResultsReturned()) {
              // TODO 追加取得しないといけない
              currentResultObservable = atndService.searchEventMember(EventSearchQuery.next(query));
            } else {
              currentResultObservable = null;
            }

            Observable<User> userObservable =
                Observable.from(result.getEvents().get(0).getEvent().getUsers())
                    .map(new Func1<EventMember.UserContainer, User>() {
                      @Override public User call(EventMember.UserContainer userContainer) {
                        return userContainer.getUser();
                      }
                    });

            // 参加者をソート
            Observable<List<User>> acceptedMemberObservable =
                userObservable.filter(User.FILTER_ACCEPTED).toSortedList(User.NAME_COMPARATOR);

            // キャンセル待ちをソート
            Observable<List<User>> waitingMemberObservable =
                userObservable.filter(User.FILTER_WAITING).toSortedList(User.NAME_COMPARATOR);

            // マージして表示用モデルに
            Observable<List<EventDetailViewModel>> viewModelObservable =
                Observable.merge(acceptedMemberObservable, waitingMemberObservable)
                    .map(new Func1<List<User>, List<EventDetailViewModel>>() {
                      @Override public List<EventDetailViewModel> call(List<User> users) {
                        return Lists.newArrayList(Collections2.transform(users,
                            new Function<User, EventDetailViewModel>() {
                              @Override public EventDetailViewModel apply(User input) {
                                return EventDetailViewModel.user(input);
                              }
                            }));
                      }
                    });

            viewModelObservable.subscribe(new Subscriber<List<EventDetailViewModel>>() {
              @Override public void onCompleted() {
              }

              @Override public void onError(Throwable e) {
                Log.e(TAG, e.getMessage(), e);
                unsubscribe();
              }

              @Override public void onNext(List<EventDetailViewModel> eventDetailViewModels) {
                adapter.addItems(eventDetailViewModels);
              }
            });
          }
        }, new Action1<Throwable>() {
          @Override public void call(Throwable throwable) {

          }
        });
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

  class EventDetailAdapter extends SimpleRecyclerAdapter<EventDetailViewModel, BindableViewHolder> {

    public EventDetailAdapter(Context context) {
      super(context);
    }

    @Override public int getItemViewType(int position) {
      return getItem(position).getType();
    }

    @Override protected View newView(ViewGroup viewGroup, int viewType) {
      switch (viewType) {
        case EventDetailViewModel.TYPE_HEADER:
          return inflater.inflate(R.layout.item_event_header, viewGroup, false);
        case EventDetailViewModel.TYPE_DETAIL:
          return inflater.inflate(R.layout.item_event_detail, viewGroup, false);
        case EventDetailViewModel.TYPE_MEMBER:
          return inflater.inflate(R.layout.item_event_member, viewGroup, false);
        default:
          throw new IllegalArgumentException("viewType");
      }
    }

    @Override protected BindableViewHolder newViewHolder(View view, int viewType) {
      switch (viewType) {
        case EventDetailViewModel.TYPE_HEADER:
          return new HeaderViewHolder(view);
        case EventDetailViewModel.TYPE_DETAIL:
          return new DetailViewHolder(view);
        case EventDetailViewModel.TYPE_MEMBER:
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

      public HeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
      }

      @Override public void bind(int position) {

        title.setText(getItem(position).getTitle());
      }
    }

    class DetailViewHolder extends BindableViewHolder {

      @InjectView(R.id.title) TextView title;
      @InjectView(R.id.owner) TextView owner;

      @InjectView(R.id.catchText) TextView catchText;
      @InjectView(R.id.description) TextView description;
      @InjectView(R.id.eventUrl) ButtonFlat eventUrlButton;
      @InjectView(R.id.url) ButtonFlat urlButton;

      @InjectView(R.id.date) TextView date;

      @InjectView(R.id.accepted) TextView accepted;
      @InjectView(R.id.limit) TextView limit;
      @InjectView(R.id.waiting) TextView waiting;

      @InjectView(R.id.address) TextView address;
      @InjectView(R.id.place) TextView place;

      public DetailViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
      }

      @Override public void bind(int position) {

        Event item = getItem(position).getEvent();

        // what
        title.setText(item.getTitle());
        owner.setText(item.getOwnerString());
        catchText.setText(item.getCatchText());
        description.setText(Html.fromHtml(item.getDescription()));

        final String eventUrl = item.getEventUrl();
        eventUrlButton.setVisibility(TextUtils.isEmpty(eventUrl) ? View.GONE : View.VISIBLE);
        eventUrlButton.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(eventUrl)));
          }
        });

        final String url = item.getUrl();
        urlButton.setVisibility(TextUtils.isEmpty(url) ? View.GONE : View.VISIBLE);
        urlButton.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(url)));
          }
        });

        // when
        date.setText(item.getEventDateString());

        // how
        limit.setText(String.valueOf(item.getLimit()));
        accepted.setText(String.valueOf(item.getAccepted()));
        if (item.isLimitOver()) {
          waiting.setVisibility(View.VISIBLE);
          waiting.setText(String.valueOf(item.getWaiting()));
        } else {
          waiting.setVisibility(View.GONE);
        }

        // where
        address.setText(item.getAddress());
        place.setText(item.getPlace());
        item.getLat();
        item.getLng();
      }
    }

    class MemberViewHolder extends BindableViewHolder {

      @InjectView(R.id.name) TextView name;
      @InjectView(R.id.status) TextView status;
      @InjectView(R.id.twitter) ButtonFlat twitter;

      public MemberViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
      }

      @Override public void bind(int position) {

        final User item = getItem(position).getUser();

        name.setText(item.getNameString());

        status.setText(item.getStatusString());

        twitter.setVisibility(item.hasTwitterId() ? View.VISIBLE : View.GONE);
        ViewObservable.clicks(twitter).subscribe(new Action1<OnClickEvent>() {
          @Override public void call(OnClickEvent onClickEvent) {

            startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(SnsUtil.twitterUrlById(item.getTwitterId()))));
          }
        });
      }
    }
  }
}
