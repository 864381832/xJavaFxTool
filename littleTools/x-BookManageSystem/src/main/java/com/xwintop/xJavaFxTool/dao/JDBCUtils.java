package com.xwintop.xJavaFxTool.dao;

import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 连接JDBC类
 * @author lck100
 */
public class JDBCUtils {
    public static final String LOCAL_DICT_FILE = "file/BookManageSystem.mv.db";
    //加载驱动，并建立数据库连接

    /**
     * 加载驱动建立数据库链接
     *
     * @return 返回数据库Connection连接对象
     * @throws SQLException           抛出SQLException
     * @throws ClassNotFoundException 抛出ClassNotFoundException
     */
    static Connection getConnection() throws SQLException, ClassNotFoundException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:./file/BookManageSystem");
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from tb_booktype");
        return dataSource.getConnection();
    }

    /**
     * 关闭数据库连接，释放资源
     *
     * @param stmt Statement对象
     * @param conn Connection对象
     */
    static void release(Statement stmt, Connection conn) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            stmt = null;
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }
    }

    /**
     * 关闭数据库连接，释放资源
     *
     * @param rs   ResultSet对象
     * @param stmt Statement对象
     * @param conn Connection对象
     */
    public static void release(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            rs = null;
        }
        release(stmt, conn);
    }

}
