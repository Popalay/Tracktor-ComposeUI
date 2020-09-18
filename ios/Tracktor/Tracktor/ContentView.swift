//
//  ContentView.swift
//  Tracktor
//
//  Created by Denys Nykyforov on 17/9/20.
//

import SwiftUI
import domain

struct ContentView: View {
    var repo = KoinIOS().get(objCClass: DataTrackingRepository.self, qualifier: nil, parameter: nil) as! DataTrackingRepository
    var body: some View {
        Text(repo.getTextFromSharedModule()).padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
