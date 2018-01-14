<?php 

 include_once 'SendNotification.php';

if(isset($_POST['title']) && isset($_POST['message']) && isset($_POST['token'])){
	$title = $_POST['title'];
    $message = $_POST['message'];
    $token = $_POST['token'];
    
    
    $serverObject = new SendNotification();	
    $jsonString = $serverObject->sendPushNotificationToGCMSever($token, $message, $title);

	
    $jsonObject = json_decode($jsonString);
}

