<?php
 
/*
 * Following code will list all the events
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once 'db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// get all products from products table
$result = mysql_query("SELECT * FROM events_test") or die(mysql_error());
 
// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // products node
    $response["events"] = array();
 
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $events = array();
        $events["owner_id"] = $row["owner_id"];
        $events["latitude"] = $row["latitude"];
        $events["longitude"] = $row["longitude"];
        $events["time"] = $row["time"];
        $events["date"] = $row["date"];
        $events["description"] = $row["description"];
        $events["name"] = $row["name"];
        $events["duration"] = $row["duration"];
        $events["location"] = $row["location"];
        // push single product into final response array
        array_push($response["events"], $events);
    }
    // success
    $response["success"] = 1;
 
    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No events found";
 
    // echo no users JSON
    echo json_encode($response);
}
?>