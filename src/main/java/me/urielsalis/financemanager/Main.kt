package me.urielsalis.financemanager

import com.googlecode.lanterna.gui2.*
import me.urielsalis.financemanager.readers.LastMovementsReader

fun main(args: Array<String>) {
    val data = LastMovementsReader().readPDF("test.pdf")
    SceneManager.init()
    loginWindow().addTo("Login")
    mainWindow().addTo("Main")
    SceneManager.switch("Login")
    SceneManager.waitWindow("Login")
}

fun mainWindow(): Window {
    val window = BasicWindow("Main")
    val panel = Panel(GridLayout(4))

    window.component = panel
    return window
}

fun loginWindow(): Window {
    val window = BasicWindow("Login")
    val panel = Panel(GridLayout(2))
    Label("Username").addTo(panel)
    val username = TextBox().addTo(panel)
    Label("Password").addTo(panel)
    val password = TextBox().addTo(panel)
    Button("Login", Runnable {
        run {
            if(username.text == "admin") {
                window.switchTo("Main")
            }
        }
    }).addTo(panel)
    window.component = panel
    return window
}

fun Window.switchTo(name: String) {
    SceneManager.switch(name)
}

fun Window.addTo(name: String) {
    SceneManager.addWindow(name, this)
}

fun Window.removeFrom(name: String) {
    SceneManager.removeWindow(name)
}

fun Window.show(name: String) {
    SceneManager.showWindow(name)
}

fun Window.hide(name: String) {
    SceneManager.hideWindow(name)
}

