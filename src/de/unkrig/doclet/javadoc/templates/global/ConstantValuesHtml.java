
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

package de.unkrig.doclet.javadoc.templates.global;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

import de.unkrig.commons.doclet.Docs;
import de.unkrig.commons.lang.protocol.Producer;
import de.unkrig.commons.lang.protocol.ProducerUtil;
import de.unkrig.doclet.javadoc.JavadocDoclet.Options;
import de.unkrig.doclet.javadoc.templates.JavadocUtil;
import de.unkrig.doclet.javadoc.templates.include.BottomHtml;
import de.unkrig.doclet.javadoc.templates.include.BottomNavBarHtml;
import de.unkrig.doclet.javadoc.templates.include.TopHtml;
import de.unkrig.doclet.javadoc.templates.include.TopNavBarHtml;

public
class ConstantValuesHtml extends AbstractGlobalDocument {

    @Override public void
    render(Options options, SortedSet<PackageDoc> allPackages, SortedSet<ClassDoc> allClassesAndInterfaces, RootDoc rootDoc) {

        this.include(TopHtml.class).render("Constant Field Values", options, "stylesheet.css");

        this.l(
"<script type=\"text/javascript\"><!--",
"    if (location.href.indexOf('is-external=true') == -1) {",
"        parent.document.title=\"Constant Field Values" + (options.windowTitle == null ? "" : " (" + options.windowTitle + ")") + "\";",
"    }",
"//-->",
"</script>",
"<noscript>",
"<div>JavaScript is disabled on your browser.</div>",
"</noscript>"
        );

        this.include(TopNavBarHtml.class).renderForGlobalDocument(
            options,
            "index.html?constant-values.html", // framesLink
            "constant-values.html",            // noFramesLink
            "overview-summary.html",           // overviewLink
            "overview-tree.html",              // treeLink
            "deprecated-list.html",            // deprecatedLink
            false,                             // indexLinkHighlit
            false                              // helpLinkHighlit
        );

        this.l(
"<div class=\"header\">",
"<h1 title=\"Constant Field Values\" class=\"title\">Constant Field Values</h1>",
"<h2 title=\"Contents\">Contents</h2>",
"<ul>"
        );
        List<PackageDoc> ps = new ArrayList<PackageDoc>(allPackages);
        Collections.sort(ps, Docs.DOCS_BY_NAME_COMPARATOR);
        for (PackageDoc p : ps) {

            if (JavadocUtil.classesAndInterfacesWithConstants(p).iterator().hasNext()) {
                this.l(
"<li><a href=\"#" + p.name() + "\">" + p.name() + ".*</a></li>"
                );
            }
        }
        this.l(
"</ul>",
"</div>"
        );
        this.p("<div class=\"constantValuesContainer\">");
        for (PackageDoc p : ps) {

            Iterable<ClassDoc> caiwcs = JavadocUtil.classesAndInterfacesWithConstants(p);
            if (caiwcs.iterator().hasNext()) {
                this.l(
"<a name=\"" + p.name() + "\">",
"<!--   -->",
"</a>",
"<h2 title=\"" + p.name() + "\">" + p.name() + ".*</h2>",
"<ul class=\"blockList\">"
                );
                for (ClassDoc coi : caiwcs) {
                    this.l(
"<li class=\"blockList\">",
"<table border=\"0\" cellpadding=\"3\" cellspacing=\"0\" summary=\"Constant Field Values table, listing constant fields, and values\">",
"<caption><span>" + p.name() + "." + JavadocUtil.toHtml(coi, null, "", 0) + "</span><span class=\"tabEnd\">&nbsp;</span></caption>",
"<tr>",
"<th class=\"colFirst\" scope=\"col\">Modifier and Type</th>",
"<th scope=\"col\">Constant Field</th>",
"<th class=\"colLast\" scope=\"col\">Value</th>",
"</tr>",
"<tbody>"
                    );
                    Producer<String> cls = ProducerUtil.alternate("altColor", "rowColor");
                    for (FieldDoc c : JavadocUtil.constantsOf(coi)) {
                        this.l(
"<tr class=\"" + cls.produce() + "\">",
"<td class=\"colFirst\"><a name=\"" + coi.qualifiedName() + "." + c.name() + "\">",
"<!--   -->",
"</a><code>" + c.modifiers().replaceAll(" ", "&nbsp;") + "&nbsp;" + c.type() + "</code></td>",
"<td><code><a href=\"" + coi.qualifiedName().replace('.', '/') + ".html#" + c.name() + "\">" + c.name() + "</a></code></td>",
"<td class=\"colLast\"><code>" + c.constantValue() + "</code></td>",
"</tr>"
                        );
                    }
                    this.l(
"</tbody>",
"</table>",
"</li>"
                    );
                }
                this.l(
"</ul>"
                );
            }
        }
        this.l(
"</div>"
        );

        this.include(BottomNavBarHtml.class).renderForGlobalDocument(
            options,                           // options
            "index.html?constant-values.html", // framesLink
            "constant-values.html",            // noFramesLink
            "overview-summary.html",           // overviewLink
            "overview-tree.html",              // treeLink
            "deprecated-list.html",            // deprecatedLink
            false,                             // indexLinkHighlit
            false                              // helpLinkHighlit
        );

        this.include(BottomHtml.class).render(options);
    }
}
