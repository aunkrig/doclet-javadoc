
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
import java.util.List;
import java.util.SortedSet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

import de.unkrig.doclet.javadoc.templates.include.HierarchiesHtml;
import de.unkrig.notemplate.NoTemplate;
import de.unkrig.notemplate.javadocish.Options;
import de.unkrig.notemplate.javadocish.templates.AbstractRightFrameHtml;

/**
 * Renders the "Class Hierarchy" page, "./overview-tree.html".
 */
public
class OverviewTreeHtml extends AbstractRightFrameHtml implements GlobalDocument {

    @Override public void
    render(
        Options                     options,
        final SortedSet<PackageDoc> allPackages,
        final SortedSet<ClassDoc>   allClassesAndInterfaces,
        RootDoc                     rootDoc
    ) {

        super.rRightFrameHtml(
            "Class Hierarchy",                 // windowTitle
            options,                           // options
            new String[] { "stylesheet.css" }, // stylesheetLinks
            new String[] {                     // nav1
                "Overview",   "overview-summary.html",
                "Package",    AbstractRightFrameHtml.DISABLED,
                "Class",      AbstractRightFrameHtml.DISABLED,
                "Use",        AbstractRightFrameHtml.DISABLED,
                "Tree",       AbstractRightFrameHtml.HIGHLIT,
                "Deprecated", "deprecated-list.html",
                "Index",      options.splitIndex ? "index-files/index-1.html" : "index-all.html",
                "Help",       "help-doc.html",
            },
            new String[] {                     // nav2
                "Prev",
                "Next",
            },
            new String[] {                     // nav3
                "Frames",    "index.html?overview-tree.html",
                "No Frames", "overview-tree.html",
            },
            new String[] {                     // nav4
                "All Classes", "allclasses-noframe.html",
            },
            null,                              // nav5
            null,                              // nav6
            () -> {                            // renderBody
                OverviewTreeHtml.this.rBody(allPackages, allClassesAndInterfaces);
            }
        );
    }

    private void
    rBody(SortedSet<PackageDoc> allPackages, SortedSet<ClassDoc> allClassesAndInterfaces) {

        this.l(
"    <div class=\"header\">",
"      <h1 class=\"title\">Hierarchy For All Packages</h1>",
"      <span class=\"strong\">Package Hierarchies:</span>",
"      <ul class=\"horizontal\">"
        );
        Once first = NoTemplate.once();
        for (PackageDoc p : allPackages) {
            if (!first.once()) this.l(", </li>");
            this.p("<li><a href=\"" + p.name().replace('.', '/') + "/package-tree.html\">" + p.name() + "</a>");
        }
        if (!first.once()) this.l("</li>");
        this.l(
"      </ul>",
"    </div>",
"    <div class=\"contentContainer\">"
        );

        List<ClassDoc> classes    = new ArrayList<ClassDoc>();
        List<ClassDoc> interfaces = new ArrayList<ClassDoc>();
        for (ClassDoc cd : allClassesAndInterfaces) {
            if (cd.isInterface()) {
                interfaces.add(cd);
            } else
            if (cd.isOrdinaryClass()) {
                classes.add(cd);
            }
        }

        this.include(HierarchiesHtml.class).render("", classes, interfaces);

        this.l(
"    </div>"
        );
    }
}
