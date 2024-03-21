package com.vention.agroex.util.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SearchMethodInterceptor implements StatementInspector {

    @Override
    public String inspect(String sql) {
        if (sql.contains("SEARCH_STRING")) {
            var joinInsert = "join product_category search_pc on search_pc.id = le1_0.product_category_id";
            var whereIndex = sql.indexOf("where");
            if (whereIndex != -1) {
                sql = sql.substring(0, whereIndex) + joinInsert + " " + sql.substring(whereIndex);
            } else {
                log.error("There is no 'WHERE' in search string");
            }

            String regex = "'SEARCH_STRING'='(.*?)'";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(sql);
            var keyword = "";
            if (matcher.find()) {
                keyword = matcher.group(1);
            } else {
                log.error("There is no search condition in search string");
            }
            var searchInsert = "to_tsvector(search_pc.title || ' ' || le1_0.title || ' ' ||" +
                    " description || ' ' || le1_0.variety) @@ to_tsquery('" + keyword + "')";
            sql = sql.replaceFirst("'SEARCH_STRING'='(.*?)'", searchInsert);
        }
        return sql;
    }
}
