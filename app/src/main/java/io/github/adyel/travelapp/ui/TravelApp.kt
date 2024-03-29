/*
 * Copyright 2019 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.adyel.travelapp.ui

import androidx.annotation.DrawableRes
import androidx.compose.Composable
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.animation.Crossfade
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.material.surface.Surface
import androidx.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseAuth
import io.github.adyel.travelapp.R
import io.github.adyel.travelapp.ui.article.ArticleScreen
import io.github.adyel.travelapp.ui.home.HomeScreen
import io.github.adyel.travelapp.ui.interests.InterestsScreen

@Preview
@Composable
fun TravelApp() {

    val (drawerState, onDrawerStateChange) = +state { DrawerState.Closed }

    MaterialTheme(
        colors = lightThemeColors,
        typography = themeTypography
    ) {
        ModalDrawerLayout(
            drawerState = drawerState,
            onStateChange = onDrawerStateChange,
            gesturesEnabled = drawerState == DrawerState.Opened,
            drawerContent = {
                AppDrawer(
                    currentScreen = TravelAppStatus.currentScreen,
                    closeDrawer = { onDrawerStateChange(DrawerState.Closed) }
                )
            },
            bodyContent = { AppContent { onDrawerStateChange(DrawerState.Opened) } }
        )
    }
}

@Composable
private fun AppContent(openDrawer: () -> Unit) {
    Crossfade(TravelAppStatus.currentScreen) { screen ->
        Surface(color = +themeColor { background }) {
            when (screen) {
                is Screen.Home -> HomeScreen { openDrawer() }
                is Screen.Interests -> InterestsScreen { openDrawer() }
                is Screen.Article -> ArticleScreen(postId = screen.postId)
                is Screen.LogOut -> FirebaseAuth.getInstance().signOut()
            }
        }
    }
}

@Composable
private fun AppDrawer(
    currentScreen: Screen,
    closeDrawer: () -> Unit
) {
    Column(
        crossAxisSize = LayoutSize.Expand,
        mainAxisSize = LayoutSize.Expand
    ) {
        HeightSpacer(24.dp)
        Padding(16.dp) {
            Row {
                VectorImage(
                    id = R.drawable.ic_jetnews_logo,
                    tint = +themeColor { primary }
                )
                WidthSpacer(8.dp)
                VectorImage(R.drawable.ic_jetnews_wordmark)
            }
        }
        Divider(color = Color(0x14333333))
        DrawerButton(
            icon = R.drawable.ic_home,
            label = "Home",
            isSelected = currentScreen == Screen.Home
        ) {
            navigateTo(Screen.Home)
            closeDrawer()
        }

        DrawerButton(
            icon = R.drawable.ic_interests,
            label = "Interests",
            isSelected = currentScreen == Screen.Interests
        ) {
            navigateTo(Screen.Interests)
            closeDrawer()
        }

        DrawerButton(
            icon = R.drawable.ic_back,
            label = "Sign Out",
            isSelected = currentScreen == Screen.LogOut
        ) {
            navigateTo(Screen.LogOut)
            closeDrawer()
        }
    }
}

@Composable
private fun DrawerButton(
    @DrawableRes icon: Int,
    label: String,
    isSelected: Boolean,
    action: () -> Unit
) {
    val textIconColor = if (isSelected) {
        +themeColor { primary }
    } else {
        (+themeColor { onSurface }).copy(alpha = 0.6f)
    }
    val backgroundColor = if (isSelected) {
        (+themeColor { primary }).copy(alpha = 0.12f)
    } else {
        +themeColor { surface }
    }

    Padding(left = 8.dp, top = 8.dp, right = 8.dp) {
        Surface(
            color = backgroundColor,
            shape = RoundedCornerShape(4.dp)
        ) {
            Button(onClick = action, style = TextButtonStyle()) {
                Row(
                    mainAxisSize = LayoutSize.Expand,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    VectorImage(
                        id = icon,
                        tint = textIconColor
                    )
                    WidthSpacer(16.dp)
                    Text(
                        text = label,
                        style = (+themeTextStyle { body2 }).copy(
                            color = textIconColor
                        )
                    )
                }
            }
        }
    }
}
