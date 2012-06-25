package com.dbdeploy.database;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.commons.lang.text.StrMatcher;
import org.apache.commons.lang.text.StrTokenizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryStatementSplitterExt {

    private Map<String,Delimiter> delimitersMap=new HashMap<String, Delimiter>(2);

    private Delimiter defaultDelimiter;
	private LineEnding lineEnding = LineEnding.platform;

	public QueryStatementSplitterExt(String delimiter,DelimiterType delimiterType) {
             defaultDelimiter=new Delimiter(delimiter,delimiterType);

    }

    public QueryStatementSplitterExt() {
        defaultDelimiter=new Delimiter(";",DelimiterType.normal);
    }

    public List<String> split(String input) {
        List<String> statements = new ArrayList<String>();
        StrBuilder currentSql = new StrBuilder();

        StrTokenizer lineTokenizer = new StrTokenizer(input);
        lineTokenizer.setDelimiterMatcher(StrMatcher.charSetMatcher("\r\n"));

        Delimiter currentDelimiter= defaultDelimiter;

        for (String line : lineTokenizer.getTokenArray()) {
	        String strippedLine = StringUtils.stripEnd(line, null);
            if (!currentSql.isEmpty()) {
                currentSql.append(lineEnding.get());
            }

            if (strippedLine.startsWith("--//#")) {
                currentDelimiter=delimitersMap.get(strippedLine.substring(5));

            }else {

                currentSql.append(strippedLine);

               String delimiter=currentDelimiter.getDelimiterString();
               DelimiterType delimiterType = currentDelimiter.getDelimiterType();
               if (delimiterType.matches(strippedLine, delimiter)) {
                    statements.add(currentSql.substring(0, currentSql.length() - delimiter.length()));
                    currentSql.clear();
                }
            }
        }

        if (!currentSql.isEmpty()) {
            statements.add(currentSql.toString());
        }
        
        return statements;
    }



	public void setOutputLineEnding(LineEnding lineEnding) {
		this.lineEnding = lineEnding;
	}


    public void addDelimiter(String key, Delimiter delimiter) {
        delimitersMap.put(key,delimiter);
    }
}
