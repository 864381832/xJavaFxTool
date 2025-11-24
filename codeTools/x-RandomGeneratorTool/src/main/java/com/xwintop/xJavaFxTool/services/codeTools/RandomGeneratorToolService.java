package com.xwintop.xJavaFxTool.services.codeTools;

import com.xwintop.xJavaFxTool.controller.codeTools.RandomGeneratorToolController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: RandomGeneratorToolService
 * @Description: 随机数生成工具
 * @author: xufeng
 * @date: 2019/6/15 0015 0:53
 */

@Getter
@Setter
@Slf4j
public class RandomGeneratorToolService {
    private RandomGeneratorToolController randomGeneratorToolController;

    public RandomGeneratorToolService(RandomGeneratorToolController randomGeneratorToolController) {
        this.randomGeneratorToolController = randomGeneratorToolController;
    }
}