package com.example.iyeharayeu.videoapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;

import com.example.iyeharayeu.videoapp.R;
import com.example.iyeharayeu.videoapp.utilities.Utils;
import com.example.iyeharayeu.videoapp.camera.KeyCompatibleMediaController;
import com.example.iyeharayeu.videoapp.entities.MovieEntity;
import com.example.iyeharayeu.videoapp.entities.MovieListEntity;
import com.example.iyeharayeu.videoapp.player.DashRendererBuilder;
import com.example.iyeharayeu.videoapp.player.DemoPlayer;
import com.example.iyeharayeu.videoapp.player.EventLogger;
import com.example.iyeharayeu.videoapp.player.ExtractorRendererBuilder;
import com.example.iyeharayeu.videoapp.player.HlsRendererBuilder;
import com.example.iyeharayeu.videoapp.player.SmoothStreamingRendererBuilder;
import com.example.iyeharayeu.videoapp.player.SmoothStreamingTestMediaDrmCallback;
import com.example.iyeharayeu.videoapp.player.WidevineTestMediaDrmCallback;
import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.MediaCodecUtil;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.exoplayer.metadata.id3.GeobFrame;
import com.google.android.exoplayer.metadata.id3.Id3Frame;
import com.google.android.exoplayer.metadata.id3.PrivFrame;
import com.google.android.exoplayer.metadata.id3.TxxxFrame;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.util.Util;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;

