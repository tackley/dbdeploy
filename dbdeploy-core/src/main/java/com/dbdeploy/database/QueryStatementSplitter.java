package com.dbdeploy.database;

import org.apache.commons.lang.text.StrBuilder;
import org.apache.commons.lang.text.StrMatcher;
import org.apache.commons.lang.text.StrTokenizer;

import java.util.ArrayList;
import java.util.List;

public class QueryStatementSplitter {
    private String delimiter = ";";
    private DelimiterType delimiterType = DelimiterType.normal;

    public QueryStatementSplitter() {
    }

    public List<String> split(String input) {
        List<String> statements = new ArrayList<String>();
        StrBuilder currentSql = new StrBuilder();

        StrTokenizer lineTokenizer = new StrTokenizer(input);
        lineTokenizer.setDelimiterMatcher(StrMatcher.charSetMatcher("\r\n"));
        lineTokenizer.setTrimmerMatcher(StrMatcher.trimMatcher());

        for (String line : lineTokenizer.getTokenArray()) {
            if (!currentSql.isEmpty()) {
                currentSql.append("\n");
            }

            currentSql.append(line);

           if (delimiterType.matches(line, delimiter)) {
                statements.add(currentSql.substring(0, currentSql.length() - delimiter.length()));
                currentSql.clear();
            }
        }

        if (!currentSql.isEmpty()) {
            statements.add(currentSql.toString());
        }
        
        return statements;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public DelimiterType getDelimiterType() {
        return delimiterType;
    }

    public void setDelimiterType(DelimiterType delimiterType) {
        this.delimiterType = delimiterType;
    }
}
