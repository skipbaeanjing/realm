/*
 * Copyright 2017 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.realm.entities.realmname;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;
import io.realm.annotations.RealmNamingPolicy;

@RealmClass(fieldNamingPolicy = RealmNamingPolicy.LOWER_CASE_WITH_DASHES)
public class ClassWithPolicy extends RealmObject {

    public static final String CLASS_NAME = "ClassWithPolicy";
    public static final String FIELD_CAMEL_CASE = "camel-case";
    public static final String FIELD_PASCAL_CASE = "pascal-case";
    public static final String FIELD_M_HUNGARIAN = "m-hungarian";
    public static final String FIELD_ALLCAPS = "a-l-l-c-a-p-s";
    public static final String FIELD_ALLLOWER = "alllower";
    public static final String FIELD_WITH_UNDERSCORES = "with_underscores";
    public static final String FIELD_WITH_SPECIAL_CHARS = "$_internal-var";
    public static final List<String> ALL_FIELDS = Arrays.asList(
            FIELD_CAMEL_CASE,
            FIELD_PASCAL_CASE,
            FIELD_M_HUNGARIAN,
            FIELD_ALLCAPS,
            FIELD_ALLLOWER,
            FIELD_WITH_UNDERSCORES,
            FIELD_WITH_SPECIAL_CHARS
    );

    public String camelCase;
    public int PascalCase;
    public boolean mHungarian;
    public byte[] ALLCAPS;
    public Date alllower;
    public ClassWithPolicy with_underscores;
    public RealmList<ClassWithPolicy> $_internalVar;
}
