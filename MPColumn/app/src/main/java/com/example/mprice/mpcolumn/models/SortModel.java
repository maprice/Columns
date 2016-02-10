package com.example.mprice.mpcolumn.models;

import org.parceler.Parcel;

/**
 * Created by mprice on 2/9/16.
 */
@Parcel
public class SortModel {
    public enum SortOrder {
        DEFAULT("Default", ""),
        NEWEST("Newest", "newest"),
        OLDEST("Oldest", "oldest");

        private String friendlyName;
        private String paramName;

        private SortOrder(String friendlyName, String param) {
            this.friendlyName = friendlyName;
            this.paramName = param;
        }

        @Override
        public String toString() {
            return friendlyName;
        }

        public String toParam() {
            return paramName;
        }
    }

    public int beginDateDay;
    public int beginDateMonth;
    public int beginDateYear;

    public SortOrder order;

    public boolean newDeskArts;
    public boolean newDeskSports;

    public SortModel() {
        beginDateDay = 1;
        beginDateMonth = 1;
        beginDateYear = 1970;

        order = SortOrder.DEFAULT;

        newDeskArts = false;
        newDeskSports = false;
    }
}
