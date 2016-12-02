package udacity.nanodegree.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.FixedPreloadSizeProvider;

import java.lang.annotation.Retention;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import timber.log.Timber;
import udacity.nanodegree.popularmovies.MovieApp;
import udacity.nanodegree.popularmovies.R;
import udacity.nanodegree.popularmovies.adapter.MainAdapter;
import udacity.nanodegree.popularmovies.api.TMDbApi;
import udacity.nanodegree.popularmovies.api.models.ConfigurationResponse;
import udacity.nanodegree.popularmovies.api.models.GenresResponse;
import udacity.nanodegree.popularmovies.api.models.MoviesResponse;
import udacity.nanodegree.popularmovies.api.models.MoviesResponse.Movie;
import udacity.nanodegree.popularmovies.utils.glide.GlideRequestBuilder;
import udacity.nanodegree.popularmovies.utils.glide.RecyclerViewPreloader;

import static io.reactivex.android.MainThreadDisposable.verifyMainThread;
import static java.lang.annotation.RetentionPolicy.SOURCE;
import static udacity.nanodegree.popularmovies.R.id.recyclerView_activity_main;
import static udacity.nanodegree.popularmovies.R.id.swipeRefreshLayout_activity_main;
import static udacity.nanodegree.popularmovies.R.id.toolbar_activity_main;

public class MainActivity extends BaseActivity implements MainAdapter.OnMovieInteractionListener {

    public static final String SAVED_LAYOUT_MANAGER = "SAVED_LAYOUT_MANAGER";

    @BindView(toolbar_activity_main)            Toolbar            toolbar;
    @BindView(swipeRefreshLayout_activity_main) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(recyclerView_activity_main)       RecyclerView       recyclerView;

    @Inject MovieApp app;
    @Inject TMDbApi  api;

    // configuration
    private PublishProcessor<Response<ConfigurationResponse>>           configurationApiResponseSubject = PublishProcessor.create();
    private BehaviorProcessor<ConfigurationResponse.ImageConfiguration> configurationResponseSubject    = BehaviorProcessor.create();
    private PublishProcessor<Response<GenresResponse>>                  genresApiResponseSubject        = PublishProcessor.create();
    private BehaviorProcessor<List<GenresResponse.Genre>>               genresResponseSubject           = BehaviorProcessor.create();

    // data
    private BehaviorProcessor<Integer>                 loadSubject            = BehaviorProcessor.create();
    private BehaviorProcessor<Boolean>                 loadingSubject         = BehaviorProcessor.create();
    private PublishProcessor<Response<MoviesResponse>> dataApiResponseSubject = PublishProcessor.create();
    private PublishProcessor<MoviesResponse>           dataSubject            = PublishProcessor.create();

    @Nullable private GridLayoutManager layoutManager;
    @Nullable private MainAdapter       adapter;
    @Nullable private Parcelable        layoutManagerSavedState;

    private int totalPages   = 1; // we assume that we have at least 1 page


    @Retention(SOURCE)
    @IntDef({SORTORDER_MOST_POPULAR, SORTORDER_TOP_RATED})
    public @interface SortOrder {}
    public static final int SORTORDER_MOST_POPULAR = 0;
    public static final int SORTORDER_TOP_RATED    = 1;

    @SortOrder
    private int sortOrder = SORTORDER_MOST_POPULAR; // has to be in sync with menu_main.xml

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setupFullscreen(toolbar);
        setupSwipeRefresh(swipeRefreshLayout);
        setTitle(null);

        MovieApp.graph().inject(this);

        final int spanCount = getResources().getInteger(R.integer.gallery_columns);
        layoutManager = new GridLayoutManager(this, spanCount);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MainAdapter(this, this, GlideRequestBuilder.preloadRequest(this));
        adapter.setSortOrder(sortOrder);

        recyclerView.setAdapter(adapter);

