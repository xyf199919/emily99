//
//  MapViewController.swift
//  Herd
//
//  Created by Yunfang Xiao on 11/3/18.
//  Copyright © 2018 Draphix. All rights reserved.
//
import UIKit
import MapKit
import CoreLocation
import Firebase

class MapViewController: UIViewController {
    
    var code: String!
    var name: String?
   
    
    @IBOutlet var map: MKMapView!
    
    

    let locationManager = CLLocationManager()
    let regionInMeters: Double = 1000
    
    var ref: DatabaseReference?
    var Handle: DatabaseHandle?
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        checkLocationServices()
        
        ref = Database.database().reference()
        
        ref?.child(code).observeSingleEvent(of: .value, with: { (snapshot) in
            
            var array = [[String: Any?]]()
            
            guard let children = snapshot.children.allObjects as? [DataSnapshot]
                else { return }
            
            for child in children {
                guard let dict = child.value as? [String: Any] else { continue }
                array.append(["user": child.key, "lat": dict["lat"], "lon": dict["lon"]])
            }
            
            print(array)
            
            array.forEach { item in
                print(item)
                let userpin = MKPointAnnotation()
                let coord = CLLocationCoordinate2DMake(item["lat"] as! CLLocationDegrees, item["lon"] as! CLLocationDegrees)
                userpin.coordinate = coord
                userpin.title = item["user"] as? String
                self.map.addAnnotation(userpin)
                
            }
        })
        
        
//        Handle = ref?.child(code!).observe(.value, with: { (snapshot) in
//            if snapshot.exists() {
//                self.ref?.child(self.EnteredCode.text!).child(self.NAme.text!).child("lat").setValue("what")
//                self.ref?.child(self.EnteredCode.text!).child(self.NAme.text!).child("lon").setValue("what")
//
//            }
//        })

    }
    
    //2
    func setupLocationManager() {
        locationManager.delegate = self as CLLocationManagerDelegate
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
    }
    
    //4
    func centerViewOnUserLocation() {
        if let location = locationManager.location?.coordinate {
            let region = MKCoordinateRegionMakeWithDistance(location, regionInMeters, regionInMeters)
            map.setRegion(region, animated: true)
            
            
        }
    }
    
    //1
    func checkLocationServices() {
        if CLLocationManager.locationServicesEnabled() {
            setupLocationManager()
            checkLocationAuthorization()
        } else {
            // Show alert letting the user know they have to turn this on.
        }
    }
    
    //3
    func checkLocationAuthorization() {
        switch CLLocationManager.authorizationStatus() {
        case .authorizedWhenInUse:
            map.showsUserLocation = true
            centerViewOnUserLocation()
            locationManager.startUpdatingLocation()
            break
        case .denied:
            // Show alert instructing them how to turn on permissions
            break
        case .notDetermined:
            locationManager.requestWhenInUseAuthorization()
        case .restricted:
            // Show an alert letting them know what's up
            break
        case .authorizedAlways:
            break
        }
    }
}


extension MapViewController: CLLocationManagerDelegate {
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        manager.startUpdatingLocation()
        let myLocation = location.coordinate
        
        let lat = myLocation.latitude
        print(lat)
        let lon = myLocation.longitude
        print(lon)
        ref?.child(code!).child(name!).child("lat").setValue(lat)
        ref?.child(code!).child(name!).child("lon").setValue(lon)
        
        let region = MKCoordinateRegionMakeWithDistance(myLocation, regionInMeters, regionInMeters)
        map.setRegion(region, animated: true)
    }
    
    
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        checkLocationAuthorization()
    }
}
