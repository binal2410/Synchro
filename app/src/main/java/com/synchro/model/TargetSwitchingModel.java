package com.synchro.model;

import java.io.Serializable;

public class TargetSwitchingModel implements Serializable {
    public String targetName;
    public String widthInRealLife;
    public String heightInRealLife;
    public String horizontalOffcet;
    public String verticalOffcet;
    public String imageAspectRatio;
    public boolean isSelected = false;

    public TargetSwitchingModel(String targetName, String widthInRealLife, String heightInRealLife, String horizontalOffcet, String verticalOffcet, String imageAspectRatio) {
        this.targetName = targetName;
        this.widthInRealLife = widthInRealLife;
        this.heightInRealLife = heightInRealLife;
        this.horizontalOffcet = horizontalOffcet;
        this.verticalOffcet = verticalOffcet;
        this.imageAspectRatio = imageAspectRatio;
    }

    public TargetSwitchingModel() {

    }
}
