
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

package de.unkrig.doclet.javadoc.templates.clasS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;

import de.unkrig.commons.doclet.Docs;
import de.unkrig.commons.doclet.Tags;
import de.unkrig.commons.doclet.html.Html;
import de.unkrig.commons.lang.AssertionUtil;
import de.unkrig.commons.lang.StringUtil;
import de.unkrig.commons.lang.protocol.Longjump;
import de.unkrig.commons.lang.protocol.Producer;
import de.unkrig.commons.lang.protocol.ProducerUtil;
import de.unkrig.commons.nullanalysis.Nullable;
import de.unkrig.commons.util.collections.IterableUtil.ElementWithContext;
import de.unkrig.doclet.javadoc.templates.JavadocUtil;
import de.unkrig.notemplate.NoTemplate;
import de.unkrig.notemplate.javadocish.Options;
import de.unkrig.notemplate.javadocish.templates.AbstractRightFrameHtml;

/**
 * Rendition of the per-class JAVADOC page.
 */
public
class ClassFrameHtml extends AbstractRightFrameHtml implements PerClassDocument {

    static { AssertionUtil.enableAssertionsForThisClass(); }

    enum MemberType { NESTED, FIELD, CONSTR, METHOD }

    private static final Html HTML = new Html(Html.STANDARD_LINK_MAKER);

    /**
     * Renders this page.
     *
     * @param home The relative path to the "home directory", e.g. "{@code ../../}"
     */
    @Override public void
    render(
        final String                       home,
        final ElementWithContext<ClassDoc> clasS,
        final Options                      options,
        final RootDoc                      rootDoc
    ) {

        super.rRightFrameHtml(
            clasS.current().name(),                   // windowTitle
            options,                                  // options
            new String[] { home + "stylesheet.css" }, // stylesheetLinks
            new String[] {                            // nav1
                "Overview",   home + "overview-summary.html",
                "Package",    "package-summary.html",
                "Class",      AbstractRightFrameHtml.HIGHLIT,
                "Tree",       "package-tree.html",
                "Deprecated", home + "deprecated-list.html",
                "Index",      home + "index-all.html",
                "Help",       home + "help-doc.html",
            },
            new String[] {                            // nav2
                ClassFrameHtml.classLink("Prev Class", home, clasS.previous()),
                ClassFrameHtml.classLink("Next Class", home, clasS.next()),
            },
            new String[] {                            // nav3
                "Frames",    home + "index.html?" + ClassFrameHtml.classHref("", clasS.current()),
                "No Frames", clasS.current().name() + ".html",
            },
            new String[] {                            // nav4
                "All Classes", home + "allclasses-noframe.html",
            },
            new String[] {                            // nav5
                "Nested", clasS.current().innerClasses().length == 0 ? AbstractRightFrameHtml.DISABLED : "#nested_class_summary",
                "Field",  clasS.current().fields().length       == 0 ? AbstractRightFrameHtml.DISABLED : "#field_summary",
                "Constr", clasS.current().constructors().length == 0 ? AbstractRightFrameHtml.DISABLED : "#constructor_summary",
                "Method", clasS.current().methods().length      == 0 ? AbstractRightFrameHtml.DISABLED : "#method_summary",
            },
            new String[] {                            // nav6
                "Field",  clasS.current().fields().length       == 0 ? AbstractRightFrameHtml.DISABLED : "#field_detail",
                "Constr", clasS.current().constructors().length == 0 ? AbstractRightFrameHtml.DISABLED : "#constructor_detail",
                "Method", clasS.current().methods().length      == 0 ? AbstractRightFrameHtml.DISABLED : "#method_detail"
            },
            () -> {                                   // renderBody
                ClassFrameHtml.this.rBody(clasS, home, rootDoc);
            }
        );
    }

    private static String
    classLink(String labelHtml, String home, @Nullable ClassDoc clasS) {

        if (clasS == null) return labelHtml;

        return (
            "<a href=\""
            + ClassFrameHtml.classHref(home, clasS)
            + "\" title=\"" + JavadocUtil.category(clasS) + " in "
            + clasS.containingPackage().name()
            + "\"><span class=\"strong\">"
            + labelHtml
            + "</span></a>"
        );
    }

    private static String
    classHref(String home, ClassDoc clasS) {

        return home + clasS.containingPackage().name().replace('.', '/') + "/" + clasS.name() + ".html";
    }

