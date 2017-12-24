package me.urielsalis.financemanager

import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal

object SceneManager {
    val windows = mutableMapOf<String, Window>()
    var activeWindow: Window? = null
    var terminal: Terminal? = null
    var screen: Screen? = null
    var gui: MultiWindowTextGUI? = null
    fun init() {
        terminal = DefaultTerminalFactory().createTerminal()
        screen = TerminalScreen(terminal)
        gui = MultiWindowTextGUI(screen, DefaultWindowManager(), EmptySpace(TextColor.ANSI.BLUE))
        screen!!.startScreen()
        val initWindow = BasicWindow("Loading")
        val initPanel = Panel(GridLayout(2))
        Label("Loading...").addTo(initPanel)
        initWindow.component = initPanel
        addWindow("_init", initWindow)
        activeWindow = initWindow
        showWindow("_init")
    }

    fun addWindow(name: String, window: Window) {
        windows.put(name, window)
    }

    fun removeWindow(name: String) {
        windows.remove(name)
    }

    fun getWindow(name: String): Window? {
        return windows[name]
    }

    fun showWindow(name: String) {
        gui!!.addWindow(getWindow(name))
    }

    fun waitWindow(name: String) {
        getWindow(name)?.waitUntilClosed()
    }

    fun hideWindow(name: String) {
        gui!!.removeWindow(getWindow(name))
    }

    fun switch(name: String) {
        gui!!.removeWindow(activeWindow)
        activeWindow = getWindow(name)
        gui!!.addWindow(activeWindow)
    }
}