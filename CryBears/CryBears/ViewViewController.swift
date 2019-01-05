//
//  ViewViewController.swift
//  CryBears
//
//  Created by Yunfang Xiao on 11/28/18.
//  Copyright © 2018 韩笑尘. All rights reserved.
//

import UIKit
import Firebase

class ViewViewController: UIViewController {
    
    @IBOutlet weak var textfield: UITextView!
    
    @IBOutlet weak var likeBy: UILabel!
    
    var labelText: String?
    var ref: DatabaseReference!
    var postid: String?
    var cur_like: Int?
    
    @IBAction func like(_ sender: Any) {
        ref = Database.database().reference()
        ref.child("Posts").child(postid!).updateChildValues(["likes": cur_like! + 1])
        ref.child("Posts").child(postid!).observeSingleEvent(of: .value, with: { (snapshot) in
            let value = snapshot.value as? NSDictionary
            self.cur_like = value!["likes"] as! Int
            self.likeBy.text = "Liked by: " + String(self.cur_like!)
        })
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        textfield.text = labelText!
        ref = Database.database().reference()
        ref.child("Posts").child(postid!).observeSingleEvent(of: .value, with: { (snapshot) in
            let value = snapshot.value as? NSDictionary
            self.cur_like = value!["likes"] as! Int
            self.likeBy.text = "Liked by: " + String(self.cur_like!)
        })
        // Do any additional setup after loading the view.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
