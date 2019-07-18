
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

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

import de.unkrig.commons.doclet.Docs;
import de.unkrig.commons.util.collections.ElementWithContext;
import de.unkrig.notemplate.NoTemplate;
import de.unkrig.notemplate.javadocish.Options;
import de.unkrig.notemplate.javadocish.templates.AbstractBottomLeftFrameHtml;

/**
 * Renders the "Package Summary" page for the bottom left frame, "my/package/package-frame.html".
 */
public
class PackageDetailHtml extends AbstractBottomLeftFrameHtml implements PerPackageDocument {

    @Override public void
    render(String home, final ElementWithContext<PackageDoc> packagE, Options options, RootDoc rootDoc) {

        super.rBottomLeftFrameHtml(
            packagE.current().name(),                                                    // windowTitle
            options,                                                                     // options
            new String[] { home + "stylesheet.css", },                                   // styleSheetLinks
            packagE.current().name(),                                                    // heading
            home + packagE.current().name().replace('.', '/') + "/package-summary.html", // headingLink
            null,                                                                        // renderIndexHeader
            () -> {                                                                      // renderIndexContainer
                ClassDoc[] interfaces = packagE.current().interfaces();
                if (interfaces.length > 0) {
                    PackageDetailHtml.this.l(
"    <h2 title=\"Interfaces\">Interfaces</h2>",
"    <ul title=\"Interfaces\">"
                    );
                    Arrays.sort(interfaces, Docs.DOCS_BY_NAME_COMPARATOR);
                    for (ClassDoc i : interfaces) {
                        PackageDetailHtml.this.l(
"      <li><a href=\"" + i.name() + ".html\" title=\"interface in " + packagE.current().name() + "\" target=\"classFrame\"><i>" + i.name() + "</i></a></li>"
                        );
                    }
                    PackageDetailHtml.this.l(
"    </ul>"
                    );
                }

                ClassDoc[] ordinaryClasses = packagE.current().ordinaryClasses();
                if (ordinaryClasses.length > 0) {
                    PackageDetailHtml.this.l(
"    <h2 title=\"Classes\">Classes</h2>",
"    <ul title=\"Classes\">"
                    );
                    Arrays.sort(ordinaryClasses, Docs.DOCS_BY_NAME_COMPARATOR);
                    for (ClassDoc oc : ordinaryClasses) {
                        PackageDetailHtml.this.l(
"      <li><a href=\"" + NoTemplate.html(oc.name()) + ".html\" title=\"class in " + packagE.current().name() + "\" target=\"classFrame\">" + oc.name() + "</a></li>"
                        );
                    }
                    PackageDetailHtml.this.l(
"    </ul>"
                    );
                }
            }
        );
    }
}
