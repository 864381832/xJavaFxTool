package com.xwintop.xJavaFxTool.service.liteappcode;
import com.xwintop.xJavaFxTool.controller.liteappcode.LiteappCodeController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
@Getter
@Setter
@Slf4j
public class LiteappCodeService{
private LiteappCodeController liteappCodeController;
public LiteappCodeService(LiteappCodeController liteappCodeController) {
this.liteappCodeController = liteappCodeController;
}
}