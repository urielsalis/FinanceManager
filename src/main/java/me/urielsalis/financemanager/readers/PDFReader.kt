package me.urielsalis.financemanager.readers

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File
import java.util.logging.Logger

abstract class PDFReader<T> {
    val logger = Logger.getLogger(this.javaClass.name)
    private object readers {
        val pdfStripper = PDFTextStripper();
    }

    abstract protected fun writeData(data: PreProcessedData<T>): T
    abstract protected fun preProcessData(text: String): PreProcessedData<T>

    fun readPDF(file: String): T {
        readers.pdfStripper.sortByPosition = true
        logger.info("Loading $file")
        val document = PDDocument.load(File(file))
        logger.info("Loaded $file")
        val text = readers.pdfStripper.getText(document)
        logger.info("Reading data from $file: ")
        val returnValue = writeData(preProcessData(text))
        logger.info("Closing $file")
        document.close()
        return returnValue
    }

}

data class PreProcessedData<T>(var header: Map<String, String>, var data: List<String>)
