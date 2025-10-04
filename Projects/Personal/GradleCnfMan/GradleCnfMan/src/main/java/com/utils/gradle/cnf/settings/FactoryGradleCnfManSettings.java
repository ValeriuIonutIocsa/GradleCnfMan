package com.utils.gradle.cnf.settings;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.utils.gradle.cnf.roots.GradleProjectRoot;
import com.utils.io.PathUtils;
import com.utils.log.Logger;
import com.utils.xml.dom.XmlDomUtils;

public final class FactoryGradleCnfManSettings {

	private FactoryGradleCnfManSettings() {
	}

	public static GradleCnfManSettings newInstance() {

		GradleCnfManSettings gradleCnfManSettings = null;
		try {
			final List<GradleProjectRoot> gradleProjectRootsList = new ArrayList<>();

			final String settingsFilePathString = PathUtils.computePath("GradleCnfManSettings.xml");
			final Document document = XmlDomUtils.openDocument(settingsFilePathString);
			final Element documentElement = document.getDocumentElement();

			final List<Element> gradleRootElementList =
					XmlDomUtils.getElementsByTagName(documentElement, "GradleRoot");
			for (final Element gradleRootElement : gradleRootElementList) {

				final String gradleRootPathString = gradleRootElement.getAttribute("Path");
				final GradleProjectRoot gradleProjectRoot = new GradleProjectRoot(gradleRootPathString);
				gradleProjectRootsList.add(gradleProjectRoot);
			}

			gradleCnfManSettings = new GradleCnfManSettings(gradleProjectRootsList);

		} catch (final Throwable throwable) {
			Logger.printThrowable(throwable);
		}
		return gradleCnfManSettings;
	}

}
