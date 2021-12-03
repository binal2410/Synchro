package com.synchro.model;

import java.io.Serializable;

public class ScoreModel implements Serializable {
    public String noOfHits;
    public String avgScore;
    public String totelScore;

    public ScoreModel(String noOfHits, String avgScore, String totelScore) {
        this.noOfHits = noOfHits;
        this.avgScore = avgScore;
        this.totelScore = totelScore;
    }
}
