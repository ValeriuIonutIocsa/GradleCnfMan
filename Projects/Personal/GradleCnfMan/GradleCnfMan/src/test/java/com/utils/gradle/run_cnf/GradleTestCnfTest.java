package com.utils.gradle.run_cnf;

import org.junit.jupiter.api.Test;

class GradleTestCnfTest {

	@Test
	void testWriteIdeaRunCnfFile() {

		final String pkgName = "com.utils.gradle.run_cnf";
		final String runCnfCreatorTest = "RunCnfCreatorTest";
		final String testWork = "testWork";
		final GradleTestCnf gradleTestCnf =
				new GradleTestCnf(pkgName, runCnfCreatorTest, testWork);
		final String allProjects = "AllProjects";
		final String gradleCnfMan = "GradleCnfMan";
		final String runCnfFolderPathString =
				"C:\\IVI\\Conti\\Main\\Projects\\_ALL_\\AllProjects\\AllProjects\\.idea\\runConfigurations";
		gradleTestCnf.writeIdeaRunCnfFile(allProjects, gradleCnfMan, runCnfFolderPathString);
	}
}
