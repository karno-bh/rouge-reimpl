package il.ac.sce.ir.metric.concrete_metric.elena.score;

import edu.northwestern.at.morphadorner.corpuslinguistics.syllablecounter.DefaultSyllableCounter;
import edu.northwestern.at.morphadorner.corpuslinguistics.syllablecounter.SyllableCounter;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import il.ac.sce.ir.metric.core.utils.StringUtils;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;
import il.ac.sce.ir.metric.core.score_calculator.ReadabilityMetricsScoreCalculator;
import il.ac.sce.ir.metric.core.score.ReadabilityMetricScore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ElenaReadabilityMetricScoreCalculator implements ReadabilityMetricsScoreCalculator {

    private TextProcessor<String, String> textReadProcessor;

    private TextProcessor<String, Annotation> documentAnnotationProcessor;

    public TextProcessor<String, Annotation> getDocumentAnnotationProcessor() {
        return documentAnnotationProcessor;
    }

    public void setDocumentAnnotationProcessor(TextProcessor<String, Annotation> documentAnnotationProcessor) {
        this.documentAnnotationProcessor = documentAnnotationProcessor;
    }

    public TextProcessor<String, String> getTextReadProcessor() {
        return textReadProcessor;
    }

    public void setTextReadProcessor(TextProcessor<String, String> textReadProcessor) {
        this.textReadProcessor = textReadProcessor;
    }

    @Override
    public ReadabilityMetricScore computeScore(Text<String> originalText) {
        Text<Annotation> annotatedText = documentAnnotationProcessor.process(originalText);
        Annotation document = annotatedText.getTextData();
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        Set<String> uniqueWords = new HashSet<>();
        Set<String> uniqueProperNouns = new HashSet<>();
        int wordsNum = 0;
        int sentencesNum = 0;
        int properNouns = 0;
        StringUtils stringUtils = new StringUtils();
        for (CoreMap sentence : sentences) {
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // this is the NER label of the token
                // String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);

                if (!stringUtils.contains(word, ",", ".", "?", "!", "'")) {
                    wordsNum++;
                    uniqueWords.add(word);
                    if (pos.contains("NNP")) {
                        properNouns++;
                        uniqueProperNouns.add(word);
                    }
                }
            }
            sentencesNum++;
        }

        SyllableCounter syllableCounter = new DefaultSyllableCounter();
        Text<String> readText = textReadProcessor.process(originalText);
        int syllablesNum = syllableCounter.countSyllables(readText.getTextData());

        ReadabilityMetricScore readabilityMetricScore = new ReadabilityMetricScore();
        readabilityMetricScore.setSentenceNum(sentencesNum);
        readabilityMetricScore.setWordsNum(wordsNum);
        readabilityMetricScore.setSyllablesNum(syllablesNum);
        readabilityMetricScore.setProperNounsNum(properNouns);
        readabilityMetricScore.setUniqueProperNounsNum(uniqueProperNouns.size());
        readabilityMetricScore.setUniqueWordsNum(uniqueWords.size());

        return readabilityMetricScore;
    }
}
