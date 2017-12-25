package me.urielsalis.financemanager

import com.googlecode.lanterna.gui2.*
import me.urielsalis.financemanager.gui.SceneManager
import me.urielsalis.financemanager.readers.LastMovementsReader
import me.urielsalis.financemanager.readers.PDFReader
import me.urielsalis.financemanager.readers.ReaderData
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder
import com.googlecode.lanterna.gui2.dialogs.FileDialogBuilder
import java.io.File
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder


val readers = mapOf<String, PDFReader<*>>(Pair("Ultimos movimientos", LastMovementsReader()))
val data = mutableMapOf<String, ReaderData>()

fun main(args: Array<String>) {
    //val data = LastMovementsReader().readPDF("test.pdf")
    SceneManager.init()
    SceneManager.addWindow("Import", importWindow())
    SceneManager.addWindow("View", viewWindow())
    SceneManager.show()
}

fun viewWindow(): Window {
    val window = BasicWindow("View")
    val panel = Panel(GridLayout(2))
    val button = Button("View Table")
    button.addListener {
        val builder = ActionListDialogBuilder()
                .setTitle("Select Table")
                .setDescription("Select one")

        for ((name, reader) in readers) {
            builder.addAction(name, {
                val data = data[name]
                if(data==null) {
                    MessageDialogBuilder()
                            .setTitle("Error")
                            .setText("You have to import data first!")
                            .addButton(MessageDialogButton.OK)
                            .build()
                            .showDialog(SceneManager.gui)
                } else {
                    val (table, extra) = data.generateTable()
                    table.addTo(panel)
                }
            })
        }

        builder.build()
                .showDialog(SceneManager.gui)


    }
    window.component  = panel
    return window
}

fun importWindow(): Window {
    val window = BasicWindow("Import")
    val panel = Panel(GridLayout(2))
    Label("File: ").addTo(panel)
    var file: File? = null
    panel.addComponent(Button("Select", Runnable {
        file = FileDialogBuilder()
                .setTitle("Open File")
                .setDescription("Choose a file")
                .setActionLabel("Open")
                .build()
                .showDialog(SceneManager.gui)
    }))


    val button = Button("Import")
    button.addListener {
        if (file == null) {
            MessageDialogBuilder()
                    .setTitle("Error")
                    .setText("You have to select a file first!")
                    .addButton(MessageDialogButton.OK)
                    .build()
                    .showDialog(SceneManager.gui)
        } else {
            val builder = ActionListDialogBuilder()
                    .setTitle("Select Importer")
                    .setDescription("Select one")

            for ((name, reader) in readers) {
                builder.addAction(name, {
                    val contents: ReaderData = reader.readPDF(file!!)!! as ReaderData
                    data[name] = contents
                })
            }

            builder.build()
                    .showDialog(SceneManager.gui)

        }
    }

    window.component = panel
    return window
}

