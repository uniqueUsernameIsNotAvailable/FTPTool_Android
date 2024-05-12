package com.tyoma.testingzone.libs.exceptions

//case: client/server not init
class MyFtpNoInitExc : IllegalStateException {
    constructor() : super()
    constructor(s: String?) : super(s)
}
