package com.board.job.model.entity.sample;

import lombok.Getter;

@Getter
public enum LanguageLevel {
    NO("You have no language knowledge", "No"),
    BEGINNER_ELEMENTARY("You have base language knowledge", "Beginner/Elementary"),
    PRE_INTERMEDIATE("You can read documentations and conduct working correspondence", "Pre-Intermediate"),
    INTERMEDIATE("You can read and speak, but with simple phrases", "Intermediate"),
    UPPER_INTERMEDIATE("You can participate in meetings or be interviewed with this language", "Upper-Intermediate"),
    ADVANCED_FLUENT("You can speak fluently", "Advanced/Fluent");

    private final String description;
    private final String value;

    LanguageLevel(String description, String value){
        this.description = description;
        this.value = value;
    }

    public static LanguageLevel checkValuesAndGet(String insertedValue) {
        for (LanguageLevel languageLevel : LanguageLevel.values()) {
            if (insertedValue.equals(languageLevel.getValue())) {
                return languageLevel;
            }
        }
        throw new EnumConstantNotPresentException(LanguageLevel.class, "This language level does not found!");
    }
}
