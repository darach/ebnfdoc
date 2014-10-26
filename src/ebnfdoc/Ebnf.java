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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.antlr.v4.runtime.*;

public class Ebnf {
    public interface Visitor {
        public void visitHeader(final File srcFile, final EbnfParser.EbnfContext node) throws IOException;
        public void visitStatement(final EbnfParser.StatementContext node) throws IOException;
        public void visitRhs(final EbnfParser.RhsContext node) throws IOException;
        public void visitClause(final EbnfParser.ClauseContext node) throws IOException;
        public void visitLiteral(final EbnfParser.LiteralContext node) throws IOException;
    };

    private Ebnf() { }

    public static void main(String[] args) throws Exception {
        File docsDir = new File("docs");
        final List<File> srcs = new ArrayList<File>();

        if (args.length == 0) {
            usage();
        }

        for(int i = 0; i < args.length; i++) {
            if ("-h".equals(args[i])) {
                usage();
            }

            if ("-d".equals(args[i])) {
                i++;
                docsDir = new File(args[i]);
                if (!docsDir.exists()) {
                    docsDir.mkdirs();
                }
                i++;
            }
    
            final File candidate = new File(args[i]);
            if (candidate.isFile()) {
                srcs.add(candidate);
            } else {
                usage();
            }
        }

        final Properties options = new Properties();
        options.setProperty("destDir",docsDir.toString());

        final Visitor visitor = new EbnfStVisitor(options);

        for (File ebnfFile : srcs) {
            EbnfParser.EbnfContext ebnf = parse(ebnfFile);
            visitor.visitHeader(ebnfFile, ebnf);
        }
    }

    private static void usage() {
       System.out.printf("%s: -h\t This help\n", Ebnf.class.getName()); 
       System.out.printf("%s: [-d <dir>] <file ...> \t Document EBNF as Markdown\n", Ebnf.class.getName()); 
        System.exit(1);
    }

    public static EbnfParser.EbnfContext parse(File ebnfDoc)
        throws IOException {
        final FileInputStream fis = new FileInputStream(ebnfDoc);
        final ANTLRInputStream in = new ANTLRInputStream(fis);

        final EbnfLexer lex = new EbnfLexer(in);
        final CommonTokenStream ts = new CommonTokenStream(lex);

        final EbnfParser ast = new EbnfParser(ts);
        
        return ast.ebnf();
    }
}
