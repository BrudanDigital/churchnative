<?php

/*
 * Following code will get a list of all the events
 */


// array for JSON response
$response = array();

// include db connect class
require_once 'db_connect.php';

//get ip address
$ip_address = $_SERVER['REMOTE_ADDR'];
$invitee_name = $_POST['invitee_name'];
$invitee_number = $_POST['invitee_number'];

// connecting to db
$db = new DB_CONNECT();

function getTotalPeopleWhoHaveHeard($event_id) {
    //get total number of people who know about this event
    $querys = "Select * from heardOfEvent where event_id=$event_id";
    // get all events from events table
    $results = mysql_query($querys) or die(mysql_error());
    $total = 0;
    // check for empty result
    while (mysql_fetch_array($results)) {
        $total++;
    }
    return $total;
}

function hasUserHeardOfEvent($event_id, $ip_address) {
    //check to see if this user has heard about this event
    $querys = "Select * from heardOfEvent where ip_address='$ip_address' and event_id=$event_id";
    // get all events from events table
    $results = mysql_query($querys) or die(mysql_error());
    $bool = false;
    // check for empty result
    if (mysql_num_rows($results) > 0) {
        $bool = true;
    }
    return $bool;
}


//get event_id of event to which user is invited
$querys = "Select event_id from invited_contacts where invitee_name='$invitee_name' and invitee_number='$invitee_number'";
// get all events from events table
$results = mysql_query($querys) or die(mysql_error());
// check for empty result
if (mysql_num_rows($results) > 0) {
   
    // temp user array
    $response["events"] = array();
    // looping through all results
    // events node
    while ($rows = mysql_fetch_array($results)) {
       
        
        $event_id = $rows["event_id"];
        
        //use the event id to get the actual event
        $query = "SELECT * FROM events_test where id='$event_id'";
        
        // get all events from events table
        $result = mysql_query($query) or die(mysql_error());

        // check for empty result
        if (mysql_num_rows($result) > 0) {
            
            // looping through all results
            // events node
            while ($row = mysql_fetch_array($result)) {

                $events = array();
                $id = $row["id"];
                $events["event_id"] = $id;
                $events["latitude"] = $row["latitude"];
                $events["longitude"] = $row["longitude"];
                $events["time"] = $row["start_time"];
                $events["date"] = $row["date"];
                $events["description"] = $row["description"];
                $events["name"] = $row["name"];
                $events["duration"] = $row["duration"];
                $events["location"] = $row["location"];
                $events["user_id"] = $row["owner_id"];
                $events["type"] = $row["type_of_event"];
                $events["heard_of_event"] = hasUserHeardOfEvent($event_id, $ip_address);
                $events["total"] = getTotalPeopleWhoHaveHeard($event_id);

                // push single event into final response array
                array_push($response["events"], $events);
            }
        }
    }

    // success
    $response["success"] = 1;
    $response["message"] = "Events Retrieved";

    // echoing JSON response
    echo json_encode($response);
} else {
    // no events found
    $response["success"] = 0;
    $response["message"] = "OPPS SORRY!!!\nIt Appears Like You Have Not Been Invited To Any UpComming Events";

    // echo no users JSON
    echo json_encode($response);
}
?>