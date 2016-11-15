
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
 *    3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote
 *       products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.unkrig.doclet.javadoc.templates;

import java.util.Arrays;
import java.util.regex.Pattern;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

import de.unkrig.commons.doclet.html.Html;
import de.unkrig.commons.lang.AssertionUtil;
import de.unkrig.commons.lang.ExceptionUtil;
import de.unkrig.commons.lang.protocol.Longjump;
import de.unkrig.commons.lang.protocol.Predicate;
import de.unkrig.commons.lang.protocol.PredicateUtil;
import de.unkrig.commons.nullanalysis.Nullable;
import de.unkrig.commons.util.collections.IterableUtil;
import de.unkrig.notemplate.NoTemplate;
import de.unkrig.notemplate.NoTemplate.Once;

/**
 * Javadoc-related utility methods.
 */
public final
class JavadocUtil {

    static { AssertionUtil.enableAssertionsForThisClass(); }

    private JavadocUtil() {}

    private static final Html HTML = new Html(Html.STANDARD_LINK_MAKER);

    public static String
    toHtml(Doc from, Doc to, boolean plain, @Nullable String label, @Nullable String target, RootDoc rootDoc) {

        try {

            return JavadocUtil.HTML.makeLink(from, to, plain, label, target, rootDoc);
        } catch (Longjump l) {
            return "???";
        }
    }

    /**
     * <dl>
     *   <dt>Mode &amp; 1:</dt>
     *   <dd>Print type variable <em>with</em> bounds</dd>
     *   <dt>Mode &amp; 2:</dt>
     *   <dd>Print array type as ellipsis</dd>
     *   <dt>Mode &amp; 4:</dt>
     *   <dd>Do not print type parameters</dd>
     *   <dt>Mode &amp; 8:</dt>
     *   <dd>Print interface names in italics</dd>
     *   <dt>Mode &amp; 16:</dt>
     *   <dd>Optimize links, i.e. remove unnecessary "../x" portions</dd>
     *   <dt>Mode &amp; 32:</dt>
     *   <dd>Enclose class name in "{@code <span class="strong">...</span>}</dd>
     * </dl>
     */
    public static String
    toHtml(Type type, @Nullable Doc ref, String home, int mode) {
        return JavadocUtil.toHtml(type, ref, home, mode, null);
    }

