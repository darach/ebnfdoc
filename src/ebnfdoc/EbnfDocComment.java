// Copyright (c) 2014 Darach Ennis < darach at gmail dot com >.
//
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the
// following conditions:  
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
// NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
// USE OR OTHER DEALINGS IN THE SOFTWARE.

package ebnfdoc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import org.antlr.v4.runtime.*;

import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

public class EbnfDocComment {
    private final List<String> parts = new ArrayList<String>();
    private final STGroup stGroup;

    public static void main(String args[])
        throws IOException {
        STGroup sg = new STGroupFile("ebnfdoc/md.stg");
        EbnfDocComment.process(sg,new String(readAllBytes(get(args[0]))));
    }

    public EbnfDocComment(
        final STGroup stGroup,
        final String docCommentText)
        throws IOException {
        this.stGroup = stGroup;

        ByteArrayInputStream bais = new ByteArrayInputStream(docCommentText.getBytes("UTF-8"));

        // Parse comment for doc tags
        EbnfDocParser.DocContext doc = parse(bais);

        // Process tags and text
        for (EbnfDocParser.LineContext p : doc.line()) {
            if (p.tag() != null) {
                final String tagLine = p.tag().TAG().getText();
                final String[] tagArgs = tagLine.split(" ");
                final String tagName = tagArgs[0].substring(1);
                ST tagTpl = null;
                if ((tagTpl = stGroup.getInstanceOf(tagName)) != null) {
                    if (tagTpl.getAttributes().containsKey("line")) {
                        final String[] t = Arrays.copyOfRange(tagArgs,1,tagArgs.length);
                        final String ta = URLEncoder.encode(join(" ",t));
                        tagTpl.add("line",ta);
                }
                for (int i = 1; i < tagArgs.length; i++) {
                    if (tagTpl.getAttributes().containsKey("arg" + i))
                        tagTpl.add("arg" + i, URLEncoder.encode(tagArgs[i]));
                    else
                        break;
                }
                    parts.add(tagTpl.render());
                } else {
                    throw new Error("Unknown tag: " + tagName + " " + p.tag());
                }
            }
            if (p.text() != null) {
                parts.add(p.text().TEXT().getText());
            }
        }
    }

    public static EbnfDocParser.DocContext parse(InputStream docCommentStream)
        throws IOException {
        final ANTLRInputStream in = new ANTLRInputStream(docCommentStream);
        final EbnfDocLexer lex = new EbnfDocLexer(in);
        final CommonTokenStream ts = new CommonTokenStream(lex);
        final EbnfDocParser ast = new EbnfDocParser(ts);
        return ast.doc();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (String s : parts) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static String process(final STGroup stGroup, final String docCommentText)
        throws IOException {
        return (new EbnfDocComment(stGroup, docCommentText)).toString();
    }

    private static String join(String delimiter, String[] sa) {
        StringBuilder sb = new StringBuilder();
        if (sa == null) return null;
        if (sa.length == 0) return null;
        for(int i = 0; i < sa.length-1; i++) {
            sb.append(sa[i]);
            sb.append(delimiter);
        }
        sb.append(sa[sa.length-1]);
        return sb.toString();
    }
}
