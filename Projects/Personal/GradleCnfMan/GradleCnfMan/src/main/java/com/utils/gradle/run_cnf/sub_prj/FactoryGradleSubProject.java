package com.utils.gradle.run_cnf.sub_prj;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.utils.io.PathUtils;
import com.utils.io.processes.InputStreamReaderThread;
import com.utils.io.processes.ReadBytesHandlerLinesCollect;
import com.utils.log.Logger;

public final class FactoryGradleSubProject {

	private FactoryGradleSubProject() {
	}

	public static void newInstance(
			final String rootProjectPathString,
			final List<GradleSubProject> gradleSubProjectList) {

		try {
			final List<String> commandList = new ArrayList<>();
			final String gradleWrapperPathString =
					PathUtils.computePath(rootProjectPathString, "gradlew.bat");
			commandList.add(gradleWrapperPathString);
			commandList.add("subProjectDependencyTree");

			final Process process = new ProcessBuilder(commandList)
					.directory(new File(rootProjectPathString))
					.redirectErrorStream(true)
					.start();

			final InputStream inputStream = process.getInputStream();
			final ReadBytesHandlerLinesCollect readBytesHandlerLinesCollect =
					new ReadBytesHandlerLinesCollect();
			final InputStreamReaderThread inputStreamReaderThread =
					new InputStreamReaderThread("subProjectDependencyTree input stream reader",
							inputStream, StandardCharsets.UTF_8, readBytesHandlerLinesCollect);
			inputStreamReaderThread.start();

			process.waitFor();
			inputStreamReaderThread.join();

			final List<String> lineList = readBytesHandlerLinesCollect.getLineList();
			final Map<String, GradleSubProject> gradleSubProjectsByPathMap = new HashMap<>();
			boolean insideSubProjectsSection = false;
			for (final String line : lineList) {

				if ("> Task :subProjectDependencyTree".equals(line)) {

					insideSubProjectsSection = true;
					continue;
				}
				if (insideSubProjectsSection) {

					if (StringUtils.isBlank(line)) {

						insideSubProjectsSection = false;
						continue;
					}

					final GradleSubProject gradleSubProject = createGradleSubProject(line);
					final String gradleSubProjectPath = gradleSubProject.getPath();
					gradleSubProjectsByPathMap.putIfAbsent(gradleSubProjectPath, gradleSubProject);
				}
			}

			gradleSubProjectList.addAll(gradleSubProjectsByPathMap.values());

		} catch (final Exception exc) {
			Logger.printException(exc);
		}
	}

	private static GradleSubProject createGradleSubProject(
			final String line) {

		int levelInTree = 0;
		for (int i = 0; i < line.length(); i++) {

			final char ch = line.charAt(i);
			if (ch == '\t') {
				levelInTree++;
			} else {
				break;
			}
		}

		final String subProjectPathString = line.substring(levelInTree);
		final String subProjectName = PathUtils.computeFileName(subProjectPathString);
		return new GradleSubProject(subProjectName, subProjectPathString);
	}
}