    private void
    rBody(ElementWithContext<ClassDoc> clasS, String home, RootDoc rootDoc) {

        final MethodDoc[] sortedMethods = clasS.current().methods();
        Arrays.sort(sortedMethods);

        this.l(
"    <!-- ======== START OF CLASS DATA ======== -->",
"    <div class=\"header\">",
"      <div class=\"subTitle\">" + clasS.current().containingPackage().name() + "</div>",
"      <h2 title=\"" + ClassFrameHtml.capFirst(JavadocUtil.category(clasS.current())) + " " + clasS.current().name() + "\" class=\"title\">" + ClassFrameHtml.capFirst(JavadocUtil.category(clasS.current())) + " " + clasS.current().name() + NoTemplate.html(ClassFrameHtml.typeParameters(clasS.current())) + "</h2>",
"    </div>",
"    <div class=\"contentContainer\">"
        );

        // Superclass chain.
        ClassDoc[] scs = ClassFrameHtml.superclassChain(clasS.current());
        if (scs.length > 0) {
            for (int sci = scs.length - 1; sci >= 0; sci--) {
                ClassDoc sc = scs[sci];
                this.l(
"      <ul class=\"inheritance\">",
"        <li>" + sc.qualifiedName() + "</li>",
"        <li>"
                );
            }
            this.l(
"          <ul class=\"inheritance\">",
"            <li>" + JavadocUtil.toHtml(clasS.current(), clasS.current(), home, 0) + "</li>",
"          </ul>"
            );
            for (int i = 0; i < scs.length; i++) {
                this.l(
"        </li>",
"      </ul>"
                );
            }
        }
        this.l(
"      <div class=\"description\">",
"        <ul class=\"blockList\">",
"          <li class=\"blockList\">"
        );

        // Class/interface type parameters.
        if (clasS.current().typeParamTags().length > 0) {
            this.p("        <dl><dt><span class=\"strong\">Type Parameters:</span></dt>");
            for (ParamTag tpt : clasS.current().typeParamTags()) {
                String comment = tpt.parameterComment();
                try {
                    comment = ClassFrameHtml.HTML.fromJavadocText(comment, clasS.current(), rootDoc);
                } catch (Longjump e) {
                    ;
                }
                this.p("<dd><code>" + tpt.parameterName() + "</code> - " + comment + "</dd>");
            }
            this.l("</dl>");
        }

        // Implemented interfaces / superinterfaces.
        {
            List<Type> sis = this.getImplementedInterfaces(clasS.current());
            if (sis.size() > 0) {
                this.l(
"            <dl>",
"              <dt>" + (clasS.current().isInterface() && clasS.current().interfaces().length == sis.size() ? "All Superinterfaces" : "All Implemented Interfaces") + ":</dt>"
                );
                this.p(
"              <dd>"
                );
                Once first = NoTemplate.once();
                Collections.sort(sis, Docs.TYPE_COMPARATOR);
                for (Type si : sis) {
                    if (!first.once()) this.p(", ");
                    this.p(JavadocUtil.toHtml(si, clasS.current(), home, 0));
                }
                this.l("</dd>");
                this.l(
"            </dl>"
                );
            }
        }

        // Enclosing clasS.current().
        if (clasS.current().containingClass() != null) {
            this.l(
"            <dl>",
"              <dt>Enclosing class:</dt>",
"              <dd>" + JavadocUtil.toHtml(clasS.current().containingClass(), clasS.current(), home, 0) + "</dd>",
"            </dl>"
            );
        }

        // Known subinterfaces.
        if (ClassFrameHtml.knownSubinterfaces(clasS.current(), rootDoc).size() > 0) {
            this.l(
"            <dl>",
"              <dt>All Known Subinterfaces:</dt>",
"              <dd>"
            );
            for (Iterator<ClassDoc> it = ClassFrameHtml.knownSubinterfaces(clasS.current(), rootDoc).iterator(); it.hasNext();) {
                ClassDoc superinterface = it.next();
                this.l(
"                <a href=\"" + home + JavadocUtil.href(superinterface) + "\" title=\"" + JavadocUtil.title(superinterface) + "\">" + superinterface.name() + "</a>" + NoTemplate.html(ClassFrameHtml.typeParameters(superinterface)) + (it.hasNext() ? "," : "")
                );
            }
            this.l(
"              </dd>",
"            </dl>"
            );
        }
        this.l(
"            <hr />",
"            <br />"
        );

        // Class/interface modifiers.
        this.p(
"            <pre>"
        );
        this.p("public ");
        if (clasS.current().isStatic()) this.p("static ");
        if (clasS.current().isAbstract() && !clasS.current().isInterface()) this.p("abstract ");
        if (clasS.current().isFinal()) this.p("final ");

        this.p(clasS.current().isClass() ? "class " : "interface ");

        // Class/interface name.
        // Class/interface type parameters.
        this.p("<span class=\"strong\">" + clasS.current().name() + NoTemplate.html(ClassFrameHtml.typeParameters(clasS.current())) + "</span>");

        // Class's superclasS.current().
        if (clasS.current().superclass() != null) {
            this.l();
            this.p("extends " + clasS.current().superclass().qualifiedName() + ClassFrameHtml.typeArguments(clasS.current().superclass()));
        }

        // Interface's superinterfaces.
        if (clasS.current().interfaces().length > 0) {
            this.l();
            this.p("extends ");
            Once first = NoTemplate.once();
            for (Type it : clasS.current().interfaceTypes()) {
                if (!first.once()) this.p(", ");
                this.p(JavadocUtil.toHtml(it, clasS.current(), home, 0));
            }
//            p("<a href=\"../../../../../de/unkrig/commons/lang/protocol/Producer.html\" title=\"interface in de.unkrig.commons.lang.protocol\">Producer</a>&lt;PT&gt;");
//            p(", ");
//            p("<a href=\"../../../../../de/unkrig/commons/lang/protocol/Consumer.html\" title=\"interface in de.unkrig.commons.lang.protocol\">Consumer</a>&lt;CT&gt;");
        }

        this.l(
"            </pre>"
        );

        // Class/interface description.
        if (!ClassFrameHtml.description(clasS.current(), rootDoc).isEmpty()) {
            this.l(
"            <div class=\"block\">" + ClassFrameHtml.description(clasS.current(), rootDoc) + "</div>"
            );
        }

        // @see pkg.cls#mem
        boolean implementsSerializable;
        IS: {
            for (Type t : this.getImplementedInterfaces(clasS.current())) {
                if ("java.io.Serializable".equals(t.qualifiedTypeName())) {
                    implementsSerializable = true;
                    break IS;
                }
            }
            implementsSerializable = false;
        }
        if (clasS.current().seeTags().length > 0 || implementsSerializable) {
            this.p(
"            <dl><dt><span class=\"strong\">See Also:</span></dt><dd>");
            Once once = NoTemplate.once();
            for (SeeTag st : clasS.current().seeTags()) {
                if (!once.once()) {
                    this.l(", ");
                }
                Doc reference = ClassFrameHtml.reference(st);
                this.p("<a href=\"" + home + JavadocUtil.href(reference) + "\"");
                if (JavadocUtil.title(reference) != null) {
                    this.p(" title=\"" + JavadocUtil.title(reference) + "\"");
                }
                this.p("><code>" + ClassFrameHtml.toString(reference, clasS.current()) + "</code></a>" + st.label());
            }
            if (implementsSerializable) {
                this.p("<a href=\"../../../../../serialized-form.html#de.unkrig.commons.lang.protocol.Longjump\">Serialized Form</a>");
            }
            this.l("</dd></dl>");
        }
        this.l(
"          </li>",
"        </ul>",
"      </div>",
"      <div class=\"summary\">",
"        <ul class=\"blockList\">",
"          <li class=\"blockList\">"
        );

        // Class's/interface's fields.
        if (clasS.current().fields().length > 0) {
            this.l(
"            <!-- =========== FIELD SUMMARY =========== -->",
"            <ul class=\"blockList\">",
"              <li class=\"blockList\"><a name=\"field_summary\">",
"                <!--   -->",
"                </a>",
"                <h3>Field Summary</h3>",
"                <table class=\"overviewSummary\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\" summary=\"Field Summary table, listing fields, and an explanation\">",
"                  <caption><span>Fields</span><span class=\"tabEnd\">&nbsp;</span></caption>",
"                  <tr>",
"                    <th class=\"colFirst\" scope=\"col\">Modifier and Type</th>",
"                    <th class=\"colLast\" scope=\"col\">Field and Description</th>",
"                  </tr>"
            );
            Producer<? extends String> cls = ProducerUtil.alternate("altColor", "rowColor");
            for (FieldDoc fd : clasS.current().fields()) {
                this.l(
"                  <tr class=\"" + cls.produce() + "\">",
"                    <td class=\"colFirst\"><code>" + (fd.isStatic() ? "static " : "") + JavadocUtil.toHtml(fd.type(), fd, home, 0) + "</code></td>",
"                    <td class=\"colLast\"><code><strong><a href=\"" + home + JavadocUtil.href(fd) + "\">" + fd.name() + "</a></strong></code>",
"                      <div class=\"block\">" + JavadocUtil.firstSentenceOfDescription(clasS.current(), fd, rootDoc) + "</div>",
"                    </td>",
"                  </tr>"
                );
            }
            this.l(
"                </table>",
"              </li>",
"            </ul>"
            );
        }
        if (clasS.current().innerClasses().length > 0) {
            this.l(
"            <!-- ======== NESTED CLASS SUMMARY ======== -->",
"            <ul class=\"blockList\">",
"              <li class=\"blockList\"><a name=\"nested_class_summary\">",
"                <!--   -->",
"                </a>",
"                <h3>Nested Class Summary</h3>",
"                <table class=\"overviewSummary\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\" summary=\"Nested Class Summary table, listing nested classes, and an explanation\">",
"                  <caption><span>Nested Classes</span><span class=\"tabEnd\">&nbsp;</span></caption>",
"                  <tr>",
"                    <th class=\"colFirst\" scope=\"col\">Modifier and Type</th>",
"                    <th class=\"colLast\" scope=\"col\">Class and Description</th>",
"                  </tr>"
            );
            Producer<? extends String> cls = ProducerUtil.alternate("altColor", "rowColor");
            for (ClassDoc ncd : clasS.current().innerClasses()) {
                this.l(
"                  <tr class=\"" + cls.produce() + "\">",
"                    <td class=\"colFirst\"><code>static " + JavadocUtil.category(ncd) + "&nbsp;</code></td>"
                );
                this.l(
"                    <td class=\"colLast\"><code><strong>" + JavadocUtil.toHtml(ncd, clasS.current(), home, 0) + "</strong></code>",
"                      <div class=\"block\">" + JavadocUtil.firstSentenceOfDescription(clasS.current(), ncd, rootDoc) + "</div>",
"                    </td>",
"                  </tr>"
                );
            }
            this.l(
"                </table>",
"              </li>",
"            </ul>"
            );
        }

        // Constructor summary section.
        if (clasS.current().constructors().length > 0) {
            this.l(
"            <!-- ======== CONSTRUCTOR SUMMARY ======== -->",
"            <ul class=\"blockList\">",
"              <li class=\"blockList\"><a name=\"constructor_summary\">",
"                <!--   -->",
"                </a>",
"                <h3>Constructor Summary</h3>",
"                <table class=\"overviewSummary\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\" summary=\"Constructor Summary table, listing constructors, and an explanation\">",
"                  <caption><span>Constructors</span><span class=\"tabEnd\">&nbsp;</span></caption>",
"                  <tr>",
"                    <th class=\"colOne\" scope=\"col\">Constructor and Description</th>",
"                  </tr>"
            );
            Producer<? extends String>  cls = ProducerUtil.alternate("altColor", "rowColor");
            for (ConstructorDoc cd : clasS.current().constructors()) {
                this.l(
"                  <tr class=\"" + cls.produce() + "\">"
                );
                this.p(
"                    <td class=\"colOne\"><code>" + "<strong><a href=\"" + home + JavadocUtil.href(cd) + "\">" + cd.name() + "</a></strong>");
                this.pParameters(home, cd);
                this.l("</code>&nbsp;</td>");
                this.l(
"                  </tr>"
                );
            }
            this.l(
"                </table>",
"              </li>",
"            </ul>"
            );
        }

        // Method summary section.
        this.l(
"            <!-- ========== METHOD SUMMARY =========== -->",
"            <ul class=\"blockList\">",
"              <li class=\"blockList\"><a name=\"method_summary\">",
"                <!--   -->",
"                </a>",
"                <h3>Method Summary</h3>"
        );
        if (clasS.current().methods().length > 0) {

            // Table headers.
            this.l(
"                <table class=\"overviewSummary\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\" summary=\"Method Summary table, listing methods, and an explanation\">",
"                  <caption><span>Methods</span><span class=\"tabEnd\">&nbsp;</span></caption>",
"                  <tr>",
"                    <th class=\"colFirst\" scope=\"col\">Modifier and Type</th>",
"                    <th class=\"colLast\" scope=\"col\">Method and Description</th>",
"                  </tr>"
            );
            Producer<? extends String>  cls = ProducerUtil.alternate("altColor", "rowColor");
            for (MethodDoc md : sortedMethods) {
                this.l(
"                  <tr class=\"" + cls.produce() + "\">"
                );
                this.p(
"                    <td class=\"colFirst\"><code>"
                );

                // Method modifiers.
                if (md.isStatic())                                     this.p("static ");
                if (md.isAbstract() && !clasS.current().isInterface()) this.p("abstract ");

                // Method type parameters.
                if (md.typeParameters().length > 0) {
                    this.p("&lt;");
                    Once once = NoTemplate.once();
                    for (TypeVariable tp : md.typeParameters()) {
                        if (!once.once()) this.p(",");
//                        this.p(NoTemplate.html(tp.toString()));
                        this.p(JavadocUtil.toHtml(tp, md, home, 1));
                    }
                    this.p("&gt;&nbsp;");

                    int l = 0;
                    if (md.isStatic()) l += 7;
                    if (md.isAbstract()) l += 9;
                    if (md.typeParameters().length > 0) {
                        l++;
                        for (TypeVariable tp : md.typeParameters()) {
                            l += tp.toString().length() + 1;
                        }
                    }
                    if (l >= 18) {
                        this.p("<br>");
                    }
                }

                // Method return type.
                this.l(JavadocUtil.toHtml(md.returnType(), md, home, 0) + "</code></td>");

                // Method name.
                this.p(
"                    <td class=\"colLast\"><code><strong><a href=\"" + home + JavadocUtil.href(md) + "\">" + md.name() + "</a></strong>"
                );

                // Method parameters.
                this.pParameters(home, md);

                // First sentence of method description.
                if (JavadocUtil.firstSentenceOfDescription(clasS.current(), md, rootDoc).isEmpty()) {
                    this.p("</code>&nbsp;");
                } else {
                    this.l("</code>");
                    this.l(
"                      <div class=\"block\">" + JavadocUtil.firstSentenceOfDescription(clasS.current(), md, rootDoc) + "</div>"
                    );
                }
                this.l(
"                    </td>",
"                  </tr>"
                );
            }
            this.l(
"                </table>"
            );
        }

        // Methods inherited from base classes and interfaces.
        Set<String> seen = new HashSet<String>();
        for (MethodDoc m : clasS.current().methods()) {
            if (m.isStatic()) continue;
            seen.add(JavadocUtil.fragments(m)[0]);
        }
        List<Type> bcais = this.baseClassesAndInterfaces(clasS.current());
//        Collections.sort(bcais, Docs.COMPARE_TYPES);
        for (Type bt : bcais) {
            List<MethodDoc> btms = new ArrayList<MethodDoc>();
            for (MethodDoc btm : bt.asClassDoc().methods()) {
                if (btm.isStatic()) continue;
                String s = JavadocUtil.fragments(btm)[0];
                if (seen.add(s)) btms.add(btm);
            }

            if (btms.isEmpty()) continue;

            this.l(
"                <ul class=\"blockList\">",
"                  <li class=\"blockList\"><a name=\"methods_inherited_from_class_" + bt.qualifiedTypeName() + "\">",
"                    <!--   -->",
"                    </a>"
            );
            this.p(
"                    <h3>Methods inherited from " + JavadocUtil.category(bt.asClassDoc()) + "&nbsp;" + bt.asClassDoc().containingPackage().name() + '.'
            );
            if (bt.asClassDoc().isIncluded()) {
                this.p("<a href=\"" + home + JavadocUtil.href(bt.asClassDoc()) + "\" title=\"" + JavadocUtil.title(bt.asClassDoc()) + "\">" + bt.typeName() + "</a>");
            } else {
                this.p(bt.typeName());
            }
            this.l("</h3>");

            this.p(
"                    <code>"
            );
            Once        once = NoTemplate.once();
            Collections.sort(btms);
            for (MethodDoc btm : btms) {

                if (!once.once()) this.p(", ");

                if (btm.isIncluded()) {
                    this.p("<a href=\"" + home + JavadocUtil.href(btm) + "\">" + btm.name() + "</a>");
                } else {
                    this.p(btm.name());
                }
            }
            this.l("</code></li>");
            this.l(
"                </ul>"
            );
        }
        this.l(
"              </li>",
"            </ul>",
"          </li>",
"        </ul>",
"      </div>"
        );

        // "Details" sections.
        if (clasS.current().fields().length > 0 || clasS.current().methods().length > 0) {
            this.l(
"      <div class=\"details\">",
"        <ul class=\"blockList\">",
"          <li class=\"blockList\">"
            );

            // Class's/interface's fields.
            if (clasS.current().fields().length > 0) {
                this.l(
"            <!-- ============ FIELD DETAIL =========== -->",
"            <ul class=\"blockList\">",
"              <li class=\"blockList\"><a name=\"field_detail\">",
"                <!--   -->",
"                </a>",
"                <h3>Field Detail</h3>"
                );
                for (int fi = 0; fi < clasS.current().fields().length; fi++) {
                    FieldDoc fd = clasS.current().fields()[fi];
                    this.l(
"                <a name=\"" + fd.name() + "\">",
"                  <!--   -->",
"                </a>"
                    );
                    String cls = fi == clasS.current().fields().length - 1 ? "blockListLast" : "blockList";
                    this.l(
"                <ul class=\"" + cls + "\">",
"                  <li class=\"blockList\">",
"                    <h4>" + fd.name() + "</h4>",
"                    <pre>" + fd.modifiers() + "&nbsp;" + JavadocUtil.toHtml(fd.type(), fd, home, 0) + " " + fd.name() + "</pre>",
"                    <div class=\"block\">" + ClassFrameHtml.description(fd, rootDoc) + "</div>"
                    );
                    if (fd.seeTags().length > 0 || fd.constantValue() != null) {
                        this.p(
"                    <dl>"
                        );
                        this.p("<dt><span class=\"strong\">See Also:</span></dt>");
                        this.p("<dd>");
                        Once first = NoTemplate.once();
                        for (SeeTag st : fd.seeTags()) {
                            if (!first.once()) this.l(", ");
                            Doc reference = ClassFrameHtml.reference(st);
                            this.p("<a href=\"" + home + JavadocUtil.href(reference) + "\"");
                            if (JavadocUtil.title(reference) != null) {
                                this.p(" title=\"" + JavadocUtil.title(reference) + "\"");
                            }
                            this.p((
                                "><code>"
                                + ClassFrameHtml.toString(ClassFrameHtml.reference(st), clasS.current())
                                + "</code></a>"
                                + st.label()
                            ));
                        }
                        if (fd.constantValue() != null) {
                            if (!first.once()) this.l(", ");
                            this.p((
                                "<a href=\""
                                + home
                                + "constant-values.html#"
                                + clasS.current().qualifiedName()
                                + "."
                                + fd.name()
                                + "\">Constant Field Values</a></dd>"
                            ));
                        }
                        this.l("                </dl>");
                    }
                    this.l(
"                  </li>",
"                </ul>"
                    );
                }
                this.l(
"              </li>",
"            </ul>"
                );
            }

            // Contructors' details.
            if (clasS.current().constructors().length > 0) {
                this.l(
"            <!-- ========= CONSTRUCTOR DETAIL ======== -->",
"            <ul class=\"blockList\">",
"              <li class=\"blockList\"><a name=\"constructor_detail\">",
"                <!--   -->",
"                </a>",
"                <h3>Constructor Detail</h3>"
                );
                for (int i = 0; i < clasS.current().constructors().length; i++) {
                    this.pExecutableMemberDetail(clasS.current().constructors()[i], home, i == clasS.current().constructors().length - 1, rootDoc);
                }
                this.l(
"              </li>",
"            </ul>"
                );
            }

            // Methods' details.
            this.l(
"            <!-- ============ METHOD DETAIL ========== -->",
"            <ul class=\"blockList\">",
"              <li class=\"blockList\"><a name=\"method_detail\">",
"                <!--   -->",
"                </a>",
"                <h3>Method Detail</h3>"
            );
            for (int mi = 0; mi < clasS.current().methods().length; mi++) {
                this.pExecutableMemberDetail(clasS.current().methods()[mi], home, mi == clasS.current().methods().length - 1, rootDoc);
            }
            this.l(
"              </li>",
"            </ul>"
            );

            // End of "details" sections.
            this.l(
"          </li>",
"        </ul>",
"      </div>"
            );
        }
        this.l(
"    </div>",
"    <!-- ========= END OF CLASS DATA ========= -->"
        );
    }

