
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
import java.util.SortedSet;

import com.sun.javadoc.ClassDoc;
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
class OverviewSummaryHtml extends AbstractGlobalDocument {

    @Override public void
    render(Options options, SortedSet<PackageDoc> allPackages, SortedSet<ClassDoc> allClassesAndInterfaces, RootDoc rootDoc) {

        this.include(TopHtml.class).render("Overview", options, "stylesheet.css");

        this.l(
"<script type=\"text/javascript\"><!--",
"    if (location.href.indexOf('is-external=true') == -1) {",
"        parent.document.title=\"Overview" + (options.windowTitle == null ? "" : " (" + options.windowTitle + ")") + "\";",
"    }",
"//-->",
"</script>",
"<noscript>",
"<div>JavaScript is disabled on your browser.</div>",
"</noscript>"
        );

        this.include(TopNavBarHtml.class).renderForGlobalDocument(
            options,                            // options
            "index.html?overview-summary.html", // framesLink
            "overview-summary.html",            // noFramesLink
            null,                               // overviewLink
            "overview-tree.html",               // treeLink
            "deprecated-list.html",             // deprecatedLink
            false,                              // indexLinkHighlit
            false                               // helpLinkHighlit
        );


        this.l(
"<div class=\"header\">",
"<h1 class=\"title\">DOCTITLE</h1>",
"</div>",
"<div class=\"contentContainer\">",
"<table class=\"overviewSummary\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\" summary=\"Packages table, listing packages, and an explanation\">",
"<caption><span>Packages</span><span class=\"tabEnd\">&nbsp;</span></caption>",
"<tr>",
"<th class=\"colFirst\" scope=\"col\">Package</th>",
"<th class=\"colLast\" scope=\"col\">Description</th>",
"</tr>",
"<tbody>"
        );
        ArrayList<PackageDoc> aps = new ArrayList<PackageDoc>(allPackages);
        Collections.sort(aps, Docs.DOCS_BY_NAME_COMPARATOR);
        Producer<String> cls = ProducerUtil.alternate("altColor", "rowColor");
        for (PackageDoc p : aps) {
            this.l(
"<tr class=\"" + cls.produce() + "\">",
"<td class=\"colFirst\"><a href=\"" + p.name().replace('.', '/') + "/package-summary.html\">" + p.name() + "</a></td>"
            );
            String desc = JavadocUtil.firstSentenceOfDescription(p, rootDoc);
            if (desc.isEmpty()) {
                this.l(
"<td class=\"colLast\">&nbsp;</td>"
                );
            } else {
                this.l(
"<td class=\"colLast\">",
"<div class=\"block\">" + JavadocUtil.firstSentenceOfDescription(p, rootDoc) + "</div>",
"</td>"
                );
            }
            this.l(
"</tr>"
            );
        }
        this.l(
"</tbody>",
"</table>",
"</div>"
        );

        this.include(BottomNavBarHtml.class).renderForGlobalDocument(
            options,                            // options
            "index.html?overview-summary.html", // framesLink
            "overview-summary.html",            // noFramesLink
            null,                               // overviewLink
            "overview-tree.html",               // treeLink
            "deprecated-list.html",             // deprecatedLink
            false,                              // indexLinkHighlit
            false                               // helpLinkHighlit
        );

        this.include(BottomHtml.class).render(options);
    }
}
