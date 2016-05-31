
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

package de.unkrig.doclet.javadoc.templates.packagE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

import de.unkrig.commons.doclet.Docs;
import de.unkrig.commons.lang.protocol.Producer;
import de.unkrig.commons.lang.protocol.ProducerUtil;
import de.unkrig.commons.nullanalysis.Nullable;
import de.unkrig.commons.util.collections.IterableUtil.ElementWithContext;
import de.unkrig.doclet.javadoc.templates.JavadocUtil;
import de.unkrig.notemplate.javadocish.Options;
import de.unkrig.notemplate.javadocish.templates.AbstractRightFrameHtml;
import de.unkrig.notemplate.javadocish.templates.AbstractSummaryHtml;

/**
 * Renders the "Package Summary" page.
 */
public
class PackageSummaryHtml extends AbstractSummaryHtml implements PerPackageDocument {

    @Override public void
    render(final String home, final ElementWithContext<PackageDoc> packagE, Options options, final RootDoc rootDoc) {

        Section classesSection = new Section();

        classesSection.firstColumnHeading = "Interface";
        classesSection.items              = new ArrayList<>();
        classesSection.summary            = "Interface Summary table, listing interfaces, and an explanation";
        classesSection.title              = "Interface Summary";

        ClassDoc[] interfaces = packagE.current().interfaces();
        Arrays.sort(interfaces, Docs.DOCS_BY_NAME_COMPARATOR);
        for (ClassDoc i : interfaces) {

            SectionItem item = new SectionItem();

            item.link    = i.name() + ".html";
            item.name    = i.name();
            item.summary = JavadocUtil.firstSentenceOfDescription(i, i, rootDoc);

            classesSection.items.add(item);
        }

        super.rSummary(
            packagE.current().name(),                 // windowTitle
            options,                                  // options
            new String[] { home + "stylesheet.css" }, // stylesheetLinks
            new String[] {                            // nav1
                "Overview",   home + "overview-summary.html",
                "Package",    AbstractRightFrameHtml.HIGHLIT,
                "Class",      AbstractRightFrameHtml.DISABLED,
                "Tree",       "package-tree.html",
                "Deprecated", home + "deprecated-list.html",
                "Index",      home + "index-all.html",
                "Help",       home + "help-doc.html",
            },
            new String[] {                            // nav2
                PackageSummaryHtml.packageSummaryLink("Prev Package", home, packagE.previous()),
                PackageSummaryHtml.packageSummaryLink("Next Package", home, packagE.next()),
            },
            new String[] {                            // nav3
                "Frames",    home + "index.html?" + packagE.current().name().replace('.', '/') + "/package-summary.html",
                "No Frames", "package-summary.html",
            },
            new String[] {                            // nav4
                "All Classes", home + "allclasses-noframe.html",
            },
            () -> {                                   // prolog
                this.l(
"<h1 title=\"Package\" class=\"title\">Package&nbsp;" + packagE.current().name() + "</h1>",
"<div class=\"docSummary\">",
"  <div class=\"block\">" + JavadocUtil.description(packagE.current(), rootDoc) + "</div>",
"</div>",
"<p>See:&nbsp;<a href=\"#package_description\">Description</a></p>"
                );
            },
            () -> {                                   // epilog
            },
            Collections.singletonList(classesSection) // sections
        );
    }

    private void
    rBody(ElementWithContext<PackageDoc> packagE, RootDoc rootDoc, String home) {

        this.l(
"<div class=\"header\">",
"<h1 title=\"Package\" class=\"title\">Package&nbsp;" + packagE.current().name() + "</h1>",
"<div class=\"docSummary\">",
"<div class=\"block\">" + JavadocUtil.description(packagE.current(), rootDoc) + "</div>",
"</div>",
"<p>See:&nbsp;<a href=\"#package_description\">Description</a></p>",
"</div>",
"<div class=\"contentContainer\">",
"<ul class=\"blockList\">"
        );

        ClassDoc[] is = packagE.current().interfaces();
        if (is.length > 0) {
            this.l(
"<li class=\"blockList\">",
"<table class=\"packageSummary\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\" summary=\"Interface Summary table, listing interfaces, and an explanation\">",
"<caption><span>Interface Summary</span><span class=\"tabEnd\">&nbsp;</span></caption>",
"<tr>",
"<th class=\"colFirst\" scope=\"col\">Interface</th>",
"<th class=\"colLast\" scope=\"col\">Description</th>",
"</tr>",
"<tbody>"
            );
            Arrays.sort(is, Docs.DOCS_BY_NAME_COMPARATOR);
            Producer<? extends String> cls = ProducerUtil.alternate("altColor", "rowColor");
            for (ClassDoc i : is) {
                this.l(
"<tr class=\"" + cls.produce() + "\">",
"<td class=\"colFirst\">" + JavadocUtil.toHtml(i, packagE.current(), home, 1) + "</td>"
                );
                String fsod = JavadocUtil.firstSentenceOfDescription(i, i, rootDoc);
                if (fsod.isEmpty()) {
                    this.l(
"<td class=\"colLast\">&nbsp;</td>"
                    );
                } else {
                    this.l(
"<td class=\"colLast\">",
"<div class=\"block\">" + fsod + "</div>",
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
"</li>"
            );
        }

        ClassDoc[] cs = packagE.current().ordinaryClasses();
        if (cs.length > 0) {
            this.l(
"<li class=\"blockList\">",
"<table class=\"packageSummary\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\" summary=\"Class Summary table, listing classes, and an explanation\">",
"<caption><span>Class Summary</span><span class=\"tabEnd\">&nbsp;</span></caption>",
"<tr>",
"<th class=\"colFirst\" scope=\"col\">Class</th>",
"<th class=\"colLast\" scope=\"col\">Description</th>",
"</tr>",
"<tbody>"
            );
            Arrays.sort(cs, Docs.DOCS_BY_NAME_COMPARATOR);
            Producer<? extends String> cls = ProducerUtil.alternate("altColor", "rowColor");
            for (ClassDoc c : cs) {
                this.l(
"<tr class=\"" + cls.produce() + "\">",
"<td class=\"colFirst\">" + JavadocUtil.toHtml(c, packagE.current(), home, 1) + "</td>"
                );
                String fsod = JavadocUtil.firstSentenceOfDescription(c, c, rootDoc);
                if (fsod.isEmpty()) {
                    this.l(
"<td class=\"colLast\">&nbsp;</td>"
                    );
                } else {
                    this.l(
"<td class=\"colLast\">",
"<div class=\"block\">" + fsod + "</div>",
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
"</li>"
            );
        }

        this.l(
"</ul>",
"<a name=\"package_description\">",
"<!--   -->",
"</a>",
"<h2 title=\"Package " + packagE.current().name() + " Description\">Package " + packagE.current().name() + " Description</h2>",
"<div class=\"block\">" + JavadocUtil.description(packagE.current(), rootDoc) + "</div>",
"</div>"
        );
    }

    private static String
    packageSummaryLink(String labelHtml, String home, @Nullable PackageDoc pd) {

        if (pd == null) return labelHtml;

        return "<a href=\"" + home + pd.name().replace('.', '/') + "/package-summary.html\">" + labelHtml + "</a>";
    }
}
