package de.unkrig.doclet.javadoc.templates.include;

import java.text.SimpleDateFormat;

import de.unkrig.doclet.javadoc.JavadocDoclet.Options;
import de.unkrig.notemplate.NoTemplate;

public class TopHtml extends NoTemplate {

    public void
    render(String title, Options options, String stylesheetLink) {
        this.l(
"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">",
"<!-- NewPage -->",
"<html lang=\"de\">",
"<head>"
        );
        if (!options.noTimestamp) {
            this.l(
"<!-- Generated by de.unkrig.doclet.javadoc" + (options.noTimestamp ? "" : " on " + options.generationDate) + " -->"
            );
        }
        this.l(
"<title>" + title + (options.windowTitle == null ? "" : " (" + options.windowTitle + ")") + "</title>"
        );
        if (!options.noTimestamp) {
            this.l(
"<meta name=\"date\" content=\"" + new SimpleDateFormat("yyyy-MM-dd").format(options.generationDate) + "\">"
            );
        }
        this.l(
"<link rel=\"stylesheet\" type=\"text/css\" href=\"" + stylesheetLink + "\" title=\"Style\">",
"</head>",
"<body>"
        );
    }
}
