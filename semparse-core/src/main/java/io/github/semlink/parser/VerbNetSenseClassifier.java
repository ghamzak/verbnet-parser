package io.github.semlink.parser;

import io.github.clearwsd.DefaultSensePrediction;
import io.github.clearwsd.DefaultSensePredictor;
import io.github.clearwsd.ParsingSensePredictor;
import io.github.clearwsd.SensePrediction;
import io.github.clearwsd.parser.Nlp4jDependencyParser;
import io.github.clearwsd.type.DepTree;
import io.github.clearwsd.type.FeatureType;
import io.github.clearwsd.verbnet.DefaultVnIndex;
import io.github.clearwsd.verbnet.VnClass;
import io.github.clearwsd.verbnet.VnIndex;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * VerbNet-specific {@link ParsingSensePredictor} implementation with {@link VnClass} senses.
 *
 * @author jgung
 */
@AllArgsConstructor
public class VerbNetSenseClassifier implements ParsingSensePredictor<VnClass> {

    private ParsingSensePredictor<VnClass> basePredictor;
    private VnIndex verbNet;

    @Override
    public List<SensePrediction<VnClass>> predict(@NonNull DepTree depTree) {
        List<SensePrediction<VnClass>> senses = basePredictor.predict(depTree);
        return senses.stream().map(sense -> convert(sense, depTree)).collect(Collectors.toList());
    }

    @Override
    public List<SensePrediction<VnClass>> predict(List<String> tokens) {
        return predict(basePredictor.parse(tokens));
    }

    @Override
    public DepTree parse(@NonNull List<String> tokens) {
        return basePredictor.parse(tokens);
    }

    @Override
    public List<String> segment(@NonNull String document) {
        return basePredictor.segment(document);
    }

    @Override
    public List<String> tokenize(@NonNull String sentence) {
        return basePredictor.tokenize(sentence);
    }

    private SensePrediction<VnClass> convert(@NonNull SensePrediction<VnClass> sense, DepTree tree) {
        VnClass result = null;
        if (sense.sense() != null) {
            Set<VnClass> senses = verbNet
                .getByBaseIdAndLemma(sense.id(), tree.get(sense.index()).feature(FeatureType.Lemma));
            if (!senses.isEmpty()) {
                result = senses.stream().findFirst().get();
            }
        }
        return new DefaultSensePrediction<>(sense.index(), sense.originalText(), sense.id(), result);
    }

    /**
     * Initialize from a given {@link io.github.clearwsd.SensePredictor} model path and {@link VnIndex} lexicon.
     */
    public static VerbNetSenseClassifier fromModelPath(@NonNull String modelPath, @NonNull VnIndex verbNet) {
        DefaultSensePredictor<VnClass> predictor = DefaultSensePredictor.loadFromResource(modelPath,
            new Nlp4jDependencyParser());
        return new VerbNetSenseClassifier(predictor, verbNet);
    }

    /**
     * Initialize from a given {@link io.github.clearwsd.SensePredictor} model path.
     */
    public static VerbNetSenseClassifier fromModelPath(@NonNull String modelPath) {
        return fromModelPath(modelPath, new DefaultVnIndex());
    }

}
