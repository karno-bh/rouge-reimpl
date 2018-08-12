package il.ac.sce.ir.metric.core.score;

import il.ac.sce.ir.metric.core.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ReadabilityMetricScore implements Serializable, ReportedProperties {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final Set<String> REPORTED_PROPERTIES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    Constants.FLESCH_READING_EASE,
                    Constants.PROPER_NOUN_RATION,
                    Constants.UNIQUE_PROPER_NOUN_RATIO,
                    Constants.WORD_VARIATION_INDEX
            )));

    private int wordsNum;

    private int uniqueWordsNum;

    private int sentenceNum;

    private int syllablesNum;

    private int properNounsNum;

    private int uniqueProperNounsNum;

    public int getWordsNum() {
        return wordsNum;
    }

    public void setWordsNum(int wordsNum) {
        this.wordsNum = wordsNum;
    }

    public int getUniqueWordsNum() {
        return uniqueWordsNum;
    }

    public void setUniqueWordsNum(int uniqueWordsNum) {
        this.uniqueWordsNum = uniqueWordsNum;
    }

    public int getSentenceNum() {
        return sentenceNum;
    }

    public void setSentenceNum(int sentenceNum) {
        this.sentenceNum = sentenceNum;
    }

    public int getSyllablesNum() {
        return syllablesNum;
    }

    public void setSyllablesNum(int syllablesNum) {
        this.syllablesNum = syllablesNum;
    }

    public int getProperNounsNum() {
        return properNounsNum;
    }

    public void setProperNounsNum(int properNounsNum) {
        this.properNounsNum = properNounsNum;
    }

    public int getUniqueProperNounsNum() {
        return uniqueProperNounsNum;
    }

    public void setUniqueProperNounsNum(int uniqueProperNounsNum) {
        this.uniqueProperNounsNum = uniqueProperNounsNum;
    }

    public double getFleschReadingEase() {

        double fre = 206.825 -
                1.015 * ((double) wordsNum / (double) sentenceNum) -
                84.6 * ((double) syllablesNum / (double) wordsNum);
        logger.info("getFleschReadingEase: wordsNum = {}, sentenceNum = {}, syllablesNum = {}, fre = {}", wordsNum,
                sentenceNum, syllablesNum, fre);

        return fre;
    }

    public double getWordVariationIndex() {
        return Math.log10((double) wordsNum) / Math.log10(2 - Math.log10((double) uniqueWordsNum) / Math.log10((double) wordsNum));
    }

    public double getProperNounRation() {
        return (double) properNounsNum / (double) wordsNum;
    }

    public double getUniqueProperNounsRatio() {
        return (double) uniqueProperNounsNum / (double) wordsNum;
    }

    @Override
    public Set<String> resolveReportedProperties() {
        return REPORTED_PROPERTIES;
    }
}
