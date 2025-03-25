package com.marcusdoucette.petfoodtracker.DefaultView

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marcusdoucette.petfoodtracker.ui.theme.PetFoodTrackerTheme

@Composable
fun DefaultView(modifier: Modifier = Modifier) {
    val vm = viewModel<DefaultViewModel>()
    val state by vm.state.collectAsStateWithLifecycle()
    AnimatedDefaultView(
        vm::ActionHandler,
        state,
        vm.prev_state,
        modifier = modifier
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedDefaultView(
    action: (DefaultAction) -> Unit,
    state: DefaultState,
    prevState: DefaultState,
    modifier: Modifier = Modifier
) {
    val animating = state.animating
    val offset by animateFloatAsState(
        targetValue = if (animating) 1f else 0f,
        animationSpec = tween(if (animating) DefaultViewModel.AnimationTime.toInt() else 0)
    )

    val dir =
        LocalConfiguration.current.screenWidthDp * if (state.headerText < prevState.headerText) 1 else -1

    if (animating) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationX = (offset * dir).dp.toPx()
                }
        ) {
            DumbDefaultView(action, prevState, modifier)
        }
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationX = ((offset * dir) - dir).dp.toPx()
                }
        ) {
            DumbDefaultView(action, state, modifier)
        }
    } else {
        DumbDefaultView(action, state, modifier)
    }
}

@Composable
fun DumbDefaultView(
    action: (DefaultAction) -> Unit,
    state: DefaultState,
    modifier: Modifier = Modifier
) {
    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(innerPadding)
        ) {
            Text(
                state.headerText,
                color=Color.Black,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider()
            Content(action, state, modifier = Modifier.weight(1f))
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()

            ) {
                Button(onClick = { action(DefaultAction.LeftScrollButton) }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "back"
                    )
                }
                Button(onClick = { action(DefaultAction.RightScrollButton) }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "next"
                    )
                }
            }
        }
    }
}

@Composable
fun Content(action: (DefaultAction) -> Unit, state: DefaultState, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        item {
            TopRow()
        }
        item {
            ContentRow("Sun", action, state, 0)
        }
        item {
            ContentRow("Mon", action, state, 1)
        }
        item {
            ContentRow("Tue", action, state, 2)
        }
        item {
            ContentRow("Wed", action, state, 3)
        }
        item {
            ContentRow("Thu", action, state, 4)
        }
        item {
            ContentRow("Fri", action, state, 5)
        }
        item {
            ContentRow("Sat", action, state, 6)
        }

    }
}

@Composable
fun TopRow(modifier: Modifier = Modifier) {
    val wid = LocalConfiguration.current.screenWidthDp.dp / 5
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.fillMaxWidth(0.2f))
        Text(
            "AM",
            textAlign = TextAlign.Center,
            color = Color.Black,
            modifier = Modifier.width(wid)
        )
        Spacer(modifier = Modifier.width(wid))
        Text(
            "PM",
            textAlign = TextAlign.Center,
            color = Color.Black,
            modifier = Modifier.width(wid)
        )
        Spacer(modifier = Modifier.width(wid))
    }
}

@Composable
fun ContentRow(
    name: String,
    action: (DefaultAction) -> Unit,
    state: DefaultState,
    daynum: Int,
    modifier: Modifier = Modifier
) {
    val wid = LocalConfiguration.current.screenWidthDp.dp / 5
    Row(
        modifier = modifier.height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var textMod = Modifier.width(wid)
        if (state.today_num == daynum) {
            textMod = textMod.background(Color.Green)
        }
        Text(
            name,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = textMod
        )
        Switch(
            state.bools[daynum * 2],
            { action(DefaultAction.SwitchButton(daynum * 2)) },
            modifier = Modifier.width(wid)
        )
        Spacer(modifier = Modifier.width(wid))
        Switch(
            state.bools[daynum * 2 + 1],
            { action(DefaultAction.SwitchButton(daynum * 2 + 1)) },
            modifier = Modifier.width(wid)
        )
        Spacer(modifier = Modifier.width(wid))
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    PetFoodTrackerTheme {
        AnimatedDefaultView(
            action = {},
            state = DefaultState(
                "State",
                bools = listOf(
                    false, true,
                    true, false,
                    false, true,
                    true, false,
                    false, true,
                    true, false,
                    false, true
                ),
                animating = true
            ),
            prevState = DefaultState(
                "PrevState",
                bools = listOf(
                    false, true,
                    true, false,
                    false, true,
                    true, false,
                    false, true,
                    true, false,
                    false, true
                )
            )
        )
    }
}
