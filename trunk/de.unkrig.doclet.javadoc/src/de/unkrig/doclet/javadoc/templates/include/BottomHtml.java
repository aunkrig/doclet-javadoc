package de.unkrig.doclet.javadoc.templates.include;

import de.unkrig.doclet.javadoc.JavadocDoclet.Options;
import de.unkrig.notemplate.NoTemplate;

public class BottomHtml extends NoTemplate {

    public void
    render(Options options) {
        if (options.bottom != null) {
            this.l(
"<p class=\"legalCopy\"><small>" + options.bottom + "</small></p>"
            );
        }
        this.l(
"</body>",
"</html>"
        );
    }
}
