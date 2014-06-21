package com.dbdeploy.mojo;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import com.dbdeploy.DbDeploy;
import com.dbdeploy.database.DelimiterType;
import com.dbdeploy.database.LineEnding;

/**
 * Abstract class that all dbdeploy database goals should extend.
 */
public abstract class AbstractDbDeployMojo extends AbstractMojo {
    /**
     * Full or relative path to the directory containing the delta scripts.
     *
     * @parameter expression="${dbdeploy.scriptdirectory}" default-value="${project.src.directory}/main/sql"
     * @required
     */
    protected File scriptdirectory;

	/**
	 * Encoding to use for change scripts and output files.
	 *
	 * @parameter expression="${dbdeploy.encoding}" default-value="${project.build.sourceEncoding}"
	 */
	protected String encoding;


    /**
     * Specifies the jdbc driver.
     *
     * @parameter expression="${dbdeploy.driver}"
     * @required
     */
    protected String driver;

    /**
     * Specifies the url of the database that the deltas are to be applied to. 
     *
     * @parameter expression="${dbdeploy.url}"
     * @required
     */
    protected String url;

    /**
     * The password of the dbms user who has permissions to select from the
     * schema version table.
     *
     * @parameter expression="${dbdeploy.password}"
     */
    protected String password;

    /**
     * The ID of a dbms user who has permissions to select from the schema
     * version table.
     *
     * @parameter expression="${dbdeploy.userid}"
     * @required
     */
    protected String userid;

    /**
     * The name of the changelog table to use. Useful if you need to separate
     * DDL and DML when deploying to replicated environments. If not supplied
     * defaults to "changelog"
     *
     * @parameter expression="${dbdeploy.changeLogTableName}"
     */
    protected String changeLogTableName;

    /**
     * Delimiter to use to separate scripts into statements, if dbdeploy will
     * apply the scripts for you i.e. you haven't specified outputfile. Default ;
     *
     * @parameter expression="${dbdeploy.delimiter}"
     */
    protected String delimiter;

    /**
     * Either normal: split on delimiter wherever it occurs or row  only split
     * on delimiter if it features on a line by itself. Default normal.
     *
     * @parameter expression="${dbdeploy.delimiterType}"
     */
    protected String delimiterType;

	/**
	 * Line ending to separate indiviual statement lines when applying directly
	 * to the database. Can be platform (the default line ending for the current platform),
	 * cr, crlf or lf. Default platform.
	 *
	 * @parameter expression="${dbdeploy.lineEnding}"
	 */
	protected String lineEnding;

    /**
     * The highest numbered delta script to apply.
     *
     * @parameter expression="${dbdeploy.lastChange}"
     */
    protected Long lastChangeToApply;

    /**
    * The change scripts to ignore.
    *
    * @parameter expression="${dbdeploy.changeScriptIdsToIgnore}"
    */
	protected List<String> changeScriptIdsToIgnore;

    protected DbDeploy getConfiguredDbDeploy() {
        DbDeploy dbDeploy = new DbDeploy();
        dbDeploy.setScriptdirectory(scriptdirectory);
        dbDeploy.setDriver(driver);
        dbDeploy.setUrl(url);
        dbDeploy.setPassword(getPassword());
        dbDeploy.setUserid(userid);

	    if (encoding != null) {
	        dbDeploy.setEncoding(encoding);
	    }

        if (lastChangeToApply != null) {
            dbDeploy.setLastChangeToApply(lastChangeToApply);
        }

        if (changeLogTableName != null) {
            dbDeploy.setChangeLogTableName(changeLogTableName);
        }

        if (delimiter != null) {
            dbDeploy.setDelimiter(delimiter);
        }

        if (delimiterType != null) {
            dbDeploy.setDelimiterType(DelimiterType.valueOf(delimiterType));
        }

	    if (lineEnding != null) {
		    dbDeploy.setLineEnding(LineEnding.valueOf(lineEnding));
	    }
	    
	    if (changeScriptIdsToIgnore != null && !changeScriptIdsToIgnore.isEmpty()) {
	    	List<Long> changeScriptLongIdsToIgnore = toLongs(changeScriptIdsToIgnore);
			dbDeploy.setChangeScriptIdsToIgnore(changeScriptLongIdsToIgnore);
	    }

        return dbDeploy;
    }

	private String getPassword() {
		return isEncrypted(password) ? unwrapAndDecrypt(password) : password;
	}

	private boolean isEncrypted(String password) {
		return password != null && password.startsWith("ENC(") && password.endsWith(")");
	}
	
	private String unwrapAndDecrypt(String password) {
		return decrypt(unwrap(password));
	}
	
	private String unwrap(String password) {
		return password.substring(4, password.length() - 1);
	}
	
	private String decrypt(String encrypted) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		String encryptionKey = System.getProperty("encryptionKey");
		if (encryptionKey == null || encryptionKey.trim().isEmpty()) {
			throw new IllegalArgumentException("VM property 'encryptionKey' must be set when using encrypted password.");
		}
		encryptor.setPassword(encryptionKey);

		try {
			return encryptor.decrypt(encrypted);
		} catch (Exception e) {
			throw new RuntimeException("There was an error decrypting the password. Make sure it is a valid encrypted password.", e);
		}
	}
	
	private List<Long> toLongs(List<String> stringIds) {
		List<Long> longIds = new ArrayList<Long>();
		for (String id : stringIds) {
			longIds.add(Long.valueOf(id));
		}
		return longIds;
	}
}
