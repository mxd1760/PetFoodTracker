package com.marcusdoucette.petfoodtracker.DefaultView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcusdoucette.petfoodtracker.Data.DataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class DefaultState(
    val headerText:String,
    val bools:List<Boolean> = listOf(),
    val today_num:Int = -1,
    val prevHeaderText:String = headerText,
    val animating:Boolean=false
)

sealed interface DefaultAction{
    data class SwitchButton(val id:Int): DefaultAction
    data object LeftScrollButton:DefaultAction
    data object RightScrollButton:DefaultAction
}

class DefaultViewModel: ViewModel() {

    companion object {
        val AnimationTime = 500L
    }

    var updateAllowed = true
    val today = DataManager.getCurrentDate()

    var current_week_offset = 0
    var current_week_data = DataManager.GetWeek(today)

    val day_of_week = today.compareTo(current_week_data.startDate)

    private val _state = MutableStateFlow(DefaultState(getHeader(),current_week_data.booleans,day_of_week))
    val state = _state.asStateFlow()
    var prev_state = state.value

    fun ActionHandler(action:DefaultAction){
        when(action){
            DefaultAction.LeftScrollButton -> {
                if (updateAllowed) {
                    current_week_offset -= 1
                    current_week_data = DataManager.GetWeek(today, current_week_offset)
                    val header = getHeader()
                    val today_num = if (current_week_offset == 0) day_of_week else -1
                    prev_state = state.value
                    _state.update { old ->
                        DefaultState(header, current_week_data.booleans, today_num, old.headerText,true)
                    }
                    DisallowRapidUpdates()
                }
            }
            DefaultAction.RightScrollButton -> {
                if (updateAllowed) {
                    current_week_offset += 1
                    current_week_data = DataManager.GetWeek(today, current_week_offset)
                    val header = getHeader()
                    val today_num = if (current_week_offset == 0) day_of_week else -1
                    prev_state = state.value
                    _state.update { old ->
                        DefaultState(header, current_week_data.booleans, today_num, old.headerText,true)
                    }
                    DisallowRapidUpdates()
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

    fun DisallowRapidUpdates(){
        updateAllowed = false
        viewModelScope.launch(Dispatchers.IO){
            delay(AnimationTime + AnimationTime/10)
            updateAllowed = true
            _state.update{old->
                old.copy(animating=false)
            }
        }
    }


    fun getHeader():String{
        return current_week_data.startDate.toString()+ " to " + current_week_data.endDate.toString()
    }
}