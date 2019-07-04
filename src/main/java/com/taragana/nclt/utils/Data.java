package com.taragana.nclt.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains static data of certain input values required for operation and functionality.
 * @Author Supratim
 */
public class Data {

    public static final String PRINCIPAL_BENCH = "Principal Bench";
    public static final String FINAL_ORDER = "final order";
    public static final String INTERIM_ORDER = "interim order";
    public static final String NO_RESULT_FOUND = "no result found";
    public static final String PDF_ICON = "PDF icon";
    private static final String Year_2019 = "2019";
    private static final String Year_2018 = "2018";
    private static final String Year_2017 = "2017";
    private static final String Year_2016 = "2016";
    private static final String CORPORATE_INSOLVENCY_RESOLUTION = "Public Announcement of Corporate Insolvency Resolution Process";
    private static final String VOLUNTARY_LIQUIDATION = "Public Announcement of Voluntary Liquidation Process";
    private static final String LIQUIDATION = "Public Announcement of Liquidation Process";

    public static List<String> yearList = new ArrayList<String>() {
        {
            add(Year_2019);
            add(Year_2018);
            add(Year_2017);
            add(Year_2016);
        }
    };
    public static List<String> announcementList = new ArrayList<String>() {
        {
            add(CORPORATE_INSOLVENCY_RESOLUTION);
            add(VOLUNTARY_LIQUIDATION);
            add(LIQUIDATION);
        }
    };

}
