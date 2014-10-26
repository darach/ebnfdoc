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

import java.nio.ByteBuffer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.LinkedList;
import java.util.Properties;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

public class EbnfStVisitor implements Ebnf.Visitor {
    private final Properties options;
    private final File destDir;
    private final String tplFile;
    private final STGroup stGroup;
    private FileWriter fos = null;
    private int prodId = 0;

    public EbnfStVisitor(final Properties options) {
        this.options = options;
        this.destDir = new File(options == null ? "doc" : options.getProperty("destDir", "doc"));
        this.tplFile = options == null ? "ebnfdoc/md.stg" : options.getProperty("tplFile", "ebnfdoc/md.stg");
        stGroup = new STGroupFile(tplFile);
        if (!destDir.exists()) {
            if (!destDir.mkdir()) {
                throw new EbnfDocException("Cannot create destination doc directory " + destDir);
            }
        }
    };

    public void visitHeader(final File srcFile, final EbnfParser.EbnfContext node) 
        throws IOException {
        final String doc = doc(node.doc());
        
        fos = new FileWriter(destDir + "/" + srcFile.getName() + ".md");
      
        ST prologTpl = stGroup.getInstanceOf("prolog");
        fos.append(prologTpl.render());
 
        ST docTpl = stGroup.getInstanceOf("doc");
        docTpl.add("doc",doc);
        fos.append(docTpl.render());

        ST productionsTpl = stGroup.getInstanceOf("productions");
        fos.append(productionsTpl.render());

        for (EbnfParser.StatementContext child : node.statement()) {
            visitStatement(child);
        }

        ST epilogTpl = stGroup.getInstanceOf("epilog");
        fos.append(prologTpl.render());

        try {
            fos.close();
        } catch(IOException ignore) { }
    };

    public void visitStatement(final EbnfParser.StatementContext node) 
        throws IOException {
        final String doc = doc(node.doc());
        final String lhsId = node.lhs().Id().getText();
        ++prodId; // 1-based

        ST statementPrologTpl = stGroup.getInstanceOf("statementProlog");
        statementPrologTpl.add("num",""+prodId);
        statementPrologTpl.add("name",""+lhsId);
        fos.append(statementPrologTpl.render());
        visitRhs(node.rhs());
        ST semicolonTpl = stGroup.getInstanceOf("semicolon");
        fos.append(semicolonTpl.render());

        ST docTpl = stGroup.getInstanceOf("doc");
        docTpl.add("doc",doc);
        fos.append(docTpl.render());

        ST statementEpilogTpl = stGroup.getInstanceOf("statementEpilog");
        fos.append(statementEpilogTpl.render());
    }

    public void visitRhs(final EbnfParser.RhsContext node) 
        throws IOException {

        if (node.doc() != null) {
            final String doc = doc(node.doc());
            ST docTpl = stGroup.getInstanceOf("doc");
            docTpl.add("doc",doc);
            fos.append(docTpl.render());
        }

        visitClause(node.clause(0));

        for(int i = 1; i < node.clause().size(); i++) {
            EbnfParser.OpContext op =  node.op(i-1);
            if (op.PipePipe() != null) {
                ST alternativeTpl = stGroup.getInstanceOf("long_alternative");
                fos.append(alternativeTpl.render());
            } 
            if (op.Pipe() != null) {
                ST alternativeTpl = stGroup.getInstanceOf("short_alternative");
                fos.append(alternativeTpl.render());
            }

            if (op.Comma() != null) {
                ST concatenativeTpl = stGroup.getInstanceOf("concatenative");
                fos.append(concatenativeTpl.render());
            }

            visitClause(node.clause(i));
        }
    }

    public void visitClause(final EbnfParser.ClauseContext node) 
        throws IOException {
        final boolean hasBrace = node.LBrace() != null;
        final boolean hasSquigly = node.LSquigly() != null;
        final boolean hasCurly = node.LCurly() != null;
        final boolean hasQuestion = node.Question(0) != null;
        final boolean hasLiteral = node.literal() != null;

        if (hasBrace) {
            ST boTpl = stGroup.getInstanceOf("braceOpen");
            fos.append(boTpl.render());
            visitRhs(node.rhs());
            ST bcTpl = stGroup.getInstanceOf("braceClose");
            fos.append(bcTpl.render());
        }
        if (hasSquigly) {
            ST soTpl = stGroup.getInstanceOf("squiglyOpen");
            fos.append(soTpl.render());
            visitRhs(node.rhs());
            ST scTpl = stGroup.getInstanceOf("squiglyClose");
            fos.append(scTpl.render());
        }
        if (hasCurly) {
            ST coTpl = stGroup.getInstanceOf("curlyOpen");
            fos.append(coTpl.render());
            visitRhs(node.rhs());
            ST ccTpl = stGroup.getInstanceOf("curlyClose");
            fos.append(ccTpl.render());
        }
        if (hasQuestion) {
            ST qoTpl = stGroup.getInstanceOf("questionOpen");
            fos.append(qoTpl.render());
            ST identifierTpl = stGroup.getInstanceOf("identifier");
            identifierTpl.add("text",node.Id().getText());
            fos.append(identifierTpl.render());
            ST qcTpl = stGroup.getInstanceOf("questionClose");
            fos.append(qcTpl.render());
        }
        if (hasLiteral) {
           visitLiteral(node.literal());
        }
    }

    public void visitLiteral(EbnfParser.LiteralContext node) 
        throws IOException {
        if (node.Id() != null) {
            ST identifierTpl = stGroup.getInstanceOf("identifier");
            identifierTpl.add("text",node.Id().getText());
            fos.append(identifierTpl.render());
        }
        if (node.Terminal() != null) {
            ST terminalTpl = stGroup.getInstanceOf("terminal");
            terminalTpl.add("text",node.Terminal().getText());
            fos.append(node.Terminal().getText());
        }
    }

    private String doc(final EbnfParser.DocContext doc) throws IOException {
        final String docc = doc == null ? null : doc.DOC_COMMENT().getText();
        final String docx = docc == null ? null : docc.substring(3);
        return docx == null ? "" :  EbnfDocComment.process(stGroup, docx) + "\n\n";
    }

    private static void copyStreamToFile(InputStream source, File dest)
        throws IOException {
        ReadableByteChannel ic = null;
        FileChannel oc = null;
        final ByteBuffer bf = ByteBuffer.allocate(4096);

        try {
            ic = Channels.newChannel(source);
            oc = new FileOutputStream(dest).getChannel();

            for(;;) {
                if(ic.read(bf) == -1) break;
                bf.flip();
                oc.write(bf);
                bf.clear();
            }
        } finally {
            ic.close();
            oc.close();
        }
    }
}
