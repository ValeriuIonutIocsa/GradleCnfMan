package com.utils.gradle.run_cnf;

import org.junit.jupiter.api.Test;

class RunCnfCreatorTest {

	@Test
	void testWork() {

		final String rootProjectPathString;
		final int input = Integer.parseInt("1");
		if (input == 1) {
			rootProjectPathString =
					"C:\\IVI\\Prog\\JavaGradle\\Scripts\\General\\GradleCnfMan\\" +
							"Projects\\Personal\\GradleCnfManAllModules\\GradleCnfManAllModules";

		} else if (input == 11) {
			rootProjectPathString =
					"C:\\IVI\\Vitesco\\Main\\Projects\\CRO\\ProjectAnalyzerAllModules\\ProjectAnalyzerAllModules";
		} else if (input == 12) {
			rootProjectPathString =
					"C:\\IVI\\Prog\\Main\\GradleJava\\Projects\\_ALL_\\AllProjects\\AllProjects";

		} else if (input == 21) {
			rootProjectPathString =
					"C:\\IVI\\Conti\\Main\\Projects\\ADV\\ConsistencyNeedAnalyzer\\ConsistencyNeedAnalyzer";

		} else {
			throw new RuntimeException();
		}
		RunCnfCreator.work(rootProjectPathString);
	}
}
