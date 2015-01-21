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
 * validate user email id and password and if correct return the userInfo
 * Login authentication
 */      
    require_once "sqlCon.php";
    
	function login($obj){
	
		$username = urldecode(mysql_escape_string($obj->{'param1'}));
		$password = urldecode(mysql_escape_string(md5($obj->{'param2'})));
    
		$query = "SELECT * FROM userInfo where username='$username' and password='$password'";
		$sql = executeQuery($query);
		$line = mysql_fetch_array($sql);
		$verifiedUser = $line["verified"];
    
    //if user is verified then send his information to Android
		if($verifiedUser==1){
			$query1 = "SELECT * FROM userInfo where username='$username' and password='$password'";
			$sql1 = executeQuery($query1);
			while($row=mysql_fetch_assoc($sql1))
				$output[]=$row;

			json_encode($output);
			return json_encode($output);
		}   
		else{
			$output[]="-1";
			json_encode($output);
			return json_encode($output);
		}
	}
?>