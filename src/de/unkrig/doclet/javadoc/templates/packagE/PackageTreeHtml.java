
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

import java.util.Arrays;

import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

import de.unkrig.commons.util.collections.IterableUtil.ElementWithContext;
import de.unkrig.doclet.javadoc.templates.include.HierarchiesHtml;
import de.unkrig.notemplate.javadocish.Options;
import de.unkrig.notemplate.javadocish.templates.AbstractClassFrameHtml;

public
class PackageTreeHtml extends AbstractClassFrameHtml implements PerPackageDocument {

    @Override public void
    render(final String home, final ElementWithContext<PackageDoc> packagE, Options options, RootDoc rootDoc) {

        super.rClassFrameHtml(
            "Package " + packagE.current().name(), // title
            options,                               // options
            "stylesheet.css",                      // stylesheetLink
            new String[] {                         // nav1
                "Overview",   home + "overview-summary.html",
                "Package",    home + "package-summary.html",
                "Class",      AbstractClassFrameHtml.DISABLED,
                "Use",        "package-use.html",
                "Tree",       AbstractClassFrameHtml.HIGHLIT,
                "Deprecated", home + "deprecated-list.html",
                "Index",      home + "index-all.html",
                "Help",       home + "help-doc.html",
            },
            new String[] {                         // nav2
                "Prev", packagE.hasPrevious() ? home + packagE.previous().name().replace('.', '/') + "/package-tree.html" : AbstractClassFrameHtml.DISABLED,
                "Next", packagE.hasNext()     ? home + packagE.next().name().replace('.', '/')     + "/package-tree.html" : AbstractClassFrameHtml.DISABLED,
            },
            new String[] {                         // nav3
                "Frames",    home + "index.html?" + packagE.current().name().replace('.', '/') + "/package-tree.html",
                "No Frames", "package-tree.html",
            },
            new String[] {                         // nav4
                "All Classes", home + "allclasses-noframe.html",
            },
            null,                                  // nav5
            null,                                  // nav6
            new Runnable() {

                @Override public void
                run() {
                    PackageTreeHtml.this.rBody(packagE, home);
                }
            }
        );
    }

    private void
    rBody(ElementWithContext<PackageDoc> packagE, String home) {

        this.l(
"<div class=\"header\">",
"<h1 class=\"title\">Hierarchy For Package " + packagE.current().name() + "</h1>",
"<span class=\"strong\">Package Hierarchies:</span>",
"<ul class=\"horizontal\">",
"<li><a href=\"" + home + "overview-tree.html\">All Packages</a></li>",
"</ul>",
"</div>",
"<div class=\"contentContainer\">"
        );

        this.include(HierarchiesHtml.class).render(
            home,
            Arrays.asList(packagE.current().ordinaryClasses()),
            Arrays.asList(packagE.current().interfaces())
        );
    }
}
