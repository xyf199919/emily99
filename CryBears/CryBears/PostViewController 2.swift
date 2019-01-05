//
//  PostViewController.swift
//  CryBears
//
//  Created by Yunfang Xiao on 11/28/18.
//  Copyright © 2018 韩笑尘. All rights reserved.
//

import UIKit
import Firebase

class PostViewController: UIViewController {
    var ref: DatabaseReference?
    var Handle: DatabaseHandle?
    
    var lon = -122.1
    var lat = 32.4
    
    @IBOutlet weak var postcontent: UITextField!
    
    @IBAction func submit(_ sender: Any) {
        ref = Database.database().reference()
        let content = postcontent.text
        
        let date = Date()
        let formatter = DateFormatter()
        formatter.dateFormat = "dd.MM.yyyy"
        let result = formatter.string(from: date)
        
        let post:[String: AnyObject] =
            ["content": content as AnyObject,
             "likes": 0 as AnyObject,
             "date": result as AnyObject,
             "lat": lat as AnyObject,
             "lon": lon as AnyObject]
        ref?.child("Posts").childByAutoId().setValue(post)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        postcontent.text = ""

        // Do any additional setup after loading the view.
    }
}
