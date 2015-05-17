
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
import java.util.List;
import java.util.regex.Pattern;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParameterizedType;
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

        SeeTagg[] getSeeTags();

        /**
         * @return The (home-relative) URL that links to the documentation of this element
         */
        String getHref();

        /**
         * @return A human-readable text, as displayed in "{@code @see}" tags, depending on the <var>ref</var>
         * @see    ClassDocc#toString(Doc)
         * @see    FieldDocc#toString(Doc)
         * @see    MethodDocc#toString(Doc)
         * @see    PackageDocc#toString(Doc)
         */
        String toString(Doc ref);

        /**
         * Links to some elements have a "{@code title="..."}" attribute.
         */
        @Nullable String getTitle();
    }

    /**
     * A wrapper for a {@link ClassDoc} or a {@link ParameterizedType}, which adds painfully missing bean getters.
     */
    public
    interface ClassDocc extends Docc {

        /**
         * @return The simple class name
         * @see    Docc#toString(Doc)
         */
        @Override String toString(Doc ref);

        /**
         * @return E.g. "{@code <K, V>}" or ""
         */
        String getTypeParameters();

        /**
         * @return E.g. "{@code <String, java.util.List>}" or ""
         */
        String getTypeArguments();

        /**
         * @return "class", "interface", "annotation type", ...
         */
        String getCategory();

        /**
         * @return The "simple name", e.g. "{@code Outer.Inner}"; useful for sorting by simple name within FTL
         */
        String getSimpleName();

        /**
         * @return The "qualified name", e.g. "{@code pkg.Outer.Inner}"; useful for sorting by simple name within FTL
         */
        String getQualifiedName();

        @Nullable ClassDocc getSuperclass();

        /**
         * @return The superclass chain, starting with the immediate superclass
         */
        List<ClassDocc> getSuperclassChain();

        /**
         * @return The interfaces that this class (directly and indirectly) implements, in "javadoc order"
         */
        Collection<ClassDocc> getImplementedInterfaces();

        /**
         * @return The interfaces that this interface (directly and indirectly) extends, in "javadoc order"
         */
        Collection<ClassDocc> getAllSuperInterfaces();

        /**
         * @return The classes and interfaces that this class or interface extends and implements, in "javadoc order"
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
         * @return The fields declared in this class or interface, in declaration order
         */
        Collection<FieldDocc> getFields();

        /**
         * @return The fields declared in this class or interface, in "javadoc order"
         */
        Collection<FieldDocc> getFieldsSorted();

        /**
         * @return The "constants" declared in this class or interface
         * @see    FieldDocc#isConstant()
         */
        Collection<FieldDocc> getConstants();

        /**
         * @return All known interfaces that extend this interface
         */
        Collection<ClassDocc> getKnownSubinterfaces();

        Collection<ClassDocc> getNestedClassesAndInterfaces();
    }

    /**
     * A wrapper for {@link ClassDoc} which adds painfully missing bean getters.
     */
    public
    interface FieldDocc extends Docc {

        /**
         * @return The field name iff ref is in the same class, otherwise "<var>simple-class-name</var>{@code
         *         .}<var>field-name</var>"
         * @see    Docc#toString(Doc)
         */
        @Override String toString(Doc ref);

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

    public
    interface ParamTagg {
        String           getName();
        @Nullable String getParameterComment();
    }

    public
    interface SeeTagg {
        String           getLabel();
        Docc             getReference();
    }

    /**
     * A wrapper for {@link MethodDoc} which adds painfully missing bean getters.
     */
    public
    interface MethodDocc extends Docc {

        /**
         * @return "<var>method-name</var>{@code (}<var>qualified-parameter-types</var>{@code )}" iff ref is in the
         *         same class, otherwise "<var>simple-class-name</var>{@code .}<var>method-name</var>{@code
         *         (}<var>qualified-parameter-types</var>{@code )}"
         * @see    Docc#toString(Doc)
         */
        @Override String toString(Doc ref);

        /**
         * @return The HTML markup of the return value description
         */
        @Nullable String getReturnValueDescription();

        /**
         * @return The "{@code @throws}" tags of the method
         */
        Collection<ThrowsTagg> getThrowsTags();

        /**
         * @return The "{@code @param}" tags of the method
         */
        Collection<ParamTagg> getParamTags();

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
         * @return The package name
         * @see    Docc#toString(Doc)
         */
        @Override String toString(Doc ref);

        /**
         * @return The subset of classes and interfaces which declare at least one "constant"
         * @see    FieldDocc#isConstant()
         */
        Collection<ClassDocc> getClassesAndInterfacesWithConstants();

        /**
         * @return The classes, annotation types, enums, errors, exceptions and interfaces included in this package
         */
        Collection<ClassDocc> getAllClasses();

        /**
         * @return The annotation types included in this package
         */
        Collection<ClassDocc> getAnnotationTypes();

        /**
         * @return The classes (excluding annotation types, enums, errors, exceptions and interfaces) included in this
         *         package
         */
        Collection<ClassDocc> getClasses();

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
                String result = Doccs.HTML.fromTags(this.doc.firstSentenceTags(), this.doc, Doccs.this.rootDoc);
                result = Doccs.ANY_BLOCK_TAG.matcher(result).replaceAll("");
//                result = Doccs.WHITESPACE.matcher(result).replaceAll(" ");
                return result;
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

        @Override @Nullable public String
        getTitle() { return null; }
    }

    private static final Pattern
    ANY_BLOCK_TAG = Pattern.compile((
        "</?(?:address|article|aside|audio|blockquote|canvas|dd|div|dl|dt|fieldset|figcaption|figure|footer"
        + "|form|h1|h2|h3|h4|h5|h6|header|hgroup|hr|main|nav|noscript|ol|output|p|pre|section|table|tfoot|ul|video"
        + ")\\b.*?>"
    ), Pattern.CASE_INSENSITIVE);
}
