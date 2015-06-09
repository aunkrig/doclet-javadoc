
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

import java.util.SortedSet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

import de.unkrig.doclet.javadoc.JavadocDoclet.Options;
import de.unkrig.doclet.javadoc.templates.include.TopHtml;
import de.unkrig.doclet.javadoc.templates.include.TopNavBarHtml;

public
class SerializedFormHtml extends AbstractGlobalDocument {

    @Override public void
    render(Options options, SortedSet<PackageDoc> allPackages, SortedSet<ClassDoc> allClassesAndInterfaces, RootDoc rootDoc) {

        this.include(TopHtml.class).render("Serialized Form", options, "stylesheet.css");

        this.l(
            "<script type=\"text/javascript\"><!--",
            "    if (location.href.indexOf('is-external=true') == -1) {",
            "        parent.document.title=\"Serialized Form (WINDOWTITLE)\";",
            "    }",
            "//-->",
            "</script>",
            "<noscript>",
            "<div>JavaScript is disabled on your browser.</div>",
            "</noscript>"
        );

        this.include(TopNavBarHtml.class).renderForGlobalDocument(
            options,                           // options
            "index.html?serialized-form.html", // framesLink
            "serialized-form.html",            // noFramesLink
            "overview-summary.html",           // overviewLink
            "overview-tree.html",              // treeLink
            "deprecated-list.html",            // deprecatedLink
            false,                             // indexLinkHighlit
            false                              // helpLinkHighlit
        );

        this.l(
            "<div class=\"header\">",
            "<h1 title=\"Serialized Form\" class=\"title\">Serialized Form</h1>",
            "</div>",
            "<div class=\"serializedFormContainer\">",
            "<ul class=\"blockList\">",
            "<li class=\"blockList\">",
            "<h2 title=\"Package\">Package&nbsp;de.unkrig.commons.lang.protocol</h2>",
            "<ul class=\"blockList\">",
            "<li class=\"blockList\"><a name=\"de.unkrig.commons.lang.protocol.Longjump\">",
            "<!--   -->",
            "</a>",
            "<h3>Class <a href=\"de/unkrig/commons/lang/protocol/Longjump.html\" title=\"class in de.unkrig.commons.lang.protocol\">de.unkrig.commons.lang.protocol.Longjump</a> extends java.lang.Throwable implements Serializable</h3>",
            "<dl class=\"nameValue\">",
            "<dt>serialVersionUID:</dt>",
            "<dd>1L</dd>",
            "</dl>",
            "</li>",
            "</ul>",
            "</li>",
            "</ul>",
            "</div>",
            "<!-- ======= START OF BOTTOM NAVBAR ====== -->",
            "<div class=\"bottomNav\"><a name=\"navbar_bottom\">",
            "<!--   -->",
            "</a><a href=\"#skip-navbar_bottom\" title=\"Skip navigation links\"></a><a name=\"navbar_bottom_firstrow\">",
            "<!--   -->",
            "</a>",
            "<ul class=\"navList\" title=\"Navigation\">",
            "<li><a href=\"overview-summary.html\">Overview</a></li>",
            "<li>Package</li>",
            "<li>Class</li>",
            "<li><a href=\"overview-tree.html\">Tree</a></li>",
            "<li><a href=\"deprecated-list.html\">Deprecated</a></li>",
            "<li><a href=\"index-all.html\">Index</a></li>",
            "<li><a href=\"help-doc.html\">Help</a></li>",
            "</ul>",
            "<div class=\"aboutLanguage\"><em>FOOTER</em></div>",
            "</div>",
            "<div class=\"subNav\">",
            "<ul class=\"navList\">",
            "<li>Prev</li>",
            "<li>Next</li>",
            "</ul>",
            "<ul class=\"navList\">",
            "<li><a href=\"index.html?serialized-form.html\" target=\"_top\">Frames</a></li>",
            "<li><a href=\"serialized-form.html\" target=\"_top\">No Frames</a></li>",
            "</ul>",
            "<ul class=\"navList\" id=\"allclasses_navbar_bottom\">",
            "<li><a href=\"allclasses-noframe.html\">All Classes</a></li>",
            "</ul>",
            "<div>",
            "<script type=\"text/javascript\"><!--",
            "  allClassesLink = document.getElementById(\"allclasses_navbar_bottom\");",
            "  if(window==top) {",
            "    allClassesLink.style.display = \"block\";",
            "  }",
            "  else {",
            "    allClassesLink.style.display = \"none\";",
            "  }",
            "  //-->",
            "</script>",
            "</div>",
            "<a name=\"skip-navbar_bottom\">",
            "<!--   -->",
            "</a></div>",
            "<!-- ======== END OF BOTTOM NAVBAR ======= -->",
            "<p class=\"legalCopy\"><small>BOTTOM</small></p>",
            "</body>",
            "</html>"
        );
    }
}
