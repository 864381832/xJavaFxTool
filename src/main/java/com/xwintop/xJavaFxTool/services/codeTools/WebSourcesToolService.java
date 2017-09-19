package com.xwintop.xJavaFxTool.services.codeTools;

import com.xwintop.xJavaFxTool.controller.codeTools.WebSourcesToolController;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
public class WebSourcesToolService {
	private WebSourcesToolController webSourcesToolController;

	public WebSourcesToolService(WebSourcesToolController webSourcesToolController) {
		this.webSourcesToolController = webSourcesToolController;
	}
	
}
