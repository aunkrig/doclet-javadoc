
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

package de.unkrig.doclet.javadoc.templates.global;

import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedSet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

import de.unkrig.commons.doclet.Docs;
import de.unkrig.doclet.javadoc.templates.JavadocUtil;
import de.unkrig.notemplate.javadocish.Options;
import de.unkrig.notemplate.javadocish.templates.AbstractRightFrameHtml;
import de.unkrig.notemplate.javadocish.templates.AbstractSummaryHtml;

/**
 * Renders the "Overview Summary" page, the one which is intially loaded in the right frame, "./overview-summary.html".
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

        final String overviewFirstSentenceHtml = JavadocUtil.firstSentenceOfDescription(rootDoc, rootDoc, rootDoc);
        final String overviewHtml              = JavadocUtil.description(rootDoc, rootDoc);

        // There is only one section: "Packages".
        Section section = new Section(
            null,                                                   // anchor
            "Packages",                                             // title
            "Packages table, listing packages, and an explanation", // summary
            "Package"                                               // firstColumnHeading
        );

        ArrayList<PackageDoc> aps = new ArrayList<PackageDoc>(allPackages);
        Collections.sort(aps, Docs.DOCS_BY_NAME_COMPARATOR);
        for (PackageDoc p : aps) {

            section.items.add(new SectionItem(
                p.name().replace('.', '/') + "/package-summary.html",       // link
                p.name(),                                                   // name
                JavadocUtil.firstSentenceOfDescription(rootDoc, p, rootDoc) // summary
            ));
        }

        this.rSummary(
            "Overview",                             // windowTitle
            options,                                // options
            new String[] { "stylesheet.css" },      // stylesheetLinks
            new String[] {                          // nav1
                "Overview",   AbstractRightFrameHtml.HIGHLIT,
                "Package",    AbstractRightFrameHtml.DISABLED,
                "Class",      AbstractRightFrameHtml.DISABLED,
                "Tree",       "overview-tree.html",
                "Deprecated", "deprecated-list.html",
                "Index",      options.splitIndex ? "index-files/index-1.html" : "index-all.html",
                "Help",       "help-doc.html",
            },
            new String[] { "Prev", "Next", },       // nav2
            new String[] {                          // nav3
                "Frames",    "index.html?overview-summary.html",
                "No Frames", "overview-summary.html",
            },
            new String[] {                          // nav4
                "All Classes", "allclasses-noframe.html",
            },
            new Runnable[] {                        // renderHeaders
                () -> {
                    if (options.docTitle != null) {
                        this.l(
"      <h1 class=\"title\">" + options.docTitle + "</h1>"
                        );
                    }
                },
                overviewFirstSentenceHtml.isEmpty() ? null : () -> {
                    this.l(
"      <div class=\"docSummary\">",
"        <div class=\"subTitle\">",
"          <div class=\"block\">" + overviewFirstSentenceHtml + "</div>",
"        </div>"
                    );
                    if (!overviewHtml.isEmpty()) {
                        this.l(
"        <p>See: <a href=\"#description\">Description</a></p>"
                        );
                    }
                    this.l(
"      </div>"
                    );
                }
            },
            overviewHtml.isEmpty() ? null : () -> { // epilog
                this.l(
"      <a name=\"description\" />",
"      " + overviewHtml
                );
            },
            Collections.singletonList(section)      // sections
        );
    }
}
