package com.tyoma.testingzone.libs.main

import com.tyoma.testingzone.libs.user.MyFtpUser

class MyFtpServer private constructor(users: List<MyFtpUser>, port: Int) : IMyFtpServer {
    private val ftpServerImpl: IMyFtpServer = MyFtpServerImpl(users, port)

    override fun start() {
        ftpServerImpl.start()
    }

    override fun stop() {
        ftpServerImpl.stop()
    }

    override fun isStopped(): Boolean {
        return ftpServerImpl.isStopped()
    }

    class Builder {
        private val users: MutableList<MyFtpUser> = ArrayList()
        private var port = 0

        fun addUser(user: MyFtpUser): Builder {
            users.add(user)
            return this
        }

        fun setListenPort(port: Int): Builder {
            this.port = port
            return this
        }

        fun create(): MyFtpServer {
            return MyFtpServer(users, port)
        }
    }
}
