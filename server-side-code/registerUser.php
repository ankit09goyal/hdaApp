<?php
/*
	   * Copyright (c) Hochschule Darmstadt. All rights reserved.
       * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
       *
       * You can redistribute and/or modify the code
       * under the terms of the GNU General Public License version 2 only, as
       * published by the Free Software Foundation. 
       *
       * Please contact Hochschule Darmstadt if you need additional information or have any
       * questions.
       * 
       * @author Ankit Goyal
       * @version 1.1
       * FUTURE EXTENSION NOTE: Another more efficient way to check duplicate email ID
       * 
       */
       
    require_once "sqlCon.php";
    
    
	function registerUser($obj){
	
    //getting values sent from android
    $userEmail = $obj->{'param1'};
    $userPhone = $obj->{'param2'};
    $userUniv = $obj->{'param3'};
    $userGradYear = $obj->{'param4'};
    $userCourseName = $obj->{'param5'};
    
    
    //encrypt the user email and phn nr
    $encryptedUserEmail = mc_encrypt($userEmail, ENCRYPTION_KEY);
    $encryptedUserPhone = mc_encrypt($userPhone, ENCRYPTION_KEY);
    
    //check for duplicate email ID
    $queryForDupEmail = "SELECT * FROM userInfo where username='$userEmail'";
    $rowGetDupEmail = executeQuery($queryForDupEmail);
    $lineDupEmail = mysql_fetch_array($rowGetDupEmail);
    $email = $lineDupEmail["username"];
    
    if($email==$userEmail){
        $dupEmail[] = "DuplicateEmail";
        json_encode($dupEmail);
        return(json_encode($dupEmail));
    }
    
    //not duplicate
    else{
        //get university id for the user
        $queryToGetUnivID = "SELECT * FROM University where UniversityName='$userUniv'";
        $rowGetUnivID = executeQuery($queryToGetUnivID);
        $lineUnivID = mysql_fetch_array($rowGetUnivID);
        $univID = $lineUnivID["UniversityID"];
        
        //get the course id for user
        $queryToGetCourseID = "SELECT * FROM Course where CourseName='$userCourseName' and UniversityId='$univID'and Batch='$userGradYear'";
        $rowGetCourseID = executeQuery($queryToGetCourseID);
        $lineCourseID = mysql_fetch_array($rowGetCourseID);
        $courseID = $lineCourseID["CourseID"];
        
        //generate one time verification code
        $OTVerificationCode = rand_md5(5);
        
        //generate exchange key
        $exchangeKey = rand_md5(16);
        $encryptedExchangeKey = mc_encrypt($exchangeKey, ENCRYPTION_KEY);
        
        //genearate hash key
        $hash = md5(rand(0,1000));
        
        //insert user info into database
        $query = "INSERT INTO userInfo (username, phoneNr, email, password, CourseID, OTVerificationCode, exchangeKey, hash) VALUES ('". mysql_escape_string($userEmail) ."', '$encryptedUserPhone', '$encryptedUserEmail', '". mysql_escape_string(md5($userPhone)) ."', '". mysql_escape_string($courseID) ."', '". mysql_escape_string($OTVerificationCode) ."', '$encryptedExchangeKey', '". mysql_escape_string($hash) ."')";

        $result = executeQuery($query);
        
        //send verification link to the user's email ID
        sendEmail($userEmail, $userPhone, $hash);
 
        $output[] = "1";
        json_encode($output);
        return(json_encode($output));
    }
  }  
  
	//function to send email to the user
	//currently the email is hard coded, just replace with "userEmail" variable 
	//once the online server is activated  
    function sendEmail($email1, $phone1, $hash1){
        $to      = 'ankit09goyal@gmail.com'; // Send email to our user | email is harcoded, change it in future
        $subject = 'Signup | Verification'; // Give the email a subject
        $message = '
        
        Thanks for signing up!
            Your account has been created, you can login with the following credentials after you have activated your account by pressing the url below.
            
            ------------------------
            Username: '.$email1.'
            Password: Your mobile number given during the time of registration
            ------------------------
            
            Please click this link to activate your account:
            http://localhost/mycode/verify.php?email='.$email1.'&hash='.$hash1.'
            
            ';
            
            $headers = 'From:ankit09goyal@gmail.com' . "\r\n"; // Set from headers
        mail($to, $subject, $message, $headers); // Send our email
    }
    
    //function to generate random string of particular length
    //use md5 for generating random characters
    //@param length the length of generated random string
    function rand_md5($length) {
        $random = '';
        for ($i = 0; $i < $length; $i ++) {
            $random .= md5(microtime(true).mt_rand(10000,90000));
        }
        return substr($random, 0, $length);
    }
    ?>