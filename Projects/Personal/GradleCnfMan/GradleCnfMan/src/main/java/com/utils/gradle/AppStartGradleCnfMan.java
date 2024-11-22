package com.utils.gradle;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.utils.cli.CliUtils;
import com.utils.gradle.cnf.roots.GradleProjectRoot;
import com.utils.gradle.cnf.settings.FactoryGradleCnfManSettings;
import com.utils.gradle.cnf.settings.GradleCnfManSettings;
import com.utils.gradle.run_cnf.RunCnfCreator;
import com.utils.log.Logger;

final class AppStartGradleCnfMan {

	private AppStartGradleCnfMan() {
	}

	public static void main(
			final String[] args) {

		final Instant start = Instant.now();

		if (args.length < 1) {

			final String helpMessage = createHelpMessage();
			Logger.printError("insufficient arguments" + System.lineSeparator() + helpMessage);
			System.exit(-1);
		}

		if ("--help".equals(args[0])) {

			final String helpMessage = createHelpMessage();
			Logger.printLine(helpMessage);
			System.exit(0);
		}

		Logger.printProgress("starting GradleCnfMan");

		final Map<String, String> cliArgsByNameMap = new HashMap<>();
		CliUtils.fillCliArgsByNameMap(args, cliArgsByNameMap);

		final String debugModeString = cliArgsByNameMap.get("debug");
		final boolean debugMode = Boolean.parseBoolean(debugModeString);
		Logger.setDebugMode(debugMode);

		final String mode = cliArgsByNameMap.get("mode");
		Logger.printLine("mode: " + mode);
		if ("create_configurations".equals(mode)) {

			final GradleCnfManSettings gradleCnfManSettings = FactoryGradleCnfManSettings.newInstance();
			if (gradleCnfManSettings != null) {

				final List<GradleProjectRoot> gradleProjectRootsList =
						gradleCnfManSettings.getGradleProjectRootsList();
				for (final GradleProjectRoot gradleProjectRoot : gradleProjectRootsList) {
					gradleProjectRoot.createConfigurationFiles();
				}
			}

		} else if ("create_run_configurations".equals(mode)) {

			final String rootProjectPathString = cliArgsByNameMap.get("root_project_path");
			RunCnfCreator.work(rootProjectPathString);

		} else {
			Logger.printError("invalid or missing \"mode\" command line argument");
		}

		Logger.printNewLine();
		Logger.printFinishMessage(start);
	}

	private static String createHelpMessage() {

		return "usage:" + System.lineSeparator() +
				"gradle_cnf_man [--debug=(true,false)] --mode=create_configurations" + System.lineSeparator() +
				"gradle_cnf_man [--debug=(true,false)] --mode=create_run_configurations " +
				"--root_project_path=ROOT_PROJECT_PATH";
	}
}
