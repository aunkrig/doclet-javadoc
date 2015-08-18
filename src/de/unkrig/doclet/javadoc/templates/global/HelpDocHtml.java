
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
import de.unkrig.notemplate.javadocish.templates.AbstractRightFrameHtml;

public
class HelpDocHtml extends AbstractRightFrameHtml implements GlobalDocument {

    @Override public void
    render(Options options, SortedSet<PackageDoc> allPackages, SortedSet<ClassDoc> allClassesAndInterfaces, RootDoc rootDoc) {

        super.rRightFrameHtml(
            "API Help",                        // windowTitle
            options,                           // options
            new String[] { "stylesheet.css" }, // stylesheetLinks
            new String[] {                     // nav1
                "Overview",   "overview-summary.html",
                "Package",    AbstractRightFrameHtml.DISABLED,
                "Class",      AbstractRightFrameHtml.DISABLED,
                "Use",        AbstractRightFrameHtml.DISABLED,
                "Tree",       "overview-tree.html",
                "Deprecated", "deprecated-list.html",
                "Index",      "index-all.html",
                "Help",       AbstractRightFrameHtml.HIGHLIT,
            },
            new String[] {                     // nav2
                "Prev",
                "Next",
            },
            new String[] {                     // nav3
                "Frames",    "index.html?help-doc.html",
                "No Frames", "help-doc.html",
            },
            new String[] {                     // nav4
                "All Classes", "allclasses-noframe.html",
            },
            null,                              // nav5
            null,                              // nav6
            () -> {                            // renderBody
                HelpDocHtml.this.rBody();
            }
        );
    }

    private void
    rBody() {

        this.l(
            "<div class=\"header\">",
            "<h1 class=\"title\">How This API Document Is Organized</h1>",
            "<div class=\"subTitle\">This API (Application Programming Interface) document has pages corresponding to the items in the navigation bar, described as follows.</div>",
            "</div>",
            "<div class=\"contentContainer\">",
            "<ul class=\"blockList\">",
            "<li class=\"blockList\">",
            "<h2>Overview</h2>",
            "<p>The <a href=\"overview-summary.html\">Overview</a> page is the front page of this API document and provides a list of all packages with a summary for each.  This page can also contain an overall description of the set of packages.</p>",
            "</li>",
            "<li class=\"blockList\">",
            "<h2>Package</h2>",
            "<p>Each package has a page that contains a list of its classes and interfaces, with a summary for each. This page can contain six categories:</p>",
            "<ul>",
            "<li>Interfaces (italic)</li>",
            "<li>Classes</li>",
            "<li>Enums</li>",
            "<li>Exceptions</li>",
            "<li>Errors</li>",
            "<li>Annotation Types</li>",
            "</ul>",
            "</li>",
            "<li class=\"blockList\">",
            "<h2>Class/Interface</h2>",
            "<p>Each class, interface, nested class and nested interface has its own separate page. Each of these pages has three sections consisting of a class/interface description, summary tables, and detailed member descriptions:</p>",
            "<ul>",
            "<li>Class inheritance diagram</li>",
            "<li>Direct Subclasses</li>",
            "<li>All Known Subinterfaces</li>",
            "<li>All Known Implementing Classes</li>",
            "<li>Class/interface declaration</li>",
            "<li>Class/interface description</li>",
            "</ul>",
            "<ul>",
            "<li>Nested Class Summary</li>",
            "<li>Field Summary</li>",
            "<li>Constructor Summary</li>",
            "<li>Method Summary</li>",
            "</ul>",
            "<ul>",
            "<li>Field Detail</li>",
            "<li>Constructor Detail</li>",
            "<li>Method Detail</li>",
            "</ul>",
            "<p>Each summary entry contains the first sentence from the detailed description for that item. The summary entries are alphabetical, while the detailed descriptions are in the order they appear in the source code. This preserves the logical groupings established by the programmer.</p>",
            "</li>",
            "<li class=\"blockList\">",
            "<h2>Annotation Type</h2>",
            "<p>Each annotation type has its own separate page with the following sections:</p>",
            "<ul>",
            "<li>Annotation Type declaration</li>",
            "<li>Annotation Type description</li>",
            "<li>Required Element Summary</li>",
            "<li>Optional Element Summary</li>",
            "<li>Element Detail</li>",
            "</ul>",
            "</li>",
            "<li class=\"blockList\">",
            "<h2>Enum</h2>",
            "<p>Each enum has its own separate page with the following sections:</p>",
            "<ul>",
            "<li>Enum declaration</li>",
            "<li>Enum description</li>",
            "<li>Enum Constant Summary</li>",
            "<li>Enum Constant Detail</li>",
            "</ul>",
            "</li>",
            "<li class=\"blockList\">",
            "<h2>Tree (Class Hierarchy)</h2>",
            "<p>There is a <a href=\"overview-tree.html\">Class Hierarchy</a> page for all packages, plus a hierarchy for each package. Each hierarchy page contains a list of classes and a list of interfaces. The classes are organized by inheritance structure starting with <code>java.lang.Object</code>. The interfaces do not inherit from <code>java.lang.Object</code>.</p>",
            "<ul>",
            "<li>When viewing the Overview page, clicking on \"Tree\" displays the hierarchy for all packages.</li>",
            "<li>When viewing a particular package, class or interface page, clicking \"Tree\" displays the hierarchy for only that package.</li>",
            "</ul>",
            "</li>",
            "<li class=\"blockList\">",
            "<h2>Deprecated API</h2>",
            "<p>The <a href=\"deprecated-list.html\">Deprecated API</a> page lists all of the API that have been deprecated. A deprecated API is not recommended for use, generally due to improvements, and a replacement API is usually given. Deprecated APIs may be removed in future implementations.</p>",
            "</li>",
            "<li class=\"blockList\">",
            "<h2>Index</h2>",
            "<p>The <a href=\"index-all.html\">Index</a> contains an alphabetic list of all classes, interfaces, constructors, methods, and fields.</p>",
            "</li>",
            "<li class=\"blockList\">",
            "<h2>Prev/Next</h2>",
            "<p>These links take you to the next or previous class, interface, package, or related page.</p>",
            "</li>",
            "<li class=\"blockList\">",
            "<h2>Frames/No Frames</h2>",
            "<p>These links show and hide the HTML frames.  All pages are available with or without frames.</p>",
            "</li>",
            "<li class=\"blockList\">",
            "<h2>All Classes</h2>",
            "<p>The <a href=\"allclasses-noframe.html\">All Classes</a> link shows all classes and interfaces except non-static nested types.</p>",
            "</li>",
            "<li class=\"blockList\">",
            "<h2>Serialized Form</h2>",
            "<p>Each serializable or externalizable class has a description of its serialization fields and methods. This information is of interest to re-implementors, not to developers using the API. While there is no link in the navigation bar, you can get to this information by going to any serialized class and clicking \"Serialized Form\" in the \"See also\" section of the class description.</p>",
            "</li>",
            "<li class=\"blockList\">",
            "<h2>Constant Field Values</h2>",
            "<p>The <a href=\"constant-values.html\">Constant Field Values</a> page lists the static final fields and their values.</p>",
            "</li>",
            "</ul>",
            "<em>This help file applies to API documentation generated using the standard doclet.</em></div>"
        );
    }
}
