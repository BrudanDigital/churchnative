<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['latitude'])&& isset($_POST['longitude']) && isset($_POST['time'])&& isset($_POST['date']) && isset($_POST['description'])) {
 
    $latitude = $_POST['latitude'];
    $longitude = $_POST['longitude'];
    $time = $_POST['time'];
    $date = $_POST['date'];
    $description = $_POST['description'];
    $name=$_POST['name'];
    $duration=$_POST['duration'];
 
     // include db connect class
    require_once 'db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql inserting a new row
    $result = mysql_query("INSERT INTO events_test(latitude,longitude, time, date,description,name,duration) VALUES('$latitude','$longitude', '$time','$date', '$description','$name','$duration')");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Event successfully created.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
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