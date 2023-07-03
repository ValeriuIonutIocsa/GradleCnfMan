package com.utils.gradle.run_cnf;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.utils.io.ListFileUtils;
import com.utils.io.PathUtils;
import com.utils.io.file_deleters.FactoryFileDeleter;
import com.utils.io.folder_creators.FactoryFolderCreator;
import com.utils.log.Logger;
import com.utils.string.StrUtils;
import com.utils.xml.dom.XmlDomUtils;

class GradlePrjCnf {

	private final boolean rootProject;
	private final String gradlePrjName;
	private final String gradlePrjPathString;
	private final List<GradleGradleCnf> gradleGradleCnfList;
	private final List<GradleTestCnf> gradleTestCnfList;

	GradlePrjCnf(
			final boolean rootProject,
			final String gradlePrjName,
			final String gradlePrjPathString,
			final List<GradleGradleCnf> gradleGradleCnfList,
			final List<GradleTestCnf> gradleTestCnfList) {

		this.rootProject = rootProject;
		this.gradlePrjName = gradlePrjName;
		this.gradlePrjPathString = gradlePrjPathString;
		this.gradleGradleCnfList = gradleGradleCnfList;
		this.gradleTestCnfList = gradleTestCnfList;
	}

	void writeEclipseRunCnfFiles() {

		final String runCnfFolderPathString = PathUtils.computePath(gradlePrjPathString, "run");
		final boolean success = FactoryFolderCreator.getInstance()
				.createDirectories(runCnfFolderPathString, false, true);
		if (success) {

			Logger.printNewLine();
			Logger.printProgress("deleting existing invalid Eclipse run cnf files for module:");
			Logger.printLine(gradlePrjPathString);

			deleteExistingEclipseRunCnfFiles(runCnfFolderPathString);

			Logger.printNewLine();
			Logger.printProgress("writing Eclipse run cnf files for module:");
			Logger.printLine(gradlePrjPathString);

			if (rootProject) {
				GradleExternalCnf.writeEclipseExternalCnfFile(gradlePrjName, runCnfFolderPathString);
			}

			for (final GradleGradleCnf gradleGradleCnf : gradleGradleCnfList) {
				gradleGradleCnf.writeEclipseRunCnfFile(gradlePrjName, runCnfFolderPathString);
			}

			for (final GradleTestCnf gradleTestCnf : gradleTestCnfList) {
				gradleTestCnf.writeEclipseRunCnfFile(gradlePrjName, runCnfFolderPathString);
			}
		}
	}

	private void deleteExistingEclipseRunCnfFiles(
			final String runCnfFolderPathString) {

		final Set<String> testEclipseRunCnfFileNameSet = new HashSet<>();
		for (final GradleTestCnf gradleTestCnf : gradleTestCnfList) {

			final String testEclipseRunCnfFileName = gradleTestCnf.createEclipseRunCnfFileName();
			testEclipseRunCnfFileNameSet.add(testEclipseRunCnfFileName);
		}

		final List<String> eclipseRunCnfFilePathStringList = ListFileUtils.listFiles(runCnfFolderPathString,
				filePath -> filePath.toString().endsWith(".launch"));
		for (final String eclipseRunCnfFilePathString : eclipseRunCnfFilePathStringList) {

			final String eclipseRunCnfType = parseEclipseRunCnfType(eclipseRunCnfFilePathString);
			if ("org.eclipse.jdt.junit.launchconfig".equals(eclipseRunCnfType)) {

				final String eclipseRunCnfFileName = PathUtils.computeFileName(eclipseRunCnfFilePathString);
				if (!testEclipseRunCnfFileNameSet.contains(eclipseRunCnfFileName)) {

					Logger.printProgress("deleting Eclipse run cnf file:");
					Logger.printLine(eclipseRunCnfFilePathString);
					FactoryFileDeleter.getInstance().deleteFile(eclipseRunCnfFilePathString, false, true);
				}
			}
		}
	}

	private static String parseEclipseRunCnfType(
			final String eclipseRunCnfFilePathString) {

		String eclipseRunCnfType = "";
		try {
			final Document document = XmlDomUtils.openDocument(eclipseRunCnfFilePathString);
			final Element documentElement = document.getDocumentElement();
			if (documentElement != null) {

				final String tagName = documentElement.getTagName();
				if ("launchConfiguration".equals(tagName)) {
					eclipseRunCnfType = documentElement.getAttribute("type");
				}
			}

		} catch (final Exception exc) {
			Logger.printError("failed to parse Eclipse run cnf type!");
			Logger.printException(exc);
		}
		return eclipseRunCnfType;
	}

	void fillTestIdeaRunCnfFileNameSet(
			final Set<String> testIdeaRunCnfFileNameSet) {

		for (final GradleTestCnf gradleTestCnf : gradleTestCnfList) {

			final String testIdeaRunCnfName = gradleTestCnf.createIdeaRunCnfName();
			testIdeaRunCnfFileNameSet.add(testIdeaRunCnfName);
		}
	}

	void writeIdeaRunCnfFiles(
			final String rootPrjName,
			final String runCnfFolderPathString) {

		Logger.printNewLine();
		Logger.printProgress("writing IntelliJ Idea run cnf files for module:");
		Logger.printLine(gradlePrjPathString);

		for (final GradleGradleCnf gradleGradleCnf : gradleGradleCnfList) {
			gradleGradleCnf.writeIdeaGradleCnfFile(gradlePrjName, runCnfFolderPathString);
		}

		for (final GradleTestCnf gradleTestCnf : gradleTestCnfList) {
			gradleTestCnf.writeIdeaRunCnfFile(rootPrjName, gradlePrjName, runCnfFolderPathString);
		}
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}
}
