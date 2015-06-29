
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
class SerializedFormHtml extends AbstractClassFrameHtml implements GlobalDocument {

    @Override public void
    render(Options options, SortedSet<PackageDoc> allPackages, SortedSet<ClassDoc> allClassesAndInterfaces, RootDoc rootDoc) {

        super.rClassFrameHtml(
            "Serialized Form",         // title
            options,                   // options
            "stylesheet.css",          // stylesheetLink
            new String[] {             // nav1
                "Overview",   "overview-summary.html",
                "Package",    AbstractClassFrameHtml.DISABLED,
                "Use",        AbstractClassFrameHtml.DISABLED,
                "Class",      AbstractClassFrameHtml.DISABLED,
                "Tree",       "overview-tree.html",
                "Deprecated", "deprecated-list.html",
                "Index",      "index-all.html",
                "Help",       "help-doc.html",
            },
            new String[] {             // nav2
                "Prev", AbstractClassFrameHtml.DISABLED,
                "Next", AbstractClassFrameHtml.DISABLED,
            },
            new String[] {             // nav3
                "Frames",    "index.html?serialized-form.html",
                "No Frames", "serialized-form.html",
            },
            new String[] {             // nav4
                "All Classes", "allclasses-noframe.html",
            },
            null,                      // nav5
            null,                      // nav6
            new Runnable() {           // renderBody
                @Override public void run() { SerializedFormHtml.this.rBody(); }
            }
        );
    }

    private void
    rBody() {

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
            "</div>"
        );
    }
}
