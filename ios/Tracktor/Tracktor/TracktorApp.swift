//
//  TracktorApp.swift
//  Tracktor
//
//  Created by Denys Nykyforov on 17/9/20.
//

import SwiftUI

@main
struct TracktorApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
