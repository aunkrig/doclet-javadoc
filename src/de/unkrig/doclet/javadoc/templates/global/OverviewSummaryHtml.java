
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
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

import de.unkrig.commons.doclet.Docs;
import de.unkrig.commons.lang.protocol.Producer;
import de.unkrig.commons.lang.protocol.ProducerUtil;
import de.unkrig.doclet.javadoc.templates.JavadocUtil;
import de.unkrig.notemplate.javadocish.Options;
import de.unkrig.notemplate.javadocish.templates.AbstractRightFrameHtml;
import de.unkrig.notemplate.javadocish.templates.AbstractSummaryHtml;

/**
 * Renders the "Overview Summary" page.
 */
public
class OverviewSummaryHtml extends AbstractSummaryHtml implements GlobalDocument {

    @Override public void
    render(
        final Options               options,
        final SortedSet<PackageDoc> allPackages,
        SortedSet<ClassDoc>         allClassesAndInterfaces,
        final RootDoc               rootDoc
    ) {


        // There is only one section: "Packages".
        Section section = new Section();

        section.firstColumnHeading = "Package";
        section.summary            = "Packages table, listing packages, and an explanation";
        section.title              = "Packages";
        section.items              = new ArrayList<>();


        ArrayList<PackageDoc> aps = new ArrayList<PackageDoc>(allPackages);
        Collections.sort(aps, Docs.DOCS_BY_NAME_COMPARATOR);
        for (PackageDoc p : aps) {

            SectionItem item = new SectionItem();

            item.link    = p.name().replace('.', '/') + "/package-summary.html";
            item.name    = p.name();
            item.summary = JavadocUtil.firstSentenceOfDescription(rootDoc, p, rootDoc);

            section.items.add(item);
        }

        this.rSummary(
            "Overview",                        // windowTitle
            options,                           // options
            new String[] { "stylesheet.css" }, // stylesheetLinks
            new String[] {                     // nav1
                "Overview",   AbstractRightFrameHtml.HIGHLIT,
                "Package",    AbstractRightFrameHtml.DISABLED,
                "Class",      AbstractRightFrameHtml.DISABLED,
                "Tree",       "overview-tree.html",
                "Deprecated", "deprecated-list.html",
                "Index",      "index-all.html",
                "Help",       "help-doc.html",
            },
            new String[] {                     // nav2
                "Prev",
                "Next",
            },
            new String[] {                     // nav3
                "Frames",    "?overview-summary.html",
                "No Frames", "overview-summary.html",
            },
            new String[] {                     // nav4
                "All Classes", "allclasses-noframe.html",
            },
            () -> {                            // prolog
                if (options.docTitle != null) {
                    this.l(
"<h1 class=\"title\">" + options.docTitle + "</h1>"
                    );
                }
            },
            () -> {                            // epilog
            },
            Collections.singletonList(section) // sections
        );
    }

    private void
    rBody(Options options, SortedSet<PackageDoc> allPackages, RootDoc rootDoc) {

        if (options.docTitle != null) {
            this.l(
"<div class=\"header\">",
"<h1 class=\"title\">" + options.docTitle + "</h1>",
"</div>"
            );
        }

        this.l(
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
        Producer<? extends String> cls = ProducerUtil.alternate("altColor", "rowColor");
        for (PackageDoc p : aps) {
            this.l(
"<tr class=\"" + cls.produce() + "\">",
"<td class=\"colFirst\"><a href=\"" + p.name().replace('.', '/') + "/package-summary.html\">" + p.name() + "</a></td>"
            );
            String desc = JavadocUtil.firstSentenceOfDescription(rootDoc, p, rootDoc);
            if (desc.isEmpty()) {
                this.l(
"<td class=\"colLast\">&nbsp;</td>"
                );
            } else {
                this.l(
"<td class=\"colLast\">",
"<div class=\"block\">" + JavadocUtil.firstSentenceOfDescription(rootDoc, p, rootDoc) + "</div>",
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
    }
}
