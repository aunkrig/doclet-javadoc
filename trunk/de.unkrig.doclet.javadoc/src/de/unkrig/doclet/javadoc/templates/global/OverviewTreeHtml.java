
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
import java.util.List;
import java.util.SortedSet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

import de.unkrig.doclet.javadoc.JavadocDoclet.Options;
import de.unkrig.doclet.javadoc.templates.include.BottomHtml;
import de.unkrig.doclet.javadoc.templates.include.BottomNavBarHtml;
import de.unkrig.doclet.javadoc.templates.include.HierarchiesHtml;
import de.unkrig.doclet.javadoc.templates.include.TopHtml;
import de.unkrig.doclet.javadoc.templates.include.TopNavBarHtml;
import de.unkrig.notemplate.NoTemplate;

public
class OverviewTreeHtml extends AbstractGlobalDocument {

    @Override public void
    render(Options options, SortedSet<PackageDoc> allPackages, SortedSet<ClassDoc> allClassesAndInterfaces, RootDoc rootDoc) {

        this.include(TopHtml.class).render("Class Hierarchy", options, "stylesheet.css");

        this.l(
"<script type=\"text/javascript\"><!--",
"    if (location.href.indexOf('is-external=true') == -1) {",
"        parent.document.title=\"Class Hierarchy" + (options.windowTitle == null ? "" : " (" + options.windowTitle + ")") + "\";",
"    }",
"//-->",
"</script>",
"<noscript>",
"<div>JavaScript is disabled on your browser.</div>",
"</noscript>"
        );

        this.include(TopNavBarHtml.class).renderForGlobalDocument(
            options,                         // options
            "index.html?overview-tree.html", // framesLink
            "overview-tree.html",            // noFramesLink
            "overview-summary.html",         // overviewLink
            "HIGHLIGHT",                     // treeLink
            "deprecated-list.html",          // deprecatedLink
            false,                           // indexLinkHighlit
            false                            // helpLinkHighlit
        );

        this.l(
"<div class=\"header\">",
"<h1 class=\"title\">Hierarchy For All Packages</h1>",
"<span class=\"strong\">Package Hierarchies:</span>",
"<ul class=\"horizontal\">"
        );
        Once first = NoTemplate.once();
        for (PackageDoc p : allPackages) {
            if (!first.once()) this.l(", </li>");
            this.p("<li><a href=\"" + p.name().replace('.', '/') + "/package-tree.html\">" + p.name() + "</a>");
        }
        if (!first.once()) this.l("</li>");
        this.l(
"</ul>",
"</div>",
"<div class=\"contentContainer\">"
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
"</div>"
        );

        this.include(BottomNavBarHtml.class).renderForGlobalDocument(
            options,
            "index.html?overview-tree.html", // framesLink
            "overview-tree.html",            // noFramesLink
            "overview-summary.html",         // overviewLink
            "HIGHLIGHT",                     // treeLink
            "deprecated-list.html",          // deprecatedLink
            false,                           // indexLinkHighlit
            false                            // helpLinkHighlit
        );

        this.include(BottomHtml.class).render(options);
    }
}
