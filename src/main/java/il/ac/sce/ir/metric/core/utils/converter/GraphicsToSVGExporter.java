package il.ac.sce.ir.metric.core.utils.converter;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import java.awt.*;
import java.io.*;
import java.util.function.Consumer;

public class GraphicsToSVGExporter {

    private final File file;

    public GraphicsToSVGExporter(File file) {

        this.file = file;
    }

    public void export(Consumer<Graphics2D> graphics2DConsumer) {
        // Get a DOMImplementation and create an XML document
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);

        // Create an instance of the SVG Generator
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        graphics2DConsumer.accept(svgGenerator);

        // Write svg file
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            svgGenerator.stream(out, true /* use css */);
        } catch (IOException ioe) {
            throw new RuntimeException("Cannot write SVG file", ioe);
        }
    }
}
