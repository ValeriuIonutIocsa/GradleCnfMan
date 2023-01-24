package com.utils.gradle.cnf.roots;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import com.utils.gradle.cnf.roots.files.CnfFile;
import com.utils.gradle.cnf.roots.files.CnfFileCreateExecutables;
import com.utils.gradle.cnf.roots.files.CnfFileEclipsePmd;
import com.utils.gradle.cnf.roots.files.CnfFileGitIgnore;
import com.utils.gradle.cnf.roots.files.CnfFileGradleWrapperJar;
import com.utils.gradle.cnf.roots.files.CnfFileGradleWrapperProperties;
import com.utils.io.IoUtils;
import com.utils.io.PathUtils;
import com.utils.log.Logger;
import com.utils.string.StrUtils;

public class GradleProjectRoot {

	private static final CnfFile[] CONFIG_FILE_ARRAY = {
			new CnfFileCreateExecutables(),
			new CnfFileEclipsePmd(),
			new CnfFileGitIgnore(),
			new CnfFileGradleWrapperJar(),
			new CnfFileGradleWrapperProperties()
	};

	private final String gradleRootProjectFolderPathString;

	public GradleProjectRoot(
			final String gradleRootProjectFolderPathString) {

		this.gradleRootProjectFolderPathString = gradleRootProjectFolderPathString;
	}

	public void createConfigurationFiles() {

		try {
			if (IoUtils.directoryExists(gradleRootProjectFolderPathString)) {

				final Path gradleProjectRootFolderPath = Paths.get(gradleRootProjectFolderPathString);
				Files.walkFileTree(gradleProjectRootFolderPath, new SimpleFileVisitor<>() {

					@Override
					public FileVisitResult preVisitDirectory(
							final Path dir,
							final BasicFileAttributes attrs) throws IOException {

						final String folderPathString = dir.toString();
						final String buildGradleFolderPathString =
								PathUtils.computePath(folderPathString, "build.gradle");
						if (IoUtils.fileExists(buildGradleFolderPathString)) {

							final String settingsGradleFolderPathString =
									PathUtils.computePath(folderPathString, "settings.gradle");
							if (IoUtils.fileExists(settingsGradleFolderPathString)) {

								Logger.printNewLine();
								Logger.printStatus("Gradle project folder:");
								Logger.printLine(folderPathString);
								for (final CnfFile cnfFile : CONFIG_FILE_ARRAY) {
									cnfFile.create(folderPathString);
								}
							}
						}
						return super.preVisitDirectory(dir, attrs);
					}
				});
			}

		} catch (final Exception exc) {
			Logger.printException(exc);
		}
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}
}