public class PickMovieFragment extends BaseFragment implements SurfaceHolder.Callback,
        DemoPlayer.Listener, DemoPlayer.CaptionListener, DemoPlayer.Id3MetadataListener,
        AudioCapabilitiesReceiver.Listener {

    public static final String TAG = PickMovieFragment.class.getSimpleName();
    public static final String BUNDLE_MOVIES = "BUNDLE_MOVIES";
    public static final String BUNDLE_ID_TO_PLAY = "ID_TO_PlAY";
    private static final String BUNDLE_PLAYER_POSITION_EXTRA = "BUNDLE_PLAYER_POSITION_EXTRA";
    private static final String PLAY_READY_EXTRA = "PLAY_READY_EXTRA";
    public static final String BUNDLE_IMAGES_VISIBILITY = "IMAGES_VISIBILITY";
    public static final String BUNDLE_CONTROL_PANEL_IS_SHOWING = "BUNDLE_CONTROL_PANEL_IS_SHOWING";

    private static final CookieManager sDefaultCookieManager;

    static {
        sDefaultCookieManager = new CookieManager();
        sDefaultCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private MovieListEntity mMoviesList;

    private EventLogger mEventLogger;
    private MediaController mMediaController;
    private DemoPlayer mPlayer;
    private boolean mMustPreparePlayer;

    private long mLastPlayerPosition;

    private Uri mContentUri;
    private int mContentType;

    private boolean mIsVideoPrepared;
    private AudioCapabilitiesReceiver mAudioCapabilitiesReceiver;
    private String mIdToPlay;
    private Holder mHolder;

    public static PickMovieFragment newInstance(MovieListEntity entity, String idToPlay) {
        PickMovieFragment fragment = new PickMovieFragment();
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_MOVIES, entity);
        args.putString(BUNDLE_ID_TO_PLAY, idToPlay);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mMoviesList = (MovieListEntity) getArguments().getSerializable(BUNDLE_MOVIES);
            mIdToPlay = getArguments().getString(BUNDLE_ID_TO_PLAY);

            if (savedInstanceState != null) {
                mLastPlayerPosition = savedInstanceState.getLong(BUNDLE_PLAYER_POSITION_EXTRA);
                mIsVideoPrepared = savedInstanceState.getBoolean(PLAY_READY_EXTRA, false);
            } else {
                mIsVideoPrepared = false;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PLAY_READY_EXTRA, mIsVideoPrepared);
        if (mPlayer != null) {
            mLastPlayerPosition = mPlayer.getCurrentPosition();
        }
        outState.putLong(BUNDLE_PLAYER_POSITION_EXTRA, mLastPlayerPosition);
        outState.putBoolean(BUNDLE_IMAGES_VISIBILITY, (mHolder.mImagesContainer.getVisibility() == View.VISIBLE));
        outState.putBoolean(BUNDLE_CONTROL_PANEL_IS_SHOWING, mMediaController.isShowing());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            onShown();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || mPlayer == null) {
            onShown();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            onHidden();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            onHidden();
        }
    }


    @Override
    public void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {

        mHolder = null;
        mAudioCapabilitiesReceiver.unregister();

        super.onDestroyView();
    }

    private void onShown() {
        MovieEntity mMovie = Utils.getMovieAccordingToIndex(mMoviesList, mIdToPlay);
        if (mMovie != null) {
            mContentUri = Uri.parse(mMovie.getStreams().getUrl());
        }
        mContentType = Util.TYPE_OTHER;

        if (mPlayer == null) {
            if (!maybeRequestPermission()) {

                preparePlayer(mIsVideoPrepared);
            }
        } else {
            mPlayer.setBackgrounded(false);
        }
    }

    private void onHidden() {
        releasePlayer();
    }


    // AudioCapabilitiesReceiver.Listener methods

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        Log.d(TAG, "onAudioCapabilitiesChanged() called with: " + "audioCapabilities = [" + audioCapabilities + "]");
        if (mPlayer == null) {
            return;
        }
        boolean backgrounded = mPlayer.getBackgrounded();
        releasePlayer();
        preparePlayer(mIsVideoPrepared);
        mPlayer.setBackgrounded(backgrounded);
    }

    // Permission request listener method

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            preparePlayer(mIsVideoPrepared);
        } else {
            Toast.makeText((getActivity()), R.string.error_permission_denied,Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Checks whether it is necessary to ask for permission to read storage. If necessary, it also
     * requests permission.
     *
     * @return true if a permission request is made. False if it is not necessary.
     */

    private boolean maybeRequestPermission() {
        if (requiresPermission(mContentUri)) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            return true;
        } else {
            return false;
        }
    }

    private boolean requiresPermission(Uri uri) {
        return Util.isLocalFileUri(uri) && (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
    }

    private DemoPlayer.RendererBuilder getRendererBuilder() {
        Activity activity = getActivity();
        String userAgent = Util.getUserAgent(activity, "videoApp");
        switch (mContentType) {
            case Util.TYPE_SS:
                return new SmoothStreamingRendererBuilder(activity, userAgent,
                        mContentUri.toString(), new SmoothStreamingTestMediaDrmCallback());
            case Util.TYPE_DASH:
                return new DashRendererBuilder(activity, userAgent,
                        mContentUri.toString(), new WidevineTestMediaDrmCallback("", ""));
            case Util.TYPE_HLS:
                return new HlsRendererBuilder(activity, userAgent,
                        mContentUri.toString());
            case Util.TYPE_OTHER:
                return new ExtractorRendererBuilder(activity, userAgent, mContentUri);
            default:
                throw new IllegalStateException("Unsupported type: " + mContentType);
        }
    }

    private void preparePlayer(boolean playWhenReady) {
        Log.d(TAG, "preparePlayer() called with: " + "playWhenReady = [" + playWhenReady + "]");
        if (mPlayer == null) {
            mPlayer = new DemoPlayer(getRendererBuilder());
            mPlayer.addListener(this);
            mPlayer.setMetadataListener(this);
            mPlayer.seekTo(mLastPlayerPosition);
            mMustPreparePlayer = true;
            mMediaController.setMediaPlayer(mPlayer.getPlayerControl());
            mMediaController.setEnabled(true);
            mEventLogger = new EventLogger();
            mEventLogger.startSession();
            mPlayer.addListener(mEventLogger);
            mPlayer.setInfoListener(mEventLogger);
            mPlayer.setInternalErrorListener(mEventLogger);
        }

        if (mMustPreparePlayer) {
            mPlayer.prepare();
            mMustPreparePlayer = false;
        }

        mPlayer.setSurface(mHolder.mSurfaceView.getHolder().getSurface());
        Log.d(TAG, "preparePlayer() called with: " + "playWhenReady = [" + playWhenReady + "]");
        mPlayer.setPlayWhenReady(playWhenReady);
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            Log.d(TAG, "releasePlayer() called with: " + "");
            mLastPlayerPosition = mPlayer.getCurrentPosition();
            mPlayer.removeListener(this);
            mPlayer.removeListener(mEventLogger);
            mPlayer.setMetadataListener(null);
            mPlayer.setInfoListener(null);
            mPlayer.setInternalErrorListener(null);
            mPlayer.release();
            mPlayer = null;
            mEventLogger.endSession();
            mEventLogger = null;
        }
    }

    // DemoPlayer.Listener implementation

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            showControls();
        }
        boolean showProgress = true;
        String text = "playWhenReady=" + playWhenReady + ", playbackState=";
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                text += "buffering";
                break;
            case ExoPlayer.STATE_ENDED:
                text += "ended";
                showProgress = false;
                break;
            case ExoPlayer.STATE_IDLE:
                text += "idle";
                showProgress = false;
                break;
            case ExoPlayer.STATE_PREPARING:
                text += "preparing";
                break;
            case ExoPlayer.STATE_READY:
                showProgress = false;
                if(!mIsVideoPrepared) {
                    preparePlayer(true);
                    mIsVideoPrepared = true;
                    mActivityListener.onReadyToPlay();
                }
                text += "ready";
                break;
            default:
                text += "unknown";
                break;
        }

        Log.d(TAG, "onStateChanged() called.state=[" + text + "]");

        mHolder.mProgressBar.setVisibility(showProgress ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onError(Exception e) {
        String errorString = null;
        if (e instanceof UnsupportedDrmException) {
            // Special case DRM failures.
            UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
            errorString = Util.SDK_INT < 18 ? "drm not supported" : unsupportedDrmException.reason
                    == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME ? "drm unsupported scheme" :
                    "error ! drm unknown";
        } else if (e instanceof ExoPlaybackException && e.getCause() instanceof
                MediaCodecTrackRenderer.DecoderInitializationException) {
            // Special case for decoder initialization failures.
            MediaCodecTrackRenderer.DecoderInitializationException decoderInitializationException =
                    (MediaCodecTrackRenderer.DecoderInitializationException) e.getCause();
            if (decoderInitializationException.decoderName == null) {
                if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                    errorString = "querying decoders";
                } else if (decoderInitializationException.secureDecoderRequired) {
                    errorString = "error! no_secure_decoder";
                } else {
                    errorString = "error!no decoder";
                }
            } else {
                errorString = "error! instantiating decoder...";
            }
        }
        if (errorString != null) {
            Toast.makeText(getActivity(), errorString, Toast.LENGTH_LONG).show();
        }

        mMustPreparePlayer = true;
        showControls();
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                   float pixelWidthAspectRatio) {
        mHolder.mVideoFrame.setAspectRatio(
                height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
    }

    private void toggleControlsVisibility() {
        if (mMediaController.isShowing()) {
            mMediaController.hide();
            mHolder.mImagesContainer.setVisibility(View.VISIBLE);
        } else {
            showControls();
        }
    }

    private void showControls() {
        mMediaController.show(0);
        mHolder.mImagesContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCues(List<Cue> cues) {

    }


    @Override
    public void onId3Metadata(List<Id3Frame> id3Frames) {
        for (Id3Frame id3Frame : id3Frames) {
            if (id3Frame instanceof TxxxFrame) {
                TxxxFrame txxxFrame = (TxxxFrame) id3Frame;
                Log.i(TAG, String.format("ID3 TimedMetadata %s: description=%s, value=%s", txxxFrame.id,
                        txxxFrame.description, txxxFrame.value));
            } else if (id3Frame instanceof PrivFrame) {
                PrivFrame privFrame = (PrivFrame) id3Frame;
                Log.i(TAG, String.format("ID3 TimedMetadata %s: owner=%s", privFrame.id, privFrame.owner));
            } else if (id3Frame instanceof GeobFrame) {
                GeobFrame geobFrame = (GeobFrame) id3Frame;
                Log.i(TAG, String.format("ID3 TimedMetadata %s: mimeType=%s, filename=%s, description=%s",
                        geobFrame.id, geobFrame.mimeType, geobFrame.filename, geobFrame.description));
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mPlayer != null) {
            mPlayer.setSurface(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mPlayer != null) {
            mPlayer.blockingClearSurface();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pick_movie, container, false);

        mHolder = new Holder(view);
        restoreVisibilityState(savedInstanceState);
        return view;
    }

    private void restoreVisibilityState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Boolean isVisible = savedInstanceState.getBoolean(BUNDLE_IMAGES_VISIBILITY, false);
            mHolder.mImagesContainer.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
            if (mMediaController != null) {
                if (savedInstanceState.getBoolean(BUNDLE_CONTROL_PANEL_IS_SHOWING, false)) {
                    mMediaController.show(0);
                } else {
                    mMediaController.hide();
                }
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CookieHandler currentHandler = CookieHandler.getDefault();
        if (currentHandler != sDefaultCookieManager) {
            CookieHandler.setDefault(sDefaultCookieManager);
        }

        mMediaController = new KeyCompatibleMediaController(getActivity());
        int orientation = getActivity().getResources().getConfiguration().orientation;

        mMediaController.setAnchorView((orientation == Configuration.ORIENTATION_PORTRAIT) ? mHolder.
                mImagesContainer : mHolder.mVideoControlContainer);


        mAudioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getActivity(), this);
        mAudioCapabilitiesReceiver.register();

        mHolder.mSurfaceView.getHolder().addCallback(PickMovieFragment.this);

        Point size = new Point();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        display.getSize(size);

        mHolder.mVideoFrame.setAspectRatio(1f * size.x / size.y);

        setEntitiesOnUi();
        setImagesTapControls();
        setVideoTapControls();

    }


    private void setImagesTapControls() {
        mHolder.mImagesContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "onTouch1() called with: " + "view = [" + view + "], motionEvent = [" + motionEvent + "]");
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    toggleControlsVisibility();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mHolder.mImagesContainer.performClick();
                }
                return true;
            }
        });
        mHolder.mImagesContainer.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d(TAG, "onKey() called with: " + "v = [" + v + "], keyCode = [" + keyCode + "], event = [" + event + "]");
                return !(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE
                        || keyCode == KeyEvent.KEYCODE_MENU) && mMediaController.dispatchKeyEvent(event);
            }
        });
    }


    private void setVideoTapControls() {
        mHolder.mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "onTouch2() called with: " + "view = [" + view + "], motionEvent = [" + motionEvent + "]");
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    toggleControlsVisibility();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.performClick();
                }
                return true;
            }
        });
    }


    private void setEntitiesOnUi() {
        for (int index = 0; index < (mHolder.mImagesContainer).getChildCount(); ++index) {
            MovieEntity currentEntity = mMoviesList.getMovies()[index];
            ImageView nextChild = (ImageView) (mHolder.mImagesContainer).getChildAt(index);
            Bitmap bmp = Utils.getBitmapFromAssets(getActivity(), currentEntity.getImages().getCover());
            nextChild.setImageBitmap(bmp);
            nextChild.setTag(currentEntity);
            nextChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick() called with: " + "view = [" + view + "]");
                    mHolder.mImagesContainer.setVisibility(View.INVISIBLE);
                    openAccordingToTapped((MovieEntity) view.getTag());
                }

                private void openAccordingToTapped(MovieEntity tag) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(PreloaderFragment.BUNDLE_DESCRIPTION, tag);
                    mActivityListener.onStartSelectedVideo(tag);
                }
            });
        }
    }

    private static class Holder {

        private final View mView;
        private final LinearLayout mImagesContainer;
        private final LinearLayout mVideoControlContainer;
        private final AspectRatioFrameLayout mVideoFrame;
        private final SurfaceView mSurfaceView;
        private final View mProgressBar;

        public Holder(View view) {
            mView = view;
            mImagesContainer = (LinearLayout) view.findViewById(R.id.pick_movie_container);
            mVideoControlContainer = (LinearLayout) view.findViewById(R.id.pick_movie_control_container);
            mVideoFrame = (AspectRatioFrameLayout) view.findViewById(R.id.pick_movie_video_frame);
            mSurfaceView = (SurfaceView) view.findViewById(R.id.pick_movie_surface_view);
            mProgressBar = view.findViewById(R.id.pick_movie_progress);
        }
    }

}