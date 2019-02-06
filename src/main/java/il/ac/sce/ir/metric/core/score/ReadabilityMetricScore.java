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
                    Constants.WORD_VARIATION_INDEX,
                    Constants.AVERAGE_WORD_LENGTH,
                    Constants.AVERAGE_SENTENCE_LENGTH,
                    Constants.NOUN_RATIO,
                    Constants.PRONOUN_RATIO
            )));

    private long wordsNum;

    private long charsNum;

    private long uniqueWordsNum;

    private long sentenceNum;

    private long syllablesNum;

    private long nounsNum;

    private long properNounsNum;

    private long uniqueProperNounsNum;

    private long pronounsNum;

    public long getWordsNum() {
        return wordsNum;
    }

    public void setWordsNum(long wordsNum) {
        this.wordsNum = wordsNum;
    }

    public long getCharsNum() {
        return charsNum;
    }

    public void setCharsNum(long charsNum) {
        this.charsNum = charsNum;
    }

    public long getUniqueWordsNum() {
        return uniqueWordsNum;
    }

    public void setUniqueWordsNum(long uniqueWordsNum) {
        this.uniqueWordsNum = uniqueWordsNum;
    }

    public long getSentenceNum() {
        return sentenceNum;
    }

    public void setSentenceNum(long sentenceNum) {
        this.sentenceNum = sentenceNum;
    }

    public long getSyllablesNum() {
        return syllablesNum;
    }

    public void setSyllablesNum(long syllablesNum) {
        this.syllablesNum = syllablesNum;
    }

    public long getNounsNum() {
        return nounsNum;
    }

    public void setNounsNum(long nounsNum) {
        this.nounsNum = nounsNum;
    }

    public long getProperNounsNum() {
        return properNounsNum;
    }

    public void setProperNounsNum(long properNounsNum) {
        this.properNounsNum = properNounsNum;
    }

    public long getUniqueProperNounsNum() {
        return uniqueProperNounsNum;
    }

    public void setUniqueProperNounsNum(long uniqueProperNounsNum) {
        this.uniqueProperNounsNum = uniqueProperNounsNum;
    }

    public long getPronounsNum() {
        return pronounsNum;
    }

    public void setPronounsNum(long pronounsNum) {
        this.pronounsNum = pronounsNum;
    }

    public double getFleschReadingEase() {

        double fre = 206.825 -
                1.015 * ((double) wordsNum / (double) sentenceNum) -
                84.6 * ((double) syllablesNum / (double) wordsNum);
        /*logger.info("getFleschReadingEase: wordsNum = {}, sentenceNum = {}, syllablesNum = {}, fre = {}", wordsNum,
                sentenceNum, syllablesNum, fre);*/

        return fre;
    }

    public double getWordVariationIndex() {
        return Math.log10((double) wordsNum) / Math.log10(2 - Math.log10((double) uniqueWordsNum) / Math.log10((double) wordsNum));
    }

    public double getAverageWordLength() {
        return (double) charsNum / (double) wordsNum;
    }

    public double getAverageSentenceLength() {
        return (double) wordsNum / (double) sentenceNum;
    }

    public double getNounRatio() {
        return (double) nounsNum / (double) wordsNum;
    }

    public double getPronounRatio() {
        return (double) pronounsNum / (double) wordsNum;
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
