
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

import de.unkrig.notemplate.NoTemplate;
import de.unkrig.notemplate.javadocish.Options;

/**
 * Renders the frame set document, "index.html".
 */
public
class IndexHtml extends NoTemplate implements GlobalDocument {

    @Override public void
    render(Options options, SortedSet<PackageDoc> allPackages, SortedSet<ClassDoc> allClassesAndInterfaces, RootDoc rootDoc) {
        this.l(
"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">",
"<!-- NewPage -->",
"<html lang=\"de\">",
"<head>",
"<title>WINDOWTITLE</title>",
"<script type=\"text/javascript\">",
"    targetPage = \"\" + window.location.search;",
"    if (targetPage != \"\" && targetPage != \"undefined\")",
"        targetPage = targetPage.substring(1);",
"    if (targetPage.indexOf(\":\") != -1 || (targetPage != \"\" && !validURL(targetPage)))",
"        targetPage = \"undefined\";",
"    function validURL(url) {",
"        var pos = url.indexOf(\".html\");",
"        if (pos == -1 || pos != url.length - 5)",
"            return false;",
"        var allowNumber = false;",
"        var allowSep = false;",
"        var seenDot = false;",
"        for (var i = 0; i < url.length - 5; i++) {",
"            var ch = url.charAt(i);",
"            if ('a' <= ch && ch <= 'z' ||",
"                    'A' <= ch && ch <= 'Z' ||",
"                    ch == '$' ||",
"                    ch == '_') {",
"                allowNumber = true;",
"                allowSep = true;",
"            } else if ('0' <= ch && ch <= '9'",
"                    || ch == '-') {",
"                if (!allowNumber)",
"                     return false;",
"            } else if (ch == '/' || ch == '.') {",
"                if (!allowSep)",
"                    return false;",
"                allowNumber = false;",
"                allowSep = false;",
"                if (ch == '.')",
"                     seenDot = true;",
"                if (ch == '/' && seenDot)",
"                     return false;",
"            } else {",
"                return false;",
"            }",
"        }",
"        return true;",
"    }",
"    function loadFrames() {",
"        if (targetPage != \"\" && targetPage != \"undefined\")",
"             top.classFrame.location = top.targetPage;",
"    }",
"</script>",
"</head>",
"<frameset cols=\"20%,80%\" title=\"Documentation frame\" onload=\"top.loadFrames()\">",
"<frameset rows=\"30%,70%\" title=\"Left frames\" onload=\"top.loadFrames()\">",
"<frame src=\"overview-frame.html\" name=\"packageListFrame\" title=\"All Packages\">",
"<frame src=\"allclasses-frame.html\" name=\"packageFrame\" title=\"All classes and interfaces (except non-static nested types)\">",
"</frameset>",
"<frame src=\"overview-summary.html\" name=\"classFrame\" title=\"Package, class and interface descriptions\" scrolling=\"yes\">",
"<noframes>",
"<noscript>",
"<div>JavaScript is disabled on your browser.</div>",
"</noscript>",
"<h2>Frame Alert</h2>",
"<p>This document is designed to be viewed using the frames feature. If you see this message, you are using a non-frame-capable web client. Link to <a href=\"overview-summary.html\">Non-frame version</a>.</p>",
"</noframes>",
"</frameset>",
"</html>"
        );
    }
}
