package com.board.job.model.entity.sample;

import lombok.Getter;

@Getter
public enum JobDomain {
    ADULT("Adult"), ADVERTISING_MARKETING("Advertising Marketing"), AUTOMOTIVE("Automotive"),
    BLOCKCHAIN_CRYPTO("Blockchain/Crypto"), DATING("Dating"), E_COMMERCE_MARKETPLACE("E-commerce/Marketplace"),
    EDUCATION("Education"), FINTECH("Fintech"), GAMBLING("Gambling"), GAMEDEV("GameDEV"),
    HARDWARE("Hardware"), HEALTHCARE_MEDTECH("Health/Medtech"), MACHINE_LEARNING("Machine Learning"),
    BIG_DATA("Big Data"), MEDIA("Media"), MILTECH("Miltech"), MOBILE("Mobile"), SAAS("SAAS"),
    SECURITY("Security"), TELECOM_COMMUNICATIONS("Telecom communications"), TRAVEL("Travel"), OTHER("Other");

    private String value;

    JobDomain(String value) {
        this.value = value;
    }

    public static JobDomain checkValuesAndGet(String insertedValue) {
        for (JobDomain jobDomain : JobDomain.values()) {
            if (insertedValue.equals(jobDomain.getValue())) {
                return jobDomain;
            }
        }
        throw new EnumConstantNotPresentException(LanguageLevel.class, "This job domain does not found!");
    }
}
