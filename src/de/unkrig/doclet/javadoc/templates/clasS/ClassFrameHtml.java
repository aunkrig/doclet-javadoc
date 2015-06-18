
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
import java.util.EnumSet;
import java.util.HashSet;
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
import de.unkrig.doclet.javadoc.JavadocDoclet.Options;
import de.unkrig.doclet.javadoc.templates.JavadocUtil;
import de.unkrig.doclet.javadoc.templates.include.BottomHtml;
import de.unkrig.doclet.javadoc.templates.include.BottomNavBarHtml;
import de.unkrig.doclet.javadoc.templates.include.TopHtml;
import de.unkrig.doclet.javadoc.templates.include.TopNavBarHtml;
import de.unkrig.notemplate.NoTemplate;

/**
 * Rendition of the per-class JAVADOC page.
 */
public
class ClassFrameHtml extends AbstractPerClassDocument {

    static { AssertionUtil.enableAssertionsForThisClass(); }

    private static final Html HTML = new Html(Html.STANDARD_LINK_MAKER);

    /**
     * Renders this page.
     * @param home           The relative path to the "home directory", e.g. "{@code ../../}"
     * @param rootDoc
     * @param previousClass  Used for the "Prev" links in the top and bottom navigation bars
     * @param clasS          Subject class
     * @param nextClass      Used for the "Next" links in the top and bottom navigation bars
     */
    @Override public void
    render(
        String             home,
        @Nullable ClassDoc previousClass,
        ClassDoc           clasS,
        @Nullable ClassDoc nextClass,
        Options            options,
        RootDoc            rootDoc
    ) {

        this.include(TopHtml.class).render(clasS.name(), options, home + "stylesheet.css");

        final MethodDoc[] sortedMethods = clasS.methods();
        Arrays.sort(sortedMethods);

        this.l(
"<script type=\"text/javascript\"><!--",
"    if (location.href.indexOf('is-external=true') == -1) {",
"        parent.document.title=\"" + clasS.name() + (options.windowTitle == null ? "" : " (" + options.windowTitle + ")") + "\";",
"    }",
"//-->",
"</script>",
"<noscript>",
"<div>JavaScript is disabled on your browser.</div>",
"</noscript>"
        );

        {
            String prev = "Prev Class";
            if (previousClass != null) {
                try {
                    prev = ClassFrameHtml.HTML.makeLink(
                        clasS,                                        // from
                        previousClass,                                // to
                        true,                                         // plain
                        "<span class=\"strong\">" + prev + "</span>", // label
                        null,                                         // target
                        rootDoc                                       // rootDoc
                    );
                } catch (Longjump l) {}
            }
            String next = "Next Class";
            if (nextClass != null) {
                try {
                    next = ClassFrameHtml.HTML.makeLink(
                        clasS,                                        // from
                        nextClass,                                    // to
                        true,                                         // plain
                        "<span class=\"strong\">" + next + "</span>", // label
                        null,                                         // target
                        rootDoc                                       // rootDoc
                    );
                } catch (Longjump l) {}
            }

            EnumSet<TopNavBarHtml.MemberType> memberTypes = EnumSet.noneOf(TopNavBarHtml.MemberType.class);
            if (clasS.innerClasses().length > 0) memberTypes.add(TopNavBarHtml.MemberType.NESTED);
            if (clasS.fields().length       > 0) memberTypes.add(TopNavBarHtml.MemberType.FIELD);
            if (clasS.constructors().length > 0) memberTypes.add(TopNavBarHtml.MemberType.CONSTR);
            if (clasS.methods().length      > 0) memberTypes.add(TopNavBarHtml.MemberType.METHOD);
            this.include(TopNavBarHtml.class).renderForClassDocument(
                home,
                prev,
                clasS,
                next,
                options,
                memberTypes
            );
        }

        this.l(
"<!-- ======== START OF CLASS DATA ======== -->",
"<div class=\"header\">",
"<div class=\"subTitle\">" + clasS.containingPackage().name() + "</div>",
"<h2 title=\"" + ClassFrameHtml.capFirst(JavadocUtil.category(clasS)) + " " + clasS.name() + "\" class=\"title\">" + ClassFrameHtml.capFirst(JavadocUtil.category(clasS)) + " " + clasS.name() + NoTemplate.html(ClassFrameHtml.typeParameters(clasS)) + "</h2>",
"</div>",
"<div class=\"contentContainer\">"
        );

        // Superclass chain.
        ClassDoc[] scs = ClassFrameHtml.superclassChain(clasS);
        if (scs.length > 0) {
            for (int sci = scs.length - 1; sci >= 0; sci--) {
                ClassDoc sc = scs[sci];
                this.l(
"<ul class=\"inheritance\">",
"<li>" + sc.qualifiedName() + "</li>",
"<li>"
                );
            }
            this.l(
"<ul class=\"inheritance\">",
"<li>" + JavadocUtil.toHtml(clasS, clasS, home, 0) + "</li>",
"</ul>"
            );
            for (int i = 0; i < scs.length; i++) {
                this.l(
"</li>",
"</ul>"
                );
            }
        }
        this.l(
"<div class=\"description\">",
"<ul class=\"blockList\">",
"<li class=\"blockList\">"
        );

        // Class/interface type parameters.
        if (clasS.typeParamTags().length > 0) {
            this.p("<dl><dt><span class=\"strong\">Type Parameters:</span></dt>");
            for (ParamTag tpt : clasS.typeParamTags()) {
                String comment = tpt.parameterComment();
                try {
                    comment = ClassFrameHtml.HTML.fromJavadocText(comment, clasS, rootDoc);
                } catch (Longjump e) {
                    ;
                }
                this.p("<dd><code>" + tpt.parameterName() + "</code> - " + comment + "</dd>");
            }
            this.l(
"</dl>"
            );
        }

        // Implemented interfaces / superinterfaces.
        {
            List<Type> sis = this.getImplementedInterfaces(clasS);
            if (sis.size() > 0) {
                this.l(
"<dl>",
"<dt>" + (clasS.isInterface() && clasS.interfaces().length == sis.size() ? "All Superinterfaces" : "All Implemented Interfaces") + ":</dt>"
                );
                this.p("<dd>");
                Once first = NoTemplate.once();
                Collections.sort(sis, Docs.TYPE_COMPARATOR);
                for (Type si : sis) {
                    if (!first.once()) this.p(", ");
                    this.p(JavadocUtil.toHtml(si, clasS, home, 0));
                }
                this.l(
"</dd>",
"</dl>"
                );
            }
        }

        // Enclosing class.
        if (clasS.containingClass() != null) {
            this.l(
"<dl>",
"<dt>Enclosing class:</dt>",
"<dd>" + JavadocUtil.toHtml(clasS.containingClass(), clasS, home, 0) + "</dd>",
"</dl>"
            );
        }

        // Known subinterfaces.
        if (this.knownSubinterfaces(clasS, rootDoc).size() > 0) {
            this.l(
"<dl>",
"<dt>All Known Subinterfaces:</dt>"
            );
            for (ClassDoc si : this.knownSubinterfaces(clasS, rootDoc)) {
                this.l(
"<dd><a href=\"" + home + JavadocUtil.href(si) + "\" title=\"" + JavadocUtil.title(si) + "\">" + si.name() + "</a>" + NoTemplate.html(ClassFrameHtml.typeParameters(si)) + "</dd>"
                );
            }
            this.l(
"</dl>"
            );
        }
        this.l(
"<hr>",
"<br>"
        );

        // Class/interface modifiers.
        this.p("<pre>");
        this.p("public ");
        if (clasS.isStatic()) this.p("static ");
        if (clasS.isAbstract() && !clasS.isInterface()) this.p("abstract ");
        if (clasS.isFinal()) this.p("final ");

        this.p(clasS.isClass() ? "class " : "interface ");

        // Class/interface name.
        // Class/interface type parameters.
        this.p("<span class=\"strong\">" + clasS.name() + NoTemplate.html(ClassFrameHtml.typeParameters(clasS)) + "</span>");

        // Class's superclass.
        if (clasS.superclass() != null) {
            this.l();
            this.p("extends " + clasS.superclass().qualifiedName() + ClassFrameHtml.typeArguments(clasS.superclass()));
        }

        // Interface's superinterfaces.
        if (clasS.interfaces().length > 0) {
            this.l();
            this.p("extends ");
            Once first = NoTemplate.once();
            for (Type it : clasS.interfaceTypes()) {
                if (!first.once()) this.p(", ");
                this.p(JavadocUtil.toHtml(it, clasS, home, 0));
            }
//            p("<a href=\"../../../../../de/unkrig/commons/lang/protocol/Producer.html\" title=\"interface in de.unkrig.commons.lang.protocol\">Producer</a>&lt;PT&gt;");
//            p(", ");
//            p("<a href=\"../../../../../de/unkrig/commons/lang/protocol/Consumer.html\" title=\"interface in de.unkrig.commons.lang.protocol\">Consumer</a>&lt;CT&gt;");
        }

        this.l(
"</pre>"
        );

        // Class/interface description.
        if (!this.description(clasS, rootDoc).isEmpty()) {
            this.l(
"<div class=\"block\">" + this.description(clasS, rootDoc) + "</div>"
            );
        }

        // @see pkg.cls#mem
        boolean implementsSerializable;
        IS: {
            for (Type t : this.getImplementedInterfaces(clasS)) {
                if ("java.io.Serializable".equals(t.qualifiedTypeName())) {
                    implementsSerializable = true;
                    break IS;
                }
            }
            implementsSerializable = false;
        }
        if (clasS.seeTags().length > 0 || implementsSerializable) {
            this.p("<dl><dt><span class=\"strong\">See Also:</span></dt>");
            this.p("<dd>");
            Once once = NoTemplate.once();
            for (SeeTag st : clasS.seeTags()) {
                if (!once.once()) {
                    this.l(
", "
                    );
                }
                Doc reference = ClassFrameHtml.reference(st);
                this.p("<a href=\"" + home + JavadocUtil.href(reference) + "\"");
                if (JavadocUtil.title(reference) != null) {
                    this.p(" title=\"" + JavadocUtil.title(reference) + "\"");
                }
                this.p("><code>" + ClassFrameHtml.toString(reference, clasS) + "</code></a>" + st.label());
            }
            if (implementsSerializable) {
                this.p("<a href=\"../../../../../serialized-form.html#de.unkrig.commons.lang.protocol.Longjump\">Serialized Form</a>");
            }
            this.l(
"</dd></dl>"
            );
        }
        this.l(
"</li>",
"</ul>",
"</div>",
"<div class=\"summary\">",
"<ul class=\"blockList\">",
"<li class=\"blockList\">"
        );

        // Class's/interface's fields.
        if (clasS.fields().length > 0) {
            this.l(
"<!-- =========== FIELD SUMMARY =========== -->",
"<ul class=\"blockList\">",
"<li class=\"blockList\"><a name=\"field_summary\">",
"<!--   -->",
"</a>",
"<h3>Field Summary</h3>",
"<table class=\"overviewSummary\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\" summary=\"Field Summary table, listing fields, and an explanation\">",
"<caption><span>Fields</span><span class=\"tabEnd\">&nbsp;</span></caption>",
"<tr>",
"<th class=\"colFirst\" scope=\"col\">Modifier and Type</th>",
"<th class=\"colLast\" scope=\"col\">Field and Description</th>",
"</tr>"
            );
            Producer<String> cls = ProducerUtil.alternate("altColor", "rowColor");
            for (FieldDoc fd : clasS.fields()) {
                this.l(
"<tr class=\"" + cls.produce() + "\">",
"<td class=\"colFirst\"><code>" + (fd.isStatic() ? "static " : "") + JavadocUtil.toHtml(fd.type(), fd, home, 0) + "</code></td>",
"<td class=\"colLast\"><code><strong><a href=\"" + home + JavadocUtil.href(fd) + "\">" + fd.name() + "</a></strong></code>",
"<div class=\"block\">" + JavadocUtil.firstSentenceOfDescription(fd, rootDoc) + "</div>",
"</td>",
"</tr>"
                );
            }
            this.l(
"</table>",
"</li>",
"</ul>"
            );
        }
        if (clasS.innerClasses().length > 0) {
            this.l(
"<!-- ======== NESTED CLASS SUMMARY ======== -->",
"<ul class=\"blockList\">",
"<li class=\"blockList\"><a name=\"nested_class_summary\">",
"<!--   -->",
"</a>",
"<h3>Nested Class Summary</h3>",
"<table class=\"overviewSummary\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\" summary=\"Nested Class Summary table, listing nested classes, and an explanation\">",
"<caption><span>Nested Classes</span><span class=\"tabEnd\">&nbsp;</span></caption>",
"<tr>",
"<th class=\"colFirst\" scope=\"col\">Modifier and Type</th>",
"<th class=\"colLast\" scope=\"col\">Class and Description</th>",
"</tr>"
            );
            Producer<String> cls = ProducerUtil.alternate("altColor", "rowColor");
            for (ClassDoc ncd : clasS.innerClasses()) {
                this.l(
"<tr class=\"" + cls.produce() + "\">",
"<td class=\"colFirst\"><code>static " + JavadocUtil.category(ncd) + "&nbsp;</code></td>"
                );
                this.l(
"<td class=\"colLast\"><code><strong>" + JavadocUtil.toHtml(ncd, clasS, home, 0) + "</strong></code>",
"<div class=\"block\">" + JavadocUtil.firstSentenceOfDescription(ncd, rootDoc) + "</div>",
"</td>",
"</tr>"
                );
            }
            this.l(
"</table>",
"</li>",
"</ul>"
            );
        }

        // Constructor summary section.
        if (clasS.constructors().length > 0) {
            this.l(
"<!-- ======== CONSTRUCTOR SUMMARY ======== -->",
"<ul class=\"blockList\">",
"<li class=\"blockList\"><a name=\"constructor_summary\">",
"<!--   -->",
"</a>",
"<h3>Constructor Summary</h3>",
"<table class=\"overviewSummary\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\" summary=\"Constructor Summary table, listing constructors, and an explanation\">",
"<caption><span>Constructors</span><span class=\"tabEnd\">&nbsp;</span></caption>",
"<tr>",
"<th class=\"colOne\" scope=\"col\">Constructor and Description</th>",
"</tr>"
            );
            Producer<String>  cls = ProducerUtil.alternate("altColor", "rowColor");
            for (ConstructorDoc cd : clasS.constructors()) {
                this.l(
"<tr class=\"" + cls.produce() + "\">"
                );
                this.p("<td class=\"colOne\"><code>" + "<strong><a href=\"" + home + JavadocUtil.href(cd) + "\">" + cd.name() + "</a></strong>");
                this.pParameters(home, cd);
                this.l(
"</code>&nbsp;</td>",
"</tr>"
                );
            }
            this.l(
"</table>",
"</li>",
"</ul>"
            );
        }

        // Method summary section.
        this.l(
"<!-- ========== METHOD SUMMARY =========== -->",
"<ul class=\"blockList\">",
"<li class=\"blockList\"><a name=\"method_summary\">",
"<!--   -->",
"</a>",
"<h3>Method Summary</h3>"
        );
        if (clasS.methods().length > 0) {

            // Table headers.
            this.l(
"<table class=\"overviewSummary\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\" summary=\"Method Summary table, listing methods, and an explanation\">",
"<caption><span>Methods</span><span class=\"tabEnd\">&nbsp;</span></caption>",
"<tr>",
"<th class=\"colFirst\" scope=\"col\">Modifier and Type</th>",
"<th class=\"colLast\" scope=\"col\">Method and Description</th>",
"</tr>"
            );
            Producer<String>  cls = ProducerUtil.alternate("altColor", "rowColor");
            for (MethodDoc md : sortedMethods) {
                this.l(
"<tr class=\"" + cls.produce() + "\">"
                );
                this.p("<td class=\"colFirst\"><code>");

                // Method modifiers.
                if (md.isStatic()) this.p("static ");
                if (md.isAbstract()) this.p("abstract ");

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
                this.l(
JavadocUtil.toHtml(md.returnType(), md, home, 0) + "</code></td>"
                );

                // Method name.
                this.p("<td class=\"colLast\"><code><strong><a href=\"" + home + JavadocUtil.href(md) + "\">" + md.name() + "</a></strong>");

                // Method parameters.
                this.pParameters(home, md);

                // First sentence of method description.
                if (JavadocUtil.firstSentenceOfDescription(md, rootDoc).isEmpty()) {
                    this.p("</code>&nbsp;");
                } else {
                    this.l(
"</code>",
"<div class=\"block\">" + JavadocUtil.firstSentenceOfDescription(md, rootDoc) + "</div>"
                    );
                }
                this.l(
"</td>",
"</tr>"
                );
            }
            this.l(
"</table>"
            );
        }

        // Methods inherited from base classes and interfaces.
        Set<String> seen = new HashSet<String>();
        for (MethodDoc m : clasS.methods()) {
            if (m.isStatic()) continue;
            seen.add(JavadocUtil.fragments(m)[0]);
        }
        List<Type> bcais = this.baseClassesAndInterfaces(clasS);
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
"<ul class=\"blockList\">",
"<li class=\"blockList\"><a name=\"methods_inherited_from_class_" + bt.qualifiedTypeName() + "\">",
"<!--   -->",
"</a>"
            );
            this.p("<h3>Methods inherited from " + JavadocUtil.category(bt.asClassDoc()) + "&nbsp;" + bt.asClassDoc().containingPackage().name() + '.');
            if (bt.asClassDoc().isIncluded()) {
                this.p("<a href=\"" + home + JavadocUtil.href(bt.asClassDoc()) + "\" title=\"" + JavadocUtil.title(bt.asClassDoc()) + "\">" + bt.typeName() + "</a>");
            } else {
                this.p(bt.typeName());
            }
            this.l(
"</h3>"
            );
            this.p("<code>");
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
            this.l(
"</code></li>",
"</ul>"
            );
        }
        this.l(
"</li>",
"</ul>",
"</li>",
"</ul>",
"</div>"
        );

        // "Details" sections.
        if (clasS.fields().length > 0 || clasS.methods().length > 0) {
            this.l(
"<div class=\"details\">",
"<ul class=\"blockList\">",
"<li class=\"blockList\">"
            );

            // Class's/interface's fields.
            if (clasS.fields().length > 0) {
                this.l(
"<!-- ============ FIELD DETAIL =========== -->",
"<ul class=\"blockList\">",
"<li class=\"blockList\"><a name=\"field_detail\">",
"<!--   -->",
"</a>",
"<h3>Field Detail</h3>"
                );
                for (int fi = 0; fi < clasS.fields().length; fi++) {
                    FieldDoc fd = clasS.fields()[fi];
                    this.l(
"<a name=\"" + fd.name() + "\">",
"<!--   -->",
"</a>"
                    );
                    String cls = fi == clasS.fields().length - 1 ? "blockListLast" : "blockList";
                    this.l(
"<ul class=\"" + cls + "\">",
"<li class=\"blockList\">",
"<h4>" + fd.name() + "</h4>",
"<pre>" + fd.modifiers() + "&nbsp;" + JavadocUtil.toHtml(fd.type(), fd, home, 0) + " " + fd.name() + "</pre>",
"<div class=\"block\">" + this.description(fd, rootDoc) + "</div>"
                    );
                    if (fd.seeTags().length > 0 || fd.constantValue() != null) {
                        this.p("<dl>");
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
                                + ClassFrameHtml.toString(ClassFrameHtml.reference(st), clasS)
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
                                + clasS.qualifiedName()
                                + "."
                                + fd.name()
                                + "\">Constant Field Values</a></dd>"
                            ));
                        }
                        this.l(
"</dl>"
                        );
                    }
                    this.l(
"</li>",
"</ul>"
                    );
                }
                this.l(
"</li>",
"</ul>"
                );
            }

            // Contructors' details.
            if (clasS.constructors().length > 0) {
                this.l(
"<!-- ========= CONSTRUCTOR DETAIL ======== -->",
"<ul class=\"blockList\">",
"<li class=\"blockList\"><a name=\"constructor_detail\">",
"<!--   -->",
"</a>",
"<h3>Constructor Detail</h3>"
                );
                for (int i = 0; i < clasS.constructors().length; i++) {
                    this.pExecutableMemberDetail(clasS.constructors()[i], home, i == clasS.constructors().length - 1, rootDoc);
                }
                this.l(
"</li>",
"</ul>"
                );
            }

            // Methods' details.
            this.l(
"<!-- ============ METHOD DETAIL ========== -->",
"<ul class=\"blockList\">",
"<li class=\"blockList\"><a name=\"method_detail\">",
"<!--   -->",
"</a>",
"<h3>Method Detail</h3>"
            );
            for (int mi = 0; mi < clasS.methods().length; mi++) {
                this.pExecutableMemberDetail(clasS.methods()[mi], home, mi == clasS.methods().length - 1, rootDoc);
            }
            this.l(
"</li>",
"</ul>"
            );

            // End of "details" sections.
            this.l(
"</li>",
"</ul>",
"</div>"
            );
        }
        this.l(
"</div>",
"<!-- ========= END OF CLASS DATA ========= -->"
        );

        {
            String prev = "Prev Class";
            if (previousClass != null) {
                try {
                    prev = ClassFrameHtml.HTML.makeLink(clasS, previousClass, true, "<span class=\"strong\">" + prev + "</span>", null, rootDoc);
                } catch (Longjump l) {}
            }
            String next = "Next Class";
            if (nextClass != null) {
                try {
                    next = ClassFrameHtml.HTML.makeLink(clasS, nextClass, true, "<span class=\"strong\">" + next + "</span>", null, rootDoc);
                } catch (Longjump l) {}
            }

            EnumSet<BottomNavBarHtml.MemberType> memberTypes = EnumSet.noneOf(BottomNavBarHtml.MemberType.class);
            if (clasS.innerClasses().length > 0) memberTypes.add(BottomNavBarHtml.MemberType.NESTED);
            if (clasS.fields().length       > 0) memberTypes.add(BottomNavBarHtml.MemberType.FIELD);
            if (clasS.constructors().length > 0) memberTypes.add(BottomNavBarHtml.MemberType.CONSTR);
            if (clasS.methods().length      > 0) memberTypes.add(BottomNavBarHtml.MemberType.METHOD);
            this.include(BottomNavBarHtml.class).renderForClassDocument(
                home,
                prev,
                clasS,
                next,
                options,
                memberTypes
            );
        }

        this.include(BottomHtml.class).render(options);
    }

    private void
    pExecutableMemberDetail(ExecutableMemberDoc emd, String home, boolean isLast, RootDoc rootDoc) {

        ClassDoc clasS = emd.containingClass();

        // Executable member anchors.
        for (String f : JavadocUtil.fragments(emd)) {
            this.l(
"<a name=\"" + f + "\">",
"<!--   -->"
            );
            this.p("</a>");
        }
        this.l();

        // Beginning of executable member detail.
        this.l(
"<ul class=\"" + (isLast ? "blockListLast" : "blockList") + "\">",
"<li class=\"blockList\">",
"<h4>" + emd.name() + "</h4>"
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

        this.p("<pre>");

        // Executable member annotations.
        for (AnnotationDesc a : emd.annotations()) {
            for (AnnotationDesc aa : a.annotationType().annotations()) {
                if ("java.lang.annotation.Documented".equals(aa.annotationType().qualifiedName())) {
                    this.l(
"@" + a.annotationType().simpleTypeName()
                    );
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
            Parameter p          = emd.parameters()[i];
            boolean   isEllipsis = emd.isVarArgs() && i == emd.parameters().length - 1;

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
        if (!this.description(emd, rootDoc).isEmpty()) {
            this.l(
"<div class=\"block\">" + this.description(emd, rootDoc) + "</div>"
            );
        }

        // Executable member's block tags.
        if (
            emd.typeParamTags().length > 0
            || emd.paramTags().length > 0
            || (emd.isMethod() && this.returnValueDescription((MethodDoc) emd, rootDoc) != null)
            || emd.throwsTags().length > 0
            || emd.thrownExceptionTypes().length > 0
            || emd.seeTags().length > 0
        ) {

            this.p("<dl>");

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
                if (emd.isMethod() && this.returnValueDescription((MethodDoc) emd, rootDoc) != null) this.l();
            }

            // @return
            if (emd.isMethod() && this.returnValueDescription((MethodDoc) emd, rootDoc) != null) {
                this.p((
                    "<dt><span class=\"strong\">Returns:</span></dt><dd>"
                    + this.returnValueDescription((MethodDoc) emd, rootDoc)
                    + "</dd>"
                ));
            }

            // @throws
            if (emd.throwsTags().length + emd.thrownExceptionTypes().length > 0) {

                if (emd.isMethod() && this.returnValueDescription((MethodDoc) emd, rootDoc) != null) this.l();

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

            this.l(
"</dl>"
            );
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
"<dl>",
"<dt><strong>Overrides:</strong></dt>",
"<dd><code>" + overriddenMethod.name() + "</code>&nbsp;in " + JavadocUtil.category(omcd) + "&nbsp;<code>" + JavadocUtil.toHtml(omcd, clasS, home, 0) + "</code></dd>",
"</dl>"
                );
            }
        }

        // End of executable member detail.
        this.l(
"</li>",
"</ul>"
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
    getImplementedInterfaces(ClassDoc cd) {
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

    private String
    description(Doc doc, RootDoc rootDoc) {

        try {
            return ClassFrameHtml.HTML.fromTags(doc.inlineTags(), doc, rootDoc);
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

    private List<ClassDoc>
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

    @Nullable private String
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
