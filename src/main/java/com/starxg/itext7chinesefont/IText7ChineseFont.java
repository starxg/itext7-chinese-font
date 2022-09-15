package com.starxg.itext7chinesefont;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.media.MediaType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

public class IText7ChineseFont {
    public static void main(String[] args) throws Exception {

        final File fontDir = new File(SystemUtils.getJavaIoTmpDir(), "IText7ChineseFont");
        if (!fontDir.exists() && !fontDir.mkdirs()) {
            throw new IllegalStateException();
        }

        final InputStream is = Objects.requireNonNull(IText7ChineseFont.class.getResourceAsStream("/Template.html"));
        final InputStream fontLight = Objects.requireNonNull(IText7ChineseFont.class.getResourceAsStream("/fonts/AlibabaPuHuiTi-2-45-Light.ttf"));
        final InputStream fontBold = Objects.requireNonNull(IText7ChineseFont.class.getResourceAsStream("/fonts/AlibabaPuHuiTi-2-85-Bold.ttf"));

        IOUtils.copy(fontLight, Files.newOutputStream(new File(fontDir, "AlibabaPuHuiTi-2-45-Light.ttf").toPath()));
        IOUtils.copy(fontBold, Files.newOutputStream(new File(fontDir, "AlibabaPuHuiTi-2-85-Bold.ttf").toPath()));

        final File file = File.createTempFile("itext-chinese-font-", ".pdf");

        final ConverterProperties properties = new ConverterProperties();
        final FontProvider fontProvider = new FontProvider();
        fontProvider.addDirectory(fontDir.getAbsolutePath());
        properties.setFontProvider(fontProvider);
        properties.setMediaDeviceDescription(new MediaDeviceDescription(MediaType.PRINT));

        try (final OutputStream os = Files.newOutputStream(file.toPath());
             final PdfWriter pdfWriter = new PdfWriter(os);
             final PdfDocument pdfDocument = new PdfDocument(pdfWriter)) {
            try (final Document doc = HtmlConverter.convertToDocument(IOUtils.toString(is, StandardCharsets.UTF_8), pdfDocument, properties)) {
                doc.add(new AreaBreak());
            }
        }

        System.out.println(file.getAbsolutePath());

        FileUtils.deleteQuietly(fontDir);
    }
}
