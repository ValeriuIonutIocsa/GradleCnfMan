package com.utils.gradle.cnf.settings;

import java.util.List;

import com.utils.gradle.cnf.roots.GradleProjectRoot;
import com.utils.string.StrUtils;

public class GradleCnfManSettings {

	private final List<GradleProjectRoot> gradleProjectRootsList;

	GradleCnfManSettings(
			final List<GradleProjectRoot> gradleProjectRootsList) {

		this.gradleProjectRootsList = gradleProjectRootsList;
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}

	public List<GradleProjectRoot> getGradleProjectRootsList() {
		return gradleProjectRootsList;
	}
}
