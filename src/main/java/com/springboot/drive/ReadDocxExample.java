package com.springboot.drive;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class ReadDocxExample {
    public static void main(String[] args) {
        String filePath = "C:\\Users\\MY DREAMS\\Downloads\\PBL3.docx";

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis)) {

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                System.out.println(paragraph.getText());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
