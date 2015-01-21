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
* used to get all the forum topics posted in a particular batch
*/
    require_once "sqlCon.php";

	function forum($obj){
	
    $identifier = $obj->{'param1'};
    
    //query forum table to get the forum topics posted in that course
    if($identifier=="0"){
        $CourseID = $obj->{'param2'};
        $query = "SELECT * FROM Forum where CourseID='$CourseID' ORDER BY TimeStamp ASC";
        $sql = executeQuery($query);
        while($row=mysql_fetch_assoc($sql))
            $output[]=$row;
        
        if($output != null){
            foreach($output as $key => $value)
            {
                $output[$key]['Title'] = mc_decrypt($output[$key]['Title'], ENCRYPTION_KEY);
                $output[$key]['Description'] = mc_decrypt($output[$key]['Description'], ENCRYPTION_KEY);
            }
        }
        json_encode($output);
        return(json_encode($output));
    }
    
    //query userInfo table to get the user info who has posted that topic
    else if($identifier=="1"){
        $userID = $obj->{'param2'};
        $query = "SELECT * FROM userInfo where userID='$userID'";
        $sql = executeQuery($query);
        while($row=mysql_fetch_assoc($sql))
            $output[]=$row;
        json_encode($output);
        return(json_encode($output));
    }
    
    //query comments table to get the comments posted in that topic
    else if($identifier=="2"){
        $ForumTopicID = $obj->{'param2'};
        $query = "SELECT * FROM Comment where TopicID='$ForumTopicID'";
        $sql = executeQuery($query);
        while($row=mysql_fetch_assoc($sql))
            $output[]=$row;
        json_encode($output);
        return(json_encode($output));
    }
    
    //query course table for batch year table
    else if($identifier=="3"){
        $courseID = $obj->{'param2'};
        $query = "SELECT * FROM Course where CourseID='$courseID'";
        $sql = executeQuery($query);
        while($row=mysql_fetch_assoc($sql))
            $output[]=$row;
        json_encode($output);
        return(json_encode($output));
    }
  }  
    ?>