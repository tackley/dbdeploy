package com.dbdeploy.database;

import org.apache.commons.lang.SystemUtils;

public enum LineEnding {
	platform { public String get() { return SystemUtils.LINE_SEPARATOR; } },
	cr { public String get() { return "\r"; } },
	crlf { public String get() { return "\r\n"; } },
	lf { public String get() { return "\n"; } };

	abstract public String get();
}
