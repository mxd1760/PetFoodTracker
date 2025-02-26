package com.marcusdoucette.petfoodtracker.DefaultView

import android.provider.ContactsContract.Data
import androidx.lifecycle.ViewModel
import com.marcusdoucette.petfoodtracker.Data.DataManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class DefaultState(
    val headerText:String,
    val bools:List<Boolean> = listOf(),
    val today_num:Int = -1
)

sealed interface DefaultAction{
    data class SwitchButton(val id:Int): DefaultAction
    data object LeftScrollButton:DefaultAction
    data object RightScrollButton:DefaultAction
}

class DefaultViewModel: ViewModel() {

    val today = DataManager.getCurrentDate()



    var current_week_offset = 0
    var current_week_data = DataManager.GetWeek(today)

    val day_of_week = today.compareTo(current_week_data.startDate)


    private val _state = MutableStateFlow(DefaultState(getHeader(),current_week_data.booleans,day_of_week))
    val state = _state.asStateFlow()

    fun ActionHandler(action:DefaultAction){
        when(action){
            DefaultAction.LeftScrollButton -> {
                current_week_offset -= 1
                current_week_data = DataManager.GetWeek(today,current_week_offset)
                val header = getHeader()
                val today_num = if (current_week_offset==0) day_of_week else -1
                _state.update{
                    DefaultState(header,current_week_data.booleans,today_num)
                }
            }
            DefaultAction.RightScrollButton -> {
                current_week_offset += 1
                current_week_data = DataManager.GetWeek(today,current_week_offset)
                val header = getHeader()
                val today_num = if (current_week_offset==0) day_of_week else -1
                _state.update{
                    DefaultState(header,current_week_data.booleans,today_num)
                }
            }
            is DefaultAction.SwitchButton -> {
                current_week_data = current_week_data.copy(
                    booleans = current_week_data.booleans.mapIndexed{index,bool->
                        if (index==action.id){
                            !bool
                        }else{
                            bool
                        }
                    }
                )
                _state.update{old->
                    old.copy(
                        bools = current_week_data.booleans
                    )
                }
                DataManager.PostWeek(current_week_data)
            }
        }

    }

    fun getHeader():String{
        return current_week_data.startDate.toString()+ " to " + current_week_data.endDate.toString()
    }
}