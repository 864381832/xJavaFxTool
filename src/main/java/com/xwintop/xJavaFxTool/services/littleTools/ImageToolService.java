package com.xwintop.xJavaFxTool.services.littleTools;
import com.xwintop.xJavaFxTool.controller.littleTools.ImageToolController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
@Getter
@Setter
@Log4j
public class ImageToolService{
private ImageToolController imageToolController;
public ImageToolService(ImageToolController imageToolController) {
this.imageToolController = imageToolController;
}
}