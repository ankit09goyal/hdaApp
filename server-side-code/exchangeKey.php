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
       */
       
/**
* here we extract user's exchange key from the server, decrypt it and send it to android
* We also verify the user's friend email and corresponding exchange key.
* if verified, user's friend authentication level is incereased by one
*/
    require_once "sqlCon.php";
    
	function exchangeKey($obj){
	 
	
    //getting values sent from android
    $option = $obj->{'param1'};
    
    //get exchange key from the server corresponding to the userID
    if($option=="1"){
        $userID = $obj->{'param2'};
        $query = "SELECT * FROM userInfo where userID='$userID'";
        $sql = executeQuery($query);
        while($row=mysql_fetch_assoc($sql))
            $output[]=$row;
        
        //decrypt the exchangeKey rcvd from server and replace it with encrypted exchange key
        if($output != null){
            foreach($output as $key => $value)
            {
                $output[$key]['exchangeKey'] = mc_decrypt($output[$key]['exchangeKey'], ENCRYPTION_KEY);
            }
        }
        
        //encode data in json and send to android
        json_encode($output);
        return(json_encode($output));
    }
    
    //verify user's frnd email and exchange key
    else if($option=="2"){
        $userID = $obj->{'param2'};
        $frndEmail = $obj->{'param3'};
        $frndVerifcationCode = $obj->{'param4'};
        
        //check whether the user is veryfying himself
        $checkUser = checkUserNotVerifyHim($userID, $frndEmail);
        
        //user is trying to verify himself
        //send error message to the android
        if($checkUser==1){
            $output[]="userVerifyHimself";
            json_encode($output);
            return(json_encode($output));
        }
        
        //check whether the user has already verified his frnd or not
        else{
            $checkFrnd = checkFrndVerification($userID, $frndEmail);
            
            //user is verifying his frnd again, send error msg to android
            if($checkFrnd==1){
                $output[]="userAlreadyVerifiedFrnd";
                json_encode($output);
                return(json_encode($output));
            }
         //check the validity of the exchange key corresponding to the frnd's email id   
         else{
                return checkTheValidityOfVerificationCode($userID, $frndEmail, $frndVerifcationCode);
            }
        }
    }
   } 
   
   //check user is not verifing himself
    function checkUserNotVerifyHim($userID, $frndEmail){
        $query = "SELECT * FROM userInfo where userID='$userID'";
        $sql = executeQuery($query);
        $line = mysql_fetch_array($sql);
        $userEmail = $line["email"];
        
        //decrypt the user email
        $userEmailDecrypted = mc_decrypt($userEmail, ENCRYPTION_KEY);
        
        if($userEmailDecrypted == $frndEmail){
            $result = 1;        //user is verifying himself, not allowed
            return $result;
        }
        else{
            $result = 0;
            return $result;
        }
    }
    
    //check whether user is verifing his frnd again
    function checkFrndVerification($userID, $frndEmail){
        $query = "SELECT * FROM userAndFrndsTheyVerified where userID='$userID' AND verifiedFrndsEmailID='$frndEmail'";
        $sql = executeQuery($query);
        $match  = mysql_num_rows($sql);
        if($match > 0){
            $result = 1;        //user has already verified his frnd
            return $result;
        }
        else{
            $result = 0;
            return $result;
        }
    }
    
    function updateUserAndFrndVeriyTable($userID, $frndEmail){
        $query = "INSERT INTO userAndFrndsTheyVerified (userID, verifiedFrndsEmailID) VALUES ('$userID', '$frndEmail')";
        $result = executeQuery($query);
    }
    
    function checkTheValidityOfVerificationCode($userID, $frndEmail, $frndVerifcationCode){
        $query = "SELECT * FROM userInfo where username='$frndEmail'";
        $sql = executeQuery($query);
        $line = mysql_fetch_array($sql);
        $exchangeKey = $line["exchangeKey"];
        
        $exchangeKeyDecrypted = mc_decrypt($exchangeKey, ENCRYPTION_KEY);
        
        if($frndVerifcationCode == $exchangeKeyDecrypted){
            //update the frndVerify table and add the userID and his frnd's email ID
            //This table is used to check whether user is trying to verify his frnd again or not  
        	updateUserAndFrndVeriyTable($userID, $frndEmail);
        	
        	//increase user's frnd authentication level by one
            mysql_query("UPDATE userInfo SET userAuthLevel = userAuthLevel + 1 WHERE username='".$frndEmail."'");
            $output[]="1";
            json_encode($output);
            return(json_encode($output));
        }
    }
    
    ?>