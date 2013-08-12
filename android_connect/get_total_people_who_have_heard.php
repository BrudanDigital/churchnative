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
   

    // include db connect class
    require_once 'db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    $query = "Select * From heardOfEvent where event_id='$id'";

    // mysql selecting a  row
    $result = mysql_query($query);

        // check for empty result
    if (mysql_num_rows($result) > 0) {
        $total=0;
        while ($row = mysql_fetch_array($result)) 
        {
            $total++;
        }
        // success
        $response["success"] = 1;
        $response["total"] = $total;
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "No People Found";

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