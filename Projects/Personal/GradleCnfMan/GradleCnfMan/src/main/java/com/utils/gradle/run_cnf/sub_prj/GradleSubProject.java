package com.utils.gradle.run_cnf.sub_prj;

import com.utils.string.StrUtils;

public class GradleSubProject {

	private final String name;
	private final String path;

	GradleSubProject(
			final String name,
			final String path) {

		this.name = name;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}
}