        final FixedPreloadSizeProvider<Movie> preloadSizeProvider = new FixedPreloadSizeProvider<>(50, 75);
        final RecyclerViewPreloader<Movie> preloader = new RecyclerViewPreloader<>(Glide.with(this), adapter, preloadSizeProvider, 12);
        recyclerView.addOnScrollListener(preloader);
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
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if (layoutManager != null) {
            outState.putParcelable(SAVED_LAYOUT_MANAGER, layoutManager.onSaveInstanceState());
        }
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            layoutManagerSavedState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER);
            if (layoutManager != null) {
                layoutManager.onRestoreInstanceState(layoutManagerSavedState);
            }
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
                sortOrder = SORTORDER_MOST_POPULAR;
                break;
            case R.id.top_rated:
                sortOrder = SORTORDER_TOP_RATED;
                break;
        }

        // clear adapter
        adapter.clear();
        adapter.setSortOrder(sortOrder);

        // start new loading
        totalPages = 1;
        loadSubject.onNext(1);

        // set menu item
        item.setChecked(true);

        return super.onOptionsItemSelected(item);
    }

    @Override
    void bindObservables() {


        //
        // configuration
        //

        // load configuration
        addToLifecycle(api.configuration()
                          .subscribeOn(Schedulers.io())
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(response -> configurationApiResponseSubject.onNext(response),
                                            error -> Timber.e(error, "bindObservables: ")));

        // show error toast if api request fails
        addToLifecycle(configurationApiResponseSubject
                           .filter(response -> !response.isSuccessful())
                           .subscribe(response -> longToast(R.string.api_request_failed),
                                      error -> Timber.e(error, "bindObservables: ")));

        // extract images configuration from request if api request is successful
        addToLifecycle(configurationApiResponseSubject
                           .filter(response -> response.isSuccessful())
                           .map(response -> response.body())
                           .map(configuration -> configuration.images)
                           .subscribe(config -> configurationResponseSubject.onNext(config),
                                      error -> Timber.e(error, "bindObservables: ")));

        // store images configuration in MovieApp that we can access it from binding adapters
        addToLifecycle(configurationResponseSubject
                           .subscribe(config -> app.imageConfiguration = config,
                                      error -> Timber.e(error, "bindObservables: ")));


        //
        // genres
        //

        // load genres
        addToLifecycle(api.genres()
                          .subscribeOn(Schedulers.io())
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(response -> genresApiResponseSubject.onNext(response),
                                            error -> Timber.e(error, "bindObservables: ")));

        // show error toast if api request fails
        addToLifecycle(genresApiResponseSubject
                           .filter(response -> !response.isSuccessful())
                           .subscribe(response -> longToast(R.string.api_request_failed),
                                      error -> Timber.e(error, "bindObservables: ")));

        // extract images configuration from request if api request is successful
        addToLifecycle(genresApiResponseSubject
                           .filter(response -> response.isSuccessful())
                           .map(response -> response.body())
                           .map(genres -> genres.genres)
                           .subscribe(genres -> genresResponseSubject.onNext(genres),
                                      error -> Timber.e(error, "bindObservables: ")));

        // store genres in MovieApp that we can access it from binding adapters
        addToLifecycle(genresResponseSubject
                           .subscribe(genres -> app.genres = genres,
                                      error -> Timber.e(error, "bindObservables: ")));


        //
        // movies
        //

        // start loading if we have both configuration and genres
        addToLifecycle(Flowable.combineLatest(configurationResponseSubject,
                                              genresResponseSubject,
                                              Pair::create)
                               .filter(pair -> pair.first != null && pair.second != null)
                               .subscribe(pair -> loadSubject.onNext(1),
                                          error -> Timber.e(error, "bindObservables: ")));

        // load data
        addToLifecycle(loadSubject
                           .distinctUntilChanged()
                           .filter(page -> page <= totalPages)
                           .flatMap(page -> {
                               loadingSubject.onNext(true);

                               switch (sortOrder) {
                                   case SORTORDER_TOP_RATED:
                                       return api.toprated(page)
                                                 .subscribeOn(Schedulers.io())
                                                 .observeOn(AndroidSchedulers.mainThread())
                                                 .doOnComplete(() -> loadingSubject.onNext(false));
                                   case SORTORDER_MOST_POPULAR:
                                   default:
                                       return api.popular(page)
                                                 .subscribeOn(Schedulers.io())
                                                 .observeOn(AndroidSchedulers.mainThread())
                                                 .doOnComplete(() -> loadingSubject.onNext(false));
                               }
                           })
                           .subscribe(response -> dataApiResponseSubject.onNext(response),
                                      error -> Timber.e(error, "bindObservables: ")));

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

        // load response into adapter
        addToLifecycle(dataSubject
                           .map(data -> data.results)
                           .filter(movies -> movies != null)
                           .subscribe(movies -> adapter.addMovies(movies),
                                      error -> Timber.e(error, "bindObservables: ")));

        // set max page count
        addToLifecycle(dataSubject
                           .subscribe(response -> totalPages = response.totalPages,
                                      error -> Timber.e(error, "bindObservables: ")));

        final RecyclerViewOnScrollObservable scrollObservable = new RecyclerViewOnScrollObservable(recyclerView);
        addToLifecycle(scrollObservable
                           .withLatestFrom(loadSubject.toObservable(),
                                           loadingSubject.toObservable(),
                                           (ignored, page, isLoading) -> Pair.create(page, isLoading))
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


    //
    // MainAdapter.OnMovieInteractionListener
    //

    @Override
    public void onClick(final Movie movie) {
        final Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.ARG_MOVIE, movie);
        startActivity(intent);
    }


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
                        if (layoutManager.findLastVisibleItemPosition() > adapter.getItemCount() - 10) {
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
