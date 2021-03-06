/*
 * Copyright 2019 James Gung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.semlink.app.api.model;

import com.google.common.collect.Maps;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.semlink.app.Chunking;
import io.github.semlink.parser.VerbNetProp;
import io.github.semlink.semlink.SemlinkRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * JSON-serializable model for VerbNet semantic analyses.
 *
 * @author jgung
 */
@Setter
@Getter
@Accessors(fluent = true)
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PropModel {

    private String sense;
    private EventModel mainEvent;
    private List<EventModel> events = new ArrayList<>();
    private List<SemlinkRoleModel> spans = new ArrayList<>();

    public PropModel(VerbNetProp prop) {
        this.sense = prop.proposition().predicate().verbNetId().toString();
        this.events = prop.subEvents().stream().map(EventModel::new).collect(Collectors.toList());
        this.mainEvent = prop.mainEvent().map(EventModel::new).orElse(null);
        this.spans = getSpans(prop.proposition().arguments(), prop.tokens(), prop.proposition().relIndex());
        Map<String, EventModel> modelMap = Maps.uniqueIndex(events, EventModel::name);
        if (null != mainEvent) {
            for (SemanticPredicateModel predicate : mainEvent.predicates()) {
                for (SemanticPredicateModel.SemanticArgumentModel arg : predicate.args()) {
                    arg.eventIndex(Optional.ofNullable(modelMap.get(arg.value())).map(EventModel::eventIndex).orElse(-1));
                }
            }
        }
    }

    private static List<SemlinkRoleModel> getSpans(Chunking<SemlinkRole> chunking, List<String> tokens, int predIndex) {
        return chunking.spans().stream()
                .map(role -> new SemlinkRoleModel(role, tokens, predIndex))
                .collect(Collectors.toList());
    }

}
