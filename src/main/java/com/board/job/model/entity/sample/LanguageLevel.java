package com.board.job.model.entity.sample;

public enum LanguageLevel {
    NO("You have no language knowledge"),
    BEGINNER_ELEMENTARY("You have base language knowledge"),
    PRE_INTERMEDIATE("You can read documentations and conduct working correspondence"),
    INTERMEDIATE("You can read and speak, but with simple phrases"),
    UPPER_INTERMEDIATE("You can participate in meetings or be interviewed with this language"),
    ADVANCED_FLUENT("You can speak fluently");

    private final String description;

    LanguageLevel(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
