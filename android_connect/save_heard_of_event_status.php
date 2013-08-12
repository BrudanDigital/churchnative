<?php

/*
 * Following code will check to see if the ip_address has heard of the given event 
 * An event is identified by event_id (event_id)
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['id'])) {
    $id = $_POST['id'];
    $ip_address=$_SERVER['REMOTE_ADDR'];
   

    // include db connect class
    require_once 'db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    $query = "Insert into heardOfEvent(event_id,ip_address) values($id,'$ip_address')";

    // mysql selecting a  row
    $result = mysql_query($query);

        // check for empty result
    if ($result) {

        // success
        $response["success"] = 1;
        $response["message"] = "Status Saved";
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = " Status Not Saved";

        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) Is Missing";

    // echoing JSON response
    echo json_encode($response);
}
?>