/*
 * Copyright 2020 Realm Inc.
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

package io.realm;

import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import java.util.Date;

import javax.annotation.Nullable;

import io.realm.internal.ManageableObject;
import io.realm.internal.Row;
import io.realm.internal.Table;


/**
 * {@link Mixed} is used to represent a polymorphic Realm value.
 * It has two modes: a managed and unmanaged mode. In managed mode contents are persisted inside a Realm, in
 * unmanaged mode contents are persisted in the object instance.
 * <p>
 * Only Realm can create managed Mixed. Managed Mixed will automatically update the content whenever the
 * underlying Realm is updated, and can only be accessed using the getter of a {@link io.realm.Mixed}.
 * <p>
 * Unmanaged Mixed can be created by the user and can contain any Realm value, or both managed and unmanaged RealmObjects.
 */
public abstract class Mixed implements ManageableObject {
    private static final class Unmanaged extends Mixed {
        @Nullable
        private final Object value;
        @Nullable
        private final MixedType mixedType;

        Unmanaged() {
            this.value = null;
            this.mixedType = MixedType.NO_TYPE;
        }

        Unmanaged(@Nullable Object value, MixedType mixedType) {
            this.value = value;
            this.mixedType = (value == null) ? MixedType.NO_TYPE : mixedType;
        }

        @Override
        public boolean isManaged() {
            return false;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public boolean isFrozen() {
            return false;
        }

        @Override
        @Nullable
        protected <T> T get(Class<T> clazz, MixedType fieldType) {
            return clazz.cast(value);
        }

        @Override
        public boolean isNull() {
            return value == null;
        }

        @Override
        public MixedType getType() {
            return mixedType;
        }
    }

    abstract static class Managed<M extends RealmModel> extends Mixed {
        protected abstract ProxyState<M> getProxyState();

        protected abstract long getColumnIndex();

        @Override
        public boolean isManaged() {
            return true;
        }

        @Override
        public final boolean isValid() {
            return !getRealm().isClosed() && getRow().isValid();
        }

        @Override
        public boolean isFrozen() {
            return getRealm().isFrozen();
        }

        @Override
        @Nullable
        protected <T> T get(Class<T> clazz, MixedType fieldType) {
            Row row = getRow();
            Table table = row.getTable();
            long rowIndex = row.getObjectKey();
            long columnIndex = getColumnIndex();

            switch (fieldType) {
                case INTEGER:
                    return clazz.cast(table.mixedAsLong(columnIndex, rowIndex));
                case BOOLEAN:
                    return clazz.cast(table.mixedAsBoolean(columnIndex, rowIndex));
                case FLOAT:
                    return clazz.cast(table.mixedAsFloat(columnIndex, rowIndex));
                case DOUBLE:
                    return clazz.cast(table.mixedAsDouble(columnIndex, rowIndex));
                case STRING:
                    return clazz.cast(table.mixedAsString(columnIndex, rowIndex));
                case BINARY:
                    return clazz.cast(table.mixedAsBinaryByteArray(columnIndex, rowIndex));
                case DATE:
                    return clazz.cast(table.mixedAsDate(columnIndex, rowIndex));
                case OBJECT_ID:
                    return clazz.cast(table.mixedAsObjectId(columnIndex, rowIndex));
                case DECIMAL128:
                    return clazz.cast(table.mixedAsDecimal128(columnIndex, rowIndex));
                default:
                    throw new ClassCastException("Couldn't cast to " + fieldType);
            }
        }

        @Override
        public boolean isNull() {
            return getType() == MixedType.NO_TYPE;
        }

        private BaseRealm getRealm() {
            return getProxyState().getRealm$realm();
        }

        @Override
        public MixedType getType() {
            Row row = getRow();
            Table table = row.getTable();
            long rowIndex = row.getObjectKey();
            long columnIndex = getColumnIndex();

            return MixedType.fromNativeValue(table.mixedGetType(columnIndex, rowIndex));
        }

        private Row getRow() {
            return getProxyState().getRow$realm();
        }
    }

    @Nullable
    protected abstract <T> T get(Class<T> clazz, MixedType fieldType);

    /**
     * Creates a new, unmanaged {@link Mixed} with the specified initial value.
     * If the value is not null the type will be {@link MixedType#INTEGER}, {@link MixedType#NO_TYPE} otherwise.
     *
     * @param value initial value
     * @return a new, unmanaged {@link Mixed} of a Long
     */
    public static Mixed valueOf(@Nullable Long value) {
        return new Unmanaged(value, MixedType.INTEGER);
    }

    /**
     * Creates a new, unmanaged {@link Mixed} with the specified initial value.
     * If the value is not null the type will be {@link MixedType#BOOLEAN}, {@link MixedType#NO_TYPE} otherwise.
     *
     * @param value initial value
     * @return a new, unmanaged {@link Mixed} of a Boolean
     */
    public static Mixed valueOf(@Nullable Boolean value) {
        return new Unmanaged(value, MixedType.BOOLEAN);
    }

    /**
     * Creates a new, unmanaged {@link Mixed} with the specified initial value.
     * If the value is not null the type will be {@link MixedType#FLOAT}, {@link MixedType#NO_TYPE} otherwise.
     *
     * @param value initial value
     * @return a new, unmanaged {@link Mixed} of a Float
     */
    public static Mixed valueOf(@Nullable Float value) {
        return new Unmanaged(value, MixedType.FLOAT);
    }

    /**
     * Creates a new, unmanaged {@link Mixed} with the specified initial value.
     * If the value is not null the type will be {@link MixedType#DOUBLE}, {@link MixedType#NO_TYPE} otherwise.
     *
     * @param value initial value
     * @return a new, unmanaged {@link Mixed} of a Double
     */
    public static Mixed valueOf(@Nullable Double value) {
        return new Unmanaged(value, MixedType.DOUBLE);
    }

