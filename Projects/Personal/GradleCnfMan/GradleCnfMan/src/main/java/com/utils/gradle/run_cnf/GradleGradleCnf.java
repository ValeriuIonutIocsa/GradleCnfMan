package com.utils.gradle.run_cnf;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.utils.io.PathUtils;
import com.utils.log.Logger;
import com.utils.xml.dom.XmlDomUtils;

final class GradleGradleCnf {

	private final String[] gradleTaskArray;

	GradleGradleCnf(
			final String[] gradleTaskArray) {

		this.gradleTaskArray = gradleTaskArray;
	}

	void writeEclipseRunCnfFile(
			final String gradlePrjName,
			final String runCnfFolderPathString) {

		final String runCnfFileName = gradlePrjName + " [" + StringUtils.join(gradleTaskArray, ' ') + "].launch";
		final String runCnfFilePathString =
				PathUtils.computePath(runCnfFolderPathString, runCnfFileName);
		try {
			Logger.printProgress("writing Eclipse Gradle run cnf file:");
			Logger.printLine(runCnfFilePathString);

			final Document document = XmlDomUtils.createNewDocument();
			final Element documentElement = document.createElement("launchConfiguration");
			documentElement.setAttribute("type", "org.eclipse.buildship.core.launch.runconfiguration");

			EclipseRunCnfUtils.writeEclipseRunCnfAttribute("listAttribute", "arguments",
					null, null, documentElement);
			EclipseRunCnfUtils.writeEclipseRunCnfAttribute("stringAttribute", "bad_container_name",
					"\\" + gradlePrjName + "\\r", null, documentElement);
			EclipseRunCnfUtils.writeEclipseRunCnfAttribute("booleanAttribute", "build_scans_enabled",
					"false", null, documentElement);
			EclipseRunCnfUtils.writeEclipseRunCnfAttribute("stringAttribute", "gradle_distribution",
					"GRADLE_DISTRIBUTION(WRAPPER)", null, documentElement);
			EclipseRunCnfUtils.writeEclipseRunCnfAttribute("stringAttribute", "gradle_user_home",
					"", null, documentElement);
			EclipseRunCnfUtils.writeEclipseRunCnfAttribute("stringAttribute", "java_home",
					"", null, documentElement);
			EclipseRunCnfUtils.writeEclipseRunCnfAttribute("listAttribute", "jvm_arguments",
					null, null, documentElement);
			EclipseRunCnfUtils.writeEclipseRunCnfAttribute("booleanAttribute", "offline_mode",
					"false", null, documentElement);

			EclipseRunCnfUtils.writeEclipseRunCnfAttribute("listAttribute", "org.eclipse.debug.ui.favoriteGroups",
					null, new String[] { "org.eclipse.debug.ui.launchGroup.run" }, documentElement);

			EclipseRunCnfUtils.writeEclipseRunCnfAttribute("booleanAttribute", "override_workspace_settings",
					"false", null, documentElement);
			EclipseRunCnfUtils.writeEclipseRunCnfAttribute("booleanAttribute", "show_console_view",
					"true", null, documentElement);
			EclipseRunCnfUtils.writeEclipseRunCnfAttribute("booleanAttribute", "show_execution_view",
					"true", null, documentElement);

			EclipseRunCnfUtils.writeEclipseRunCnfAttribute("listAttribute", "tasks",
					null, gradleTaskArray, documentElement);

			EclipseRunCnfUtils.writeEclipseRunCnfAttribute("stringAttribute", "working_dir",
					"${workspace_loc:/" + gradlePrjName + "}", null, documentElement);

			document.appendChild(documentElement);

			XmlDomUtils.saveXmlFile(document, false, 4, runCnfFilePathString);

		} catch (final Throwable throwable) {
			Logger.printError("failed to write Idea run cnf file:" +
					System.lineSeparator() + runCnfFilePathString);
			Logger.printThrowable(throwable);
		}
	}

	void writeIdeaGradleCnfFile(
			final String gradlePrjName,
			final String gradlePrjPathString,
			final String runCnfFolderPathString) {

		final String runCnfFileName =
				gradlePrjName + "__" + StringUtils.join(gradleTaskArray, '_') + "_.xml";
		final String runCnfFilePathString =
				PathUtils.computePath(runCnfFolderPathString, runCnfFileName);
		try {
			Logger.printProgress("writing IntelliJ Idea Gradle run cnf file:");
			Logger.printLine(runCnfFilePathString);

			final Document document = XmlDomUtils.createNewDocument();
			final Element documentElement = document.createElement("component");
			documentElement.setAttribute("name", "ProjectRunConfigurationManager");

			final Element configurationElement = document.createElement("configuration");
			configurationElement.setAttribute("default", Boolean.FALSE.toString());
			final String cnfName = gradlePrjName + " [" + StringUtils.join(gradleTaskArray, ' ') + "]";
			configurationElement.setAttribute("name", cnfName);
			configurationElement.setAttribute("type", "GradleRunConfiguration");
			configurationElement.setAttribute("factoryName", "Gradle");
			configurationElement.setAttribute("folderName", gradlePrjName);

			writeExternalSystemSettings(gradlePrjPathString, configurationElement);

			writeExternalSystemDebugServerProcess(configurationElement);
			writeExternalSystemReattachDebugProcess(configurationElement);
			writeDebugAllEnabled(configurationElement);
			writeMethod(configurationElement);

			documentElement.appendChild(configurationElement);

			document.appendChild(documentElement);

			XmlDomUtils.saveXmlFile(document, true, 4, runCnfFilePathString);

		} catch (final Throwable throwable) {
			Logger.printError("failed to write Idea run cnf file:" +
					System.lineSeparator() + runCnfFilePathString);
			Logger.printThrowable(throwable);
		}
	}

