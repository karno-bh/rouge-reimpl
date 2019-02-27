package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.starter.gui.main.element.FilterSelectionCheckbox;
import il.ac.sce.ir.metric.starter.gui.main.event.component_event.FilterSelectionPanelEvent;
import il.ac.sce.ir.metric.starter.gui.main.model.FilterSelectionPanelModel;
import il.ac.sce.ir.metric.starter.gui.main.util.Caret;
import il.ac.sce.ir.metric.starter.gui.main.util.ModelsManager;
import il.ac.sce.ir.metric.starter.gui.main.util.WholeSpaceFiller;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class FiltersSelectionPanel extends JPanel {

    private final PubSub pubSub;

    private final FilterSelectionPanelModel model;

    private final FilterSelectionCheckbox lowerCaseCheckbox;

    private final FilterSelectionCheckbox punctuationCheckbox;

    private final FilterSelectionCheckbox stopwordsRemovalCheckbox;

    private final FilterSelectionCheckbox porterStemmingCheckbox;


    public FiltersSelectionPanel(PubSub pubSub, ModelsManager modelsManager) {
        this.pubSub = pubSub;
        this.model = new FilterSelectionPanelModel(pubSub);

        modelsManager.register(model);

        setLayout(new GridBagLayout());
        WholeSpaceFiller filler = new WholeSpaceFiller();
        GridBagConstraints fillingConstraints = filler.getFillingConstraints();
        fillingConstraints.gridx = 1000;
        add(new JPanel(), fillingConstraints);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 10, 10, 10);

        Caret caret = new Caret(0,0, 100);
        constraints = caret.asGridBag(constraints);
        JLabel l = new JLabel("Lower Case Filter");
        add(l, constraints);

        constraints = caret.next(constraints);
        lowerCaseCheckbox = new FilterSelectionCheckbox(Constants.LOWER_CASE_FILTER);
        lowerCaseCheckbox.addItemListener(this::onCheckBoxClicked);
        add(lowerCaseCheckbox, constraints);

        constraints = caret.next(constraints);
        l = new JLabel("Punctuation Filter");
        add(l, constraints);

        constraints = caret.next(constraints);
        punctuationCheckbox = new FilterSelectionCheckbox(Constants.PUNCTUATION_FILTER);
        punctuationCheckbox.addItemListener(this::onCheckBoxClicked);
        add(punctuationCheckbox, constraints);

        constraints = caret.next(constraints);
        l = new JLabel("Stopwords Filter");
        add(l, constraints);

        constraints = caret.next(constraints);
        stopwordsRemovalCheckbox = new FilterSelectionCheckbox(Constants.STOP_WORDS_REMOVAL_FILTER);
        stopwordsRemovalCheckbox.addItemListener(this::onCheckBoxClicked);
        add(stopwordsRemovalCheckbox, constraints);

        constraints = caret.next(constraints);
        l = new JLabel("Porter Stemming");
        add(l, constraints);

        constraints = caret.next(constraints);
        porterStemmingCheckbox = new FilterSelectionCheckbox(Constants.PORTER_STEMMER_FILTER);
        porterStemmingCheckbox.addItemListener(this::onCheckBoxClicked);
        add(porterStemmingCheckbox, constraints);
    }

    private void onCheckBoxClicked(ItemEvent event) {
        FilterSelectionCheckbox source = (FilterSelectionCheckbox) event.getSource();

        FilterSelectionPanelEvent panelEvent = new FilterSelectionPanelEvent(source.getFilter(),
                event.getStateChange() == ItemEvent.SELECTED);

        pubSub.publish(panelEvent);
    }
}
