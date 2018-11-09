package il.ac.sce.ir.metric.core.utils;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryPathResolver {

    public List<ProcessedCategory> resolveCategories(String startDirLocation) {
        File startDir = new File(startDirLocation);
        String[] categoriesDirs = startDir.list((startDirInnerFile, fileName) -> startDirInnerFile.isDirectory());
        if (categoriesDirs == null || categoriesDirs.length == 0) {
            throw new RuntimeException("Nothing to process. No category found at " + startDirLocation);
        }
        return Arrays.stream(categoriesDirs)
                .map(category -> {
                    File categoryDescriptionFile = new File(category + File.separator + Constants.DESCRIPTION_FILE);
                    String description = null;
                    if (categoryDescriptionFile.isFile()) {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(categoryDescriptionFile), StandardCharsets.UTF_8.name()))){
                            description = reader.readLine();
                        } catch (IOException ignored) {
                        }
                    }
                    return ProcessedCategory.as().dirLocation(category).description(description).build();
                })
                .collect(Collectors.toList());
    }
}
