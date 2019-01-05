//
//  LoginViewController.swift
//  CryBears
//
//  Created by Yunfang Xiao on 11/28/18.
//  Copyright © 2018 韩笑尘. All rights reserved.
//

import UIKit
import FirebaseAuth

class LoginViewController: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var emailTextField: UITextField!
    
    @IBOutlet weak var passwordTextField: UITextField!
    
    var userEmail = ""
    var userPassword = ""
    
    @IBAction func logInPressed(_ sender: Any) {
        self.userEmail = emailTextField.text! + "@berkeley.edu"
        self.userPassword = passwordTextField.text!
        
        if self.userEmail == "" || self.userPassword == "" {
            //Alert to tell the user that there was an error because they didn't fill anything in the textfields
            let alertController = UIAlertController(title: "Log In Error", message: "Please enter an email and password.", preferredStyle: .alert)
            let defaultAction = UIAlertAction(title: "OK", style: .cancel, handler: nil)
            alertController.addAction(defaultAction)
            self.present(alertController, animated: true, completion: nil)
        }
        else {
            Auth.auth().signIn(withEmail: self.userEmail, password: self.userPassword) { (user, error) in
                if error == nil {
                    self.performSegue(withIdentifier:"segueLogInToMapPage", sender: self)
                } else {
                    let alertController = UIAlertController(title: "Log In Error", message: error?.localizedDescription, preferredStyle: .alert)
                    let defaultAction = UIAlertAction(title: "OK", style: .cancel, handler: nil)
                    alertController.addAction(defaultAction)
                    self.present(alertController, animated: true, completion: nil)
                }
            }
        }
    }
    
    @IBAction func registerPressed(_ sender: Any) {
        performSegue(withIdentifier:"segueLogInToRegister", sender: self)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    override func viewDidAppear(_ animated: Bool) {
        if Auth.auth().currentUser != nil {
            performSegue(withIdentifier:"segueLogInToMapPage", sender: self)
        }
    }
}
