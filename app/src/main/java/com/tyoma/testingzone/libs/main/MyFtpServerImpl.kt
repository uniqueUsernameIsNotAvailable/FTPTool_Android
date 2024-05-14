package com.tyoma.testingzone.libs.main

import com.tyoma.testingzone.libs.exceptions.MyFtpNoInitExc
import com.tyoma.testingzone.libs.user.MyFtpUser
import org.apache.ftpserver.FtpServer
import org.apache.ftpserver.FtpServerFactory
import org.apache.ftpserver.ftplet.Authority
import org.apache.ftpserver.ftplet.FtpException
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.usermanager.impl.BaseUser


internal class MyFtpServerImpl(users: List<MyFtpUser>, port: Int) : IMyFtpServer {
    private val ftpServer: FtpServer?
    private val lock = Any()
    private var isInit = false


    init {
        val serverFactory = FtpServerFactory()
        for (user in users) {
            val baseUser = BaseUser()
            baseUser.name = user.name
            baseUser.password = user.password
            baseUser.homeDirectory = user.sharedPath
            val authorities: MutableList<Authority> = ArrayList()
            authorities.add(user.permission.authority)
            baseUser.authorities = authorities
            try {
                serverFactory.userManager.save(baseUser)
            } catch (e: FtpException) {
                e.printStackTrace()
            }
        }

        val factory = ListenerFactory()
        factory.port = port
        serverFactory.addListener("default", factory.createListener())
        ftpServer = serverFactory.createServer()
        isInit = true
    }


    private fun checkInit() {
        synchronized(lock) {
            if (!isInit) {
                throw MyFtpNoInitExc("MyFTP server: no init or released")
            }
        }
    }


    private fun release() {
        synchronized(lock) {
            if (ftpServer != null && !ftpServer.isStopped) {
                ftpServer.stop()
            }
            isInit = false
        }
    }


    override fun start() {
        checkInit()
        try {
            ftpServer!!.start()
        } catch (e: FtpException) {
            e.printStackTrace()
        }
    }


    override fun stop() {
        checkInit()
        ftpServer!!.stop()
    }

    override fun isStopped(): Boolean {
        return ftpServer!!.isStopped
    }

    companion object {
        private const val TAG = "MyFtpServerImpl"
    }
}
