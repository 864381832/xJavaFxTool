package com.xwintop.xJavaFxTool.utils;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public enum CorrectionLevel {

    L(ErrorCorrectionLevel.L, "~7%", 7),
    M(ErrorCorrectionLevel.M, "~15%", 15),
    Q(ErrorCorrectionLevel.Q, "~25%", 25),
    H(ErrorCorrectionLevel.H, "~30%", 30),

    ;

    private ErrorCorrectionLevel errorCorrectionLevel;

    private String name;

    private int maxOverlay;

    CorrectionLevel(ErrorCorrectionLevel errorCorrectionLevel, String name, int maxOverlay) {
        this.errorCorrectionLevel = errorCorrectionLevel;
        this.name = name;
        this.maxOverlay = maxOverlay;
    }

    public ErrorCorrectionLevel getErrorCorrectionLevel() {
        return errorCorrectionLevel;
    }

    public String getName() {
        return name;
    }

    public int getMaxOverlay() {
        return maxOverlay;
    }
}
