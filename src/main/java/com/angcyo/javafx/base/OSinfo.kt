package com.angcyo.javafx.base

class OSinfo {
    private var platform: EPlatform? = null

    companion object {
        private val OS = System.getProperty("os.name").toLowerCase()
        private val _instance = OSinfo()
        val isLinux: Boolean
            get() = OS.indexOf("linux") >= 0
        val isMacOS: Boolean
            get() = OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") < 0
        val isMacOSX: Boolean
            get() = OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") > 0
        val isWindows: Boolean
            get() = OS.indexOf("windows") >= 0
        val isOS2: Boolean
            get() = OS.indexOf("os/2") >= 0
        val isSolaris: Boolean
            get() = OS.indexOf("solaris") >= 0
        val isSunOS: Boolean
            get() = OS.indexOf("sunos") >= 0
        val isMPEiX: Boolean
            get() = OS.indexOf("mpe/ix") >= 0
        val isHPUX: Boolean
            get() = OS.indexOf("hp-ux") >= 0
        val isAix: Boolean
            get() = OS.indexOf("aix") >= 0
        val isOS390: Boolean
            get() = OS.indexOf("os/390") >= 0
        val isFreeBSD: Boolean
            get() = OS.indexOf("freebsd") >= 0
        val isIrix: Boolean
            get() = OS.indexOf("irix") >= 0
        val isDigitalUnix: Boolean
            get() = OS.indexOf("digital") >= 0 && OS.indexOf("unix") > 0
        val isNetWare: Boolean
            get() = OS.indexOf("netware") >= 0
        val isOSF1: Boolean
            get() = OS.indexOf("osf1") >= 0
        val isOpenVMS: Boolean
            get() = OS.indexOf("openvms") >= 0

        /**
         * 获取操作系统名字
         * @return 操作系统名
         */
        val oSname: EPlatform?
            get() {
                if (isAix) {
                    _instance.platform = EPlatform.AIX
                } else if (isDigitalUnix) {
                    _instance.platform = EPlatform.Digital_Unix
                } else if (isFreeBSD) {
                    _instance.platform = EPlatform.FreeBSD
                } else if (isHPUX) {
                    _instance.platform = EPlatform.HP_UX
                } else if (isIrix) {
                    _instance.platform = EPlatform.Irix
                } else if (isLinux) {
                    _instance.platform = EPlatform.Linux
                } else if (isMacOS) {
                    _instance.platform = EPlatform.Mac_OS
                } else if (isMacOSX) {
                    _instance.platform = EPlatform.Mac_OS_X
                } else if (isMPEiX) {
                    _instance.platform = EPlatform.MPEiX
                } else if (isNetWare) {
                    _instance.platform = EPlatform.NetWare_411
                } else if (isOpenVMS) {
                    _instance.platform = EPlatform.OpenVMS
                } else if (isOS2) {
                    _instance.platform = EPlatform.OS2
                } else if (isOS390) {
                    _instance.platform = EPlatform.OS390
                } else if (isOSF1) {
                    _instance.platform = EPlatform.OSF1
                } else if (isSolaris) {
                    _instance.platform = EPlatform.Solaris
                } else if (isSunOS) {
                    _instance.platform = EPlatform.SunOS
                } else if (isWindows) {
                    _instance.platform = EPlatform.Windows
                } else {
                    _instance.platform = EPlatform.Others
                }
                return _instance.platform
            }

        /**
         * @param args
         */
        @JvmStatic
        fun main(args: Array<String>) {
            println(oSname) // 获取系统类型
            println(isWindows) // 判断是否为windows系统 
        }
    }
}