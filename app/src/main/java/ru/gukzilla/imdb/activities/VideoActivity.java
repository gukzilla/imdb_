package ru.gukzilla.imdb.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ru.gukzilla.imdb.R;
import ru.gukzilla.imdb.api.Api;
import ru.gukzilla.imdb.api.RestClient;
import ru.gukzilla.imdb.models.Const;
import ru.gukzilla.imdb.models.FullVideo;
import ru.gukzilla.imdb.store.BookMarksStorage;

/**
 * Created by evgeniy on 09.12.16.
 */

public class VideoActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private Api api;
    private BookMarksStorage bookMarksStorage;
    private Menu menu;
    private FullVideo video;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final View progressId = findViewById(R.id.progressId);
        final View contentLayout = findViewById(R.id.contentLayout);

        Intent intent = getIntent();
        if(intent == null) {
            Log.e(TAG, "intent is null");
            finish();
            return;
        }

        Bundle bundle = intent.getExtras();
        if(bundle == null) {
            Log.e(TAG, "bundle is null");
            finish();
            return;
        }

        String imdbID = bundle.getString(Const.imdbID, null);
        if(imdbID == null) {
            Log.e(TAG, "imdbID is null");
            finish();
            return;
        }

        progressId.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.INVISIBLE);

        bookMarksStorage = new BookMarksStorage(this);
        api = new Api();
        api.getFullVideoById(imdbID, new Api.VideoListener() {
            @Override
            public void onResult(FullVideo vid) {
                video = vid;
                load();
                updateMenu();
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error^^^ ", e);
                finish();
            }

            @Override
            public void onComplete() {
                progressId.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return true;
    }

    private void updateMenu() {
        if(menu == null) {
            Log.e(TAG, "options menu not ready");
            return;
        }

        menu.clear();

        getMenuInflater().inflate(R.menu.video_activity_menu, menu);

        MenuItem addToBookmarks = menu.findItem(R.id.addToBookmarks);
        MenuItem removeFromBookmarks = menu.findItem(R.id.removeFromBookmarks);

        boolean isBookmarks = bookMarksStorage.inTheBookMarks(video.getImdbID());

        addToBookmarks.setVisible(!isBookmarks);
        removeFromBookmarks.setVisible(isBookmarks);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.addToBookmarks:
                bookMarksStorage.save(video);
                updateMenu();
                Toast.makeText(this, R.string.addedToBookmarks, Toast.LENGTH_SHORT).show();
                break;
            case R.id.removeFromBookmarks:
                bookMarksStorage.remove(video);
                updateMenu();
                Toast.makeText(this, R.string.removedFromBookmarks, Toast.LENGTH_SHORT).show();
                break;
            case R.id.shareId:
                share();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void share() {
        String extraText = video.getTitle() + "\n" + video.getPlot();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, extraText);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void load() {
        getSupportActionBar().setTitle(video.getTitle());

        setText(R.id.titleId, video.getTitle());
        setText(R.id.runtimeId, getString(R.string.runtime) + ": " + video.getRuntime());
        setText(R.id.genreId, getString(R.string.genre) + ": " + video.getGenre());
        setText(R.id.releasedId, getString(R.string.released) + ": " + video.getReleased());
        setText(R.id.countryId, getString(R.string.country) + ": " + video.getCountry());
        setText(R.id.ratingId, getString(R.string.rating) + ": " + video.getImdbRating());
        setText(R.id.directorId, getString(R.string.director) + ": " + video.getDirector());
        setText(R.id.writersId, getString(R.string.writers) + ": " + video.getWriter());
        setText(R.id.typeId, getString(R.string.type) + ": " + video.getType());
        setText(R.id.fullDescriptionId, getString(R.string.plot) + ": " + video.getPlot());

        final ImageView imageId = (ImageView) findViewById(R.id.imageId);
        if(video.getPoster() == null) {
            imageId.setVisibility(View.GONE);
        } else {
            api.downloadBitmap(video.getPoster(), new RestClient.BitmapListener() {
                @Override
                public void onResult(Bitmap bitmap) {
                    if(bitmap == null) {
                        imageId.setVisibility(View.GONE);
                    } else {
                        imageId.setImageBitmap(bitmap);

                        float scale = (float) bitmap.getWidth() / (float) imageId.getMeasuredWidth();
                        int height = (int) ((float) bitmap.getHeight() / scale);

                        ViewGroup.LayoutParams params = imageId.getLayoutParams();
                        params.height = height;
                        imageId.setLayoutParams(params);
                    }
                }
            });
        }
    }

    private void setText(int textViewId, String text) {
        TextView textView = (TextView) findViewById(textViewId);
        textView.setText(text);
    }

    public static void open(Activity ac, String imdbID) {
        Intent intent = new Intent(ac, VideoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Const.imdbID, imdbID);
        ac.startActivity(intent);
    }

    public static Intent getIntentForResult(Activity ac, String imdbID) {
        Intent intent = new Intent(ac, VideoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Const.imdbID, imdbID);
        return intent;
    }
}
