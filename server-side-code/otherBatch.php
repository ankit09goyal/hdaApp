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
    
	function otherBatch($obj){
	
    $identifier = $obj->{'param1'};
    $batch = $obj->{'param2'};
    $userID = $obj->{'param3'};
    $courseID = $obj->{'param4'};
    
	if($identifier=="0"){
	//get course name and university ID
     $query = "SELECT * FROM Course where CourseID='$courseID'";
     $sql = executeQuery($query);
	 $line = mysql_fetch_array($sql);
	 $courseName = $line["CourseName"];
	 $universityID = $line["UniversityId"];
	 
	 //get selected batch's course ID
	$otherBatchQuery = "SELECT * FROM Course where UniversityId='$universityID' AND CourseName='$courseName' AND Batch='$batch'";
	$otherBatchSql = executeQuery($otherBatchQuery);
	while($row=mysql_fetch_assoc($otherBatchSql))
            $output[]=$row;
        json_encode($output);
        return(json_encode($output));
		
		}
	}
    ?>