
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
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

import de.unkrig.commons.doclet.Docs;
import de.unkrig.commons.lang.protocol.Producer;
import de.unkrig.commons.lang.protocol.ProducerUtil;
import de.unkrig.doclet.javadoc.templates.JavadocUtil;
import de.unkrig.notemplate.javadocish.Options;
import de.unkrig.notemplate.javadocish.templates.AbstractRightFrameHtml;

/**
 * Renders the "Constant Values" page, "./constant-values.html".
 */
public
class ConstantValuesHtml extends AbstractRightFrameHtml implements GlobalDocument {

    /**
     * Renders the "Constant Values" page.
     */
    @Override public void
    render(
        Options                     options,
        final SortedSet<PackageDoc> allPackages,
        SortedSet<ClassDoc>         allClassesAndInterfaces,
        RootDoc                     rootDoc
    ) {

        this.rRightFrameHtml(
            "Constant Field Values",           // windowTitle
            options,                           // options
            new String[] { "stylesheet.css" }, // stylesheetLinks
            new String[] {                     // nav1
                "Overview",   "overview-summary.html",
                "Package",    AbstractRightFrameHtml.DISABLED,
                "Class",      AbstractRightFrameHtml.DISABLED,
                "Tree",       "overview-tree.html",
                "Deprecated", "deprecated-list.html",
                "Index",      options.splitIndex ? "index-files/index-1.html" : "index-all.html",
                "Help",       "help-doc.html",
            },
            new String[] {                     // nav2
                "Prev",
                "Next",
            },
            new String[] {                     // nav3
                "Frames",    "index.html?constant-values.html",
                "No Frames", "constant-values.html",
            },
            new String[] {                     // nav4
                "All Classes", "allclasses-noframe.html",
            },
            null,                              // nav5
            null,                              // nav6
            () -> {                            // renderBody
                ConstantValuesHtml.this.rBody(allPackages);
            }
        );
    }

    private void
    rBody(SortedSet<PackageDoc> allPackages) {

        this.l(
"    <div class=\"header\">",
"      <h1 title=\"Constant Field Values\" class=\"title\">Constant Field Values</h1>",
"      <h2 title=\"Contents\">Contents</h2>",
"      <ul>"
        );
        List<PackageDoc> ps = new ArrayList<PackageDoc>(allPackages);
        Collections.sort(ps, Docs.DOCS_BY_NAME_COMPARATOR);
        for (PackageDoc p : ps) {

            if (JavadocUtil.classesAndInterfacesWithConstants(p).iterator().hasNext()) {
                this.l(
"        <li><a href=\"#" + p.name() + "\">" + p.name() + ".*</a></li>"
                );
            }
        }
        this.l(
"      </ul>",
"    </div>"
        );

        this.p(
"    <div class=\"constantValuesContainer\">"
        );
        for (PackageDoc p : ps) {

            Iterable<ClassDoc> caiwcs = JavadocUtil.classesAndInterfacesWithConstants(p);
            if (caiwcs.iterator().hasNext()) {
                this.l("<a name=\"" + p.name() + "\" />");
                this.l(
"      <h2 title=\"" + p.name() + "\">" + p.name() + ".*</h2>",
"      <ul class=\"blockList\">"
                );
                for (ClassDoc coi : caiwcs) {
                    this.l(
"        <li class=\"blockList\">",
"          <table class=\"constantsSummary\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\" summary=\"Constant Field Values table, listing constant fields, and values\">",
"            <caption><span>" + p.name() + "." + JavadocUtil.toHtml(coi, null, "", 0) + "</span><span class=\"tabEnd\">&nbsp;</span></caption>",
"            <tr>",
"              <th class=\"colFirst\" scope=\"col\">Modifier and Type</th>",
"              <th scope=\"col\">Constant Field</th>",
"              <th class=\"colLast\" scope=\"col\">Value</th>",
"            </tr>",
"            <tbody>"
                    );
                    Producer<? extends String> cls = ProducerUtil.alternate("altColor", "rowColor");
                    for (FieldDoc c : JavadocUtil.constantsOf(coi)) {
                        this.l(
"              <tr class=\"" + cls.produce() + "\">",
"                <td class=\"colFirst\"><a name=\"" + coi.qualifiedName() + "." + c.name() + "\" /><code>" + c.modifiers().replaceAll(" ", "&nbsp;") + "&nbsp;" + c.type() + "</code></td>",
"                <td><code><a href=\"" + coi.qualifiedName().replace('.', '/') + ".html#" + c.name() + "\">" + c.name() + "</a></code></td>",
"                <td class=\"colLast\"><code>" + c.constantValue() + "</code></td>",
"              </tr>"
                        );
                    }
                    this.l(
"            </tbody>",
"          </table>",
"        </li>"
                    );
                }
                this.l(
"      </ul>"
                );
            }
        }
        this.l(
"    </div>"
        );
    }
}
