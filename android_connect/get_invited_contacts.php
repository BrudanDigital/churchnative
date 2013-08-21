<?php

/*
 * Following code will get a list of all the events
 */


// array for JSON response
$response = array();

// include db connect class
require_once 'db_connect.php';

//get the inviters contact details and the event id
$inviter_name= $_POST['inviter_name'];
$inviter_number=$_POST['inviter_number'];
$event_id=$_POST['event_id'];

// connecting to db
$db = new DB_CONNECT();

//query sql
$query="SELECT * FROM INVITED_CONTACTS where INVITER_NAME='$inviter_name' and INVITER_NUMBER='$inviter_number' and EVENT_ID='$event_id'";

// get all contacts from invited_contacts table
$result = mysql_query($query) or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // contacts node
    $response["contacts"] = array();

    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $contact = array();
        $contact["name"] = $row["invitee_name"];
        $contact["number"] = $row["invitee_number"];

        // push single contact into final response array
        array_push($response["contacts"], $contact);
    }
    // success
    $response["success"] = 1;
    $response["message"] = "Invited Contacts Retrieved";

    // echoing JSON response
    echo json_encode($response);
} else {
    // no events found
    $response["success"] = 0;
    $response["message"] = "No Invited Contacts Found For Event:".$event_id;

    // echo no users JSON
    echo json_encode($response);
}
?>