    private void
    pExecutableMemberDetail(ExecutableMemberDoc emd, String home, boolean isLast, RootDoc rootDoc) {

        final ClassDoc clasS = emd.containingClass();

        // Executable member anchors.
        for (String f : JavadocUtil.fragments(emd)) {
            this.l(
"            <a name=\"" + f + "\">",
"              <!--   -->"
            );
            this.p(
"            </a>"
            );
        }
        this.l();

        // Beginning of executable member detail.
        this.l(
"            <ul class=\"" + (isLast ? "blockListLast" : "blockList") + "\">",
"              <li class=\"blockList\">",
"                <h4>" + emd.name() + "</h4>"
        );

        // Compute indent.
        String indent = emd.modifiers();
        if (emd.typeParameters().length > 0) {
            indent += "<";
            Once once = NoTemplate.once();
            for (TypeVariable tp : emd.typeParameters()) {
                if (!once.once()) indent += ",";
                indent += tp.toString();
            }
            indent += ">";
        }
        if (emd.isMethod()) {
            indent += ((MethodDoc) emd).returnType().toString() + " " + emd.name();
        }

        this.p(
"                <pre>"
        );

        // Executable member annotations.
        for (AnnotationDesc a : emd.annotations()) {
            for (AnnotationDesc aa : a.annotationType().annotations()) {
                if ("java.lang.annotation.Documented".equals(aa.annotationType().qualifiedName())) {
                    this.l("@" + a.annotationType().simpleTypeName());
                    break;
                }
            }
        }

        // Executable member modifiers.
        if (!clasS.isInterface()) {
            this.p(emd.modifiers() + "&nbsp;");
        }

        // Executable member type parameters.
        if (emd.typeParameters().length > 0) {
            this.p("&lt;");
            Once once = NoTemplate.once();
            for (TypeVariable tp : emd.typeParameters()) {
                if (!once.once()) this.p(",");
//                this.p(tp.toString());
                this.p(JavadocUtil.toHtml(tp, emd, home, 1));
            }
            this.p("&gt;&nbsp;");
        }

        // Method return type.
        if (emd.isMethod()) {
            this.p(JavadocUtil.toHtml(((MethodDoc) emd).returnType(), emd, home, 0));
            this.p("&nbsp;");
        }

        // Executable member name.
        this.p(emd.name() + "(");

        // Executable member parameters.
        Once once = NoTemplate.once();
        for (int i = 0; i < emd.parameters().length; i++) {
            Parameter     p          = emd.parameters()[i];
            final boolean isEllipsis = emd.isVarArgs() && i == emd.parameters().length - 1;

            if (!once.once()) {
                this.l(
","
                );
                this.p(StringUtil.repeat(indent.length() + 2, ' '));
            }

            // Executable member parameter annotations.
            for (AnnotationDesc a : p.annotations()) {
                this.l(
"@" + a.annotationType().simpleTypeName()
                );
                this.p(StringUtil.repeat(indent.length() + 2, ' '));
            }

            // Executable member parameter type.
            this.p(JavadocUtil.toHtml(p.type(), emd, home, isEllipsis ? 2 : 0));

            // Executable member parameter name.
            this.p("&nbsp;" + p.name());
        }
        this.p(")");

        // Executable member's thrown exceptions.
        Type[] tets = emd.thrownExceptionTypes();
        if (tets.length > 0) {
            Once first = NoTemplate.once();
            for (int teti = 0; teti < tets.length; teti++) {
                Type tet = tets[teti];
                if (first.once()) {
                    this.l();
                    this.p(StringUtil.repeat(indent.length() - 5, ' ') + "throws ");
                } else {
                    this.l(
","
                    );
                    this.p(StringUtil.repeat(indent.length() + 2, ' '));
                }
                this.p(JavadocUtil.toHtml(tet, emd, home, 1));
            }
        }
        this.l(
"</pre>"
        );

        // Executable member description.
        if (!ClassFrameHtml.description(emd, rootDoc).isEmpty()) {
            this.l(
"                <div class=\"block\">" + ClassFrameHtml.description(emd, rootDoc) + "</div>"
            );
        }

        // Executable member's block tags.
        if (
            emd.typeParamTags().length > 0
            || emd.paramTags().length > 0
            || (emd.isMethod() && ClassFrameHtml.returnValueDescription((MethodDoc) emd, rootDoc) != null)
            || emd.throwsTags().length > 0
            || emd.thrownExceptionTypes().length > 0
            || emd.seeTags().length > 0
        ) {

            this.p(
"                <dl>"
            );

            // @param <T>
            if (emd.typeParamTags().length > 0) {
                this.p("<dt><span class=\"strong\">Type Parameters:</span></dt>");
                for (ParamTag tpt : emd.typeParamTags()) {
                    String comment = tpt.parameterComment();
                    try {
                        comment = ClassFrameHtml.HTML.fromJavadocText(comment, clasS, rootDoc);
                    } catch (Longjump e) {
                        ;
                    }
                    this.p("<dd><code>" + tpt.parameterName() + "</code> - " + comment + "</dd>");
                }
            }

            // @param x
            if (emd.paramTags().length > 0) {
                this.p("<dt><span class=\"strong\">Parameters:</span></dt>");
                for (ParamTag pt : emd.paramTags()) {
                    String comment = pt.parameterComment();
                    try {
                        comment = ClassFrameHtml.HTML.fromJavadocText(comment, clasS, rootDoc);
                    } catch (Longjump e) {
                        ;
                    }
                    this.p("<dd><code>" + pt.parameterName() + "</code> - " + comment + "</dd>");
                }
                if (emd.isMethod() && ClassFrameHtml.returnValueDescription((MethodDoc) emd, rootDoc) != null) this.l();
            }

            // @return
            if (emd.isMethod() && ClassFrameHtml.returnValueDescription((MethodDoc) emd, rootDoc) != null) {
                this.p((
                    "<dt><span class=\"strong\">Returns:</span></dt><dd>"
                    + ClassFrameHtml.returnValueDescription((MethodDoc) emd, rootDoc)
                    + "</dd>"
                ));
            }

            // @throws
            if (emd.throwsTags().length + emd.thrownExceptionTypes().length > 0) {

                if (emd.isMethod() && ClassFrameHtml.returnValueDescription((MethodDoc) emd, rootDoc) != null) this.l();

                this.p("<dt><span class=\"strong\">Throws:</span></dt>");
                for (ThrowsTag tt : emd.throwsTags()) {
                    this.l();
                    this.p("<dd><code>" + JavadocUtil.toHtml(tt.exceptionType(), emd, home, 1) + "</code>");
                    String ec = tt.exceptionComment();
                    if (ec != null && ec.length() > 0) {
                        try {
                            ec = ClassFrameHtml.HTML.fromJavadocText(ec, emd, rootDoc);
                        } catch (Longjump e) {
                            ;
                        }
                        this.p(" - " + ec);
                    }
                    this.p("</dd>");
                }
                TET: for (Type tet : emd.thrownExceptionTypes()) {
                    for (ThrowsTag tt : emd.throwsTags()) {
                        if (tt.exceptionType() == tet) continue TET;
                    }
                    this.l();
                    this.p("<dd><code>" + JavadocUtil.toHtml(tet, emd, home, 1) + "</code></dd>");
                }
            }

            // @see
            this.pSeeTags(emd, home, clasS);

            this.l("</dl>");
        }

        // Link to overridden method.
        if (emd.isMethod() && !emd.isStatic()) {
            MethodDoc overriddenMethod;
            OM: {
                List<Type> bcais = this.baseClassesAndInterfaces(clasS);
                bcais.add(rootDoc.classNamed("java.lang.Object"));
                for (Type bt : bcais) {
                    for (MethodDoc btm : bt.asClassDoc().methods()) {
                        if (btm.isStatic()) continue;
                        if (JavadocUtil.fragments(btm)[0].equals(JavadocUtil.fragments(emd)[0])) {
                            overriddenMethod = btm;
                            break OM;
                        }
                    }
                }
                overriddenMethod = null;
            }

            if (overriddenMethod != null) {
                ClassDoc omcd = overriddenMethod.containingClass();
                this.l(
"                <dl>",
"                  <dt><strong>Overrides:</strong></dt>",
"                  <dd><code>" + overriddenMethod.name() + "</code>&nbsp;in " + JavadocUtil.category(omcd) + "&nbsp;<code>" + JavadocUtil.toHtml(omcd, clasS, home, 0) + "</code></dd>",
"                </dl>"
                );
            }
        }

        // End of executable member detail.
        this.l(
"              </li>",
"            </ul>"
        );
    }

