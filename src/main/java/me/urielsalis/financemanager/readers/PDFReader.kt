package me.urielsalis.financemanager.readers

import com.googlecode.lanterna.gui2.table.Table
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

    fun readPDF(file: File): T {
        readers.pdfStripper.sortByPosition = true
        val name = file.name
        logger.info("Loading $name")
        val document = PDDocument.load(file)
        logger.info("Loaded $name")
        val text = readers.pdfStripper.getText(document)
        logger.info("Reading data from $name: ")
        val returnValue = writeData(preProcessData(text))
        logger.info("Closing $name")
        document.close()
        return returnValue
    }

    fun readPDF(file: String): T {
        return readPDF(File(file))
    }

}
interface ReaderData {
    abstract fun generateTable(): TableData
    data class TableData(val table: Table<String>, val extra: Map<String, String>)
}

data class PreProcessedData<T>(var header: Map<String, String>, var data: List<String>)
