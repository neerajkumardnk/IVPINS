<?php 
	require_once 'DbOperation.php';
	$response = array(); 

	if($_SERVER['REQUEST_METHOD']=='POST'){

		$token = $_POST['token'];
		$email = $_POST['email'];
		$phone_number = $_POST['phone'];
		$vehicle_number = $_POST['vehicle'];
		

		$db = new DbOperation(); 
		try{
		$result = $db->registerDevice($email,$token,$phone_number,$vehicle_number);
		}
		catch(Exception $ex) { echo $ex->getMessage();}
		

		
		
		if($result == 0){
			$response['error'] = false; 
			$response['message'] = 'Device registered successfully';
		}elseif($result == 2){
			$response['error'] = true; 
			$response['message'] = 'Device already registered';
		}else{
			$response['error'] = true;
			$response['message']='Device not registered';
		}
	}else{
		$response['error']=true;
		$response['message']='Invalid Request...';
	}

	echo json_encode($response);