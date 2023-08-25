package me.tiangong
import java.awt.TrayIcon
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon.MessageType

object MessageUtils {
    public val systemTray = SystemTray.getSystemTray()
    public   val icon = Toolkit.getDefaultToolkit().createImage("notification_icon.png")
    public  val trayIcon = TrayIcon(icon, "通知").apply { isImageAutoSize = true }

    init {
        systemTray.add(trayIcon)
    }

    fun showMessage(title: String, content: String, messageType: MessageType = MessageType.INFO) {
        trayIcon.displayMessage(title, content, messageType)
    }
}
