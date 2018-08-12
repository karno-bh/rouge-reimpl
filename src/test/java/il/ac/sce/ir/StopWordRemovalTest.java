package il.ac.sce.ir;

import edu.northwestern.at.morphadorner.corpuslinguistics.stemmer.PorterStemmer;
import edu.northwestern.at.morphadorner.corpuslinguistics.stemmer.Stemmer;
import il.ac.sce.ir.metric.core.builder.TextPipeline;
import il.ac.sce.ir.metric.core.builder.TextPipelineExtractor;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.FileToStringProcessor;
import il.ac.sce.ir.metric.core.processor.TextToTokensProcessor;
import il.ac.sce.ir.metric.core.processor.token_filter.LowerCaseFilterProcessor;
import il.ac.sce.ir.metric.core.processor.token_filter.PorterStemmerFilterProcessor;
import il.ac.sce.ir.metric.core.processor.token_filter.PunctuationFilterProcessor;
import il.ac.sce.ir.metric.core.processor.token_filter.StopwordsRemovalFilterProcessor;
import org.junit.Test;

import java.util.*;
import java.util.regex.Pattern;

public class StopWordRemovalTest {

    // took from: https://gist.github.com/sebleier/554280
    public final static String STOP_WORDS = "i me my myself we our ours ourselves you your yours yourself yourselves " +
            "he him his himself she her hers herself it its itself they them their theirs themselves what which who " +
            "whom this that these those am is are was were be been being have has had having do does did doing a an " +
            "the and but if or because as until while of at by for with about against between into through during " +
            "before after above below to from up down in out on off over under again further then once here there " +
            "when where why how all any both each few more most other some such no nor not only own same so than too " +
            "very s t can will just don should now";

    @Test
    public void removeStopWordsTest() {

        String fileName = "c:\\my\\learning\\final-project\\working-set\\attempt001\\category01\\models\\M004.B.250 ";

        TextPipelineExtractor<String, List<String>> tokensExtractor = new TextPipelineExtractor<>();
        new TextPipeline<>(new FileToStringProcessor())
                .pipe(new TextToTokensProcessor())
                .extract(tokensExtractor);
        Text<List<String>> process = tokensExtractor.getTextProcessor().process(Text.asFileLocation(fileName));

        Pattern p = Pattern.compile("[^\\\\dA-Za-z0-9 ]");
        Stemmer s = new PorterStemmer();

        Set<String> stopWords = new HashSet<>(Arrays.asList(STOP_WORDS.split("\\s+")));
        System.out.println(stopWords);

        List<String> textTokens = process.getTextData();

        StringBuilder sb = new StringBuilder();
        for (String token : textTokens) {
            sb.append(token).append(' ');
        }

        System.out.println(sb.toString());

        List<String> filteredTokens = new ArrayList<>();

        for (String token : textTokens) {
            String lowerCase = token.toLowerCase();
            System.out.print(lowerCase);
            String filtered = p.matcher(lowerCase).replaceAll("");
            if (stopWords.contains(filtered)) {
                filtered = "";
            }
            System.out.print(", " + filtered);

            String stemmed = s.stem(filtered);
            System.out.println(", " + stemmed);

            if (!"".equals(stemmed)) {
                filteredTokens.add(stemmed);
            }
        }

        sb = new StringBuilder();
        for (String token : filteredTokens) {
            sb.append(token).append(' ');
        }

        System.out.println(sb.toString());
    }

    @Test
    public void stopWordsTextProcessorTest() {
        String fileName = "c:\\my\\learning\\final-project\\working-set\\attempt001\\category01\\models\\M004.B.250 ";
        TextPipelineExtractor<String, List<String>> tokensExtractor = new TextPipelineExtractor<>();
        TextPipelineExtractor<String, List<String>> lowercaseTokensExtractor = new TextPipelineExtractor<>();
        TextPipelineExtractor<String, List<String>> punctuationExtractor = new TextPipelineExtractor<>();
        TextPipelineExtractor<String, List<String>> stopwordsExtractor = new TextPipelineExtractor<>();
        TextPipelineExtractor<String, List<String>> porterStemmerExtractor = new TextPipelineExtractor<>();
        List<TextPipelineExtractor<String, List<String>>> extractors = Arrays.asList(
                tokensExtractor, lowercaseTokensExtractor, punctuationExtractor, stopwordsExtractor, porterStemmerExtractor
        );

        new TextPipeline<>(new FileToStringProcessor())
                .pipe(new TextToTokensProcessor())
                    .extract(tokensExtractor)
                .pipe(new LowerCaseFilterProcessor())
                    .extract(lowercaseTokensExtractor)
                .pipe(new PunctuationFilterProcessor())
                    .extract(punctuationExtractor)
                .pipe(new StopwordsRemovalFilterProcessor())
                    .extract(stopwordsExtractor)
                .pipe(new PorterStemmerFilterProcessor())
                    .extract(porterStemmerExtractor);

        Text<String> initialText = Text.asFileLocation(fileName);

        for (TextPipelineExtractor<String, List<String>> extractor : extractors) {
            Text<List<String>> processedText = extractor.getTextProcessor().process(initialText);
            StringBuilder sb = new StringBuilder();
            for (String token : processedText.getTextData()) {
                sb.append(token).append(' ');
            }
            System.out.println(sb.toString());
            System.out.println();
        }
    }
}
