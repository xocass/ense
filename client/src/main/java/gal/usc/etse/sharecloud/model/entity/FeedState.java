package gal.usc.etse.sharecloud.model.entity;

import java.util.ArrayList;
import java.util.List;

public class FeedState {
    private static List<FeedItem> feedItems = new ArrayList<>();
    private static int index = 0;

    public static void setFeed(List<FeedItem> items) {
        feedItems = items;
        index = 0;
    }

    public static List<FeedItem> getFeedItems() {
        return feedItems;
    }

    public static FeedItem getCurrent() {
        if (feedItems.isEmpty()) return null;
        return feedItems.get(index);
    }

    public static boolean hasNext() {
        return index < feedItems.size() - 1;
    }

    public static boolean hasPrev() {
        return index > 0;
    }

    public static void next() {
        if (hasNext()) index++;
    }

    public static void prev() {
        if (hasPrev()) index--;
    }

    public static int getIndex() {
        return index;
    }
}
