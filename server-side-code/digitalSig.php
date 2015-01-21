<?php

    require_once "sqlCon.php";
	include "login.php";
	include "exchangeKey.php";
    include "forumDetail.php";
    include "postForumTopic.php";
    include "otherBatch.php";
    include "forum.php";
    include "registerUser.php";
    include "settings.php";
    include "postComment.php";
	include "encrypt_decrypt.php";
	
	
    $error = "done";
	$space = " space ";
	
	
    $clientPublicKeySig = $_POST['clientPublicKeySig'];
    $clientData = $_POST['clientData'];
	$clientDataSig = $_POST['clientDataSig'];
	$decoded_signature=base64_decode($clientDataSig);
	$clientPublicKeySig_formatted="-----BEGIN PUBLIC KEY-----\n" . trim($clientPublicKeySig) . "\n-----END PUBLIC KEY-----";
 
	$verifyResult = openssl_verify($clientData, $decoded_signature, $clientPublicKeySig_formatted, "sha256WithRSAEncryption");
	//echo $verifyResult;
    
	if($verifyResult){
	$obj = json_decode($clientData);
	
	switch ($obj->{'param0'}) {
    case "login.php":
        $dataToClient= testDeepak($obj);
        break;
    case "exchangeKey.php":
        $dataToClient= exchangeKey($obj);
        break;
    case "forumDetail.php":
        $dataToClient= forumDetail($obj);
        break;
    case "postForumTopic.php":
        $dataToClient= postForumTopic($obj);
        break;
    case "otherBatch.php":
        $dataToClient= otherBatch($obj);
        break;
    case "forum.php":
        $dataToClient= forum($obj);
        break;
    case "registerUser.php":
        $dataToClient= registerUser($obj);
        break;
    case "settings.php":
        $dataToClient= settings($obj);
        break;
    case "postComment.php":
        $dataToClient= postComment($obj);
        break;    
	}
	}else{
	$dataToClient="unverified";
	}
	
	
	$config = array(
		"digest_alg" => "sha256",
		"private_key_bits" => 1024,
		"private_key_type" => OPENSSL_KEYTYPE_RSA,
	);
    
	// Create the private and public key
	$keyPair = openssl_pkey_new($config);

	// Extract the private key from $keyPair to $privKey
	openssl_pkey_export($keyPair, $privKey);

	// Extract the public key from $keyPair to $pubKey
	$pubKey = openssl_pkey_get_details($keyPair);
	$pubKey = $pubKey["key"];
	
	//Sign data
	openssl_sign($dataToClient, $signature, $privKey, "sha256WithRSAEncryption");
	
	//prepare array for sending to client
	$response[0] = array('data' =>  $dataToClient, 'serverPublicKeySig' => $pubKey, 'serverDataSig' => base64_encode($signature));

    //echo JSON
    echo (json_encode($response));
	

    
    ?>