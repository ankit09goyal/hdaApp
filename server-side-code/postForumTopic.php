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
       
    require_once "sqlCon.php";
   
    //post new topic in a course forum
	function postForumTopic($obj){
	
    $forumTopic = $obj->{'param1'};
    $forumDesc = $obj->{'param2'};
    $userID = $obj->{'param3'};
    $courseID = $obj->{'param4'};
    
    //encrypt the forum title and desc
    $encryptedForumTopic = mc_encrypt($forumTopic, ENCRYPTION_KEY);
    $encryptedForumDesc = mc_encrypt($forumDesc, ENCRYPTION_KEY);
    
    //insert the encrypted data
    $query = "INSERT INTO Forum (CourseID, UserID, Title, Description) VALUES ('$courseID', '$userID', '$encryptedForumTopic', '$encryptedForumDesc')";
    $result = executeQuery($query);
    $output[] = "1";
    json_encode($output);
    return(json_encode($output));
  }  
    ?>