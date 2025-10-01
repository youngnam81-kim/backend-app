package com.project.app.config;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class P6SpyFormatter implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        sql = formatSql(category, sql);
        
        if (Category.COMMIT.getName().equals(category) || 
                Category.STATEMENT.getName().equals(category)) {
                return "";  // 빈 문자열 반환하여 로그 출력 안 함
        }
        
        Date _now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        
        return format.format(_now) + " | " + elapsed + "ms | " + category + " | connection " + connectionId + "\n" 
             + sql + ";";
    }

    private String formatSql(String category, String sql) {
        if (sql == null || sql.trim().equals("")) return sql;
        
        // 로그를 보기 좋게 포맷팅 (category가 statement일 경우)
        if (Category.STATEMENT.getName().equals(category)) {
            String tmpsql = sql.trim().toLowerCase(Locale.ROOT);
            if (tmpsql.startsWith("create") || tmpsql.startsWith("alter") || tmpsql.startsWith("comment")) {
                sql = FormatStyle.DDL.getFormatter().format(sql);
            } else {
                sql = FormatStyle.BASIC.getFormatter().format(sql);
            }
            sql = "|\nHibernate: " + sql;
        }
        
        return sql;
    }
}