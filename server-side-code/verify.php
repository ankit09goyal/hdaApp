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
       
       /*
       * when the user has clicked on the link sent to his email ID
       * the hash key is checked, if it matches, user is set as verified
       * FUTURE EXTENSION NOTE: no need to send user's email ID in the link
       * only hash key is enough to verify the user.
       */
       
    require_once "sqlCon.php";
    
    if(isset($_GET['email']) && !empty($_GET['email']) AND isset($_GET['hash']) && !empty($_GET['hash'])){
        // Verify data
        $email = mysql_escape_string($_GET['email']); // Set email variable
        $hash = mysql_escape_string($_GET['hash']); // Set hash variable
        
        
        
        $search = mysql_query("SELECT email, hash, verified FROM userInfo WHERE hash='".$hash."' AND verified='0'") or die(mysql_error());
        $match  = mysql_num_rows($search);
        
        if($match > 0){
            // We have a match, activate the account
            mysql_query("UPDATE userInfo SET verified='1' WHERE hash='".$hash."' AND verified='0'") or die(mysql_error());
            echo '<div class="statusmsg">Your account has been activated, you can now login</div>';
        }else{
            // No match -> invalid url or account has already been activated.
            echo '<div class="statusmsg">The url is either invalid or you already have activated your account.</div>';
        }
        
    }else{
        // Invalid approach
        echo '<div class="statusmsg">Invalid approach, please use the link that has been send to your email.</div>';
    }
    ?>