    private void
    pSeeTags(Doc doc, String home, ClassDoc clasS) {

        if (doc.seeTags().length > 0) {
            this.p("<dt><span class=\"strong\">See Also:</span></dt>");
            this.p("<dd>");
            Once first = NoTemplate.once();
            for (SeeTag st : doc.seeTags()) {
                if (!first.once()) {
                    this.l(
", "
                    );
                }
                Doc reference = ClassFrameHtml.reference(st);
                if (reference.isIncluded()) {
                    this.p("<a href=\"" + home + JavadocUtil.href(reference) + "\"");
                    if (JavadocUtil.title(reference) != null) {
                        this.p(" title=\"" + JavadocUtil.title(reference) + "\"");
                    }
                    this.p("><code>" + ClassFrameHtml.toString(reference, clasS) + "</code></a>" + st.label());
                } else {
                    this.p("<code>" + ClassFrameHtml.toString(reference, clasS) + "</code>" + st.label());
                }
            }
            this.p("</dd>");
        }
    }

    private void
    pParameters(String home, ExecutableMemberDoc emd) {

        this.p("(");
        Once once = NoTemplate.once();
        for (int i = 0; i < emd.parameters().length; i++) {
            Parameter p          = emd.parameters()[i];
            boolean   isEllipsis = emd.isVarArgs() && i == emd.parameters().length - 1;

            if (!once.once()) {
                this.l(
","
                );
                this.p(StringUtil.repeat(emd.name().length(), ' '));
            }

            // Parameter type.
            this.p(JavadocUtil.toHtml(p.type(), emd, home, isEllipsis ? 2 : 0));

            // Parameter name.
            this.p("&nbsp;" + p.name());
        }
        this.p(")");
    }

