package udacity.nanodegree.popularmovies.ui;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseActivity extends DaggerAppCompatActivity implements LifecycleRegistryOwner {

    private final CompositeDisposable disposables = new CompositeDisposable();
    private LifecycleRegistry registry;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registry = new LifecycleRegistry(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindObservables();
    }

    protected abstract void bindObservables();

    @Override
    protected void onStop() {
        super.onStop();
        disposables.clear();
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return registry;
    }

    protected final void addToLifecycle(@NonNull final Disposable disposable) {
        disposables.add(disposable);
    }

    protected boolean isOnline() {
        final ConnectivityManager service = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = service.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    protected void longToast(@StringRes int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }

    protected final void setupFullscreen(@Nullable final View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                                             View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

            // Fixes statusbar covers toolbar issue
            if (view != null) {
                final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                layoutParams.topMargin = statusBarHeight();
            }
        }
    }

    private int statusBarHeight() {
        final int identifier = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            return getResources().getDimensionPixelSize(identifier);
        }
        return 0;
    }
}
