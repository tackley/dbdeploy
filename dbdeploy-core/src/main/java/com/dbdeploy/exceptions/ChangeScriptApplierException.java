package com.dbdeploy.exceptions;

import java.util.List;

public class ChangeScriptApplierException extends DbDeployException {
    List<ChangeScriptFailedException> changeScriptExceptions;

    public ChangeScriptApplierException(List<ChangeScriptFailedException> changeScriptExceptions) {
        this.changeScriptExceptions = changeScriptExceptions;
    }

    public List<ChangeScriptFailedException> getChangeScriptExceptions() {
        return changeScriptExceptions;
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder();
        message.append(buildMessage());
        message.append("\n");
        message.append("\n");
        message.append(buildSuggession());
        return message.toString();

    }

    private String buildSuggession() {
        StringBuilder suggession = new StringBuilder();
        suggession.append("If these scripts are already executed, run following command");
        suggession.append("\n");
        suggession.append("gradle -PjdbcUrl=<JDBC_URL> -PjdbcUserName=<JDBC_USERNAME> -PjdbcPassword=<JDBC_PASSWORD> -Pmigrations=");
        suggession.append(changeScriptNamesCsv());
        suggession.append(" :consultingdb:putMigrationInVersionTable");
        return suggession.toString();

    }

    private String changeScriptNamesCsv() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < changeScriptExceptions.size(); i++) {
            String scriptName = changeScriptExceptions.get(i).getScript().getFile().getName();
            sb.append(scriptName);
            if (i != (changeScriptExceptions.size() - 1)) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private String buildMessage() {
        StringBuilder message = new StringBuilder();
        for (ChangeScriptFailedException changeScriptException : changeScriptExceptions) {
            message.append(changeScriptException.getMessage());
            message.append("\n");
        }
        return message.toString();
    }
}
