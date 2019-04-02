package com.kross.taxi_passenger.data.repository.server.pojo.response

import com.google.gson.annotations.Expose

class HelpList {

    @Expose
    var appeals: List<String>? = null
    @Expose
    var id: Long? = null
    @Expose
    var title: String? = null

}
