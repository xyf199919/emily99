//
//  MapViewController.swift
//  CryBears
//
//  Created by Yunfang Xiao on 11/28/18.
//  Copyright © 2018 韩笑尘. All rights reserved.
//

import UIKit
import MapKit
import CoreLocation
import Firebase

class MapViewController: UIViewController{
    
    @IBAction func write(_ sender: Any) {
        performSegue(withIdentifier: "segueMapToPostPage", sender: self)
    }
    
    @IBOutlet weak var map: MKMapView!
    var locationManager: CLLocationManager!
    
    var ref: DatabaseReference?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        centerViewOnBerkeley()
        map.delegate = self
        locationManager = CLLocationManager()
        locationManager.desiredAccuracy = kCLLocationAccuracyNearestTenMeters
        locationManager.delegate = self
        checkLocationAuthorization()
        
        ref = Database.database().reference()
        ref?.child("Posts").observeSingleEvent(of: .value, with: { (snapshot) in
            
            var array = [[String: Any?]]()
            
            guard let children = snapshot.children.allObjects as? [DataSnapshot]
                else { return }
            
            for child in children {
                guard let dict = child.value as? [String: Any] else { continue }
                array.append(["postid": child.key, "lat": dict["lat"], "lon": dict["lon"], "content": dict["content"]])
            }
            
            print(array)
            
            array.forEach { item in
                print(item["lat"]!!)
                print(item["lon"]!!)
                let pin = MKPointAnnotation()
                let coord = CLLocationCoordinate2DMake(item["lat"] as! CLLocationDegrees, item["lon"] as! CLLocationDegrees)
                pin.coordinate = coord

                if let post = item["content"] {
                    let str = post as! String
                    if ((post! as! String).count > 10) {
                        pin.title = str
                    } else {
                        let firstSpace = str.firstIndex(of: " ") ?? str.endIndex
                        let rest = str[..<firstSpace]
                        let secondSpace = rest.firstIndex(of: " ") ?? rest.endIndex
                        let restofrest = str[..<secondSpace] + "..."
                        pin.title = String(restofrest)
                    }
                }
                
                if let postid = item["postid"] {
                    let str = postid as! String
                    pin.subtitle = str
                }
                self.map.addAnnotation(pin)
            }
        })
    }
    
    func centerViewOnBerkeley() {
        let location = CLLocationCoordinate2DMake(37.8713, -122.2591)
        let span = MKCoordinateSpan(latitudeDelta: 0.012, longitudeDelta: 0.012)
        let region = MKCoordinateRegion(center: location, span: span)
        map.setRegion(region, animated: true)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let identifier = segue.identifier {
            if identifier == "segueMapToViewPage" {
                if let dest = segue.destination as? ViewViewController{
                    let pin = sender as! MKPointAnnotation
                    dest.labelText = pin.title as? String
                    dest.postid = pin.subtitle as? String
                }
            } else if identifier == "segueMapToPostPage" {
                if let dest = segue.destination as? PostViewController{
                    dest.lat = lat
                    dest.lon = lon
                }
            }
        }
    }
    
    var lat = 37.4
    var lon = -122.1

    func checkLocationAuthorization() {
        switch CLLocationManager.authorizationStatus() {
        case .authorizedWhenInUse:
            map.showsUserLocation = true
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
        lat = myLocation.latitude
        print(lat)
        lon = myLocation.longitude
        print(lon)
    }
    
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        checkLocationAuthorization()
    }
}

extension MapViewController: MKMapViewDelegate {
    
    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
        
        var annotationView = mapView.dequeueReusableAnnotationView(withIdentifier: "AnnotationView")
        
        if annotationView == nil {
            annotationView = MKAnnotationView(annotation: annotation, reuseIdentifier: "AnnotationView")
            annotationView?.tintColor = .green
        }
        annotationView?.image = UIImage(named: "pin")
        annotationView?.canShowCallout = false
        return annotationView
    }
    
    func mapView(_ mapView: MKMapView, didSelect view: MKAnnotationView) {
        print("selected: \(String(describing: view.annotation?.title))")
        performSegue(withIdentifier: "segueMapToViewPage", sender: view.annotation as Any?)
    }
}
