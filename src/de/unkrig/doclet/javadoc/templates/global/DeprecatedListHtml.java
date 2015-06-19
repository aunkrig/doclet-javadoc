
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

import de.unkrig.notemplate.javadocish.Options;
import de.unkrig.notemplate.javadocish.templates.AbstractClassFrameHtml;

public
class DeprecatedListHtml extends AbstractClassFrameHtml implements GlobalDocument {

    private Options options;

    @Override public void
    render(Options options, SortedSet<PackageDoc> allPackages, SortedSet<ClassDoc> allClassesAndInterfaces, RootDoc rootDoc) {

        this.options = options;

        super.rClassFrameHtml(
            "Deprecated List",         // title
            options,                   // options
            "stylesheet.css",          // stylesheetLink
            new String[] {             // nav1
                "Overview",   "overview-summary.html",
                "Package",    AbstractClassFrameHtml.DISABLED,
                "Class",      AbstractClassFrameHtml.DISABLED,
                "Use",        AbstractClassFrameHtml.DISABLED,
                "Tree",       "overview-tree.html",
                "Deprecated", AbstractClassFrameHtml.HIGHLIT,
                "Index",      "index-all.html",
                "Help",       "help-doc.html",
            },
            new String[] {             // nav2
                "Prev", AbstractClassFrameHtml.DISABLED,
                "Next", AbstractClassFrameHtml.DISABLED,
            },
            new String[] {             // nav3
                "Frames",    "index.html?overview-tree.html",
                "No Frames", "overview-tree.html",
            },
            "allclasses-noframe.html", // allClassesLink
            null,                      // nav4
            null                       // nav5
        );
    }

    @Override
    protected void rClassFrameBody() {

        this.l(
            "<div class=\"header\">",
            "<h1 title=\"Deprecated API\" class=\"title\">Deprecated API</h1>",
            "<h2 title=\"Contents\">Contents</h2>",
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
            "<li class=\"navBarCell1Rev\">Deprecated</li>",
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
            "<li><a href=\"index.html?deprecated-list.html\" target=\"_top\">Frames</a></li>",
            "<li><a href=\"deprecated-list.html\" target=\"_top\">No Frames</a></li>",
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
