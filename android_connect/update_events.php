<?php
 
/*
 * Following code will update a product information
 * A product is identified by product id (pid)
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['location']) && isset($_POST['time']) && isset($_POST['date']) && isset($_POST['description'])) {
    $owner_id=$_POST['owner_id'];
    $location = $_POST['location'];
    $time = $_POST['time'];
    $date = $_POST['date'];
    $description = $_POST['description'];
 
    // include db connect class
    require_once 'db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql update row with matched pid
    $result = mysql_query("UPDATE products SET location = '$location', time = '$time', description = '$description' WHERE owner_id = $owner_id");
 
    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Event successfully created.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
 
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>