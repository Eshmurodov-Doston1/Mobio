package uz.idea.mobio.utils.containerui

import androidx.lifecycle.LiveData
import uz.idea.mobio.database.daoAndEntity.error.ErrorEntity

interface UiController {
    fun bottomBarView(isVisible:Boolean)
    fun errorDialog(errorCode:Int?,liveError:LiveData<List<ErrorEntity>>?,onClick:(clickType:Int)->Unit)
}