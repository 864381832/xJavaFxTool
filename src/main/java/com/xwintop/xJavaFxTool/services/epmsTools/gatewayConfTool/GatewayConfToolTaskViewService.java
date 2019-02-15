package com.xwintop.xJavaFxTool.services.epmsTools.gatewayConfTool;
import com.xwintop.xJavaFxTool.controller.epmsTools.gatewayConfTool.GatewayConfToolTaskViewController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
@Getter
@Setter
@Slf4j
public class GatewayConfToolTaskViewService{
private GatewayConfToolTaskViewController gatewayConfToolTaskViewController;
public GatewayConfToolTaskViewService(GatewayConfToolTaskViewController gatewayConfToolTaskViewController) {
this.gatewayConfToolTaskViewController = gatewayConfToolTaskViewController;
}
}