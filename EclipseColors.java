import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class EclipseColors {

	private static final String WORKSPACE_PROPERTIES_RELATIVE_PATH = ".metadata/.plugins/org.eclipse.core.runtime/.settings";
	private static final String CUSTOM_PROPERTIES_FOLDER = "resources";
	
	public static void main(String[] args) throws IOException {
		String workspaceFolderPath = args[0];
		Path outputFolderPath = Paths.get(workspaceFolderPath).resolve(WORKSPACE_PROPERTIES_RELATIVE_PATH);
			
		List<String> inputFilePaths = getFilesOfFolder(CUSTOM_PROPERTIES_FOLDER);

		for (String inputFilePath : inputFilePaths) {
			Map<String, String> inputProps = getPropertiesOfFile(inputFilePath);
			
			String outputFilePath = outputFolderPath.resolve(Paths.get(inputFilePath).getFileName()).toString();
			if (new File(outputFilePath).exists()) {
				Map<String, String> outputProps = getPropertiesOfFile(outputFilePath);
			
				for (String key : inputProps.keySet()) {
					outputProps.put(key, inputProps.get(key));
				}
				
				backupFile(outputFilePath);
				writePropertiesToFile(outputProps, outputFilePath);
			} else {
				writePropertiesToFile(inputProps, outputFilePath);
			}
		}
		
	}
	
	// Not recursive!
	public static List<String> getFilesOfFolder(final String folderPath) {
		List<String> filePaths = new ArrayList<String>();
		
		for (final File fileEntry : new File(folderPath).listFiles()) {
			if (!fileEntry.isDirectory()) {
				filePaths.add(fileEntry.getAbsolutePath());
			}
		}
		return filePaths;
	}
	
	public static Map<String, String> getPropertiesOfFile(final String filePath) throws IOException {
		final Map<String, String> props = new TreeMap<String, String>(); // the sorting is used at the output, not here
		
		try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
			lines.forEach(line -> {
				int index = line.indexOf('=');
				if (index != -1) { // for empty line at the end
					props.put(line.substring(0, index), line.substring(index + 1, line.length()));
				}
			});
		}
		
		return props;
	}
	
	public static void backupFile(final String filePath) throws IOException {
		Files.copy(Paths.get(filePath), Paths.get(filePath + '~'), StandardCopyOption.REPLACE_EXISTING);
	}

	public static void writePropertiesToFile(Map<String, String> properties, String outputFilePath) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilePath), 
				Charset.forName("UTF-8"), StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
			
			for (String key : properties.keySet()) {
				writer.write(key + "=" + properties.get(key) + '\n'); // Eclipse has an empty newline at the end of the files too
			}
		}
	}
}