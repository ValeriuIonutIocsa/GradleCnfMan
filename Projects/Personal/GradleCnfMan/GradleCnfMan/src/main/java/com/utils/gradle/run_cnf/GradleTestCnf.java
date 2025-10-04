package com.utils.gradle.run_cnf;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.Strings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.utils.io.PathUtils;
import com.utils.io.ResourceFileUtils;
import com.utils.io.WriterUtils;
import com.utils.log.Logger;
import com.utils.string.StrUtils;
import com.utils.xml.dom.XmlDomUtils;

class GradleTestCnf {

	private final String pkgName;
	private final String testClsName;
	private final String testMethodName;

	GradleTestCnf(
			final String pkgName,
			final String testClsName,
			final String testMethodName) {

		this.pkgName = pkgName;
		this.testClsName = testClsName;
		this.testMethodName = testMethodName;
	}

	public void writeEclipseRunCnfFile(
			final String gradlePrjName,
			final String runCnfFolderPathString) {

		final String eclipseRunCnfFileName = createEclipseRunCnfFileName();
		try {
			Logger.printProgress("creating Eclipse test run cnf file:");
			Logger.printLine(eclipseRunCnfFileName);

			final String runCnfFilePathString =
					PathUtils.computePath(runCnfFolderPathString, eclipseRunCnfFileName);

			final Document document = XmlDomUtils.createNewDocument();
			final Element documentElement = document.createElement("launchConfiguration");
			documentElement.setAttribute("type", "org.eclipse.jdt.junit.launchconfig");

			writeEclipseRunCnfMappedResourcePaths(documentElement, gradlePrjName);
			writeEclipseRunCnfMappedResourceTypes(documentElement);
			writeEclipseRunCnfFavoriteGroups(documentElement);
			writeEclipseRunCnfAttribute(documentElement,
					"stringAttribute", "org.eclipse.jdt.junit.CONTAINER", "");
			writeEclipseRunCnfAttribute(documentElement,
					"booleanAttribute", "org.eclipse.jdt.junit.KEEPRUNNING_ATTR",
					Boolean.FALSE.toString());
			writeEclipseRunCnfAttribute(documentElement,
					"stringAttribute", "org.eclipse.jdt.junit.TESTNAME", testMethodName);
			writeEclipseRunCnfAttribute(documentElement,
					"stringAttribute", "org.eclipse.jdt.junit.TEST_KIND",
					"org.eclipse.jdt.junit.loader.junit5");
			writeEclipseRunCnfAttribute(documentElement,
					"booleanAttribute", "org.eclipse.jdt.launching.ATTR_ATTR_USE_ARGFILE",
					Boolean.FALSE.toString());
			writeEclipseRunCnfAttribute(documentElement,
					"booleanAttribute", "org.eclipse.jdt.launching.ATTR_USE_CLASSPATH_ONLY_JAR",
					Boolean.FALSE.toString());
			writeEclipseRunCnfAttribute(documentElement,
					"stringAttribute", "org.eclipse.jdt.launching.MAIN_TYPE",
					pkgName + "." + testClsName);
			writeEclipseRunCnfAttribute(documentElement,
					"stringAttribute", "org.eclipse.jdt.launching.PROJECT_ATTR",
					gradlePrjName);
			writeEclipseRunCnfAttribute(documentElement,
					"stringAttribute", "org.eclipse.jdt.launching.VM_ARGUMENTS", "-ea");

			document.appendChild(documentElement);

			XmlDomUtils.saveXmlFile(document, false, 4, runCnfFilePathString);

		} catch (final Throwable throwable) {
			Logger.printError("failed to write Eclipse run cnf file:" +
					System.lineSeparator() + eclipseRunCnfFileName);
			Logger.printThrowable(throwable);
		}
	}

	String createEclipseRunCnfFileName() {
		return testClsName + "." + testMethodName + ".launch";
	}

	private void writeEclipseRunCnfMappedResourcePaths(
			final Element documentElement,
			final String gradlePrjName) {

		final Document document = documentElement.getOwnerDocument();
		final Element listAttributeElement = document.createElement("listAttribute");
		listAttributeElement.setAttribute("key", "org.eclipse.debug.core.MAPPED_RESOURCE_PATHS");

		final Element listEntryElement = document.createElement("listEntry");
		final String mappedResourcePath = "/" + gradlePrjName + "/" +
				pkgName.replace('.', '/') + "/" + testClsName + ".java";
		listEntryElement.setAttribute("value", mappedResourcePath);

		listAttributeElement.appendChild(listEntryElement);

		documentElement.appendChild(listAttributeElement);
	}

