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

function hasUserHeardOfEvent($event_id,$ip_address) {
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

function distance($lat1, $lng1, $lat2, $lng2, $miles = false) {
    $pi80 = M_PI / 180;
    $lat1 *= $pi80;
    $lng1 *= $pi80;
    $lat2 *= $pi80;
    $lng2 *= $pi80;

    $r = 6372.797; // mean radius of Earth in km
    $dlat = $lat2 - $lat1;
    $dlng = $lng2 - $lng1;
    $a = sin($dlat / 2) * sin($dlat / 2) + cos($lat1) * cos($lat2) * sin($dlng / 2) * sin($dlng / 2);
    $c = 2 * atan2(sqrt($a), sqrt(1 - $a));
    $km = $r * $c;

    return ($miles ? ($km * 0.621371192) : $km);
}



// get all events from events table
$result = mysql_query("SELECT * FROM events_test") or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // events node
    $response["events"] = array();

    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $events = array();
        $event_id = $row["id"];
        $events["event_id"] = $event_id;
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
        $events["heard_of_event"] = hasUserHeardOfEvent($event_id,$ip_address);     
        $events["total"] = getTotalPeopleWhoHaveHeard($event_id);

        // push single event into final response array
        array_push($response["events"], $events);
    }
    // success
    $response["success"] = 1;
    $response["message"] = "Events Retrieved";

    // echoing JSON response
    echo json_encode($response);
} else {
    // no events found
    $response["success"] = 0;
    $response["message"] = "No events found";

    // echo no users JSON
    echo json_encode($response);
}
?>