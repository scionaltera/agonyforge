package com.agonyforge.mud.models.dynamodb.constant;

public enum Pronoun {
    HE("he", "him", "his", "himself", false),
    SHE("she", "her", "her", "herself", false),
    THEY("they", "them", "their", "themselves", true),
    IT("it", "it", "its", "itself", false);

    private final String subject;
    private final String object;
    private final String possessive;
    private final String reflexive;
    private final boolean plural;

    Pronoun(String subject, String object, String possessive, String reflexive, boolean plural) {
        this.subject = subject;
        this.object = object;
        this.possessive = possessive;
        this.reflexive = reflexive;
        this.plural = plural;
    }

    public String getSubject() {
        return subject;
    }

    public String getObject() {
        return object;
    }

    public String getPossessive() {
        return possessive;
    }

    public String getReflexive() {
        return reflexive;
    }

    public boolean isPlural() {
        return plural;
    }
}
