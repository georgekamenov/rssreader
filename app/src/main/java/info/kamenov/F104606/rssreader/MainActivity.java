package info.kamenov.F104606.rssreader;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import info.kamenov.F104606.rssreader.db.DBManager;
import info.kamenov.F104606.rssreader.db.SettingsContract;
import info.kamenov.F104606.rssreader.model.BBCFeeds;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Map<BBCFeeds, SyndFeed> mapFeeds = new HashMap<>();
    Map<BBCFeeds, SettingsContract.SettingsEntry> settingsEntryMap = new HashMap<>();
    BBCFeeds defaultFeed;
    BBCFeeds currentFeed;
    int currentFeedIndex = 0;

    private Button prevButton;
    private Button nextButton;
    private Button settingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadSettings();
        loadRSSFeeds();

        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        settingButton = findViewById(R.id.settingsButton);

        prevButton.setEnabled(false);
        nextButton.setEnabled(false);
        settingButton.setEnabled(false);
        Spinner spinnerLanguages=findViewById(R.id.spinner_languages);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        prevButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                currentFeedIndex--;
                renderSingleFeed();
                prevButton.setEnabled(currentFeedIndex > 0);
                nextButton.setEnabled(true);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                currentFeedIndex++;
                renderSingleFeed();
                prevButton.setEnabled(true);
                nextButton.setEnabled(currentFeedIndex < mapFeeds.get(currentFeed).getEntries().size() - 1);
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent( view.getContext(), SettingsActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });
    }

    private void loadSettings() {
//        DBManager dbManager = new DBManager(this);
//        dbManager.open();
//        dbManager.insert(BBCFeeds.BUSINESS.toString(), true, false);
//        dbManager.close();

        DBManager dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetch();
        int count = cursor.getCount();
        dbManager.close();

        if (count == 0) {
            initDB();
            defaultFeed = BBCFeeds.WORLD;
            currentFeed = BBCFeeds.WORLD;
        } else {
            defaultFeed = BBCFeeds.WORLD;
            currentFeed = BBCFeeds.WORLD;
            for (int i = 0; i < count; i++) {
                if (cursor.getInt(2) == 1 && cursor.getInt(3) == 1) {
                    defaultFeed = BBCFeeds.valueOf(cursor.getString(1));
                    currentFeed = defaultFeed;
                    break;
                }
                cursor.moveToNext();
            }
//            for (int i = 0; i < count; i++) {
//                Log.e("GEORGE:", cursor.getString(1));
//                Log.e("GEORGE:", cursor.getString(2));
//                Log.e("GEORGE:", (cursor.getString(3)));
//                cursor.moveToNext();
//            }
        }
    }

    private void initDB() {
        DBManager dbManager = new DBManager(this);
        dbManager.open();
        dbManager.insert(BBCFeeds.TOP_STORIES.toString(), true, false);
        dbManager.insert(BBCFeeds.WORLD.toString(), true, true);
        dbManager.insert(BBCFeeds.EUROPE.toString(), true, false);
        dbManager.insert(BBCFeeds.UK.toString(), true, false);
        dbManager.insert(BBCFeeds.BUSINESS.toString(), true, false);
        dbManager.insert(BBCFeeds.POLITICS.toString(), true, false);
        dbManager.insert(BBCFeeds.HEALTH.toString(), true, false);
        dbManager.insert(BBCFeeds.EDUCATION_AND_FAMILY.toString(), true, false);
        dbManager.insert(BBCFeeds.SCIENCE_AND_ENVIRONMENT.toString(), true, false);
        dbManager.insert(BBCFeeds.TECHNOLOGY.toString(), true, false);
        dbManager.insert(BBCFeeds.ENTERTAINMENT_AND_ARTS.toString(), true, false);
        dbManager.close();
    }

    private Object makeCallParseResponse() throws IOException, FeedException {
        Map<BBCFeeds, SyndFeed> feedMap = new HashMap<>();
        SyndFeedInput input = new SyndFeedInput();
        for (BBCFeeds feed : BBCFeeds.values()) {
            feedMap.put(feed, input.build(new XmlReader(new URL(feed.getUrl()))));
        }
        return feedMap;
    }

    private void processResponse(Object o) {
        Map<BBCFeeds, SyndFeed> feeds = (Map<BBCFeeds, SyndFeed>) o;
        mapFeeds.putAll(feeds);
    }

    private void loadRSSFeeds() {
        Observable.defer(new Func0<Observable<String>>() {
                    @Override
                    public Observable<String> call() {
                        return Observable.just(null);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Func1<String, Object>() {
                    @Override
                    public Object call(final String url) {
                        try {
                            return makeCallParseResponse();
                        } catch (IOException | FeedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                               @Override
                               public void call(Object o) {
                                   processResponse(o);
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.e("RSSREADER", "Failed to load news feeds", throwable);
                            }
                        },
                        new Action0() {
                            @Override
                            public void call() {
                                currentFeedIndex = 0;
                                findViewById(R.id.prevButton).setEnabled(false);
                                findViewById(R.id.nextButton).setEnabled(!mapFeeds.get(currentFeed).getEntries().isEmpty());
                                settingButton.setEnabled(true);
                                renderNews();
                                Log.i("RSSREADER", "news feeds loaded successfully");
                            }
                        })
        ;
    }

    private void renderNews() {
        SyndFeed syndFeed = mapFeeds.get(currentFeed);
        String description = syndFeed.getDescription();
        ((TextView)findViewById(R.id.textView)).setText(description);
        String imageUrl = syndFeed.getImage().getUrl();
        try {
            ((UrlImageView)findViewById(R.id.imageView)).setImageURL(new URL(imageUrl));
        } catch (MalformedURLException e) {
            Log.e("RSSREADER", "Error loading image", e);
        }
        renderSingleFeed();
    }

    private void renderSingleFeed() {
        SyndEntry entry = (SyndEntry) mapFeeds.get(currentFeed).getEntries().get(currentFeedIndex);
        String title = entry.getTitle();
        SyndContent description = entry.getDescription();
        String link = entry.getLink();
        ((TextView)findViewById(R.id.textView2)).setText(title);
        ((TextView)findViewById(R.id.textView3)).setText(Html.fromHtml(description.getValue()));
        ((WebView)findViewById(R.id.webView)).loadUrl(link);
    }
}