	private static void writeEclipseRunCnfMappedResourceTypes(
			final Element documentElement) {

		final Document document = documentElement.getOwnerDocument();
		final Element listAttributeElement = document.createElement("listAttribute");
		listAttributeElement.setAttribute("key", "org.eclipse.debug.core.MAPPED_RESOURCE_TYPES");

		final Element listEntryElement = document.createElement("listEntry");
		listEntryElement.setAttribute("value", "1");

		listAttributeElement.appendChild(listEntryElement);

		documentElement.appendChild(listAttributeElement);
	}

	private static void writeEclipseRunCnfFavoriteGroups(
			final Element documentElement) {

		final Document document = documentElement.getOwnerDocument();
		final Element listAttributeElement = document.createElement("listAttribute");
		listAttributeElement.setAttribute("key", "org.eclipse.debug.ui.favoriteGroups");

		final Element debugListEntryElement = document.createElement("listEntry");
		debugListEntryElement.setAttribute("value", "org.eclipse.debug.ui.launchGroup.debug");
		listAttributeElement.appendChild(debugListEntryElement);

		final Element coverageListEntryElement = document.createElement("listEntry");
		coverageListEntryElement.setAttribute("value", "org.eclipse.eclemma.ui.launchGroup.coverage");
		listAttributeElement.appendChild(coverageListEntryElement);

		final Element runListEntryElement = document.createElement("listEntry");
		runListEntryElement.setAttribute("value", "org.eclipse.debug.ui.launchGroup.run");
		listAttributeElement.appendChild(runListEntryElement);

		documentElement.appendChild(listAttributeElement);
	}

	private static void writeEclipseRunCnfAttribute(
			final Element documentElement,
			final String tagName,
			final String key,
			final String value) {

		final Document document = documentElement.getOwnerDocument();
		final Element stringAttributeElement = document.createElement(tagName);
		stringAttributeElement.setAttribute("key", key);
		stringAttributeElement.setAttribute("value", value);

		documentElement.appendChild(stringAttributeElement);
	}

	void writeIdeaRunCnfFile(
			final String rootPrjName,
			final String gradlePrjName,
			final String runCnfFolderPathString) {

		final String runCnfFileName = createIdeaRunCnfName();
		final String runCnfFilePathString =
				PathUtils.computePath(runCnfFolderPathString, runCnfFileName);
		try {
			Logger.printProgress("writing IntelliJ Idea test run cnf file:");
			Logger.printLine(runCnfFilePathString);

			String runCnfContent = ResourceFileUtils
					.resourceFileToString("com/utils/gradle/run_cnf/intellij_run_cnf_test.xml");

			final String cnfName = testClsName + "." + testMethodName;
			runCnfContent = Strings.CS.replace(runCnfContent, "@@CNF_NAME@@", cnfName);

			runCnfContent = Strings.CS.replace(runCnfContent, "@@FOLDER_NAME@@", gradlePrjName);

			String moduleName;
			if (!rootPrjName.equals(gradlePrjName)) {
				moduleName = rootPrjName + "." + gradlePrjName;
			} else {
				moduleName = rootPrjName;
			}
			moduleName += ".test";
			runCnfContent = Strings.CS.replace(runCnfContent, "@@MODULE_NAME@@", moduleName);

			runCnfContent = Strings.CS.replace(runCnfContent, "@@PACKAGE_NAME@@", pkgName);

			final String mainClassName = pkgName + "." + testClsName;
			runCnfContent = Strings.CS.replace(runCnfContent, "@@MAIN_CLASS_NAME@@", mainClassName);

			runCnfContent = Strings.CS.replace(runCnfContent, "@@METHOD_NAME@@", testMethodName);

			WriterUtils.tryStringToFile(runCnfContent, StandardCharsets.UTF_8, runCnfFilePathString);

		} catch (final Throwable throwable) {
			Logger.printError("failed to write Idea run cnf file:" +
					System.lineSeparator() + runCnfFilePathString);
			Logger.printThrowable(throwable);
		}
	}

	String createIdeaRunCnfName() {

		return testClsName + "_" + testMethodName + ".xml";
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}
}
