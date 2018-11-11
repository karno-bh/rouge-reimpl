package il.ac.sce.ir.metric.core.reporter.file_system_reflection;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.utils.CategoryPathResolver;
import il.ac.sce.ir.metric.core.utils.FileSystemPath;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileSystemTopologyResolver {

    public List<ProcessedCategory> getCategories(String startDirLocation) {
        // TODO copy code from CategoryPathResolver and delete CategoryPathResolver
        CategoryPathResolver categoryPathResolver = new CategoryPathResolver();
        return categoryPathResolver.resolveCategories(startDirLocation);
    }

    public File getModelsDirectory(String workingSetDirectory, ProcessedCategory processedCategory) {
        final FileSystemPath fileSystemPath = new FileSystemPath();
        String categoryDir = fileSystemPath.combinePath(workingSetDirectory, processedCategory.getDirLocation());
        String peersDirectoryName = fileSystemPath.combinePath(categoryDir, Constants.MODELS_DIRECTORY);
        File peersDirectory = new File(peersDirectoryName);
        if (!peersDirectory.isDirectory()) {
            throw new RuntimeException(peersDirectoryName + " is not a directory");
        }
        return peersDirectory;
    }

    public File getPeersDirectory(String workingSetDirectory, ProcessedCategory processedCategory) {
        final FileSystemPath fileSystemPath = new FileSystemPath();
        String categoryDir = fileSystemPath.combinePath(workingSetDirectory, processedCategory.getDirLocation());
        String peersDirectoryName = fileSystemPath.combinePath(categoryDir, Constants.PEERS_DIRECTORY);
        File peersDirectory = new File(peersDirectoryName);
        if (!peersDirectory.isDirectory()) {
            throw new RuntimeException(peersDirectoryName + " is not a directory");
        }
        return peersDirectory;
    }

    public List<ProcessedSystem> getProcessedSystems(String workingSetDirectory, ProcessedCategory processedCategory) {
        final FileSystemPath fileSystemPath = new FileSystemPath();
//        String categoryDir = workingSetDirectory + File.separator + processedCategory.getDirLocation();
        String categoryDir = fileSystemPath.combinePath(workingSetDirectory, processedCategory.getDirLocation());
//        String peersDirectoryName = categoryDir + File.separator + Constants.PEERS_DIRECTORY;
        File peersDirectory = getPeersDirectory(workingSetDirectory, processedCategory);

//        String modelsDirectoryName = categoryDir + File.separator + Constants.MODELS_DIRECTORY;
//        File modelsDirectoryName = getModelsDirectory(workingSetDirectory, processedCategory);

        String[] systems = peersDirectory.list((file, name) -> file.isDirectory());
        if (systems == null || systems.length == 0) {
            return null;
        }
        List<ProcessedSystem> processedSystems = Arrays.stream(systems)
                .map(systemDirName -> {
//                    String pathName = peersDirectory + File.separator + systemDirName + File.separator + Constants.DESCRIPTION_FILE;
                    String pathName = fileSystemPath.combinePath(peersDirectory.toString(), systemDirName, Constants.DESCRIPTION_FILE);
                    File systemDescriptionFile = new File(pathName);
                    String description = null;
                    if (systemDescriptionFile.isFile()) {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(systemDescriptionFile), StandardCharsets.UTF_8.name()))) {
                            description = reader.readLine();
                        } catch (IOException ignored) {
                        }
                    }
                    return ProcessedSystem.as()
//                            .dirLocation(peersDirectory + File.separator + systemDirName)
                            .dirLocation(fileSystemPath.combinePath(peersDirectory.toString(), systemDirName))
                            .description(description)
                            .build();
                })
                .collect(Collectors.toList());

        return processedSystems;
    }

    public List<String> getModels(ProcessedCategory category) {
        return null;
    }

    public List<String> getTopics(ProcessedCategory category) {
        return null;
    }

    public List<String> getPeers(ProcessedSystem processedSystem) {
        return null;
    }

}
