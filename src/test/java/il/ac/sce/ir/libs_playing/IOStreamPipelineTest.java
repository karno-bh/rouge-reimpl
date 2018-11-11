package il.ac.sce.ir.libs_playing;

import il.ac.sce.ir.metric.core.builder.OutputStreamPipeline;
import org.junit.Test;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class IOStreamPipelineTest {

    @Test
    public void outputStreamBuilderTest() throws IOException {
        File f = new File("C:/my/temp");
        ObjectOutputStream build = new OutputStreamPipeline<>(new FileOutputStream(f))
                .pipe(GZIPOutputStream::new)
                .pipe(BufferedOutputStream::new)
                .pipe(ObjectOutputStream::new).build();

        //.pipe(stream -> new BufferedOutputStream(stream));
    }
}
