package com.utils.gradle.run_cnf;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.utils.gradle.run_cnf.sub_prj.FactoryGradleSubProject;
import com.utils.gradle.run_cnf.sub_prj.GradleSubProject;
import com.utils.io.IoUtils;
import com.utils.io.ListFileUtils;
import com.utils.io.PathUtils;
import com.utils.io.file_deleters.FactoryFileDeleter;
import com.utils.io.folder_creators.FactoryFolderCreator;
import com.utils.log.Logger;
import com.utils.xml.dom.XmlDomUtils;

public final class RunCnfCreator {

	private RunCnfCreator() {
	}

	public static void work(
			final String rootPrjPathString) {

		Logger.printProgress("root project path string:");
		Logger.printLine(rootPrjPathString);

		final String valRootPrjPathString = PathUtils.computePath(rootPrjPathString);
		if (valRootPrjPathString != null) {

			final List<GradleSubProject> gradleSubProjectList = new ArrayList<>();
			FactoryGradleSubProject.newInstance(rootPrjPathString, gradleSubProjectList);

			final JavaParser javaParser = new JavaParser();
			final ParserConfiguration parserConfiguration = javaParser.getParserConfiguration();
			parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_11);

			final List<GradlePrjCnf> gradlePrjCnfList = new ArrayList<>();

			final GradlePrjCnf rootGradlePrjCnf =
					createGradlePrjCnf(rootPrjPathString, javaParser);
			gradlePrjCnfList.add(rootGradlePrjCnf);

			for (final GradleSubProject gradleSubProject : gradleSubProjectList) {

				final String gradleSubProjectPathString = gradleSubProject.getPath();
				final GradlePrjCnf gradlePrjCnf =
						createGradlePrjCnf(gradleSubProjectPathString, javaParser);
				gradlePrjCnfList.add(gradlePrjCnf);
			}

			for (final GradlePrjCnf gradlePrjCnf : gradlePrjCnfList) {
				gradlePrjCnf.writeEclipseRunCnfFiles();
			}

			final String runCnfFolderPathString =
					PathUtils.computePath(rootPrjPathString, ".idea", "runConfigurations");
			final boolean success = FactoryFolderCreator.getInstance()
					.createDirectories(runCnfFolderPathString, true);
			if (success) {

				deleteExistingIdeaRunCnfFiles(gradlePrjCnfList, runCnfFolderPathString);

				final String rootPrjName = PathUtils.computeFileName(valRootPrjPathString);
				for (final GradlePrjCnf gradlePrjCnf : gradlePrjCnfList) {
					gradlePrjCnf.writeIdeaRunCnfFiles(rootPrjName, runCnfFolderPathString);
				}
			}
		}
	}

	private static void deleteExistingIdeaRunCnfFiles(
			final List<GradlePrjCnf> gradlePrjCnfList,
			final String runCnfFolderPathString) {

		Logger.printNewLine();
		Logger.printProgress("deleting existing invalid IntelliJ Idea run cnf files");

		final Set<String> testIdeaRunCnfFileNameSet = new HashSet<>();
		for (final GradlePrjCnf gradlePrjCnf : gradlePrjCnfList) {
			gradlePrjCnf.fillTestIdeaRunCnfFileNameSet(testIdeaRunCnfFileNameSet);
		}

		final List<String> ideaRunCnfFilePathStringList = ListFileUtils.listFiles(runCnfFolderPathString,
				filePath -> filePath.toString().endsWith(".xml"));
		for (final String ideaRunCnfFilePathString : ideaRunCnfFilePathStringList) {

			final String ideaRunCnfType = parseIdeaRunCnfType(ideaRunCnfFilePathString);
			if ("JUnit".equals(ideaRunCnfType)) {

				final String ideaRunCnfFileName = PathUtils.computeFileName(ideaRunCnfFilePathString);
				if (!testIdeaRunCnfFileNameSet.contains(ideaRunCnfFileName)) {

					Logger.printProgress("deleting IntelliJ Idea run cnf file:");
					Logger.printLine(ideaRunCnfFilePathString);
					FactoryFileDeleter.getInstance().deleteFile(ideaRunCnfFilePathString, true);
				}
			}
		}
	}

	private static String parseIdeaRunCnfType(
			final String ideaRunCnfFilePathString) {

		String ideaRunCnfType = "";
		try {
			final Document document = XmlDomUtils.openDocument(ideaRunCnfFilePathString);
			final Element documentElement = document.getDocumentElement();
			final Element configurationElement =
					XmlDomUtils.getFirstElementByTagName(documentElement, "configuration");
			if (configurationElement != null) {
				ideaRunCnfType = configurationElement.getAttribute("type");
			}

		} catch (final Exception exc) {
			Logger.printError("failed to parse the Idea run cnf type!");
			Logger.printException(exc);
		}
		return ideaRunCnfType;
	}

	private static GradlePrjCnf createGradlePrjCnf(
			final String gradlePrjPathString,
			final JavaParser javaParser) {

		final Path gradlePrjPath = Paths.get(gradlePrjPathString);
		final String gradlePrjName = PathUtils.computeFileName(gradlePrjPath);
		final List<GradleTestCnf> gradleTestCnfList = new ArrayList<>();
		final String testJavaFolderPathString =
				PathUtils.computePath(gradlePrjPathString, "src", "test", "java");
		if (IoUtils.directoryExists(testJavaFolderPathString)) {

			final List<String> javaFilePathStringList = ListFileUtils.listFilesRecursively(testJavaFolderPathString,
					filePath -> filePath.getFileName().toString().endsWith(".java"));
			for (final String javaFilePathString : javaFilePathStringList) {
				parseJavaFile(javaFilePathString, testJavaFolderPathString, javaParser, gradleTestCnfList);
			}
		}

		return new GradlePrjCnf(gradlePrjName, gradlePrjPathString, gradleTestCnfList);
	}

	private static void parseJavaFile(
			final String javaFilePathString,
			final String testJavaFolderPathString,
			final JavaParser javaParser,
			final List<GradleTestCnf> gradleTestCnfList) {

		try {
			final ParseResult<CompilationUnit> parseResult = javaParser.parse(Paths.get(javaFilePathString));
			final Optional<CompilationUnit> compilationUnitOptional = parseResult.getResult();
			if (compilationUnitOptional.isPresent()) {

				final String javaFileRelativePathString =
						PathUtils.computeRelativePath(testJavaFolderPathString, javaFilePathString);
				final String javaFilePkgPathString =
						PathUtils.computeParentPath(javaFileRelativePathString);
				final String pkgName = javaFilePkgPathString.replace('\\', '.').replace('/', '.');

				final String testClsName = PathUtils.computeFileNameWoExt(javaFilePathString);
				final CompilationUnit compilationUnit = compilationUnitOptional.get();
				compilationUnit.accept(new VoidVisitorAdapter<Void>() {

					@Override
					public void visit(
							final MethodDeclaration methodDeclaration,
							final Void arg) {

						super.visit(methodDeclaration, arg);

						final Optional<AnnotationExpr> testAnnotationExprOptional =
								methodDeclaration.getAnnotationByName("Test");
						final Optional<AnnotationExpr> testFactoryAnnotationExprOptional =
								methodDeclaration.getAnnotationByName("TestFactory");
						if (testAnnotationExprOptional.isPresent() ||
								testFactoryAnnotationExprOptional.isPresent()) {

							final String testMethodName = methodDeclaration.getNameAsString();
							final GradleTestCnf gradleTestCnf =
									new GradleTestCnf(pkgName, testClsName, testMethodName);
							gradleTestCnfList.add(gradleTestCnf);
						}
					}
				}, null);
			}

		} catch (final Exception exc) {
			Logger.printException(exc);
		}
	}
}
