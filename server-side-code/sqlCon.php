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
* file to connect to the server
* contains the database information, host, username and password
*/       
    @extract($_ENV);
    @extract($_SESSION);
    @extract($_GET);
    @extract($_POST);
    @extract($_FILES);
    @extract($_COOKIE);
    @extract($_SERVER);
    
    global $DB;
    $DB["dbName"] = 'hda_forum';
    $DB["host"] = 'localhost';
    $DB["user"] = 'root';
    $DB["pass"] = '';
    
    $local_mode = false;
    
    $link = mysql_connect($DB["host"], $DB["user"], $DB["pass"],false,MYSQL_CLIENT_COMPRESS) or die("<span style='FONT-SIZE:11px; FONT-COLOR: #000000; font-family=tahoma;'><center>An Internal Error has Occured while connecting to the database. Please report following error to the webmaster.<br><br>".mysql_error()."'</center>");
    mysql_select_db($DB["dbName"]);
   
   //execute mySql query 
    function executeQuery($sql)    
    {
        $result = mysql_query($sql) or die("<span style='FONT-SIZE:11px; FONT-COLOR: #000000; font-family=tahoma;'><center>An Internal Error has Occured for query execution. Please report following error to the webmaster.<br><br>".$sql."<br><br>".mysql_error()."'</center></FONT>");   
        return $result;
    }
    
    //get single row 
    function getSingleResult($sql)
    {
        $response = "";
        $result = mysql_query($sql) or die("<center>An Internal Error has Occured while fetching single result. Please report following error to the webmaster.<br><br>".$sql."<br><br>".mysql_error()."'</center>");
        if ($line = mysql_fetch_array($result)) {
            $response = $line[0];
        }
        return $response;
    }
    ?>