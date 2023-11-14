package com.board.job.controller;

public class HelperForPagesCollections {
    public static boolean checkSearchText(String searchText) {
        return searchText != null && !searchText.trim().isEmpty();
    }

    public static String getSortByValues(String sortBy) {
        return sortBy.substring(1, sortBy.length() - 1);
    }
}
