package com.siiri.lib_core.app.execption

import java.lang.RuntimeException

class ResponseException(val code: Int, override val message: String) : RuntimeException(message)