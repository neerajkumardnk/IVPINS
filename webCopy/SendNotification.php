<?php class SendNotification{ 	
 	
    public function __construct(){		
    }
    
    public function sendPushNotificationToGCMSever($token, $message, $title){
        
        include_once 'config.php';
        
        $path_to_firebase_cm = 'https://fcm.googleapis.com/fcm/send';
        
        $fields = array(
            'to' => $token,
            'notification' => array('title' => $title, 'body' => $message),
            //'data' => array('message' => $message)
        );
 
        $headers = array(
            'Authorization:key=' . SERVER_KEY,
            'Content-Type:application/json'
        );		
        $ch = curl_init();
 
        curl_setopt($ch, CURLOPT_URL, $path_to_firebase_cm); 
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true); 
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_IPRESOLVE, CURL_IPRESOLVE_V4 ); 
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
    
        $result = curl_exec($ch);
       
        curl_close($ch);

        return $result;
    }
 }