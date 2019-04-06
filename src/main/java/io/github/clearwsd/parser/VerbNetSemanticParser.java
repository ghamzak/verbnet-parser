package io.github.clearwsd.parser;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import io.github.clearwsd.propbank.type.ArgNumber;
import io.github.clearwsd.propbank.type.PropBankArg;
import io.github.clearwsd.semlink.PropBankPhrase;
import io.github.clearwsd.semlink.PropBankVerbNetAligner;
import io.github.clearwsd.semlink.aligner.PbVnAlignment;
import io.github.clearwsd.tfnlp.app.DefaultChunking;
import io.github.clearwsd.tfnlp.app.Span;
import io.github.clearwsd.type.DepTree;
import io.github.clearwsd.verbnet.VerbNetClass;
import io.github.clearwsd.verbnet.semantics.EventArgument;
import io.github.clearwsd.verbnet.semantics.SemanticPredicate;
import io.github.clearwsd.verbnet.semantics.ThematicRoleArgument;
import io.github.clearwsd.verbnet.type.FramePhrase;
import io.github.clearwsd.verbnet.type.NounPhrase;
import io.github.clearwsd.verbnet.type.SemanticArgumentType;
import io.github.clearwsd.verbnet.type.ThematicRoleType;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * VerbNet semantic parser.
 *
 * @author jgung
 */
@Slf4j
@AllArgsConstructor
public class VerbNetSemanticParser {

    private SemanticRoleLabeler<PropBankArg> roleLabeler;
    private VerbNetSenseClassifier classifier;
    private PropBankVerbNetAligner aligner;

    public List<SemanticPredicate> parsePredicates(@NonNull PbVnAlignment proposition) {
        List<SemanticPredicate> predicates = proposition.frame().frame().getSemantics().getPredicates().stream()
                .map(SemanticPredicate::of)
                .collect(Collectors.toList());

        for (SemanticPredicate predicate : predicates) {
            List<EventArgument<VerbNetClass>> args = predicate.get(SemanticArgumentType.EVENT);
            for (EventArgument<VerbNetClass> arg : args) {
                arg.variable(proposition.proposition().predicate().sense());
            }

            List<ThematicRoleArgument<PropBankPhrase>> roles = predicate.get(SemanticArgumentType.THEMROLE);
            for (ThematicRoleArgument<PropBankPhrase> role : roles) {
                Optional<FramePhrase> phrase = proposition.byRole(role.thematicRoleType());
                phrase.ifPresent(framePhrase -> role.variable(proposition.alignment().getSource(framePhrase)));
            }

        }

        return predicates;
    }

    public VerbNetSemanticParse parseSentence(@NonNull String sentence) {
        final List<String> tokens = classifier.tokenize(sentence);
        final DepTree parsed = classifier.parse(tokens);
        final VerbNetSemanticParse parse = new VerbNetSemanticParse()
                .tokens(tokens)
                .tree(parsed);

        for (Proposition<VerbNetClass, PropBankArg> prop : roleLabeler.parse(parsed, classifier.predict(parsed))) {
            if (prop.predicate().sense() == null) {
                continue;
            }

            VerbNetProp vnProp = new VerbNetProp()
                    .propbankProp(prop)
                    .tokens(tokens);

            aligner.align(prop, parsed).ifPresent(pbVnAlignment -> {
                // get thematic role alignment
                Preconditions.checkState(pbVnAlignment.sourcePhrases().size() == prop.arguments().spans().size());
                List<Span<ThematicRoleType>> types = new ArrayList<>();
                Iterator<PropBankPhrase> propBankPhrases = pbVnAlignment.sourcePhrases().iterator();
                for (Span<PropBankArg> span : prop.arguments().spans()) {
                    PropBankPhrase phrase = propBankPhrases.next();

                    if (phrase.getNumber() == ArgNumber.V) {
                        types.add(Span.convert(span, ThematicRoleType.VERB));
                    }

                    Optional<NounPhrase> nounPhrase = pbVnAlignment.alignedPhrases(phrase).stream()
                            .filter(np -> np instanceof NounPhrase)
                            .map(np -> (NounPhrase) np)
                            .findFirst();
                    nounPhrase.ifPresent(np -> types.add(Span.convert(span, np.thematicRoleType())));
                }
                vnProp.verbnetProp(new Proposition<>(prop.predicate(), new DefaultChunking<>(types)));
                // get semantic predicates
                vnProp.predicates(parsePredicates(pbVnAlignment));
            });

            parse.props().add(vnProp);
        }

        return parse;
    }

    public List<VerbNetSemanticParse> parseDocument(@NonNull String document) {
        return classifier.segment(document).stream().map(this::parseSentence).collect(Collectors.toList());
    }

    public static SemanticRoleLabeler<PropBankArg> roleLabeler(@NonNull String modelPath) {
        return new SemanticRoleLabeler<>(RoleLabelerUtils.shallowSemanticParser(modelPath), PropBankArg::fromLabel);
    }

    public static void main(String[] args) {
        String mappingsPath = "data/pbvn-mappings.json";
        String modelDir = "data/models/unified-propbank";
        String wsdModel = "data/models/verbnet/nlp4j-verbnet-3.3.bin";

        SemanticRoleLabeler<PropBankArg> roleLabeler = roleLabeler(modelDir);
        VerbNetSenseClassifier classifier = VerbNetSenseClassifier.fromModelPath(wsdModel);
        PropBankVerbNetAligner aligner = PropBankVerbNetAligner.of(mappingsPath);
        VerbNetSemanticParser parser = new VerbNetSemanticParser(roleLabeler, classifier, aligner);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.print(">> ");
                String line = scanner.nextLine().trim();
                if (line.equalsIgnoreCase("quit")) {
                    break;
                }
                parser.parseDocument(line)
                        .forEach(System.out::println);
            } catch (Exception e) {
                log.warn("An unexpected error occurred", e);
            }
        }
    }

}
