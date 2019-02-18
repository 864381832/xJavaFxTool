package com.xwintop.xJavaFxTool.services.epmsTools.gatewayConfTool;

import com.xwintop.xJavaFxTool.controller.epmsTools.gatewayConfTool.GatewayConfToolServiceViewController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class GatewayConfToolServiceViewService {
    private GatewayConfToolServiceViewController gatewayConfToolServiceViewController;

    public GatewayConfToolServiceViewService(GatewayConfToolServiceViewController gatewayConfToolServiceViewController) {
        this.gatewayConfToolServiceViewController = gatewayConfToolServiceViewController;
    }
}