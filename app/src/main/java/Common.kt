import com.example.sinicxpress.activities.Model.RiderInfoModel
import java.lang.StringBuilder

object Common {
    fun buildWelcomeMessage(): String {
       return StringBuilder("Welcome, ")
           .append(currentUser!!.firstName)
           .append(" ")
           .append(currentUser!!.lastName)
           .toString()

    }

    val RIDERS_LOCATION_REFERENCE: String="RidersLocation"
    var currentUser: RiderInfoModel?=null
    val RIDER_INFO_REFERENCE: String = "RiderInfo"
}