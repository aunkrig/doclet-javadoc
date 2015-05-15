
/*
 * de.unkrig.doclet.javadoc - A reimplementation of the JAVADOC utility for experimental purposes
 *
 * Copyright (c) 2015, Arno Unkrig
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *       following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *       following disclaimer in the documentation and/or other materials provided with the distribution.
 *    3. The name of the author may not be used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package de.unkrig.doclet.javadoc;

import java.util.Collection;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

import de.unkrig.commons.doclet.html.Html;
import de.unkrig.commons.lang.AssertionUtil;
import de.unkrig.commons.lang.protocol.Longjump;
import de.unkrig.commons.nullanalysis.Nullable;

/**
 * Wrappers for the {@link Doc}-derived interfaces and clases.
 */
public final
class Doccs {

    static { AssertionUtil.enableAssertionsForThisClass(); }

    private static final Html HTML = new Html(Html.STANDARD_LINK_MAKER);

    private final RootDoc rootDoc;

    public
    Doccs(RootDoc rootDoc) { this.rootDoc = rootDoc; }

    /**
     * A wrapper for {@link Doc} which adds painfully missing bean getters.
     */
    public
    interface Docc extends Comparable<Docc> {

        /**
         * @return The {@link Doc} wrapped in this object
         */
        Doc getDoc();

        /**
         * @return The name getter; useful for sorting by name within FTL
         * @see    Doc#name()
         */
        String getName();

        /**
         * Returns the magical "first sentence" of the description, or "".
         */
        String getFirstSentenceOfDescription();

        /**
         * Returns description of the element, or "".
         */
        String getDescription();

        /**
         * @return The (home-relative) URL that links to the documentation of this element
         */
        String getHref();
    }

    /**
     * A wrapper for {@link ClassDoc} which adds painfully missing bean getters.
     */
    public
    interface ClassDocc extends Docc {

        /**
         * @return The "simple name" getter; useful for sorting by simple name within FTL
         */
        String getSimpleName();

        /**
         * @return The "qualified name" getter; useful for sorting by simple name within FTL
         */
        String getQualifiedName();

        /**
         * @return The types that this class or interface extends and implements, in "javadoc order"
         */
        Collection<ClassDocc> getBaseClassesAndInterfaces();

        /**
         * @return The methods declared in this class or interface, in declaration order
         */
        Collection<MethodDocc> getMethods();

        /**
         * @return The methods declared in this class or interface, in "javadoc order"
         */
        Collection<MethodDocc> getMethodsSorted();

        /**
         * @return The "constants" declared in this class or interface
         * @see    FieldDocc#isConstant()
         */
        Collection<FieldDocc> getConstants();
    }

    /**
     * A wrapper for {@link ClassDoc} which adds painfully missing bean getters.
     */
    public
    interface FieldDocc extends Docc {

        /**
         * @return Whether this field poses a "constant"
         */
        boolean isConstant();
    }

    public
    interface ThrowsTagg {
        String           getExceptionQualifiedName();
        @Nullable String getExceptionComment();
    }

    /**
     * A wrapper for {@link MethodDoc} which adds painfully missing bean getters.
     */
    public
    interface MethodDocc extends Docc {

        /**
         * @return The HTML markup of the return value description
         */
        @Nullable String getReturnValueDescription();

        /**
         * @return The "{@code @throws}" tags of the method
         */
        Collection<ThrowsTagg> getThrowsTags();

        /**
         * Computes and returns the "fragment identifier" for a method; usable for "{@code <a name="x">}" and "{@code
         * <a href="...#x">}".
         * <p>
         *   Notice: Some methods have <i>two</i> fragments, e.g. "notNull-java.lang.Object-java.lang.String-" and
         *   "notNull-T-java.lang.String-".
         * </p>
         */
        String[] getFragments();
    }

    /**
     * A wrapper for {@link PackageDoc} which adds painfully missing bean getters.
     */
    public
    interface PackageDocc extends Docc {

        /**
         * @return The subset of classes and interfaces which declare at least one "constant"
         * @see    FieldDocc#isConstant()
         */
        Collection<ClassDocc> getClassesAndInterfacesWithConstants();

        /**
         * @return The annotation types included in this package
         */
        Collection<ClassDocc> getAnnotationTypes();

        /**
         * @return The enums included in this package
         */
        Collection<ClassDocc> getEnums();

        /**
         * @return The errors included in this package
         */
        Collection<ClassDocc> getErrors();

        /**
         * @return The exceptions included in this package
         */
        Collection<ClassDocc> getExceptions();

        /**
         * @return The interfaces included in this package
         */
        Collection<ClassDocc> getInterfaces();

        /**
         * @return The classes (excluding annotation types, enums, errors, exceptions and interfaces) included in this
         *         package
         */
        Collection<ClassDocc> getClasses();
    }

    /**
     * A basic implementation of the {@link Docc}.
     */
    public abstract
    class AbstractDocc implements Docc {

        private final Doc doc;

        public
        AbstractDocc(Doc doc) { this.doc = doc; }

        /**
         * A basic implementation of {@link Comparable}, based solely on the elemen <i>name</i>. Some subclasses
         * override this method to implement an ordering of element with equal names.
         *
         * @see MethodDocc#compareTo(Docc)
         */
        @Override public int
        compareTo(@Nullable Docc that) {
            assert that != null;
            assert that.getClass() == this.getClass();
            return this.getName().compareTo(that.getName());
        }

        @Override public final Doc
        getDoc() { return this.doc; }

        @Override public final String
        getName() { return this.doc.name(); }

        @Override public final String
        getFirstSentenceOfDescription() {

            try {
                return Doccs.HTML.fromTags(this.doc.firstSentenceTags(), this.doc, Doccs.this.rootDoc);
            } catch (Longjump l) {
                return "???";
            }
        }

        @Override public final String
        getDescription() {

            try {
                return Doccs.HTML.fromTags(this.doc.inlineTags(), this.doc, Doccs.this.rootDoc);
            } catch (Longjump l) {
                return "???";
            }
        }
    }
}
