/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.portable;

import org.apache.ignite.internal.util.IgniteUtils;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.marshaller.MarshallerContextTestImpl;
import org.apache.ignite.marshaller.portable.PortableMarshaller;
import org.apache.ignite.portable.PortableField;
import org.apache.ignite.portable.PortableMetadata;
import org.apache.ignite.portable.PortableObject;
import org.apache.ignite.portable.PortableTypeConfiguration;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * Contains tests for portable object fields.
 */
public abstract class PortableFieldsAbstractSelfTest extends GridCommonAbstractTest {
    /** Dummy metadata handler. */
    protected static final PortableMetaDataHandler META_HND = new PortableMetaDataHandler() {
        @Override public void addMeta(int typeId, PortableMetadata meta) {
            // No-op.
        }

        @Override public PortableMetadata metadata(int typeId) {
            return null;
        }
    };

    /** Marshaller. */
    protected PortableMarshaller marsh;

    /** Portable context. */
    protected PortableContext ctx;

    /** {@inheritDoc} */
    @Override protected void beforeTest() throws Exception {
        super.beforeTest();

        ctx = new PortableContext(META_HND, null);

        marsh = new PortableMarshaller();

        marsh.setTypeConfigurations(Arrays.asList(
            new PortableTypeConfiguration(TestObject.class.getName()),
            new PortableTypeConfiguration(TestOuterObject.class.getName()),
            new PortableTypeConfiguration(TestInnerObject.class.getName())
        ));

        marsh.setContext(new MarshallerContextTestImpl(null));

        IgniteUtils.invoke(PortableMarshaller.class, marsh, "setPortableContext", ctx);
    }

    /**
     * Test byte field.
     *
     * @throws Exception If failed.
     */
    public void testByte() throws Exception {
        check("fByte");
    }

    /**
     * Test boolean field.
     *
     * @throws Exception If failed.
     */
    public void testBoolean() throws Exception {
        check("fBool");
    }

    /**
     * Test short field.
     *
     * @throws Exception If failed.
     */
    public void testShort() throws Exception {
        check("fShort");
    }

    /**
     * Test char field.
     *
     * @throws Exception If failed.
     */
    public void testChar() throws Exception {
        check("fChar");
    }
    /**
     * Test int field.
     *
     * @throws Exception If failed.
     */
    public void testInt() throws Exception {
        check("fInt");
    }

    /**
     * Test long field.
     *
     * @throws Exception If failed.
     */
    public void testLong() throws Exception {
        check("fLong");
    }

    /**
     * Test float field.
     *
     * @throws Exception If failed.
     */
    public void testFloat() throws Exception {
        check("fFloat");
    }

    /**
     * Test double field.
     *
     * @throws Exception If failed.
     */
    public void testDouble() throws Exception {
        check("fDouble");
    }

    /**
     * Test string field.
     *
     * @throws Exception If failed.
     */
    public void testString() throws Exception {
        check("fString");
    }

    /**
     * Test date field.
     *
     * @throws Exception If failed.
     */
    public void testDate() throws Exception {
        check("fDate");
    }

    /**
     * Test timestamp field.
     *
     * @throws Exception If failed.
     */
    public void testTimestamp() throws Exception {
        check("fTimestamp");
    }

    /**
     * Test UUID field.
     *
     * @throws Exception If failed.
     */
    public void testUuid() throws Exception {
        check("fUuid");
    }

    /**
     * Test decimal field.
     *
     * @throws Exception If failed.
     */
    public void testDecimal() throws Exception {
        check("fDecimal");
    }

    /**
     * Test object field.
     *
     * @throws Exception If failed.
     */
    public void testObject() throws Exception {
        check("fObj");
    }

    /**
     * Test null field.
     *
     * @throws Exception If failed.
     */
    public void testNull() throws Exception {
        check("fNull");
    }

    /**
     * Test missing field.
     *
     * @throws Exception If failed.
     */
    public void testMissing() throws Exception {
        String fieldName = "fMissing";

        checkNormal(fieldName, false);
        checkNested(fieldName, false);
    }

    /**
     * Check field resolution in both normal and nested modes.
     *
     * @param fieldName Field name.
     * @throws Exception If failed.
     */
    public void check(String fieldName) throws Exception {
        checkNormal(fieldName, true);
        checkNested(fieldName, true);
    }

    /**
     * Check field.
     *
     * @param fieldName Field name.
     * @param exists Whether field should exist.
     * @throws Exception If failed.
     */
    private void checkNormal(String fieldName, boolean exists) throws Exception {
        TestContext ctx = context(fieldName);

        check0(fieldName, ctx, exists);
    }

    /**
     * Check nested field.
     *
     * @param fieldName Field name.
     * @param exists Whether field should exist.
     * @throws Exception If failed.
     */
    private void checkNested(String fieldName, boolean exists) throws Exception {
        TestContext ctx = nestedContext(fieldName);

        check0(fieldName, ctx, exists);
    }

