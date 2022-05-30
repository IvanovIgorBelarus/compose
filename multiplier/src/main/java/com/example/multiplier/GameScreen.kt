package com.example.multiplier

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
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
import com.example.multiplier.extantions.noRippleClickable
import com.example.multiplier.ui.theme.Dark_blue
import kotlinx.coroutines.launch

@Composable
fun GameScreen(
    itemsList: List<GameItem>,
    dip: Float
) {
    //states for result animation
    var isWinAnimation by remember { mutableStateOf(false) }
    var positionText by remember { mutableStateOf(Offset.Zero) }  // position of win item
    var winCoef by remember { mutableStateOf("") }

    Crossfade(targetState = isWinAnimation, animationSpec = tween(durationMillis = 10, delayMillis = 1500)) {
        if (it) {
            WinView(positionText = positionText, dip = dip, winCoef = winCoef)
        } else {
            GridAdapter(itemsList = itemsList, isWinAnimation = isWinAnimation) { position, selectedWinCoef ->
                isWinAnimation = true
                positionText = position
                winCoef = selectedWinCoef
            }
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
    val image = ImageVector.vectorResource(id = R.drawable.ic_icon_gift)

    var isItemOpen by remember { mutableStateOf(isWinAnimation) }
    var positionText by remember { mutableStateOf(Offset.Zero) } // save win item position

    val textAlpha by animateFloatAsState( targetValue = if (isItemOpen) { 0f } else { 1f }, animationSpec = tween(durationMillis = 3000))//for text anim
    val imageScale by animateFloatAsState( targetValue = if (isWinAnimation || isItemOpen) { 0f } else { 1f }, animationSpec = tween(durationMillis = 500))//for image anim
    val imgAlpha by animateFloatAsState( targetValue = if (isWinAnimation || isItemOpen) { 0f } else { 1f }, animationSpec = tween(durationMillis = 500))//for image anim

    Box(
        modifier = modifier
            .noRippleClickable {
                isItemOpen = true
                if (item.isWinItem) onWinItemClick(positionText, item.winValue)
            }
            .padding(2.dp)
    ) {
        //start text
        if (!isWinAnimation) {
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
                        .alpha(textAlpha)
                        .onGloballyPositioned {
                            positionText = it.positionInRoot()
                        }
                )
            }
        }

        //animate gift image visibility
            Image(
                modifier = modifier
                    .scale(imageScale)
                    .alpha(imgAlpha),
                imageVector = image,
                contentDescription = null
            )

        //show only winCoefs
        AnimatedVisibility(visible = isWinAnimation, enter = fadeIn(animationSpec = tween(durationMillis = 300))) {
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
    //calculate window center
    val configuration = LocalConfiguration.current
    val centerX = configuration.screenWidthDp / 2
    val centerY = (configuration.screenHeightDp - 64) / 2
    //start x and y for winCoef animation
    val startX = positionText.x.div(dip)
    val startY = positionText.y.div(dip)
    //mutable size for winCoef animation
    val winTextSize = remember { Animatable(1f) }
    //mutable x and y for winCoef animation
    val x = remember { Animatable(startX) }
    val y = remember { Animatable(startY) }

    LaunchedEffect(x, y) {
        launch { x.animateTo(centerX.toFloat(), animationSpec = tween(durationMillis = 1500, delayMillis = 1500)) }
        launch { y.animateTo(centerY.toFloat(), animationSpec = tween(durationMillis = 1500, delayMillis = 1500)) }
        launch { winTextSize.animateTo(8f, animationSpec = tween(durationMillis = 1500, delayMillis = 1500)) }
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