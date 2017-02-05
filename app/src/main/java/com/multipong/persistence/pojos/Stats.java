package com.multipong.persistence.pojos;

public class Stats {
    private String name;
    private Modality modality;
    private int score;

    public String getName() {
        return name;
    }

    public Stats withName(String name) { this.name = name; return this; }

    public Modality getModality() {
        return modality;
    }

    public Stats withModality(Modality modality) { this.modality = modality; return this; }

    public int getScore() {
        return score;
    }

    public Stats withScore(int score) { this.score = score; return this; }

    public enum Modality {
        SINGLE_PLAYER("SINGLE"),
        MULTI_PLAYER("MULTI");

        private final String description;

        private Modality(String value) { description = value; }

        @Override
        public String toString() {
            return description;
        }
    }
}