    /**
     * <dl>
     *   <dt>Mode &amp; 1:</dt>
     *   <dd>Print type variable <em>with</em> bounds</dd>
     *   <dt>Mode &amp; 2:</dt>
     *   <dd>Print array type as ellipsis</dd>
     *   <dt>Mode &amp; 4:</dt>
     *   <dd>Do not print type parameters</dd>
     *   <dt>Mode &amp; 8:</dt>
     *   <dd>Print interface names in italics</dd>
     *   <dt>Mode &amp; 16:</dt>
     *   <dd>Optimize links, i.e. remove unnecessary "../x" portions</dd>
     *   <dt>Mode &amp; 32:</dt>
     *   <dd>Enclose class name in "{@code <span class="strong">...</span>}</dd>
     * </dl>
     *
     * @param target The target window for the link, e.g. "{@code _blank}"
     */
    public static String
    toHtml(Type type, @Nullable Doc ref, String home, int mode, @Nullable String target) {

        if ((mode & 2) != 0) {
            String result = type.toString();
            assert result.endsWith("[]");
            return result.substring(0, result.length() - 2) + "...";
        }

        if (type.isPrimitive()) return type.typeName();

        if (type.asTypeVariable() != null) {
            StringBuilder sb = new StringBuilder();

            TypeVariable tv = type.asTypeVariable();
            if (tv.owner() != ref && tv.owner().containingPackage() != ref) {
                sb.append("<a href=\"").append(home).append(JavadocUtil.href(tv.owner()));
                sb.append("\" title=\"type parameter in ").append(tv.owner().name()).append("\"");
                if (target != null) sb.append(" target=\"" + target + "\"");
                sb.append(">");
                sb.append(tv.typeName()).append("</a>");
            } else {
                sb.append(tv.typeName());
            }

            if ((mode & 1) != 0) {
                Once first = NoTemplate.once();
                for (Type b : tv.bounds()) {
                    sb.append(first.once() ? " extends " : " & ");
//                    sb.append(NoTemplate.html(b.toString()));
                    sb.append(JavadocUtil.toHtml(b, ref, home, 0));
                }
            }

            return sb.toString();
        }

        if (type instanceof ClassDoc) {
            ClassDoc cd = (ClassDoc) type;

            String s;
            if (cd.isIncluded() && cd != ref) {
                s = (
                    "<a href=\""
                    + (
                        ref == cd.containingPackage() && (mode & 16) != 0
                        ? cd.name() + ".html"
                        : home + JavadocUtil.href(cd)
                    )
                    + "\" title=\""
                    + JavadocUtil.title(cd)
                    + "\""
                    + (target == null ? "" : " target=\"" + target + "\"")
                    + ">"
                    + ((mode & 32) != 0 ? "<span class=\"strong\">" : "")
                    + (cd.isInterface() && (mode & 8) != 0 ? "<i>" + cd.name() + "</i>" : cd.name())
                    + ((mode & 32) != 0 ? "</span>" : "")
                    + "</a>"
                );
            } else {
                s = cd.qualifiedName();
            }

            TypeVariable[] typeParameters = cd.typeParameters();

            if (typeParameters.length > 0 & (mode & 4) == 0) {
                StringBuilder sb    = new StringBuilder(s).append("&lt;");
                Once          first = NoTemplate.once();
                for (TypeVariable tp : typeParameters) {
                    if (!first.once()) sb.append(',');
                    sb.append(JavadocUtil.toHtml(tp, ref, home, mode));
                }
                s = sb.append("&gt;").toString();
            }

            return s;
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            StringBuilder     sb = new StringBuilder();
            ClassDoc          cd = pt.asClassDoc();
            if (cd.isIncluded()) {
                sb.append(
                    "<a href=\""
                    + home
                    + JavadocUtil.href(cd)
                    + "\" title=\""
                    + JavadocUtil.title(cd)
                    + "\""
                    + (target == null ? "" : " target=\"" + target + "\"")
                    + ">"
                    + (cd.isInterface() && (mode & 8) != 0 ? "<i>" + cd.name() + "</i>" : cd.name())
                    + "</a>"
                );
            } else {
                sb.append(cd.qualifiedName());
            }

//            ClassFrame.toHtml(pt.asClassDoc(), ref, home))
            sb.append("&lt;");
            Once first = NoTemplate.once();
            for (Type ta : pt.typeArguments()) {
                if (!first.once()) sb.append(',');
                sb.append(JavadocUtil.toHtml(ta, ref, home, mode));
            }
            return sb.append("&gt;").toString();
        }

        if (!type.dimension().isEmpty()) {
            return type.toString();
        }

        if (type instanceof WildcardType) {
            WildcardType wt = (WildcardType) type;

            if (wt.extendsBounds().length == 0 && wt.superBounds().length == 0) return "?";

            StringBuilder sb = new StringBuilder("?");
            for (int i = 0; i < wt.extendsBounds().length; i++) {
                sb.append(i == 0 ? " extends " : " & ");
                sb.append(JavadocUtil.toHtml(wt.extendsBounds()[i], ref, home, mode));
            }
            for (int i = 0; i < wt.superBounds().length; i++) {
                sb.append(i == 0 ? " super " : " & ").append(JavadocUtil.toHtml(wt.superBounds()[i], ref, home, mode));
            }

            return sb.toString();
        }

        throw new AssertionError(type);
    }

    /**
     * @return The URL to the given <var>doc</var>
     */
    public static String
    href(Doc doc) {
        if (doc instanceof ClassDoc)            return JavadocUtil.href((ClassDoc)            doc);
        if (doc instanceof FieldDoc)            return JavadocUtil.href((FieldDoc)            doc);
        if (doc instanceof ExecutableMemberDoc) return JavadocUtil.href((ExecutableMemberDoc) doc);
        if (doc instanceof PackageDoc)          return JavadocUtil.href((PackageDoc)          doc);
        throw new AssertionError(doc);
    }

    private static String
    href(ClassDoc cd) {

        StringBuilder sb = new StringBuilder();

        String cpn = cd.containingPackage().name();
        if (!cpn.isEmpty()) sb.append(cpn.replace('.', '/')).append('/');

        return sb.append(cd.name()).append(".html").toString();
    }

