
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

import java.util.Arrays;

import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

import de.unkrig.commons.nullanalysis.Nullable;
import de.unkrig.commons.util.collections.ElementWithContext;
import de.unkrig.doclet.javadoc.templates.include.HierarchiesHtml;
import de.unkrig.notemplate.javadocish.Options;
import de.unkrig.notemplate.javadocish.templates.AbstractRightFrameHtml;

/**
 * Renders the per-package "Class Hierarchy" page, "./my/package/package-tree.html".
 */
public
class PackageTreeHtml extends AbstractRightFrameHtml implements PerPackageDocument {

    @Override public void
    render(final String home, final ElementWithContext<PackageDoc> packagE, Options options, RootDoc rootDoc) {

        super.rRightFrameHtml(
            packagE.current().name() + " Class Hierarchy", // windowTitle
            options,                                       // options
            new String[] { home + "stylesheet.css" },      // stylesheetLinks
            new String[] {                                 // nav1
                "Overview",   home + "overview-summary.html",
                "Package",    "package-summary.html",
                "Class",      AbstractRightFrameHtml.DISABLED,
                "Tree",       AbstractRightFrameHtml.HIGHLIT,
                "Deprecated", home + "deprecated-list.html",
                "Index",      home + (options.splitIndex ? "index-files/index-1.html" : "index-all.html"),
                "Help",       home + "help-doc.html",
            },
            new String[] {                                 // nav2
                PackageTreeHtml.packageTreeLink("Prev", home, packagE.previous()),
                PackageTreeHtml.packageTreeLink("Next", home, packagE.next()),
            },
            new String[] {                                 // nav3
                "Frames",    home + "index.html?" + packagE.current().name().replace('.', '/') + "/package-tree.html",
                "No Frames", "package-tree.html",
            },
            new String[] {                                 // nav4
                "All Classes", home + "allclasses-noframe.html",
            },
            null,                                          // nav5
            null,                                          // nav6
            () -> {                                        // renderBody
                PackageTreeHtml.this.rBody(packagE, home);
            }
        );
    }

    private static String
    packageTreeLink(String labelHtml, String home, @Nullable PackageDoc pd) {

        if (pd == null) return labelHtml;

        return "<a href=\"" + home + pd.name().replace('.', '/') + "/package-tree.html\">" + labelHtml + "</a>";
    }

    private void
    rBody(ElementWithContext<PackageDoc> packagE, String home) {

        this.l(
"    <div class=\"header\">",
"      <h1 class=\"title\">Hierarchy For Package " + packagE.current().name() + "</h1>",
"      <span class=\"strong\">Package Hierarchies:</span>",
"      <ul class=\"horizontal\">",
"        <li><a href=\"" + home + "overview-tree.html\">All Packages</a></li>",
"      </ul>",
"    </div>",
"    <div class=\"contentContainer\">"
        );

        this.include(HierarchiesHtml.class).render(
            home,
            Arrays.asList(packagE.current().ordinaryClasses()),
            Arrays.asList(packagE.current().interfaces())
        );

        this.l(
"    </div>"
        );
    }
}