	private void writeExternalSystemSettings(
			final String gradlePrjPathString,
			final Element configurationElement) {

		final Document document = configurationElement.getOwnerDocument();
		final Element externalSystemSettingsElement = document.createElement("ExternalSystemSettings");

		writeExecutionNameOption(externalSystemSettingsElement);
		writeExternalProjectPathOption(gradlePrjPathString, externalSystemSettingsElement);
		writeExternalSystemIdStringOption(externalSystemSettingsElement);
		writeScriptParametersOption(externalSystemSettingsElement);
		writeTaskDescriptionsOption(externalSystemSettingsElement);
		writeTaskNamesOption(externalSystemSettingsElement);
		writeVmOptionsOption(externalSystemSettingsElement);

		configurationElement.appendChild(externalSystemSettingsElement);
	}

	private static void writeExecutionNameOption(
			final Element externalSystemSettingsElement) {

		final Document document = externalSystemSettingsElement.getOwnerDocument();
		final Element optionElement = document.createElement("option");
		optionElement.setAttribute("name", "executionName");

		externalSystemSettingsElement.appendChild(optionElement);
	}

	private static void writeExternalProjectPathOption(
			final String gradlePrjPathString,
			final Element externalSystemSettingsElement) {

		final Document document = externalSystemSettingsElement.getOwnerDocument();
		final Element optionElement = document.createElement("option");
		optionElement.setAttribute("name", "externalProjectPath");

		String gradlePrjRelativePathString = PathUtils.computeRelativePath(
				PathUtils.computeParentPath(gradlePrjPathString, 4), gradlePrjPathString);
		gradlePrjRelativePathString = gradlePrjRelativePathString.replace('\\', '/');
		final String value = "$PROJECT_DIR$/../../../../" + gradlePrjRelativePathString;
		optionElement.setAttribute("value", value);

		externalSystemSettingsElement.appendChild(optionElement);
	}

	private static void writeExternalSystemIdStringOption(
			final Element externalSystemSettingsElement) {

		final Document document = externalSystemSettingsElement.getOwnerDocument();
		final Element optionElement = document.createElement("option");
		optionElement.setAttribute("name", "externalSystemIdString");
		optionElement.setAttribute("value", "GRADLE");

		externalSystemSettingsElement.appendChild(optionElement);
	}

	private static void writeScriptParametersOption(
			final Element externalSystemSettingsElement) {

		final Document document = externalSystemSettingsElement.getOwnerDocument();
		final Element optionElement = document.createElement("option");
		optionElement.setAttribute("name", "scriptParameters");
		optionElement.setAttribute("value", "");

		externalSystemSettingsElement.appendChild(optionElement);
	}

	private static void writeTaskDescriptionsOption(
			final Element externalSystemSettingsElement) {

		final Document document = externalSystemSettingsElement.getOwnerDocument();
		final Element optionElement = document.createElement("option");
		optionElement.setAttribute("name", "taskDescriptions");

		final Element listElement = document.createElement("list");
		optionElement.appendChild(listElement);

		externalSystemSettingsElement.appendChild(optionElement);
	}

	private void writeTaskNamesOption(
			final Element externalSystemSettingsElement) {

		final Document document = externalSystemSettingsElement.getOwnerDocument();
		final Element optionElement = document.createElement("option");
		optionElement.setAttribute("name", "taskNames");

		final Element listElement = document.createElement("list");

		for (final String gradleTask : gradleTaskArray) {

			final Element taskNameOptionElement = document.createElement("option");
			taskNameOptionElement.setAttribute("value", gradleTask);

			listElement.appendChild(taskNameOptionElement);
		}

		optionElement.appendChild(listElement);

		externalSystemSettingsElement.appendChild(optionElement);
	}

	private static void writeVmOptionsOption(
			final Element externalSystemSettingsElement) {

		final Document document = externalSystemSettingsElement.getOwnerDocument();
		final Element optionElement = document.createElement("option");
		optionElement.setAttribute("name", "vmOptions");

		externalSystemSettingsElement.appendChild(optionElement);
	}

	private static void writeExternalSystemDebugServerProcess(
			final Element configurationElement) {

		final Document document = configurationElement.getOwnerDocument();
		final Element externalSystemReattachDebugProcessElement =
				document.createElement("ExternalSystemDebugServerProcess");
		externalSystemReattachDebugProcessElement.setTextContent(Boolean.TRUE.toString());

		configurationElement.appendChild(externalSystemReattachDebugProcessElement);
	}

	private static void writeExternalSystemReattachDebugProcess(
			final Element configurationElement) {

		final Document document = configurationElement.getOwnerDocument();
		final Element externalSystemReattachDebugProcessElement =
				document.createElement("ExternalSystemReattachDebugProcess");
		externalSystemReattachDebugProcessElement.setTextContent(Boolean.TRUE.toString());

		configurationElement.appendChild(externalSystemReattachDebugProcessElement);
	}

	private static void writeDebugAllEnabled(
			final Element configurationElement) {

		final Document document = configurationElement.getOwnerDocument();
		final Element debugAllEnabledElement = document.createElement("DebugAllEnabled");
		debugAllEnabledElement.setTextContent(Boolean.FALSE.toString());

		configurationElement.appendChild(debugAllEnabledElement);
	}

	private static void writeMethod(
			final Element configurationElement) {

		final Document document = configurationElement.getOwnerDocument();
		final Element methodElement = document.createElement("method");
		methodElement.setAttribute("v", "2");

		configurationElement.appendChild(methodElement);
	}
}
