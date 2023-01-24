package com.utils.gradle.cnf.roots.files;

public class CnfFileGradleWrapperProperties extends AbstractCnfFile {

	@Override
	String[] getDestLocation() {

		final String sourceFileName = getSourceFileName();
		return new String[] { "gradle", "wrapper", sourceFileName };
	}

	@Override
	String getSourceFileName() {
		return "gradle-wrapper.properties";
	}
}
