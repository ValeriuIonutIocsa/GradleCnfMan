package com.utils.gradle.run_cnf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.utils.io.PathUtils;
import com.utils.log.Logger;
import com.utils.xml.dom.XmlDomUtils;

final class GradleExternalCnf {

	private GradleExternalCnf() {
	}

	static void writeEclipseExternalCnfFile(
			final String gradlePrjName,
			final String runCnfFolderPathString) {

		final String runCnfFileName = "GradleCnfMan_external_tool_cnf.launch";
		final String runCnfFilePathString =
				PathUtils.computePath(runCnfFolderPathString, runCnfFileName);
		try {
			Logger.printProgress("writing Eclipse external tool cnf file:");
			Logger.printLine(runCnfFilePathString);

			final Document document = XmlDomUtils.createNewDocument();
			final Element documentElement = document.createElement("launchConfiguration");
			documentElement.setAttribute("type", "org.eclipse.buildship.core.launch.runconfiguration");

			EclipseRunCnfUtils.writeEclipseRunCnfAttribute("listAttribute", "org.eclipse.debug.ui.favoriteGroups",
					null, new String[] { "org.eclipse.ui.externaltools.launchGroup" }, documentElement);

			EclipseRunCnfUtils.writeEclipseRunCnfAttribute("stringAttribute",
					"org.eclipse.ui.externaltools.ATTR_LOCATION",
					"${env_var:SystemRoot}\\system32\\cmd.exe", null, documentElement);
			EclipseRunCnfUtils.writeEclipseRunCnfAttribute("stringAttribute",
					"org.eclipse.ui.externaltools.ATTR_TOOL_ARGUMENTS",
					"/c gradle_cnf_man --debug=true --mode=create_run_configurations " +
							"--root_project_path=${workspace_loc:/" + gradlePrjName + "}", null, documentElement);
			EclipseRunCnfUtils.writeEclipseRunCnfAttribute("stringAttribute",
					"org.eclipse.ui.externaltools.ATTR_WORKING_DIRECTORY",
					"${workspace_loc:/" + gradlePrjName + "}", null, documentElement);

			document.appendChild(documentElement);

			XmlDomUtils.saveXmlFile(document, false, 4, runCnfFilePathString);

		} catch (final Exception exc) {
			Logger.printError("failed to write Idea run cnf file:" +
					System.lineSeparator() + runCnfFilePathString);
			Logger.printException(exc);
		}
	}
}