    private static String
    href(FieldDoc fd) {
        ClassDoc   containingClass   = fd.containingClass();
        PackageDoc containingPackage = fd.containingPackage();

        StringBuilder sb = new StringBuilder();

        String cpn = containingPackage.name();
        if (!cpn.isEmpty()) sb.append(cpn.replace('.', '/')).append('/');

        return sb.append(containingClass.name()).append(".html#").append(fd.name()).toString();
    }

    private static String
    href(ExecutableMemberDoc emd) {
        ClassDoc   containingClass   = emd.containingClass();
        PackageDoc containingPackage = emd.containingPackage();

        StringBuilder sb = new StringBuilder();

        String cpn = containingPackage.name();
        if (!cpn.isEmpty()) sb.append(cpn.replace('.', '/')).append('/');

        sb.append(containingClass.name()).append(".html#");
        String[] fragments = JavadocUtil.fragments(emd);
        sb.append(fragments[fragments.length - 1]);
//        sb.append(emd.name());
//        sb.append('(');
//        if (emd.parameters().length > 0) {
//            Once first = NoTemplate.once();
//            for (Parameter p : emd.parameters()) {
//                if (!first.once()) sb.append(", ");
//                sb.append(p.type().qualifiedTypeName());
//            }
//        }
//        sb.append(')');

        return sb.toString();
    }

    private static String
    href(PackageDoc pd) {

        StringBuilder sb = new StringBuilder();

        String packageName = pd.name();
        if (!packageName.isEmpty()) sb.append(packageName.replace('.', '/')).append('/');

        return sb.append("package-summary.html").toString();
    }

    @Nullable public static String
    title(Doc doc) { return doc instanceof ClassDoc ? JavadocUtil.title((ClassDoc) doc) : null; }

    public static String
    title(ClassDoc cd) { return JavadocUtil.category(cd) + " in " + cd.containingPackage().name(); }

    public static String[]
    fragments(ExecutableMemberDoc emd) {

        // <a name="enableAssertionsFor-java.lang.Class-">
        // but not <a name="enableAssertionsFor-java.lang.Class<?>-">
        //
        // <a name="enableAssertionsForThisClass--">
        //
        // <a name="notNull-java.lang.Object-">
        // also <a name="notNull-T-">
        //
        // <a name="notNull-java.lang.Object-java.lang.String-">
        // <a name="notNull-T-java.lang.String-">
        // but not <a name="notNull-T-String-">
        //
        // <a name="fail--">
        //
        // <a name="fail-java.lang.String-">
        // but not <a name="fail-String-">
        //
        // <a name="fail-java.lang.Throwable-">
        // but not <a name="fail-Throwable-">
        //
        // <a name="fail-java.lang.String-java.lang.Throwable-">
        // but not <a name="fail-String-Throwable-">

        if (emd.parameters().length == 0) {
            return new String[] { emd.name() + "()" };
        }

        StringBuilder sb1 = new StringBuilder(emd.name());
        StringBuilder sb2 = new StringBuilder(emd.name());
        sb1.append('(');
        sb2.append('(');
        Once first = NoTemplate.once();
        for (Parameter p : emd.parameters()) {
            if (!first.once()) {
                sb1.append(", ");
                sb2.append(", ");
            }

            Type pt = p.type();

            if (pt instanceof ParameterizedType) {

                sb1.append(((ParameterizedType) pt).asClassDoc().qualifiedTypeName());
                sb2.append(((ParameterizedType) pt).asClassDoc().qualifiedTypeName());
            } else
            if (pt instanceof WildcardType) {
                Type firstBound = ((WildcardType) pt).extendsBounds()[0];

                sb1.append(firstBound.toString());
                sb2.append(firstBound.toString());
            } else
            if (pt instanceof TypeVariable) {
                Type[] bounds = ((TypeVariable) pt).bounds();
                sb1.append(bounds.length == 0 ? "java.lang.Object" : bounds[0].asClassDoc().qualifiedName());
                sb2.append(pt.qualifiedTypeName());
            } else
            if (!pt.dimension().isEmpty()) {
                sb1.append(pt.asClassDoc()).append(pt.dimension());
                sb2.append(pt.toString());
            } else
            {
                // "type().qualifiedTypeName()" => "java.lang.Class", "java.lang.String",
                // "java.lang.Throwable"
                sb1.append(pt.toString());// qualifiedTypeName());
                sb2.append(pt.toString());// qualifiedTypeName());
            }
        }
        String result1 = sb1.append(')').toString();
        String result2 = sb2.append(')').toString();
        if (emd.isVarArgs()) {
            assert result1.endsWith("[])");
            result1 = result1.substring(0, result1.length() - 3) + "...)";
            assert result2.endsWith("[])");
            result2 = result2.substring(0, result2.length() - 3) + "...)";
        }

        return (
            result1.equals(result2)
            ? new String[] { result1 }
            : new String[] { result1.replaceAll(", ", ","), result2 }
        );
    }

