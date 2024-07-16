package com.krokosha.test_delete_it

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.Button
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.krokosha.test_delete_it.ui.theme.Test_delete_itTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlin.time.Duration.Companion.milliseconds

private const val FIRST_SCREEN_ROUTE = "first_screen"
private const val SECOND_SCREEN_ROUTE = "second_screen"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Test_delete_itTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    val navigator: NavHostController = rememberNavController()

    NavHost(
        navController = navigator,
        startDestination = FIRST_SCREEN_ROUTE
    ) {
        composable(FIRST_SCREEN_ROUTE) { FirstScreen(onClick = { navigator.navigate(SECOND_SCREEN_ROUTE) }) }
        composable(SECOND_SCREEN_ROUTE) { SecondScreen() }
    }
}

@Composable
fun SecondScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "SECOND SCREEN")
    }
}

@Composable
fun FirstScreen(onClick: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxSize()
    ) {
        LeftPanel()
        RightPanel(onClick = onClick)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RowScope.LeftPanel() {
    val buttons: List<String> by rememberSaveable { mutableStateOf(List(5) { "Button ${it + 1}" }) }

    val isPaneFocused = rememberSaveable { mutableStateOf(true) }

    val focusModifiers = FocusRequesterModifiers.create(
        onComposableFocusEntered = { isPaneFocused.value = true },
        onComposableFocusExited = { isPaneFocused.value = false }
    )

    BackHandler(enabled = !isPaneFocused.value) {
        focusModifiers.childFocusRequester.requestFocus()
    }

    LifecycleEventObserver { event: Lifecycle.Event ->
        if (event == Lifecycle.Event.ON_RESUME){
            if (isPaneFocused.value
                && !focusModifiers.needsRestore.value
                && !focusModifiers.parentFocusRequester.restoreFocusedChild()) {
                focusModifiers.childFocusRequester.requestFocus()
            }
        }
    }

    LazyColumn(
        modifier = focusModifiers.parentModifier
            .background(Color.Blue.copy(alpha = 0.1f))
            .fillMaxHeight()
            .weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(
            items = buttons,
            key = { idx, _ -> idx }
        ) { idx, _ ->
            Button(
                modifier = Modifier
                    .let { modifier ->
                        if (idx == 0) {
                            focusModifiers.childModifier
                        } else {
                            modifier
                        }
                    },
                onClick = { /* nothing */ }
            ) {
                Text(text = "Left Panel: $idx")
            }
        }
    }
}

@Composable
fun RowScope.RightPanel(onClick: () -> Unit) {
    val focusModifiers = FocusRequesterModifiers.create()
    val buttons: List<String> by rememberSaveable { mutableStateOf(List(4) { "Button ${it + 1}" }) }

    Column(
        modifier = focusModifiers
            .parentModifier
            .background(Color.Green.copy(alpha = 0.1f))
            .fillMaxHeight()
            .weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyVerticalGrid(
            modifier = Modifier.padding(16.dp),
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(
                items = buttons,
                key = { idx, _ -> idx }
            ) { idx, _ ->
                Button(
                    modifier = Modifier
                        .padding(8.dp)
                        .let { modifier ->
                            if (idx == 0) focusModifiers.childModifier
                            else modifier
                        }
                    ,
                    onClick = {
                        focusModifiers.onNavigateOut()
                        onClick()
                    }
                ) {
                    Text(text = "Right Panel: $idx")
                }
            }
        }
    }
}

class FocusRequesterModifiers private constructor(
    val parentModifier: Modifier,
    val parentFocusRequester: FocusRequester,
    val childModifier: Modifier,
    val childFocusRequester: FocusRequester,
    val needsRestore: MutableState<Boolean>
) {
    // Whenever we have a navigation event, need to call this before actually navigating.
    @OptIn(ExperimentalComposeUiApi::class)
    fun onNavigateOut() {
        needsRestore.value = true
        parentFocusRequester.saveFocusedChild()
    }

    companion object {
        /**
         * Returns a set of modifiers [FocusRequesterModifiers] which can be used for restoring focus and
         * specifying the initially focused item.
         */
        @OptIn(ExperimentalComposeUiApi::class)
        @Composable
        fun create(
            parentFocusRequester: FocusRequester = FocusRequester(),
            onComposableFocusEntered: (() -> Unit)? = null,
            onComposableFocusExited: (() -> Unit)? = null
        ): FocusRequesterModifiers {
            val focusRequester = remember { parentFocusRequester }
            val childFocusRequester = remember { FocusRequester() }
            val needsRestore = rememberSaveable { mutableStateOf(false) }

            val parentModifier = Modifier
                .focusRequester(focusRequester)
                .focusProperties {
                    exit = {
                        onComposableFocusExited?.invoke()
                        focusRequester.saveFocusedChild()
                        FocusRequester.Default
                    }
                    enter = {
                        onComposableFocusEntered?.invoke()

                        if (focusRequester.restoreFocusedChild()) { FocusRequester.Cancel }
                        else { childFocusRequester }
                    }
                }

            val childModifier = Modifier.focusRequester(childFocusRequester)

            LifecycleEventObserver { event: Lifecycle.Event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    if (needsRestore.value) {
                        childFocusRequester.requestFocus()
                        needsRestore.value = false
                    }
                }
            }

            return FocusRequesterModifiers(
                parentModifier,
                focusRequester,
                childModifier,
                childFocusRequester,
                needsRestore
            )
        }
    }
}

@Composable
fun LifecycleEventObserver(onLifecycleEvent: (Lifecycle.Event) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            onLifecycleEvent(event)
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}