    private List<Type>
    getImplementedInterfaces(@Nullable ClassDoc cd) {
//        if (!cd.isClass()) return Collections.emptyList();

        List<Type> result = new ArrayList<Type>();
        for (; cd != null; cd = cd.superclass()) {
            for (Type it : cd.interfaceTypes()) {
                result.add(it);
                for (Type si : this.getAllSuperInterfaces(it.asClassDoc())) {
                    if (!result.contains(si)) result.add(si);
                }
            }
        }
        return result;
    }

    private List<Type>
    getAllSuperInterfaces(ClassDoc cd) {

        if (!cd.isInterface()) return Collections.emptyList();

        List<Type> result = new ArrayList<Type>();
        for (Type it : cd.interfaceTypes()) {
            result.add(it);
            for (Type si : this.getAllSuperInterfaces(it.asClassDoc())) {
                if (!result.contains(si)) result.add(si);
            }
        }
        return result;
    }

    private List<Type>
    baseClassesAndInterfaces(ClassDoc clasS) {

        if (clasS.isInterface()) return this.getAllSuperInterfaces(clasS);

        List<Type> result = this.getImplementedInterfaces(clasS);
        for (ClassDoc sc : ClassFrameHtml.superclassChain(clasS)) {
            result.add(sc);
            for (Type i : this.getImplementedInterfaces(sc)) {
                if (!result.contains(i)) result.add(i);
            }
        }

        return result;
    }

