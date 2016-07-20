package easyaccept.maven;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import easyaccept.EasyAccept;
import easyaccept.EasyAcceptException;

// TODO transformar num plugin Maven no futuro
public class RunAcceptanceTests {
	protected static class Finder extends SimpleFileVisitor<Path> {
		private final PathMatcher matcher;
		
		protected List<File> files = new LinkedList<File>();

		public Finder(String pattern) {
			matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
		}

		public List<File> getFiles() {
			return files;
		}
		
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
				throws IOException {
			if (matcher.matches(file)) {
				files.add(file.toFile());
				return FileVisitResult.SKIP_SUBTREE;
			}
			return super.visitFile(file, attrs);
		}
	}

	public static void main(String[] args) throws EasyAcceptException, IOException {
		if (shouldSkipTests()) {
			System.out.println("Acceptance tests are skipped.");
		} else if (args.length < 3) {
			throw new IllegalArgumentException(
					"Correct usage: RunTests <facadeClassName> <testFolder> <testPattern1> <testPattern2> ... <testPatternN>");
		} else {
			String[] array = Arrays.copyOfRange(args, 2, args.length);
			
			EasyAccept.executeEasyAccept(
					args[0],
					getFilesFromPattern(args[1], array));
		}
	}
	
	protected static List<String> getFilesFromPattern(String testFolder, String... patterns) throws IOException {
		List<String> fileNames = new LinkedList<String>();
		
		for (String pattern : patterns) {
	        Finder finder = new Finder(pattern);
	        
	        Files.walkFileTree(Paths.get(testFolder), finder);

			List<String> patternFileNames = new LinkedList<String>();
			for (File file : finder.getFiles()) {
				if (!shouldSkipTest(file.getName())) {
					patternFileNames.add(file.getPath());
				}
			}
			Collections.sort(patternFileNames);
			
			fileNames.addAll(patternFileNames);
		}
		
		return fileNames;
	}

	public static boolean shouldSkipTests() {
		String skipProperty = System.getProperty("skipAcceptanceTests");
		return skipProperty != null && Boolean.parseBoolean(skipProperty);
	}

	public static boolean shouldSkipTest(String fileName) {
		String testsToSkipProperty = System.getProperty("skip");
		if (testsToSkipProperty != null) {
 			for (String testFile : testsToSkipProperty.split(",")) {
 				if (fileName.trim().equals(testFile.trim())) {
 					return true;
 				}
			}
		}
		return false;
	}
}
