<?php

/*
 * Following code will create a new event row
 * All event details are read from HTTP Post Request
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['inviter_name']) && isset($_POST['inviter_number']) && isset($_POST['invitee_name']) && isset($_POST['invitee_number'])) {
    $inviter_name = $_POST['inviter_name'];

    $inviter_number = $_POST['inviter_number'];

    $invitee_name = $_POST['invitee_name'];

    $invitee_number = $_POST['invitee_number'];

    $event_id = $_POST['event_id'];


    // include db connect class
    require_once 'db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    $query = "INSERT INTO INVITED_CONTACTS(inviter_name,inviter_number,invitee_name, invitee_number,event_id) VALUES(\"$inviter_name\",\"$inviter_number\",\"$invitee_name\", \"$invitee_number\",\"$event_id\")";

    // mysql inserting a new row
    $result = mysql_query($query);

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Contacts SuccessFully Invited";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred while inviting ".$invitee_name." ".$invitee_number.".Check to See That You Are Not ReInviting Them To The Same Event";

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