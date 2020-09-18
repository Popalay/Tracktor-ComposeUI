//
//  AppDelegate.swift
//  Tracktor
//
//  Created by Denys Nykyforov on 18/9/20.
//

import UIKit
import domain

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        KoinIOS().initialize()
        return true
    }
}
