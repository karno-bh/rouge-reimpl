package il.ac.sce.ir.metric.core.utils.file_system;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedSystem;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
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

    public List<String> getPeerFileNames(ProcessedSystem processedSystem) {
        String processedSystemDirLocation = processedSystem.getDirLocation();
        File processedSystemDir = new File(processedSystemDirLocation);
        String[] peerFileNames = processedSystemDir.list((file, fileName) -> {
            File dirFile = new File(processedSystemDirLocation + File.separator + fileName);
            return dirFile.isFile();
        });
        if (peerFileNames == null) {
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(peerFileNames);
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

    public List<Text<String>> getModelTextsPerPeer(File modelsDirectory, String peerFileName) {
        final FileSystemPath fileSystemPath = new FileSystemPath();
        String modelsDirectoryName = modelsDirectory.getAbsolutePath();
        String[] modelsArr = modelsDirectory.list((file, fileName) -> fileName.startsWith(peerFileName));
        if (modelsArr == null || modelsArr.length == 0) {
            return Collections.EMPTY_LIST;
        }
        return Arrays.stream(modelsArr)
//                .map(modelFileName -> modelsDirectoryName + File.separator + modelFileName)
                .map(modelFileName -> fileSystemPath.combinePath(modelsDirectoryName, modelFileName))
                .map(Text::asFileLocation)
                .collect(Collectors.toList());
    }


}
