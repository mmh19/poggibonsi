package it.croccio.poggibonsi.model

class Match {

    var _id: String? = ""
    var status: String? = ""
    var isActive: Boolean? = null
        get() = status == "ACTIVE"
    var isPendingWithMe: Boolean? = null
        get() = status == "PENDING" && bluePlayer?._id == loggedUser._id
    var redPlayer: User? = null
    var bluePlayer: User? = null
    var redScore: Int = 0
    var blueScore: Int = 0

}