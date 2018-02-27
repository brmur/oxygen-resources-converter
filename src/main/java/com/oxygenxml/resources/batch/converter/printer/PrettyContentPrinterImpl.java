package com.oxygenxml.resources.batch.converter.printer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import com.oxygenxml.resources.batch.converter.doctype.DoctypeGetter;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

/**
 * Content pretty printer implementation.
 * 
 * @author Cosmin Duna
 *
 */
public class PrettyContentPrinterImpl implements ContentPrinter {
	
	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(PrettyContentPrinterImpl.class);
	
	/**
	 * Prettify the given content and write in given output file.
	 * 
	 * @param contentToPrint The content to print.
	 * @param transformerCreator A transformer creator.
	 * @param converterType The type of converter.
	 * @param outputFile The output file.
	 * @param styleSource The source XSL, or <code>null</code> 
	 * @throws TransformerException
	 */
		public void print(String contentToPrint, TransformerFactoryCreator transformerCreator, String converterType,
				File outputFile,  StreamSource styleSource)
				throws TransformerException {

		// create the transformer
		Transformer transformer = transformerCreator.createTransformer(styleSource);

		// set the output properties
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		if(!DoctypeGetter.getSystemDoctype(converterType).isEmpty()){
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, DoctypeGetter.getSystemDoctype(converterType));
		}
		if(!DoctypeGetter.getPublicDoctype(converterType).isEmpty()){
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, DoctypeGetter.getPublicDoctype(converterType));
		}
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		// get the input source
		InputSource inputSource = new InputSource(new StringReader(contentToPrint));

		try {
			// prettify and print
			transformer.transform(new SAXSource(inputSource), new StreamResult(outputFile));
		} catch (TransformerException e) {
			logger.debug(e.getMessage(), e);
			// Stop indenting and create the output file.
			SimpleContentPrinterImpl simpleContentPrinter = new SimpleContentPrinterImpl();
			simpleContentPrinter.print(contentToPrint, transformerCreator, converterType, outputFile, styleSource);
		}
	}


}