    /**
     * Internal check routine.
     *
     * @param fieldName Field name.
     * @param ctx Context.
     * @param exists Whether field should exist.
     * @throws Exception If failed.
     */
    private void check0(String fieldName, TestContext ctx, boolean exists) throws Exception {
        Object val = ctx.field.value(ctx.portObj);

        if (exists) {
            assertTrue(ctx.field.exists(ctx.portObj));

            Object expVal = U.field(ctx.obj, fieldName);

            if (val instanceof PortableObject)
                val = ((PortableObject) val).deserialize();

            assertEquals(expVal, val);
        }
        else {
            assertFalse(ctx.field.exists(ctx.portObj));

            assert val == null;
        }
    }

    /**
     * Get test context.
     *
     * @param fieldName Field name.
     * @return Test context.
     * @throws Exception If failed.
     */
    private TestContext context(String fieldName) throws Exception {
        TestObject obj = createObject();

        PortableObjectEx portObj = toPortable(marsh, obj);

        PortableField field = portObj.fieldDescriptor(fieldName);

        return new TestContext(obj, portObj, field);
    }

    /**
     * Get test context with nested test object.
     *
     * @param fieldName Field name.
     * @return Test context.
     * @throws Exception If failed.
     */
    private TestContext nestedContext(String fieldName) throws Exception {
        TestObject obj = createObject();
        TestOuterObject outObj = new TestOuterObject(obj);

        PortableObjectEx portOutObj = toPortable(marsh, outObj);
        PortableObjectEx portObj = portOutObj.field("fInner");

        assert portObj != null;

        PortableField field = portObj.fieldDescriptor(fieldName);

        return new TestContext(obj, portObj, field);
    }

    /**
     * Create test object.
     *
     * @return Test object.
     */
    private TestObject createObject() {
        return new TestObject(0);
    }

    /**
     * Convert object to portable object.
     *
     * @param marsh Marshaller.
     * @param obj Object.
     * @return Portable object.
     * @throws Exception If failed.
     */
    protected abstract PortableObjectEx toPortable(PortableMarshaller marsh, Object obj) throws Exception;

    /**
     * Outer test object.
     */
    @SuppressWarnings("UnusedDeclaration")
    public static class TestOuterObject {
        /** Inner object. */
        public TestObject fInner;

        /**
         * Default constructor.
         */
        public TestOuterObject() {
            // No-op.
        }

        /**
         * Constructor.
         *
         * @param fInner Inner object.
         */
        public TestOuterObject(TestObject fInner) {
            this.fInner = fInner;
        }
    }

    /**
     * Test object class, c
     */
    @SuppressWarnings("UnusedDeclaration")
    public static class TestObject {
        /** Primitive fields. */
        public byte fByte;
        public boolean fBool;
        public short fShort;
        public char fChar;
        public int fInt;
        public long fLong;
        public float fFloat;
        public double fDouble;

        /** Special fields. */
        public String fString;
        public Date fDate;
        public Timestamp fTimestamp;
        public UUID fUuid;
        public BigDecimal fDecimal;

        /** Nested object. */
        public TestInnerObject fObj;

        /** Field which is always set to null. */
        public Object fNull;

        /**
         * Default constructor.
         */
        public TestObject() {
            // No-op.
        }

        /**
         * Non-default constructor.
         *
         * @param ignore Ignored.
         */
        public TestObject(int ignore) {
            fByte = 1;
            fBool = true;
            fShort = 2;
            fChar = 3;
            fInt = 4;
            fLong = 5;
            fFloat = 6.6f;
            fDouble = 7.7;

            fString = "8";
            fDate = new Date();
            fTimestamp = new Timestamp(new Date().getTime());
            fUuid = UUID.randomUUID();
            fDecimal = new BigDecimal(9);

            fObj = new TestInnerObject(10);
        }
    }

    /**
     * Inner test object.
     */
    @SuppressWarnings("UnusedDeclaration")
    public static class TestInnerObject {
        /** Value. */
        private int val;

        /**
         * Default constructor.
         */
        public TestInnerObject() {
            // No-op.
        }

        /**
         * Constructor.
         *
         * @param val Value.
         */
        public TestInnerObject(int val) {
            this.val = val;
        }

        /** {@inheritDoc} */
        @Override public int hashCode() {
            return val;
        }

        /** {@inheritDoc} */
        @Override public boolean equals(Object other) {
            return other != null && other instanceof TestInnerObject && val == ((TestInnerObject)(other)).val;
        }
    }

    /**
     * Test context.
     */
    public static class TestContext {
        /** Object. */
        public final TestObject obj;

        /** Portable object. */
        public final PortableObjectEx portObj;

        /** Field. */
        public final PortableField field;

        /**
         * Constructor.
         *
         * @param obj Object.
         * @param portObj Portable object.
         * @param field Field.
         */
        public TestContext(TestObject obj, PortableObjectEx portObj, PortableField field) {
            this.obj = obj;
            this.portObj = portObj;
            this.field = field;
        }
    }
}
