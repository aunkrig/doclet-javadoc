[#ftl]
[#setting locale="en_US"]
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd">
<!-- NewPage -->
<html lang="de">
<head>
<!-- Generated by de.unkrig.doclet.javadoc${noTimestamp???string("", " on " + generationDate?datetime)} -->
<title>Generated Documentation (Untitled)</title>
<script type="text/javascript">
    targetPage = "" + window.location.search;
    if (targetPage != "" && targetPage != "undefined")
        targetPage = targetPage.substring(1);
    if (targetPage.indexOf(":") != -1 || (targetPage != "" && !validURL(targetPage)))
        targetPage = "undefined";
    function validURL(url) {
        try {
            url = decodeURIComponent(url);
        }
        catch (error) {
            return false;
        }
        var pos = url.indexOf(".html");
        if (pos == -1 || pos != url.length - 5)
            return false;
        var allowNumber = false;
        var allowSep = false;
        var seenDot = false;
        for (var i = 0; i < url.length - 5; i++) {
            var ch = url.charAt(i);
            if ('a' <= ch && ch <= 'z' ||
                    'A' <= ch && ch <= 'Z' ||
                    ch == '$' ||
                    ch == '_' ||
                    ch.charCodeAt(0) > 127) {
                allowNumber = true;
                allowSep = true;
            } else if ('0' <= ch && ch <= '9'
                    || ch == '-') {
                if (!allowNumber)
                     return false;
            } else if (ch == '/' || ch == '.') {
                if (!allowSep)
                    return false;
                allowNumber = false;
                allowSep = false;
                if (ch == '.')
                     seenDot = true;
                if (ch == '/' && seenDot)
                     return false;
            } else {
                return false;
            }
        }
        return true;
    }
    function loadFrames() {
        if (targetPage != "" && targetPage != "undefined")
             top.classFrame.location = top.targetPage;
    }
</script>
</head>
<frameset cols="20%,80%" title="Documentation frame" onload="top.loadFrames()">
<frameset rows="30%,70%" title="Left frames" onload="top.loadFrames()">
<frame src="overview-frame.html" name="packageListFrame" title="All Packages">
<frame src="allclasses-frame.html" name="packageFrame" title="All classes and interfaces (except non-static nested types)">
</frameset>
<frame src="overview-summary.html" name="classFrame" title="Package, class and interface descriptions" scrolling="yes">
<noframes>
<noscript>
<div>JavaScript is disabled on your browser.</div>
</noscript>
<h2>Frame Alert</h2>
<p>This document is designed to be viewed using the frames feature. If you see this message, you are using a non-frame-capable web client. Link to <a href="overview-summary.html">Non-frame version</a>.</p>
</noframes>
</frameset>
</html>
