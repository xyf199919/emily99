//
//  ViewController.swift
//  CryBears
//
//  Created by 韩笑尘 on 11/14/18.
//  Copyright © 2018 韩笑尘. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    @IBAction func logInPressed(_ sender: Any) {
        performSegue(withIdentifier:"segueMainToLogIn", sender: self)
    }
    
    @IBAction func registerPressed(_ sender: Any) {
        performSegue(withIdentifier:"segueMainToRegister", sender: self)
    }
}
