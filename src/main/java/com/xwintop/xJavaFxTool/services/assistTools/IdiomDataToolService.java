package com.xwintop.xJavaFxTool.services.assistTools;

import com.alibaba.druid.pool.DruidDataSource;
import com.xwintop.xJavaFxTool.controller.assistTools.IdiomDataToolController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: IdiomDataToolService
 * @Description: 成语字典工具
 * @author: xufeng
 * @date: 2019/11/10 0010 22:06
 */

@Getter
@Setter
@Slf4j
public class IdiomDataToolService {
    private IdiomDataToolController idiomDataToolController;

    private JdbcTemplate jdbcTemplate;

    public IdiomDataToolService(IdiomDataToolController idiomDataToolController) {
        this.idiomDataToolController = idiomDataToolController;
    }

    public void selectAction() throws Exception {
        if (jdbcTemplate == null) {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setUrl("jdbc:h2:D:\\ideaWorkspaces\\xwintop\\xJavaFxTool\\file\\IdiomDirty");
            dataSource.setDriverClassName("org.h2.Driver");
            dataSource.init();
            jdbcTemplate = new JdbcTemplate(dataSource);
        }
        String sql = "SELECT * FROM Idiom_dirty WHERE word like ?";
        String sqlArgs = "";
        if (StringUtils.isEmpty(idiomDataToolController.getSelectWordTextField().getText())) {
            String[] indexString = new String[]{
                    idiomDataToolController.getIndex1TextField().getText(),
                    idiomDataToolController.getIndex2TextField().getText(),
                    idiomDataToolController.getIndex3TextField().getText(),
                    idiomDataToolController.getIndex4TextField().getText()
            };
            for (int i = 0; i < 4; i++) {
                if (StringUtils.isEmpty(indexString[i])) {
                    sqlArgs += "_";
                } else {
                    sqlArgs += indexString[i];
                }
            }

        } else {
            sqlArgs = "%" + idiomDataToolController.getSelectWordTextField().getText() + "%";
        }
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, sqlArgs);
        idiomDataToolController.getIdiomDataTableData().clear();
        for (Map<String, Object> stringObjectMap : list) {
            Map<String, String> dataRow = new HashMap<String, String>();
            dataRow.put("word", stringObjectMap.get("word").toString());
            dataRow.put("pinyin", stringObjectMap.get("pinyin").toString());
            dataRow.put("explanation", stringObjectMap.get("explanation").toString());
            dataRow.put("derivation", stringObjectMap.get("derivation").toString());
            dataRow.put("example", stringObjectMap.get("example").toString());
            idiomDataToolController.getIdiomDataTableData().add(dataRow);
        }
    }

    public void clearAction() {
        idiomDataToolController.getIndex1TextField().setText(null);
        idiomDataToolController.getIndex2TextField().setText(null);
        idiomDataToolController.getIndex3TextField().setText(null);
        idiomDataToolController.getIndex4TextField().setText(null);
        idiomDataToolController.getSelectWordTextField().setText(null);
    }
}