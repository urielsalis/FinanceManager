package me.urielsalis.financemanager.gui

import com.googlecode.lanterna.SGR
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.terminal.DefaultTerminalFactory

object SceneManager {
    val screen = DefaultTerminalFactory().createScreen()
    var gui: MultiWindowTextGUI? = null
    var mainWindow:Window? = null

    private fun createMainWindow(): Window {
        val panel = Panel(GridLayout(2))
        val selectLabel = Label("Select Window")
        selectLabel.addStyle(SGR.BOLD)
        val nameLabel = Label("Finance Manger v1.0")
        nameLabel.foregroundColor = TextColor.ANSI.BLACK
        selectLabel.addStyle(SGR.ITALIC)

        panel.addComponent(selectLabel)
        panel.addComponent(nameLabel)
        val window = BasicWindow("Main")
        window.component = panel
        return window
    }

    fun init() {
        gui = MultiWindowTextGUI(screen)
        screen.startScreen()
        mainWindow = createMainWindow()
    }

    fun close() {
        screen.stopScreen()
    }

    fun addWindow(name: String, window: Window) {
        val button = Button(name)
        button.addListener {
            gui!!.addWindowAndWait(window)
        }
        (mainWindow!!.component as Panel).addComponent(button)
        mainWindow!!.invalidate()
    }

    fun show() {
        gui!!.addWindowAndWait(mainWindow)
    }
}
