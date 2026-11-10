
package com.example.ch11

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ch11.ui.auth.LoginRoute
import com.example.ch11.ui.auth.RegisterRoute
import com.example.ch11.ui.edit.NoteEditRoute
import com.example.ch11.ui.list.NoteListRoute
import com.example.ch11.ui.session.SessionViewModel

@Composable
fun CatatanKuApp(
    sessionViewModel: SessionViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    val user by sessionViewModel.currentUser
        .collectAsStateWithLifecycle()

    val isSignedIn = user != null

    val startDestination = remember {
        if (isSignedIn) {
            Routes.NoteList.route
        } else {
            Routes.Login.route
        }
    }

    LaunchedEffect(isSignedIn) {
        val currentRoute = navController.currentDestination?.route

        val onAuthScreen = currentRoute?.let {
            it in authRoutes
        } ?: false

        when {
            isSignedIn && onAuthScreen -> {
                navController.navigateClearingBackStack(
                    Routes.NoteList.route
                )
            }

            !isSignedIn && currentRoute != null && !onAuthScreen -> {
                navController.navigateClearingBackStack(
                    Routes.Login.route
                )
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.Login.route) {
            LoginRoute(
                onNavigateToRegister = {
                    navController.navigate(Routes.Register.route)
                }
            )
        }

        composable(Routes.Register.route) {
            RegisterRoute(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.NoteList.route) {
            NoteListRoute(
                userEmail = user?.email,
                onAddNote = {
                    navController.navigate(
                        Routes.NoteEdit.createRoute()
                    )
                },
                onEditNote = { id ->
                    navController.navigate(
                        Routes.NoteEdit.createRoute(id)
                    )
                },
                onLogout = sessionViewModel::logout
            )
        }

        composable(
            route = Routes.NoteEdit.route,
            arguments = listOf(
                navArgument(ARG_NOTE_ID) {
                    type = NavType.StringType
                }
            )
        ) {
            NoteEditRoute(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

private fun NavHostController.navigateClearingBackStack(
    route: String
) {
    navigate(route) {
        popUpTo(graph.id) {
            inclusive = true
        }
        launchSingleTop = true
    }
}
