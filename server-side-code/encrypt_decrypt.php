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
    * the file contains encryption and decryption functions
    * Here I have used mcrypt_encrypt/decrypt class for encrypting
    * and decrypting the data
    * FUTURE EXTENSION NOTE: Instead of using hard coded encryption key, generate a random key for
    * encrypting the data so that the programm writer also wont be able to decrypt the data saved on the server
    * except the user to which that data belongs
    */       
    
    //my encryption key
    define('ENCRYPTION_KEY', 'd0a7e7997b6d5fcd55f4b5c32611b87cd923e88837b63bf2941ef819dc8ca282');
    
    // Encrypt Function
    function mc_encrypt($encrypt, $key){
        $encrypt = serialize($encrypt);
        
        //get the size of IV
        $iv_size = mcrypt_get_iv_size(MCRYPT_RIJNDAEL_256, MCRYPT_MODE_CBC);
        
        //generate IV
        $iv = mcrypt_create_iv($iv_size, MCRYPT_RAND);
        
        //represent key in hexadecimal
        $key = pack('H*', $key);
        
        //generate hashMAC
        $mac = hash_hmac('sha256', $encrypt, substr(bin2hex($key), -32));
        
        //encrypt data and add mac to the data to be encrypted
        $passcrypt = mcrypt_encrypt(MCRYPT_RIJNDAEL_256, $key, $encrypt.$mac, MCRYPT_MODE_CBC, $iv);
        
        //encode the data and attach the IV to the encrypted data
        $encoded = base64_encode($passcrypt).'|'.base64_encode($iv);
        return $encoded;
    }
    
    // Decrypt Function
    function mc_decrypt($decrypt, $key){
        //seperate the encrypted data and iv
        $decrypt = explode('|', $decrypt);
        $decoded = base64_decode($decrypt[0]);
        $iv = base64_decode($decrypt[1]);
        
        //get the key
        $key = pack('H*', $key);

        //decrypt the data
        $decrypted = trim(mcrypt_decrypt(MCRYPT_RIJNDAEL_256, $key, $decoded, MCRYPT_MODE_CBC, $iv));

        //get the mac
        $mac = substr($decrypted, -64);
        
        //data used for creating the mac
        $decrypted = substr($decrypted, 0, -64);
        $calcmac = hash_hmac('sha256', $decrypted, substr(bin2hex($key), -32));
        
        //compare the calculate mac and the mac we have rcvd
        if($calcmac!==$mac){ return false; }
        
        $decrypted = unserialize($decrypted);
        return $decrypted;
    }
    ?>