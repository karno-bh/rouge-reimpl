package il.ac.sce.ir.metric.core.processor;

import il.ac.sce.ir.metric.core.builder.OutputStreamPipeline;
import il.ac.sce.ir.metric.core.data.Text;

import java.io.*;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FileSystemCacheTextProcessor<X, Y> extends AbstractCacheTextProcessor<X, Y> {

    private final String fsCachePrefix;

    private final String parameter;

    private final Map<String, String> textIdHashCache;

    public FileSystemCacheTextProcessor(TextProcessor<X, Y> textProcessor,
                                        String fileSystemCachePrefix,
                                        String parameter) {
        this(textProcessor, fileSystemCachePrefix, parameter, new ConcurrentHashMap<>());
    }

    public FileSystemCacheTextProcessor(TextProcessor<X, Y> textProcessor,
                                        String fileSystemCachePrefix,
                                        String parameter,
                                        Map<String, String> textIdHashCache) {
        super(textProcessor);
        Objects.requireNonNull(textIdHashCache, "Text ID Cache of Hashes cannot be null");
        if (parameter == null || parameter.isEmpty()) {
            throw new IllegalArgumentException("Parameter cannot be empty");
        }
        if (fileSystemCachePrefix == null || fileSystemCachePrefix.isEmpty()) {
            throw new IllegalArgumentException("File System prefix should not be empty");
        }
        this.textIdHashCache = textIdHashCache;
        this.parameter = parameter;
        this.fsCachePrefix = fileSystemCachePrefix;
    }


    @Override
    protected Text<Y> getCached(Text<X> data) {
        String encodedKey = getTextIdHash(data.getTextId());
        String fsHashBucket = fsCachePrefix + "/" + encodedKey + "/" + parameter + ".cache";
        File cached = new File(fsHashBucket);
        if (!cached.exists()) {
            return null;
        }
        try (ObjectInputStream is = new ObjectInputStream( new BufferedInputStream(new GZIPInputStream(new FileInputStream(cached))))) {
            Y result = (Y) is.readObject();
            return new Text<>(data.getTextId(), result);
        } catch (IOException | ClassNotFoundException ignored) {

            //throw new RuntimeException("Error while loading cached file");
        }
        return null;
    }

    @Override
    protected void setToCache(Text<Y> computed) {
        String encodedKey = getTextIdHash(computed.getTextId());
        String fsHashBucket = fsCachePrefix + "/" + encodedKey + "/" + parameter + ".cache";
        File cached = new File(fsHashBucket);
        if (cached.exists()) {
            return;
        }
        File dirCache = new File(fsCachePrefix + "/" + encodedKey);
        if (!dirCache.exists()) {
            dirCache.mkdirs();
        }
        try (ObjectOutputStream os = new ObjectOutputStream( new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(cached))))) {
        /*try (ObjectOutputStream os = new OutputStreamPipeline<>(new FileOutputStream(cached))
                .pipe(GZIPOutputStream::new)
                .pipe(BufferedOutputStream::new)
                .pipe(ObjectOutputStream::new).build()) {*/
            Y data = computed.getTextData();
            os.writeObject(data);
        } catch (IOException ignored) {
            //throw new RuntimeException(ignored);
        }
    }

    private String getTextIdHash(String textId) {
        return textIdHashCache.computeIfAbsent(textId,
        key -> {
            try {
                byte[] utf8TextIdBytes = key.getBytes("UTF-8");
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] encoded = digest.digest(utf8TextIdBytes);
                String encodedBase64 = Base64.getEncoder().encodeToString(encoded);
                encodedBase64 = encodedBase64.replace('/', '-');
                return encodedBase64;
            } catch (Exception e) {
                throw new RuntimeException("Error occurred while calculating Text Id hash", e);
            }
        });
    }
}