    /**
     * Creates a new, unmanaged {@link Mixed} with the specified initial value.
     * If the value is not null the type will be {@link MixedType#STRING}, {@link MixedType#NO_TYPE} otherwise.
     *
     * @param value initial value
     * @return a new, unmanaged {@link Mixed} of a String
     */
    public static Mixed valueOf(@Nullable String value) {
        return new Unmanaged(value, MixedType.STRING);
    }

    /**
     * Creates a new, unmanaged {@link Mixed} with the specified initial value.
     * If the value is not null the type will be {@link MixedType#BINARY}, {@link MixedType#NO_TYPE} otherwise.
     *
     * @param value initial value
     * @return a new, unmanaged {@link Mixed} of a byte[]
     */
    public static Mixed valueOf(@Nullable byte[] value) {
        return new Unmanaged(value, MixedType.BINARY);
    }

    /**
     * Creates a new, unmanaged {@link Mixed} with the specified initial value.
     * If the value is not null the type will be {@link MixedType#DATE}, {@link MixedType#NO_TYPE} otherwise.
     *
     * @param value initial value
     * @return a new, unmanaged {@link Mixed} of a Date
     */
    public static Mixed valueOf(@Nullable Date value) {
        return new Unmanaged(value, MixedType.DATE);
    }

    /**
     * Creates a new, unmanaged {@link Mixed} with the specified initial value.
     * If the value is not null the type will be {@link MixedType#OBJECT_ID}, {@link MixedType#NO_TYPE} otherwise.
     *
     * @param value initial value
     * @return a new, unmanaged {@link Mixed} of a ObjectId
     */
    public static Mixed valueOf(@Nullable ObjectId value) {
        return new Unmanaged(value, MixedType.OBJECT_ID);
    }

    /**
     * Creates a new, unmanaged {@link Mixed} with the specified initial value.
     * If the value is not null the type will be {@link MixedType#DECIMAL128}, {@link MixedType#NO_TYPE} otherwise.
     *
     * @param value initial value
     * @return a new, unmanaged {@link Mixed} of a Decimal128
     */
    public static Mixed valueOf(@Nullable Decimal128 value) {
        return new Unmanaged(value, MixedType.DECIMAL128);
    }

    /**
     * Creates a new, unmanaged {@link Mixed} of a null value
     *
     * @return a new, unmanaged {@link Mixed} instance of a null value
     */
    public static Mixed nullValue() {
        return new Unmanaged();
    }

    /**
     * Returns true if the inner value is null, false otherwise.
     *
     * @return true if the inner value is null, false otherwise
     */
    public abstract boolean isNull();

    /**
     * Gets the inner type of this Mixed object.
     *
     * @return the inner MixedType
     */
    public abstract MixedType getType();

    /**
     * Gets this value as a Long if it is one, otherwise throws exception.
     *
     * @return a Long
     * @throws java.lang.ClassCastException if this value is not of the expected type
     */
    public Long asInteger() {
        return get(Long.class, MixedType.INTEGER);
    }

    /**
     * Gets this value as a Boolean if it is one, otherwise throws exception.
     *
     * @return a Boolean
     * @throws java.lang.ClassCastException if this value is not of the expected type
     */
    public Boolean asBoolean() {
        return get(Boolean.class, MixedType.BOOLEAN);
    }

    /**
     * Gets this value as a Float if it is one, otherwise throws exception.
     *
     * @return a Float
     * @throws java.lang.ClassCastException if this value is not of the expected type
     */
    public Float asFloat() {
        return get(Float.class, MixedType.FLOAT);
    }

    /**
     * Gets this value as a Double if it is one, otherwise throws exception.
     *
     * @return a Double
     * @throws java.lang.ClassCastException if this value is not of the expected type
     */
    public Double asDouble() {
        return get(Double.class, MixedType.DOUBLE);
    }

    /**
     * Gets this value as a String if it is one, otherwise throws exception.
     *
     * @return a String
     * @throws java.lang.ClassCastException if this value is not of the expected type
     */
    public String asString() {
        return get(String.class, MixedType.STRING);
    }

    /**
     * Gets this value as a byte[] if it is one, otherwise throws exception.
     *
     * @return a byte[]
     * @throws java.lang.ClassCastException if this value is not of the expected type
     */
    public byte[] asBinary() {
        return get(byte[].class, MixedType.BINARY);
    }

    /**
     * Gets this value as a Date if it is one, otherwise throws exception.
     *
     * @return a Date
     * @throws java.lang.ClassCastException if this value is not of the expected type
     */
    public Date asDate() {
        return get(Date.class, MixedType.DATE);
    }

    /**
     * Gets this value as a ObjectId if it is one, otherwise throws exception.
     *
     * @return a ObjectId
     * @throws java.lang.ClassCastException if this value is not of the expected type
     */
    public ObjectId asObjectId() {
        return get(ObjectId.class, MixedType.OBJECT_ID);
    }

    /**
     * Gets this value as a Decimal128 if it is one, otherwise throws exception.
     *
     * @return a Decimal128
     * @throws java.lang.ClassCastException if this value is not of the expected type
     */
    public Decimal128 asDecimal128() {
        return get(Decimal128.class, MixedType.DECIMAL128);
    }

    /**
     * Gets this value as a RealmModel if it is one, otherwise throws exception.
     *
     * @param <T> the RealmModel type to cast the inner value to
     * @return a RealmModel of the T type
     * @throws java.lang.ClassCastException if this value is not of the expected type
     */
    public <T extends RealmModel> T asRealmModel(Class<T> clazz) {
        throw new NoSuchMethodError("Not implemented");
    }
}