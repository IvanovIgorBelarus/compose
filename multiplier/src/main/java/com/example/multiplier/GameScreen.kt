package com.example.multiplier

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.multiplier.data.GameItem
import com.example.multiplier.ui.theme.Dark_blue
import kotlinx.coroutines.launch

@Composable
fun GameScreen(
    itemsList: List<GameItem>,
    dip: Float
) {
    //states for result animation
    var isWinAnimation by remember { mutableStateOf(false) }
    var positionText by remember { mutableStateOf(Offset.Zero) }
    var winCoef by remember { mutableStateOf("") }

    if (isWinAnimation) {
        WinView(positionText = positionText, dip = dip, winCoef = winCoef)
    } else {
        GridAdapter(itemsList = itemsList, isWinAnimation = isWinAnimation) { position, selectedWinCoef ->
            isWinAnimation = true
            positionText = position
            winCoef = selectedWinCoef
        }
    }
}

@Composable
fun GridAdapter(
    itemsList: List<GameItem>,
    isWinAnimation: Boolean,
    winItemClickListener: (Offset, String) -> Unit
) {
    val configuration = LocalConfiguration.current
    //calculate item size
    val itemWidth = configuration.screenWidthDp.dp / 4
    val itemHeight = configuration.screenHeightDp.dp / 6
    val itemSize = if (itemHeight > itemWidth) itemWidth else itemHeight

    LazyVerticalGrid(
        columns = GridCells.Fixed(4)
    ) {
        items(itemsList) { item ->
            GridItem(modifier = Modifier.size(itemSize), item, isWinAnimation) { position, selectedWinCoef ->
                winItemClickListener(position, selectedWinCoef)
            }
        }
    }
}

@Composable
fun GridItem(
    modifier: Modifier = Modifier,
    item: GameItem,
    isWinAnimation: Boolean,
    onWinItemClick: (Offset, String) -> Unit
) {
    var isItemOpen by remember { mutableStateOf(item.isOpen) }
    val image = ImageVector.vectorResource(id = R.drawable.ic_icon_gift)
    val alpha by animateFloatAsState(
        targetValue = if (isItemOpen) {
            0f
        } else {
            1f
        }, animationSpec = spring(stiffness = 2f)
    )

    var positionText by remember { mutableStateOf(Offset.Zero) }
    Box(
        modifier = modifier
            .clickable {
                isItemOpen = true
                if (item.isWinItem) onWinItemClick(positionText, item.winValue)
            }
            .padding(2.dp)
    ) {
        //start text
        AnimatedVisibility(visible = !isWinAnimation, exit = fadeOut()) {
            Column(modifier = modifier, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (item.isWinItem) {
                        item.winValue
                    } else {
                        item.emptyValueText
                    },
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Dark_blue,
                    modifier = Modifier
                        .alpha(alpha)
                        .onGloballyPositioned {
                            positionText = it.positionInRoot()
                        }
                )
            }
        }
        //animate gift image visibility
        AnimatedVisibility(
            visible = !isItemOpen && !isWinAnimation,
            exit = fadeOut(animationSpec = spring(stiffness = 20f))
        ) {
            Image(
                modifier = modifier,
                imageVector = image,
                contentDescription = null
            )
        }
        //show only winCoefs
        AnimatedVisibility(visible = isWinAnimation, enter = fadeIn(animationSpec = spring(stiffness = 20f))) {
            Column(modifier = modifier, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = item.winValue,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Dark_blue
                )
            }
        }
    }
}

@Composable
fun WinView(
    positionText: Offset,
    dip: Float,
    winCoef: String
) {
    val configuration = LocalConfiguration.current
    val centerX = configuration.screenWidthDp / 2
    val centerY = configuration.screenHeightDp / 2

    val startX = positionText.x.div(dip)
    val startY = positionText.y.div(dip)

    val winTextSize = remember { Animatable(1f) }

    val x = remember { Animatable(startX) }
    val y = remember { Animatable(startY) }

    LaunchedEffect(x, y) {
        launch { x.animateTo(centerX.toFloat(), animationSpec = tween(1000)) }
        launch { y.animateTo(centerY.toFloat(), animationSpec = tween(1000)) }
        launch { winTextSize.animateTo(8f, animationSpec = tween(1000)) }
    }
    Column {
        Text(
            text = winCoef,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Dark_blue,
            modifier = Modifier
                .offset(x.value.dp, y.value.dp)
                .scale(winTextSize.value)
        )
    }
}

val items = listOf(
    GameItem(1, true, "1.5", ""),
    GameItem(2, false, "", "ищи дальше!"),
    GameItem(3, false, "", "жми ещё!"),
    GameItem(4, false, "", "не сдавайся!"),
    GameItem(5, false, "", "ты сможешь!"),
    GameItem(6, false, "", "почти угадал!"),

    GameItem(7, true, "1", ""),
    GameItem(8, false, "", "ищи дальше!"),
    GameItem(9, false, "", "жми ещё!"),
    GameItem(10, false, "", "не сдавайся!"),
    GameItem(11, false, "", "ты сможешь!"),
    GameItem(12, false, "", "почти угадал!"),

    GameItem(13, true, "2", ""),
    GameItem(14, false, "", "ищи дальше!"),
    GameItem(15, false, "", "жми ещё!"),
    GameItem(16, false, "", "не сдавайся!"),
    GameItem(17, false, "", "ты сможешь!"),
    GameItem(18, false, "", "почти угадал!"),

    GameItem(19, true, "1.5", ""),
    GameItem(20, false, "", "ищи дальше!"),
    GameItem(21, false, "", "жми ещё!"),
    GameItem(22, false, "", "не сдавайся!"),
    GameItem(23, false, "", "ты сможешь!"),
    GameItem(24, false, "", "почти угадал!"),
)