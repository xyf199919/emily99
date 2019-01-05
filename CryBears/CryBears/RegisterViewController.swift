//
//  RegisterViewController.swift
//  CryBears
//
//  Created by Yunfang Xiao on 11/28/18.
//  Copyright © 2018 韩笑尘. All rights reserved.
//

import UIKit
import FirebaseAuth

class RegisterViewController: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var emailTextField: UITextField!
    
    @IBOutlet weak var passwordTextField: UITextField!
    
    var userEmail = ""
    var userPassword = ""
    
    @IBAction func registerPressed(_ sender: Any) {
        self.userEmail = emailTextField.text! + "@berkeley.edu"
        self.userPassword = passwordTextField.text!
        
        if self.userEmail == "" || self.userPassword == "" {
            let alertController = UIAlertController(title: "Form Error.", message: "Please fill in form completely.", preferredStyle: .alert)
            let defaultAction = UIAlertAction(title: "OK", style: .cancel, handler: nil)
            alertController.addAction(defaultAction)
            present(alertController, animated: true, completion: nil)
            
        } else {
            Auth.auth().createUser(withEmail: self.userEmail, password: self.userPassword) { (user, error) in
                if error == nil {
                    self.performSegue(withIdentifier:"segueRegisterToMapPage", sender: self)
                    let alertController = UIAlertController(title: "Sign Up Success", message: error?.localizedDescription, preferredStyle: .alert)
                    let defaultAction = UIAlertAction(title: "OK", style: .cancel, handler: nil)
                    alertController.addAction(defaultAction)
                    self.present(alertController, animated: true, completion: nil)
                } else {
                    let alertController = UIAlertController(title: "Sign Up Error", message: error?.localizedDescription, preferredStyle: .alert)
                    let defaultAction = UIAlertAction(title: "OK", style: .cancel, handler: nil)
                    alertController.addAction(defaultAction)
                    self.present(alertController, animated: true, completion: nil)
                }
            }
        }
    }
    
    @IBAction func logInPressed(_ sender: Any) {
        
        performSegue(withIdentifier:"segueRegisterToLogIn", sender: self)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    override func viewDidAppear(_ animated: Bool) {
        if Auth.auth().currentUser != nil {
            performSegue(withIdentifier:"segueRegisterToMapPage", sender: self)
        }
    }
}
