package com.xwintop.xJavaFxTool.services.assistTools;

import cn.hutool.http.HttpUtil;
import com.xwintop.xJavaFxTool.controller.assistTools.IdiomDataToolController;
import com.xwintop.xcore.util.ConfigureUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.application.Platform;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
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
    public static final String DICT_URL = "https://xwintop.gitee.io/x-idiomdatatool/file/IdiomDirty.mv.db";

    public static final String LOCAL_DICT_FILE = "file/IdiomDirty.mv.db";

    private IdiomDataToolController idiomDataToolController;

    private JdbcTemplate jdbcTemplate;

    public IdiomDataToolService(IdiomDataToolController idiomDataToolController) {
        this.idiomDataToolController = idiomDataToolController;
    }

    public void initIdiomData() {
        Platform.runLater(() -> {
            File localDictFile = ConfigureUtil.getConfigureFile(LOCAL_DICT_FILE);
            try {
                if (!localDictFile.exists()) {
                    TooltipUtil.showToast("第一次使用，需下载成语字典数据，请稍后...");
                    FileUtils.touch(localDictFile);
                    HttpUtil.downloadFile(DICT_URL, localDictFile);
                    log.info("成语字典数据下载完成！");
                    TooltipUtil.showToast("成语字典数据下载完成！");
                }
                if (jdbcTemplate == null) {
//                DruidDataSource dataSource = new DruidDataSource();
                    JdbcDataSource dataSource = new JdbcDataSource();
                    dataSource.setUrl("jdbc:h2:" + ConfigureUtil.getConfigurePath("file/IdiomDirty"));
//                dataSource.setDriverClassName("org.h2.Driver");
//                dataSource.setTestWhileIdle(false);
//                dataSource.init();
                    dataSource.getConnection();
                    jdbcTemplate = new JdbcTemplate(dataSource);
                }
            } catch (Exception e) {
                localDictFile.delete();
                log.error("数据源初始化错误：", e);
            }
        });
    }

    public void selectAction() throws Exception {
        String sql = "SELECT * FROM Idiom_dirty WHERE word like ? or abbreviation like ?";
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
        List<Map<String, Object>> list = new ArrayList<>();
        if (jdbcTemplate == null) {
            TooltipUtil.showToast("成语词典数据未准备好，请稍后重试...");
        } else {
            list = jdbcTemplate.queryForList(sql, sqlArgs, sqlArgs);
        }
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

    public void destroy() {
        if (jdbcTemplate != null) {
            try {
                jdbcTemplate.getDataSource().getConnection().close();
            } catch (SQLException e) {
                log.error("关闭数据源出错", e);
            }
        }
    }
}
