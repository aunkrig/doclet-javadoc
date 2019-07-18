
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

package de.unkrig.doclet.javadoc.templates.packagE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.TypeVariable;

import de.unkrig.commons.doclet.Docs;
import de.unkrig.commons.nullanalysis.Nullable;
import de.unkrig.commons.util.collections.ElementWithContext;
import de.unkrig.doclet.javadoc.templates.JavadocUtil;
import de.unkrig.notemplate.javadocish.Options;
import de.unkrig.notemplate.javadocish.templates.AbstractRightFrameHtml;
import de.unkrig.notemplate.javadocish.templates.AbstractSummaryHtml;

/**
 * Renders the "Package Summary" page, "./my/package/package-summary.html".
 */
public
class PackageSummaryHtml extends AbstractSummaryHtml implements PerPackageDocument {

    @Override public void
    render(
        final String                         home,
        final ElementWithContext<PackageDoc> packagE,
        Options                              options,
        final RootDoc                        rootDoc
    ) {
        PackageDoc pkg = packagE.current();

        final String descriptionFirstSentenceHtml = JavadocUtil.firstSentenceOfDescription(pkg, pkg, rootDoc);
        final String descriptionHtml              = JavadocUtil.description(pkg, rootDoc);

        List<Section> sections = new ArrayList<>();

        sections.add(PackageSummaryHtml.newSection(
            "Interface Summary",                                               // title
            "Interface Summary table, listing interfaces, and an explanation", // summary
            "Interface",                                                       // firstColumnHeading
            pkg.interfaces(),                                                  // classDocs
            rootDoc                                                            // rootDoc
        ));

        sections.add(PackageSummaryHtml.newSection(
            "Class Summary",                                            // title
            "Class Summary table, listing classes, and an explanation", // summary
            "Class",                                                    // firstColumnHeading
            pkg.ordinaryClasses(),                                      // classDocs
            rootDoc                                                     // rootDoc
        ));

        sections.add(PackageSummaryHtml.newSection(
            "Enum Summary",                                          // title
            "Enum Summary table, listing enums, and an explanation", // summary
            "Enum",                                                  // firstColumnHeading
            pkg.enums(),                                             // classDocs
            rootDoc                                                  // rootDoc
        ));

        sections.add(PackageSummaryHtml.newSection(
            "Exception Summary",                                               // title
            "Exception Summary table, listing exceptions, and an explanation", // summary
            "Exception",                                                       // firstColumnHeading
            pkg.exceptions(),                                                  // classDocs
            rootDoc                                                            // rootDoc
        ));

        sections.add(PackageSummaryHtml.newSection(
            "Error Summary",                                           // title
            "Error Summary table, listing errors, and an explanation", // summary
            "Error",                                                   // firstColumnHeading
            pkg.errors(),                                              // classDocs
            rootDoc                                                    // rootDoc
        ));

        sections.add(PackageSummaryHtml.newSection(
            "Annotation Type Summary",                                                     // title
            "Annotation Type Summary table, listing annotation types, and an explanation", // summary
            "Annotation Type",                                                             // firstColumnHeading
            pkg.annotationTypes(),                                                         // classDocs
            rootDoc                                                                        // rootDoc
        ));

        super.rSummary(
            pkg.name(),                               // windowTitle
            options,                                  // options
            new String[] { home + "stylesheet.css" }, // stylesheetLinks
            new String[] {                            // nav1
                "Overview",   home + "overview-summary.html",
                "Package",    AbstractRightFrameHtml.HIGHLIT,
                "Class",      AbstractRightFrameHtml.DISABLED,
                "Tree",       "package-tree.html",
                "Deprecated", home + "deprecated-list.html",
                "Index",      home + (options.splitIndex ? "index-files/index-1.html" : "index-all.html"),
                "Help",       home + "help-doc.html",
            },
            new String[] {                            // nav2
                PackageSummaryHtml.packageSummaryLink("Prev Package", home, packagE.previous()),
                PackageSummaryHtml.packageSummaryLink("Next Package", home, packagE.next()),
            },
            new String[] {                            // nav3
                "Frames", home + "index.html?" + pkg.name().replace('.', '/') + "/package-summary.html",
                "No Frames", "package-summary.html",
            },
            new String[] {                            // nav4
                "All Classes", home + "allclasses-noframe.html",
            },
            new Runnable[] { () -> {                  // renderHeaders
                this.l(
"      <h1 title=\"Package\" class=\"title\">Package&nbsp;" + pkg.name() + "</h1>",
"      <div class=\"docSummary\">",
"        <div class=\"block\">" + descriptionFirstSentenceHtml + "</div>",
"      </div>",
"      <p>See:&nbsp;<a href=\"#package_description\">Description</a></p>"
                );
            } },
            () -> {                                   // epilog
                this.l(
"      <a name=\"package.description\" />",
"      <h2 title=\"Package " + pkg.name() + "\">Package " + pkg.name() + " Description</h2>",
"      <div class=\"block\">" + descriptionHtml + "</div>"
                );
            },
            sections                                  // sections
        );
    }

    private static Section
    newSection(
        String     title,
        String     summary,
        String     firstColumnHeading,
        ClassDoc[] classDocs,
        RootDoc    rootDoc
    ) {
        Section section = new Section(
            null,              // anchor
            title,             // title
            summary,           // summary
            firstColumnHeading // firstColumnHeading
        );

        Arrays.sort(classDocs, Docs.DOCS_BY_NAME_COMPARATOR);
        for (ClassDoc cd : classDocs) {

            StringBuilder sb = new StringBuilder(cd.name());
            {
                TypeVariable[] typeParameters = cd.typeParameters();
                if (typeParameters.length > 0) {
                    sb.append("&lt;").append(typeParameters[0]);
                    for (int j = 1; j < typeParameters.length; j++) {
                        sb.append(", ").append(typeParameters[j]);
                    }
                    sb.append("&gt;");
                }
            }

            section.items.add(new SectionItem(
                cd.name() + ".html",                                    // link
                sb.toString(),                                          // name
                JavadocUtil.firstSentenceOfDescription(cd, cd, rootDoc) // summary
            ));
        }

        return section;
    }

    private static String
    packageSummaryLink(String labelHtml, String home, @Nullable PackageDoc pd) {

        if (pd == null) return labelHtml;

        return "<a href=\"" + home + pd.name().replace('.', '/') + "/package-summary.html\">" + labelHtml + "</a>";
    }
}
