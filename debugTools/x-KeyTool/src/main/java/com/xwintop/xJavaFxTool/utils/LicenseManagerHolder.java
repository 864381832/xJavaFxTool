package com.xwintop.xJavaFxTool.utils;

import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;

public class LicenseManagerHolder {
    private static volatile LicenseManager licenseManager = null;

    private LicenseManagerHolder() {
    }

    public static LicenseManager getLicenseManager(LicenseParam param) {
        if (licenseManager == null) {
            synchronized (LicenseManagerHolder.class) {
                if (licenseManager == null) {
                    licenseManager = new LicenseManager(param);
                }
            }
        }
        return licenseManager;
    }
}
