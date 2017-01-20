package udacity.nanodegree.popularmovies.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.FixedPreloadSizeProvider;

import java.lang.annotation.Retention;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.palaima.debugdrawer.base.DebugModule;
import io.palaima.debugdrawer.commons.DeviceModule;
import io.palaima.debugdrawer.glide.GlideModule;
import io.palaima.debugdrawer.okhttp3.OkHttp3Module;
import io.palaima.debugdrawer.view.DebugView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import timber.log.Timber;
import udacity.nanodegree.popularmovies.R;
import udacity.nanodegree.popularmovies.adapter.MainAdapter;
import udacity.nanodegree.popularmovies.api.TMDbApi;
import udacity.nanodegree.popularmovies.api.models.MoviesResponse;
import udacity.nanodegree.popularmovies.model.Movie;
import udacity.nanodegree.popularmovies.services.sync.SyncJobService;
import udacity.nanodegree.popularmovies.ui.BaseActivity;
import udacity.nanodegree.popularmovies.utils.glide.GlideRequestBuilder;

import static io.reactivex.android.MainThreadDisposable.verifyMainThread;
import static java.lang.annotation.RetentionPolicy.SOURCE;

public class MainActivity extends BaseActivity
    implements MainAdapter.OnMovieInteractionListener, DrawerLayout.DrawerListener {

    @BindView(R.id.toolbar_activity_main)                Toolbar            toolbar;
    @BindView(R.id.swipeRefreshLayout_activity_main)     SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView_activity_main)           RecyclerView       recyclerView;
    @Nullable @BindView(R.id.drawerLayout_activity_main) DrawerLayout       drawerLayout;
    @Nullable @BindView(R.id.debugView_activity_main)    DebugView          debugView;

    @Inject TMDbApi               api;
    @Inject OkHttpClient          okHttpClient;
    @Inject MainActivityViewModel viewModel;

    // data
    private BehaviorProcessor<Integer>                 loadSubject            = BehaviorProcessor.create();
    private BehaviorProcessor<Integer>                 sortSubject            = BehaviorProcessor.create();
    private BehaviorProcessor<Boolean>                 loadingSubject         = BehaviorProcessor.create();
    private PublishProcessor<Response<MoviesResponse>> dataApiResponseSubject = PublishProcessor.create();
    private PublishProcessor<MoviesResponse>           dataSubject            = PublishProcessor.create();

    @Nullable private DebugModule[]     debugModules;

    private int totalPages = 1; // we assume that we have at least 1 page


    @Retention(SOURCE)
    @IntDef({SORTORDER_MOST_POPULAR, SORTORDER_TOP_RATED})
    public @interface SortOrder {}
    public static final int SORTORDER_MOST_POPULAR = 0;
    public static final int SORTORDER_TOP_RATED    = 1;

    @Override
    @SuppressWarnings("AndroidInjectionBeforeSuper")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setupFullscreen(toolbar);
        setupSwipeRefresh(swipeRefreshLayout);
        setTitle(null);

        if (drawerLayout != null) {
            drawerLayout.addDrawerListener(this);
        }

        if (debugView != null) {
            debugModules = new DebugModule[]{
                new OkHttp3Module(okHttpClient),
                new GlideModule(Glide.get(this)),
                new DeviceModule(this)
            };
            debugView.modules(debugModules);
        }

        final int spanCount = getResources().getInteger(R.integer.gallery_columns);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        recyclerView.setLayoutManager(layoutManager);

        final FooAdapter adapter =
            new FooAdapter(this, this, GlideRequestBuilder.preloadRequest(this));
        recyclerView.setAdapter(adapter);

        final FixedPreloadSizeProvider<Movie> preloadSizeProvider =
            new FixedPreloadSizeProvider<>(50, 75);
        final RecyclerViewPreloader<Movie> preloader =
            new RecyclerViewPreloader<>(this, adapter, preloadSizeProvider, 12);
        recyclerView.addOnScrollListener(preloader);

        viewModel.getData().observe(this, adapter::setList);

        createSyncJobs();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (debugView != null) {
            debugView.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isOnline()) {
            Snackbar
                .make(swipeRefreshLayout, R.string.sorry_offline, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_check_settings,
                           v -> startActivity(new Intent(Settings.ACTION_SETTINGS)))
                .show();
        }

        if (debugView != null) {
            debugView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (debugView != null) {
            debugView.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (debugView != null) {
            debugView.onStop();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // switch sort order
        switch (item.getItemId()) {
            case R.id.most_popular:
                sortSubject.onNext(SORTORDER_MOST_POPULAR);
                break;
            case R.id.top_rated:
                sortSubject.onNext(SORTORDER_TOP_RATED);
                break;
        }

        // set menu item
        item.setChecked(true);

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void bindObservables() {

    }

    private void foo() {

        // load data
        // create a Pair with
        // 1st item: page to load
        // 2nd item: sort order
        addToLifecycle(loadSubject.withLatestFrom(sortSubject, Pair::create)
                                  .distinctUntilChanged()
                                  .filter(pair -> pair.first <= totalPages)
                                  .flatMap(pair -> {
                                      loadingSubject.onNext(true);

                                      switch (pair.second) {
                                          case SORTORDER_TOP_RATED:
                                              return api.toprated(pair.first)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .doOnComplete(() -> loadingSubject.onNext(
                                                            false));
                                          case SORTORDER_MOST_POPULAR:
                                          default:
                                              return api.popular(pair.first)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .doOnComplete(() -> loadingSubject.onNext(
                                                            false));
                                      }
                                  })
                                  .subscribe(response -> dataApiResponseSubject.onNext(response),
                                             error -> Timber.e(error, "bindObservables: ")));

        // reset adapter etc and start loading from page 1 if sort order changes
        addToLifecycle(sortSubject
                           .distinctUntilChanged()
                           .subscribe(order -> {

                                          // reset adapter
//                                          viewModel.clear();
//                                          viewModel.setSortOrder(order);

                                          // reset total pages
                                          totalPages = 1;

                                          // start load from page 1
                                          loadSubject.onNext(1);
                                      },
                                      error -> Timber.e(error, "bindObservables: ")));

        // start loading if we have both configuration and genres
//        addToLifecycle(Flowable.combineLatest(configurationResponseSubject,
//                                              genresResponseSubject,
//                                              Pair::create)
//                               .filter(pair -> pair.first != null && pair.second != null)
//                               .subscribe(pair -> {
//                                              sortSubject.onNext(SORTORDER_MOST_POPULAR); // has to be in sync with menu_main.xml
//                                          },
//                                          error -> Timber.e(error, "bindObservables: ")));

        // show loading state
        addToLifecycle(loadingSubject
                           .distinctUntilChanged()
                           .subscribe(loading -> swipeRefreshLayout.setRefreshing(loading),
                                      error -> Timber.e(error, "bindObservables: ")));

        // show error toast if api request fails
        addToLifecycle(dataApiResponseSubject
                           .filter(response -> !response.isSuccessful())
                           .subscribe(response -> longToast(R.string.api_request_failed),
                                      error -> Timber.e(error, "bindObservables: ")));

        // extract data from request if api request is successful
        addToLifecycle(dataApiResponseSubject
                           .filter(response -> response.isSuccessful())
                           .map(response -> response.body())
                           .subscribe(body -> dataSubject.onNext(body),
                                      error -> Timber.e(error, "bindObservables: ")));

//        // load response into adapter
//        addToLifecycle(dataSubject
//                           .map(data -> data.results)
//                           .filter(movies -> movies != null)
//                           .subscribe(movies -> viewModel.addMovies(movies),
//                                      error -> Timber.e(error, "bindObservables: ")));

        // set max page count
        addToLifecycle(dataSubject
                           .subscribe(response -> totalPages = response.totalPages,
                                      error -> Timber.e(error, "bindObservables: ")));

        final RecyclerViewOnScrollObservable scrollObservable = new RecyclerViewOnScrollObservable(
            recyclerView);
        addToLifecycle(scrollObservable
                           .withLatestFrom(loadSubject.toObservable(),
                                           loadingSubject.toObservable(),
                                           (ignored, page, isLoading) -> Pair.create(page,
                                                                                     isLoading))
                           .filter(pair -> pair.first > 0 && !pair.second)
                           .subscribe(pair -> loadSubject.onNext(pair.first + 1),
                                      error -> Timber.e(error, "bindObservables: ")));
    }

    private static void setupSwipeRefresh(final SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                                                   android.R.color.holo_green_light,
                                                   android.R.color.holo_orange_light,
                                                   android.R.color.holo_red_light);
    }

    private void createSyncJobs() {
        final boolean successful = SyncJobService.createAndScheduleSyncJob(getApplicationContext());
        if (!successful) {
            Timber.e("failed to dispatch sync job");
        }
    }


    //
    // MainAdapter.OnMovieInteractionListener
    //

    @Override
    public void onClick(final Movie movie) {
//        final Intent intent = new Intent(this, DetailActivity.class);
//        intent.putExtra(DetailActivity.ARG_MOVIE, movie);
//        startActivity(intent);
    }


    //
    // DrawerLayout.DrawerListener
    //

    @Override
    public void onDrawerSlide(final View view, final float v) {}

    @Override
    public void onDrawerOpened(final View view) {
        if (debugModules != null) {
            for (DebugModule drawerItem : debugModules) {
                drawerItem.onOpened();
            }
        }
    }

    @Override
    public void onDrawerClosed(final View view) {
        if (debugModules != null) {
            for (DebugModule drawerItem : debugModules) {
                drawerItem.onOpened();
            }
        }
    }

    @Override
    public void onDrawerStateChanged(final int i) {}


    private static class RecyclerViewOnScrollObservable extends Observable<Integer> {

        private final RecyclerView view;

        RecyclerViewOnScrollObservable(RecyclerView view) {
            this.view = view;
        }

        @Override
        protected void subscribeActual(Observer<? super Integer> observer) {
            verifyMainThread();
            Listener listener = new Listener(view, observer);
            observer.onSubscribe(listener);
            view.addOnScrollListener(listener.scrollListener);
        }
    }


    private static class Listener extends MainThreadDisposable {

        private final RecyclerView                  recyclerView;
        private final RecyclerView.OnScrollListener scrollListener;
        private final GridLayoutManager             layoutManager;
        private final RecyclerView.Adapter          adapter;

        Listener(RecyclerView recyclerView, final Observer<? super Integer> observer) {
            this.recyclerView = recyclerView;
            this.layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            this.adapter = recyclerView.getAdapter();

            this.scrollListener = new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (!isDisposed()) {
                        if (adapter.getItemCount() > 0 &&
                            layoutManager.findLastVisibleItemPosition() > adapter.getItemCount() - 10) {

                            observer.onNext(dy);
                        }
                    }
                }
            };

            recyclerView.addOnScrollListener(scrollListener);
        }

        @Override
        protected void onDispose() {
            recyclerView.removeOnScrollListener(scrollListener);
        }
    }
}
