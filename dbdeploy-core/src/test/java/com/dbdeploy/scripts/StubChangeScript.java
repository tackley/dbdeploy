package com.dbdeploy.scripts;

public class StubChangeScript extends ChangeScript {
	private final String changeContents;

	public StubChangeScript(int changeNumber, String description, String changeContents) {
		super(changeNumber, description);
		this.changeContents = changeContents;
	}

	@Override
	public String getContent() {
		return changeContents;
	}
}
