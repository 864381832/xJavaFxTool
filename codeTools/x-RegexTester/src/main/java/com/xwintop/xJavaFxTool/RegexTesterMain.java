/**
 *    Copyright (c) [2019] [xufeng]
 *    [x-RegexTester] is licensed under Mulan PSL v2.
 *    You can use this software according to the terms and conditions of the Mulan PSL v2.
 *    You may obtain a copy of Mulan PSL v2 at:
 *             http://license.coscl.org.cn/MulanPSL2
 *    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *    See the Mulan PSL v2 for more details.
 */
package com.xwintop.xJavaFxTool;

import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegexTesterMain {
    public static void main(String[] args) {
        try {
            Application.launch(RegexTesterApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}