package com.synchro.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ReactionRoundModel implements Serializable {
    public ArrayList<Double> accuracyList = new ArrayList<>();
    public ArrayList<Double> reactionList = new ArrayList<>();
    public ArrayList<Double> scoreList = new ArrayList<>();
    public ArrayList<DotsModel> dotsList = new ArrayList<>();

    public ReactionRoundModel() {
    }
}
