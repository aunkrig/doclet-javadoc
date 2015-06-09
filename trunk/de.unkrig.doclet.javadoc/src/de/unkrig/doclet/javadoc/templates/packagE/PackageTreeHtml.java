
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
import de.unkrig.doclet.javadoc.JavadocDoclet.Options;
import de.unkrig.doclet.javadoc.templates.include.BottomHtml;
import de.unkrig.doclet.javadoc.templates.include.BottomNavBarHtml;
import de.unkrig.doclet.javadoc.templates.include.HierarchiesHtml;
import de.unkrig.doclet.javadoc.templates.include.TopHtml;
import de.unkrig.doclet.javadoc.templates.include.TopNavBarHtml;

public
class PackageTreeHtml extends AbstractPerPackageDocument {

    @Override public void
    render(String home, ElementWithContext<PackageDoc> packagE, Options options, RootDoc rootDoc) {

        this.include(TopHtml.class).render(packagE.current().name() + " Class Hierarchy", options, home + "stylesheet.css");

        this.l(
"<script type=\"text/javascript\"><!--",
"    if (location.href.indexOf('is-external=true') == -1) {",
"        parent.document.title=\"" + packagE.current().name() + " Class Hierarchy" + (options.windowTitle == null ? "" : " (" + options.windowTitle + ")") + "\";",
"    }",
"//-->",
"</script>",
"<noscript>",
"<div>JavaScript is disabled on your browser.</div>",
"</noscript>"
        );

        this.include(TopNavBarHtml.class).renderForPackageDocument(packagE, options, "package-tree.html");

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

        this.include(BottomNavBarHtml.class).renderForPackageDocument(packagE, options, "package-tree.html");

        this.include(BottomHtml.class).render(options);
    }
}
