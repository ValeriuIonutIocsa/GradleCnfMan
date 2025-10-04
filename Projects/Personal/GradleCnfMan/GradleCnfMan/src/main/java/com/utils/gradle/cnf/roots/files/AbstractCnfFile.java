package com.utils.gradle.cnf.roots.files;

import com.utils.io.PathUtils;
import com.utils.io.file_copiers.FactoryFileCopier;
import com.utils.io.folder_creators.FactoryFolderCreator;
import com.utils.io.ro_flag_clearers.FactoryReadOnlyFlagClearer;
import com.utils.log.Logger;
import com.utils.string.StrUtils;

abstract class AbstractCnfFile implements CnfFile {

	AbstractCnfFile() {
	}

	@Override
	public void create(
			final String projectFolderPathString) {

		final String sourceFileName = getSourceFileName();
		try {
			final String srcPathString = PathUtils.computePath("ConfigurationFiles", sourceFileName);

			final String[] dstLocation = getDestLocation();
			final String filePathString = PathUtils.computePath(projectFolderPathString, dstLocation);

			Logger.printProgress("creating file:");
			Logger.printLine(filePathString);

			FactoryFolderCreator.getInstance().createParentDirectories(filePathString, false, true);
			FactoryReadOnlyFlagClearer.getInstance().clearReadOnlyFlagFile(filePathString, false, true);
			FactoryFileCopier.getInstance().copyFile(srcPathString, filePathString, false, false, true);

		} catch (final Throwable throwable) {
			Logger.printError("failed to create the \"" + sourceFileName + "\" file inside project:" +
					System.lineSeparator() + projectFolderPathString);
			Logger.printThrowable(throwable);
		}
	}

	String[] getDestLocation() {

		final String sourceFileName = getSourceFileName();
		return new String[] { sourceFileName };
	}

	abstract String getSourceFileName();

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}
}
