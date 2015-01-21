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
        
	function settings($obj){
	
    $identifier = $obj->{'param1'};
       
	//get email and phone nr
	if($identifier=="0"){
	$userID = $obj->{'param2'};
    $query = "SELECT * FROM userInfo where userID='$userID'";
    $sql = executeQuery($query);
	
	while($row=mysql_fetch_assoc($sql))
            $output[]=$row;
        
        //decrypt the email and phn nr
        if($output != null){
            foreach($output as $key => $value)
            {
                $output[$key]['phoneNr'] = mc_decrypt($output[$key]['phoneNr'], ENCRYPTION_KEY);
                $output[$key]['email'] = mc_decrypt($output[$key]['email'], ENCRYPTION_KEY);
            }
        }
        json_encode($output);
        return(json_encode($output));
	 }
	 
	 //course name and batch
	if($identifier=="1"){
	 $courseID = $obj->{'param2'};
	
	$otherBatchQuery = "SELECT * FROM Course where CourseID='$courseID'";
	$otherBatchSql = executeQuery($otherBatchQuery);
	while($row=mysql_fetch_assoc($otherBatchSql))
            $output[]=$row;
        json_encode($output);
        return(json_encode($output));
	}
	
	//university name
	if($identifier=="2"){
	
	//get the university ID corrs to the given courseID
	 $courseID = $obj->{'param2'};
     $query = "SELECT * FROM Course where CourseID='$courseID'";
     $sql = executeQuery($query);
	 $line = mysql_fetch_array($sql);
	 $universityID = $line["UniversityId"];
	 
	 //get the university name from the university ID
	 $otherBatchQuery = "SELECT * FROM University where UniversityID='$universityID'";
	$otherBatchSql = executeQuery($otherBatchQuery);
	while($row=mysql_fetch_assoc($otherBatchSql))
            $output[]=$row;
        json_encode($output);
        return(json_encode($output));
	}
	}
	?>