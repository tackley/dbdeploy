package com.dbdeploy.database;

/**
 * Created with IntelliJ IDEA.
 * User: jesuspg
 * Date: 5/25/12
 * Time: 12:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class Delimiter {

    public String getDelimiterString() {
        return delimiterString;
    }

    public void setDelimiterString(String delimiterString) {
        this.delimiterString = delimiterString;
    }

    public DelimiterType getDelimiterType() {
        return delimiterType;
    }

    public void setDelimiterType(DelimiterType delimiterType) {
        this.delimiterType = delimiterType;
    }

    private String delimiterString;
    private DelimiterType delimiterType;


    public Delimiter(String delimiterString, DelimiterType delimiterType) {
        this.delimiterString=delimiterString;
        this.delimiterType=delimiterType;
    }


}
