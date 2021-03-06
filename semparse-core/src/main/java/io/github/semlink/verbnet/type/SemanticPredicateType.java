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

package io.github.semlink.verbnet.type;

import lombok.NonNull;

/**
 * VerbNet semantic predicate type.
 *
 * @author jgung
 */
public enum SemanticPredicateType {

    ABOUT,
    ACT,
    ADJUSTED,
    ADMIT,
    ADOPT,
    ADV,
    AGREE,
    ALIVE,
    ALLOW,
    APPEAR,
    APPLY_HEAT,
    APPLY_MATERIAL,
    APPROVE,
    ASSESS,
    ATTACHED,
    ATTEMPT,
    ATTRACT,
    AUTHORITY_RELATIONSHIP,
    AVOID,
    BASE,
    BE,
    BECOMES,
    BEGIN,
    BELIEVE,
    BENEFIT,
    BODY_MOTION,
    BODY_PROCESS,
    BODY_REFLEX,
    BODY_SENSATION,
    CALCULATE,
    CAPACITY,
    CAUSE,
    CHANGE_VALUE,
    CHARACTERIZE,
    CHARGE,
    CO_TEMPORAL,
    CONCLUDE,
    CONFINED,
    CONFLICT,
    CONFRONT,
    CONSIDER,
    CONSPIRE,
    CONTACT,
    CONTAIN,
    CONTINUE,
    CONVERT,
    COOKED,
    COOPERATE,
    COPE,
    CORRELATED,
    COST,
    COVERED,
    CREATE_IMAGE,
    DECLARE,
    DEDICATE,
    DEFEND,
    DEGRADATION_MATERIAL_INTEGRITY,
    DELAY,
    DEPEND,
    DESCRIBE,
    DESIGNATED,
    DESIRE,
    DESTROYED,
    DEVELOP,
    DIFFERENT,
    DIRECTION,
    DISAPPEAR,
    DISCOMFORT,
    DISCOVER,
    DO,
    EARN,
    ELLIPTICAL_MOTION,
    EMIT,
    EMOTIONAL_STATE,
    END,
    ENFORCE,
    ENSURE,
    EQUALS,
    EXCEED,
    EXERT_FORCE,
    EXIST,
    EXPERIENCE,
    FILLED_WITH,
    FINANCIAL_INTEREST_IN,
    FINANCIAL_RELATIONSHIP,
    FREE,
    FUNCTION,
    GIVE_BIRTH,
    GROUP,
    HARMED,
    HARMONIZE,
    HAS_CONFIGURATION,
    HAS_INFORMATION,
    HAS_LOCATION,
    HAS_ORGANIZATION_ROLE,
    HAS_ORIENTATION,
    HAS_POSITION,
    HAS_POSSESSION,
    HAS_PROPERTY,
    HAS_SET_MEMBER,
    HAS_STATE,
    HAS_VAL,
    HELP,
    IN_REACTION_TO,
    INDICATE,
    INTEND,
    INTRINSIC_MOTION,
    INVOLUNTARY,
    INVOLVE,
    IRREALIS,
    LICENSE,
    LIMIT,
    LINGER,
    LOCATION,
    MADE_OF,
    MANNER,
    MASQUERADE,
    MATERIAL_INTEGRITY_STATE,
    MEETS,
    MINGLED,
    MOTION,
    NECESSITATE,
    NEGLECT,
    OCCUR,
    OPPOSITION,
    OVERLAPS,
    PART_OF,
    PENETRATING,
    PERCEIVE,
    PERFORM,
    PHYSICAL_FORM,
    PROMOTE,
    PROPERTY,
    RELATE,
    REPEAT_SEQUENCE,
    REPEATED_SEQUENCE,
    REQUIRE,
    RISK,
    ROTATIONAL_MOTION,
    RUSH,
    SATISFY,
    SEARCH,
    SEEM,
    SET_MEMBER,
    SIGNIFY,
    SLEEP,
    SOCIAL_INTERACTION,
    SPEND,
    SUBJUGATED,
    SUCCESSFUL_IN,
    SUFFOCATE,
    SUFFOCATED,
    SUPPORT,
    SUSPECT,
    TAKE_CARE_OF,
    TAKE_IN,
    THINK,
    TIME,
    TOGETHER,
    TRANSFER,
    TRANSFER_INFO,
    UNDERSTAND,
    URGE,
    USE,
    UTILIZE,
    VALUE,
    VISIBLE,
    VOIDED,
    WEAR,
    WEATHER,
    WITHDRAW,
    WORK,
    YIELD,
    UNKNOWN;


    public static SemanticPredicateType fromString(@NonNull String string) {
        try {
            return valueOf(string.toUpperCase().replaceAll("-", "_").trim());
        } catch (Exception ignored) {
        }
        return SemanticPredicateType.UNKNOWN;
    }

}
