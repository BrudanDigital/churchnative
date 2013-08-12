<?php

/*
 * Following code will update an event 
 * An event is identified by event id (id)
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['id'])) {
    $id = $_POST['id'];
    $latitude = $_POST['latitude'];
    $longitude = $_POST['longitude'];
    $time = $_POST['time'];
    $date = $_POST['date'];
    $description = $_POST['description'];
    $name = $_POST['name'];
    $duration = $_POST['duration'];
    $location = $_POST['location'];
    $user_id = $_POST['user_id'];
    $type = $_POST['type'];

    // include db connect class
    require_once 'db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    $query = "UPDATE events_test SET location = '$location', start_time = '$time', description = '$description', latitude = '$latitude', longitude = '$longitude', duration = '$duration', date = '$date', name = '$name', owner_id = '$user_id', type_of_event = '$type' WHERE id = $id";
   
// mysql update row with matched pid
    $result = mysql_query($query);

    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Event successfully updated.";

        // echoing JSON response
        echo json_encode($response);
    } 
    else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";

        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    // echoing JSON response
    echo json_encode($response);
}
?>