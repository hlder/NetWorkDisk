package com.hld.networkdisk.client

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hld.networkdisk.client.pages.filelistpage.FileListPage
import com.hld.networkdisk.client.pages.homepage.HomePage

@Composable
fun ComposeApp() {
    val navController = rememberNavController()
    SunFlowerNavHost(
        navController = navController
    )
}

@Composable
fun SunFlowerNavHost(
    navController: NavHostController
) {
    val activity = (LocalContext.current as Activity)
    NavHost(navController = navController, startDestination = Routers.HOME) {
        composable(route = Routers.HOME) {
            HomePage { navController.navigate(Routers.FILE_LIST) }
        }
        composable(route = Routers.FILE_LIST) {
            FileListPage { path ->
                navController.navigate("${Routers.FILE_LIST}?filePath=$path")
            }
        }
        composable(
            route = "${Routers.FILE_LIST}?filePath={filePath}",
            arguments = listOf(navArgument("filePath") {
                type = NavType.StringType
            })
        ) {
            FileListPage { path ->
                navController.navigate("${Routers.FILE_LIST}?filePath=$path")
            }
        }
    }
}