    private static Doc
    reference(SeeTag st) {

        {
            MemberDoc rm = st.referencedMember();
            if (rm != null) return rm;
        }
        {
            ClassDoc rc = st.referencedClass();
            if (rc != null) return rc;
        }
        {
            PackageDoc rp = st.referencedPackage();
            if (rp != null) return rp;
        }

        throw new AssertionError(st);
    }

    private static String
    description(Doc doc, RootDoc rootDoc) {

        try {
            return ClassFrameHtml.HTML.fromTags(
                doc.inlineTags(),                                                             // tags
                doc instanceof MethodDoc ? ((ProgramElementDoc) doc).containingClass() : doc, // ref
                rootDoc
            );
        } catch (Longjump l) {
            return "???";
        }
    }

    private static String
    typeArguments(Type type) {

        Type[] typeArguments;
        if (type instanceof ClassDoc) {
            typeArguments = new Type[0];
        } else
        if (type instanceof ParameterizedType) {
            typeArguments = ((ParameterizedType) type).typeArguments();
        } else
        {
            throw new AssertionError(type.getClass());
        }

        if (typeArguments.length == 0) return "";

        StringBuilder sb = new StringBuilder("<").append(typeArguments[0].toString());
        for (int i = 1; i < typeArguments.length; i++) {
            sb.append(", ").append(typeArguments[i].toString());
        }
        return sb.append('>').toString();
    }

