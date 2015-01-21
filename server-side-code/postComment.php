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

    //insert comment posted by the user in comment table
	function postComment($obj){	
    	$comment = $obj->{'param1'};
	    $ForumTopicID = $obj->{'param2'};
    	$userID = $obj->{'param3'};
    
    	//encrypt the forum title and desc
	    $encryptedComment = mc_encrypt($comment, ENCRYPTION_KEY);

    	$query = "INSERT INTO Comment (TopicID, UserID, Comment) VALUES ('$ForumTopicID', '$userID', '$encryptedComment')";    
	    $result = executeQuery($query);
    	$output[] = "1";
	    json_encode($output);
    	return(json_encode($output));
   } 
    ?>