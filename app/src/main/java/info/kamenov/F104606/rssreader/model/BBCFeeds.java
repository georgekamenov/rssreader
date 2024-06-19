package info.kamenov.F104606.rssreader.model;

import java.util.Arrays;

public enum BBCFeeds {
    TOP_STORIES("https://feeds.bbci.co.uk/news/rss.xml"),
    WORLD("https://feeds.bbci.co.uk/news/world/rss.xml"),
    EUROPE("https://feeds.bbci.co.uk/news/world/europe/rss.xml"),
    UK("https://feeds.bbci.co.uk/news/uk/rss.xml"),
    BUSINESS("https://feeds.bbci.co.uk/news/business/rss.xml"),
    POLITICS("https://feeds.bbci.co.uk/news/politics/rss.xml"),
    HEALTH("https://feeds.bbci.co.uk/news/health/rss.xml"),
    EDUCATION_AND_FAMILY("https://feeds.bbci.co.uk/news/education/rss.xml"),
    SCIENCE_AND_ENVIRONMENT("https://feeds.bbci.co.uk/news/science_and_environment/rss.xml"),
    TECHNOLOGY("https://feeds.bbci.co.uk/news/technology/rss.xml"),
    ENTERTAINMENT_AND_ARTS("https://feeds.bbci.co.uk/news/entertainment_and_arts/rss.xml");

    final String url;
    BBCFeeds(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public static BBCFeeds of(String value) {
        return (BBCFeeds) Arrays.stream(values())
                .filter((bbcFeeds) -> bbcFeeds.url.equals(value))
                .findFirst()
                .orElse((BBCFeeds) null);
    }



}
