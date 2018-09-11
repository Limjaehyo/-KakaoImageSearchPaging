package com.example.limjaehyo.lezhinimageexample.model.datasource


class NetworkState(status: Status) {

    enum class Status {
        RUNNING,
        SUCCESS,
        FAILED
    }
    var status: Status = Status.SUCCESS
    var msg: String = ""

    constructor(status: Status, msg: String) : this(status) {
        this.status = status
        this.msg = msg
    }

    companion object STATE {
        var LOADED = NetworkState(Status.SUCCESS)
        var LOADING = NetworkState(Status.RUNNING)
        var FAILED = NetworkState(Status.FAILED)
    }


    fun error(message: String?): NetworkState {
        return NetworkState(Status.FAILED, message ?: "unknown error")
    }


}