    private static List<ClassDoc>
    knownSubinterfaces(ClassDoc clasS, RootDoc rootDoc) {

        List<ClassDoc> result = new ArrayList<ClassDoc>();
        for (ClassDoc cd : rootDoc.classes()) {
            if (
                cd.isInterface()
                && cd.subclassOf(clasS)
                && cd != clasS
            ) {
                result.add(cd);
            }
        }

        Collections.sort(result);

        return result;
    }

    private static ClassDoc[]
    superclassChain(ClassDoc cd) {

        List<ClassDoc> result = new ArrayList<ClassDoc>();
        for (ClassDoc scd = cd.superclass(); scd != null; scd = scd.superclass()) {
            result.add(scd);
        }
        return result.toArray(new ClassDoc[result.size()]);
    }

    private static String
    typeParameters(ClassDoc cd) {

        TypeVariable[] typeParameters = cd.typeParameters();

        if (typeParameters.length == 0) return "";

        StringBuilder sb = new StringBuilder("<").append(typeParameters[0].toString());
        for (int i = 1; i < typeParameters.length; i++) {
            sb.append(",").append(typeParameters[i].toString());
        }
        return sb.append('>').toString();
    }

    private static String
    capFirst(String s) {
        if (s.length() == 0) return "";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String
    toString(Doc doc, Doc ref) {
        if (doc instanceof ClassDoc)   return doc.name() + ((ClassDoc) doc).dimension();
        if (doc instanceof FieldDoc)   return ClassFrameHtml.toString((FieldDoc) doc, ref);
        if (doc instanceof MethodDoc)  return ClassFrameHtml.toString((MethodDoc) doc, ref);
        if (doc instanceof PackageDoc) return doc.name();
        throw new AssertionError(doc.getClass());
    }

    private static String
    toString(FieldDoc fieldDoc, Doc ref) {

        ClassDoc cc = fieldDoc.containingClass();
        if (
            cc == ref
            || (ref instanceof ProgramElementDoc && ((ProgramElementDoc) ref).containingClass() == cc)
        ) return fieldDoc.name();

        return fieldDoc.containingClass().simpleTypeName() + '.' + fieldDoc.name();
    }

    private static String
    toString(MethodDoc methodDoc, Doc ref) {

        StringBuilder sb = new StringBuilder();

        ClassDoc cc = methodDoc.containingClass();
        if (cc == ref) {
            ;
        } else
        if (ref instanceof ProgramElementDoc && ((ProgramElementDoc) ref).containingClass() == cc) {
            ;
        } else
        {
            sb.append(methodDoc.containingClass().simpleTypeName()).append('.');
        }

        sb.append(methodDoc.name()).append('(');
        for (int i = 0; i < methodDoc.parameters().length; i++) {
            if (i > 0) sb.append(", ");
            Type     pt = methodDoc.parameters()[i].type();
            ClassDoc cd = pt.asClassDoc();
            if (cd == null) {
                sb.append(pt.toString());
            } else
            if (
                "java.lang".equals(cd.containingPackage().name())
                || "java.io".equals(cd.containingPackage().name())
                || cd.containingPackage() == methodDoc.containingPackage()
            ) {
                sb.append(cd.name());
            } else
            {
                sb.append(cd.qualifiedName());
            }
        }
        sb.append(')');

        return sb.toString();
    }

    @Nullable private static String
    returnValueDescription(MethodDoc methodDoc, RootDoc rootDoc) {
        try {
            String rtd = Tags.optionalTag(methodDoc, "@return", rootDoc);
            if (rtd == null) return null;
            String xxx = ClassFrameHtml.HTML.fromJavadocText(rtd, methodDoc, rootDoc);
            if (xxx.indexOf("for a given") != -1) { // TODO
                System.currentTimeMillis();
            }
            return xxx;
        } catch (Longjump l) {
            return "???";
        }
    }
}
