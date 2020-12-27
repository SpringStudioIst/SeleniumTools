package com.angcyo.javafx.ui

import com.angcyo.javafx.base.getResource
import java.awt.*
import java.awt.event.ActionListener
import java.util.*
import javax.imageio.ImageIO


/**
 * 托盘操作类
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/26
 */
object Tray {
    fun addTray() {
        if (SystemTray.isSupported()) {
            // get the SystemTray instance
            val tray = SystemTray.getSystemTray()
            // load an image
            //val image = Toolkit.getDefaultToolkit().getImage("")
            val image = ImageIO.read(getResource("logo.png"))
            // create a action listener to listen for default action executed on the tray icon
            val listener = ActionListener {
                // execute default action of the application
                // ...
            }
            // create a popup menu
            val popup = PopupMenu()
            // create menu item for the default action
            val defaultItem = MenuItem("test")
            defaultItem.addActionListener(listener)
            popup.add(defaultItem)
            /// ... add other items
            // construct a TrayIcon
            val trayIcon = TrayIcon(image, "Tray Demo", popup)
            // set the TrayIcon properties
            trayIcon.addActionListener(listener)
            trayIcon.isImageAutoSize = true
            // ...
            // add the tray image
            try {
                tray.add(trayIcon)
            } catch (e: AWTException) {
                System.err.println(e)
            }
            // ...
        } else {
            // disable tray option in your application or
            // perform other actions
//           ...
        }
        // ...
        // some time later
        // the application state has changed - update the image
//        if (trayIcon != null) {
//            trayIcon.setImage(updatedImage);
//        }
    }

    /**获取[pos]在屏幕上安全的矩形*/
    fun getSafeScreenBounds(pos: Point): Rectangle? {
        val bounds = getScreenBoundsAt(pos)
        val insets = getScreenInsetsAt(pos)
        if (bounds != null && insets != null) {
            bounds.x += insets.left
            bounds.y += insets.top
            bounds.width -= insets.left + insets.right
            bounds.height -= insets.top + insets.bottom
        }
        return bounds
    }

    private fun getScreenInsetsAt(pos: Point): Insets? {
        val gd = getGraphicsDeviceAt(pos)
        return Toolkit.getDefaultToolkit().getScreenInsets(gd.defaultConfiguration)
    }

    private fun getScreenBoundsAt(pos: Point): Rectangle? {
        val gd = getGraphicsDeviceAt(pos)
        return gd.defaultConfiguration.bounds
    }

    private fun getGraphicsDeviceAt(pos: Point): GraphicsDevice {
        val device: GraphicsDevice
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val lstGDs = ge.screenDevices
        val lstDevices = ArrayList<GraphicsDevice>(lstGDs.size)
        for (gd in lstGDs) {
            val gc = gd.defaultConfiguration
            val screenBounds = gc.bounds
            if (screenBounds.contains(pos)) {
                lstDevices.add(gd)
            }
        }
        device = if (lstDevices.size > 0) {
            lstDevices[0]
        } else {
            ge.defaultScreenDevice
        }
        return device
    }
}