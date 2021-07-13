package io.vicevil4.sample;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

public class GenerateDependenciesTests {

	@Test
	public void generateDependencies() throws Exception {

		generateDependencies("TagName", 
				Paths.get("/path/to/lib"), 
				"group-id-whatever");
	}
	
	private void generateDependencies(String tag, Path path, String groupId) throws IOException {
		
		System.out.println(String.format("======================= %s ========================", tag));
		System.out.println("<properties>");
		System.out.println("		<webapp.lib>${basedir}/src/main/webapp/WEB-INF/lib</webapp.lib>");
		System.out.println("</properties>");
		
		System.out.println("<dependencies>");
		final AtomicInteger dependencyCount = new AtomicInteger(0);
		FileVisitor<Path> visitor = new FileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				String fileName = file.getFileName().toString();

				System.out.println("<dependency>");
				System.out.println("	<groupId>" + groupId + "</groupId>");
				System.out.println(String.format("%s%s%s", "	<artifactId>", fileName.substring(0, fileName.lastIndexOf(".")), "</artifactId>"));
				System.out.println("	<version>whatever</version>");
				System.out.println("	<scope>system</scope>");
				System.out.println(String.format("%s%s%s", "	<systemPath>${webapp.lib}/", fileName, "</systemPath>"));
				System.out.println("</dependency>");

				dependencyCount.incrementAndGet();

				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				throw new IOException("visitFileFailed");
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}
		};
		
		Files.walkFileTree(path, visitor);
		System.out.println("</dependencies>");
		System.out.println(String.format("%s%s%s", ">> Generated ", dependencyCount.get(), " dependencies."));
		System.out.println(String.format("=================================================="));
	}
}
