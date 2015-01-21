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
* used to get the detail of the forum topic corresponding to the forum topic ID
* on which user has clicked
*/
    require_once "sqlCon.php";
  
	
	function forumDetail($obj){
	
    $identifier = $obj->{'param1'};
    
    //query forum table
    if($identifier=="0"){
        $ForumTopicID = $obj->{'param2'};
        $query = "SELECT * FROM Forum where ForumTopicID='$ForumTopicID'";
        $sql = executeQuery($query);
        while($row=mysql_fetch_assoc($sql))
            $output[]=$row;
        
        foreach($output as $key => $value)
        {
            $output[$key]['Title'] = mc_decrypt($output[$key]['Title'], ENCRYPTION_KEY);
            $output[$key]['Description'] = mc_decrypt($output[$key]['Description'], ENCRYPTION_KEY);
        }
        
        
        json_encode($output);
        return(json_encode($output));
    }
    
    //query comment table
    if($identifier=="1"){
        $ForumTopicID = $obj->{'param2'};
        $query = "SELECT * FROM Comment where TopicID='$ForumTopicID'";
        $sql = executeQuery($query);
        while($row=mysql_fetch_assoc($sql))
            $output[]=$row;
        
        if($output != null){
            foreach($output as $key => $value)
            {
                $output[$key]['Comment'] = mc_decrypt($output[$key]['Comment'], ENCRYPTION_KEY);
            }
        }
        
        
        json_encode($output);
        return(json_encode($output));
    }
    
    //query userInfo table
    if($identifier=="2"){
        $userID = $obj->{'param2'};
        $query = "SELECT * FROM userInfo where userID='$userID'";
        $sql = executeQuery($query);
        while($row=mysql_fetch_assoc($sql))
            $output[]=$row;
        json_encode($output);
        return(json_encode($output));
    }
	}
    ?>