    /**
     * @return The human-readable string that describes the category of the given <var>cd</var>, e.g. {@code "class"}
     *         or {@code "annotation type"}
     */
    public static String
    category(ClassDoc cd) {

        return (
            cd.isAnnotationType() ? "annotation type" :
            cd.isClass()          ? "class"           :
            cd.isEnum()           ? "enum"            :
            cd.isInterface()      ? "interface"       :
            ExceptionUtil.<String>throwAssertionError(cd.getClass())
        );
    }

    /**
     * @return The fields of the given <var>classDoc</var> which are "constants"
     * @see    #isConstant(FieldDoc)
     */
    public static Iterable<FieldDoc>
    constantsOf(ClassDoc classDoc) {

        return IterableUtil.filter(
            Arrays.asList(classDoc.fields()),
            new Predicate<FieldDoc>() {
                @Override public boolean evaluate(FieldDoc fieldDoc) { return JavadocUtil.isConstant(fieldDoc); }
            }
        );
    }

    public static Iterable<FieldDoc>
    nonConstantFieldsOf(ClassDoc classDoc) {

        return IterableUtil.filter(
            Arrays.asList(classDoc.fields()),
            PredicateUtil.not(new Predicate<FieldDoc>() {
                @Override public boolean evaluate(FieldDoc fieldDoc) { return JavadocUtil.isConstant(fieldDoc); }
            })
        );
    }

    /**
     * @return Whether the given <var>field</var> is a "constant", i.e. is {@code static final} and has an initializer
     */
    private static boolean
    isConstant(FieldDoc field) {
        return field.isStatic() && field.isFinal() && field.constantValueExpression() != null;
    }

    /**
     * @return Those classes, interfaces, enums and annotation types declared within the given <var>packagE</var>
     *         that declare at least one constant
     * @rsee   {@link #isConstant(FieldDoc)}
     */
    public static Iterable<ClassDoc>
    classesAndInterfacesWithConstants(PackageDoc packagE) {

        return IterableUtil.filter(
            Arrays.asList(packagE.allClasses()),
            new Predicate<ClassDoc>() {

                @Override public boolean
                evaluate(ClassDoc classDoc) { return JavadocUtil.constantsOf(classDoc).iterator().hasNext(); }
            }
        );
    }

    /**
     * @return The description of the given <var>doc</var>, with all inline tags expanded
     * @see    Html#fromTags(com.sun.javadoc.Tag[], Doc, RootDoc)
     */
    public static String
    description(Doc doc, RootDoc rootDoc) {

        try {
            return JavadocUtil.HTML.fromTags(doc.inlineTags(), doc, rootDoc);
        } catch (Longjump l) {
            return "???";
        }
    }

    /**
     * @return The first sentence of the description of <var>to</var>, with all HTML tags removed
     */
    public static String
    firstSentenceOfDescription(Doc from, Doc to, RootDoc rootDoc) {

        try {
            String result = JavadocUtil.HTML.fromTags(to.firstSentenceTags(), from, rootDoc);
            result = JavadocUtil.ANY_BLOCK_TAG.matcher(result).replaceAll("");
//            result = Doccs.WHITESPACE.matcher(result).replaceAll(" ");
            return result;
        } catch (Longjump l) {
            return "???";
        }
    }

    private static final Pattern
    ANY_BLOCK_TAG = Pattern.compile((
        "</?(?:address|article|aside|audio|blockquote|canvas|dd|div|dl|dt|fieldset|figcaption|figure|footer"
        + "|form|h1|h2|h3|h4|h5|h6|header|hgroup|hr|main|nav|noscript|ol|output|p|pre|section|table|tfoot|ul|video"
        + ")\\b.*?>"
    ), Pattern.CASE_INSENSITIVE);
}
